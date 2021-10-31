package io.army.boot.reactive;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.boot.GenericSQLExecutorSupport;
import io.army.codec.StatementType;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingType;
import io.army.sqldatatype.SQLDataType;
import io.army.stmt.*;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import io.jdbd.ResultRow;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class is a base class of sql reactive executor implementation. This class provide common method.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see InsertSQLExecutorImpl
 * @see UpdateSQLExecutorImpl
 * @see SelectSQLExecutorImpl
 */
abstract class SQLExecutorSupport extends GenericSQLExecutorSupport {

    private static final Logger LOG = LoggerFactory.getLogger(SQLExecutorSupport.class);

    final InnerGenericRmSessionFactory sessionFactory;

    final MappingContext mappingContext;

    SQLExecutorSupport(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        this.mappingContext = sessionFactory.dialect().mappingContext();
    }


    /**
     * execute update method of {@link PreparedStatement},and assert optimistic lock .
     *
     * @param executeFunction {@link PreparedStatement}'s execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeUpdate()}</li>
     *                          <li>{@link PreparedStatement#executeLargeUpdate()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     */
    protected final <N extends Number> Mono<N> doExecuteUpdate(InnerGenericRmSession session
            , SimpleStmt sqlWrapper, Function<PreparedStatement, Mono<N>> executeFunction) {
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
                .doOnNext(st -> printSQL(sqlWrapper))
                //3. execute sql
                .flatMap(executeFunction)
                //4.assert optimistic Lock
                .flatMap(updatedRows -> assertOptimisticLock(updatedRows, sqlWrapper))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                ;
    }

    /**
     * execute batch update method of {@link PreparedStatement},and assert optimistic lock .
     *
     * @param executeFunction {@link PreparedStatement}'s execute batch method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeBatch()}</li>
     *                          <li>{@link PreparedStatement#executeLargeBatch()}</li>
     *                        </ul>
     * @param <N>             result type of updated rows ,must be  {@link Integer} or {@link Long}
     * @return Mono of a unmodifiable list,{@code Mono<Integer> or Mono<Long>}
     */
    protected final <N extends Number> Flux<N> doExecuteBatchUpdate(InnerGenericRmSession session
            , BatchSimpleStmt sqlWrapper, Function<PreparedStatement, Flux<N>> executeFunction) {
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamGroupList(st, sqlWrapper))
                .doOnNext(st -> printSQL(sqlWrapper))
                //3. execute sql
                .flatMapMany(executeFunction)
                //4.assert optimistic Lock
                .flatMap(updatedRows -> assertOptimisticLock(updatedRows, sqlWrapper))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                ;
    }


    protected final <R> Flux<R> doExecuteSimpleQuery(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , Class<R> resultClass) {

        //1. create statement
        Flux<? extends ResultRow> resultRowFlux = session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
                .doOnNext(st -> printSQL(sqlWrapper))
                //3. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                //4. assert optimistic lock
                .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper));

        //5. map result
        Flux<R> flux;
        if (singleType(sqlWrapper.selectionList(), resultClass)) {
            Selection selection = sqlWrapper.selectionList().get(0);
            StatementType statementType = sqlWrapper.statementType();
            // map single column result
            flux = resultRowFlux.flatMap(row -> flatMapColumnResult(row, selection, statementType, resultClass));
        } else {
            // map complex result
            flux = resultRowFlux.map(row -> extractRowResult(session, row, sqlWrapper, resultClass));
        }
        return flux;
    }

    protected final <R> Flux<Optional<R>> doExecuteColumnQuery(InnerGenericRmSession session
            , SimpleStmt sqlWrapper, Class<R> columnClass) {
        List<Selection> selectionList = sqlWrapper.selectionList();
        if (selectionList.size() != 1) {
            return Flux.error(new IllegalArgumentException("selectionList size not 1 ."));
        }

        final Selection selection = selectionList.get(0);
        final StatementType statementType = sqlWrapper.statementType();
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
                .doOnNext(st -> printSQL(sqlWrapper))
                //3. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                .map(row -> Optional.ofNullable(extractColumnResult(row, selection, statementType, columnClass)))
                ;

    }


    protected final <R> Flux<R> doReturningUpdate(InnerGenericRmSession session, Stmt stmt
            , Class<R> resultClass, boolean updateStatement) {
        Flux<R> flux;
        if (stmt instanceof SimpleStmt) {
            flux = doExecuteSimpleQuery(session, (SimpleStmt) stmt, resultClass);
        } else if (stmt instanceof ChildStmt) {
            final ChildStmt childSQLWrapper = (ChildStmt) stmt;
            final SimpleStmt firstWrapper = updateStatement
                    ? childSQLWrapper.childWrapper()
                    : childSQLWrapper.parentWrapper();

            final SimpleStmt secondWrapper = updateStatement
                    ? childSQLWrapper.parentWrapper()
                    : childSQLWrapper.childWrapper();


            final boolean onlyIdReturning = onlyIdReturning(childSQLWrapper.parentWrapper()
                    , childSQLWrapper.childWrapper());

            // 1. execute child returning update
            flux = doExecuteFirstSQLReturning(session, firstWrapper, resultClass, onlyIdReturning)
                    // 2. execute parent returning update
                    .flatMapMany(objectWrapperMap -> doExecuteSecondSQLReturning(session, secondWrapper, resultClass
                            , objectWrapperMap))
            ;
        } else {
            flux = Flux.error(createUnSupportedSQLWrapperException(stmt, "returningUpdate"));
        }
        return flux;
    }


    /*################################## blow private method ##################################*/

    private Mono<Map<Object, ObjectWrapper>> doExecuteFirstSQLReturning(InnerGenericRmSession session
            , SimpleStmt sqlWrapper, Class<?> resultClass, boolean onlyIdReturning) {
        // assert first selection is primary field selection and obtain Selection
        final Selection primaryFieldSelection = obtainPrimaryFieldForReturning(sqlWrapper.selectionList());

        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
                .doOnNext(st -> printSQL(sqlWrapper))
                //3. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                //4. assert optimistic lock
                .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //5. map ResultRow to ObjectWrapper
                .map(resultRow -> mapFirstSQLResult(session, resultRow, sqlWrapper, resultClass, onlyIdReturning))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                //6. collect bean wrapper to java.util.Map with primaryKeyValue as key.
                .collectMap(beanWrapper -> keyExtractor(beanWrapper, primaryFieldSelection))
                // make java.util.Map unmodifiable.
                .map(Collections::unmodifiableMap)
                ;
    }

    private <R> Flux<R> doExecuteSecondSQLReturning(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , Class<R> resultClass, Map<Object, ObjectWrapper> objectWrapperMap) {

        // assert first selection is primary field selection
        obtainPrimaryFieldForReturning(sqlWrapper.selectionList());

        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
                .doOnNext(st -> printSQL(sqlWrapper))
                //3. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                //4. assert optimistic lock
                .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //5. map ResultRow to T
                .map(resultRow -> mapSecondSQLResult(resultRow, objectWrapperMap, sqlWrapper, resultClass))

                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                .collectList()
                // 6. assert first sql updated row and second sql updated rows match
                .flatMap(listOfSecond -> assertFirstSQLAndSecondSQLResultMatch(listOfSecond, objectWrapperMap, sqlWrapper))
                .flatMapMany(Flux::fromIterable)
                ;
    }

    private ObjectWrapper mapFirstSQLResult(InnerGenericRmSession session, ResultRow resultRow
            , SimpleStmt sqlWrapper, Class<?> resultClass, boolean onlyIdReturning) {

        ObjectWrapper beanWrapper = onlyIdReturning
                ? ObjectAccessorFactory.forIdAccess(resultClass)
                : createObjectWrapper(resultClass, session);

        final List<Selection> selectionList = sqlWrapper.selectionList();
        for (Selection selection : selectionList) {
            Object columnResult = extractColumnResult(resultRow, selection, sqlWrapper.statementType()
                    , selection.mappingMeta().javaType());
            if (columnResult == null) {
                continue;
            }
            // set columnResult to object
            beanWrapper.setPropertyValue(selection.alias(), columnResult);
        }
        if (beanWrapper.getPropertyValue(selectionList.get(0).alias()) == null) {
            // first selection must be Primary Field
            throw createDomainFirstReturningNoIdException();
        }
        return beanWrapper;
    }

    /**
     * @param <R> row's  java type
     */
    private <R> R mapSecondSQLResult(ResultRow resultRow
            , Map<Object, ObjectWrapper> objectWrapperMap, SimpleStmt sqlWrapper, Class<R> resultClass) {

        final List<Selection> selectionList = sqlWrapper.selectionList();
        // first Selection must be PrimaryField Selection
        final Selection primaryFieldSelection = selectionList.get(0);
        final StatementType statementType = sqlWrapper.statementType();

        final Object primaryFieldValue = extractColumnResult(resultRow, primaryFieldSelection, statementType
                , primaryFieldSelection.mappingMeta().javaType());

        if (primaryFieldValue == null) {
            throw createDomainSecondReturningNoIdException();
        }
        final ObjectWrapper objectWrapper = objectWrapperMap.get(primaryFieldValue);
        if (objectWrapper == null) {
            throw new IllegalStateException(String.format(
                    "wrapperMap error,not found value for key[%s]", primaryFieldValue));
        }
        if (selectionList.size() < 2) {
            return resultClass.cast(objectWrapper.getWrappedInstance());
        }
        final int size = selectionList.size();
        for (int i = 1; i < size; i++) {
            Selection selection = selectionList.get(i);
            Object columnResult = extractColumnResult(resultRow, selection, statementType
                    , selection.mappingMeta().javaType());
            if (columnResult == null) {
                continue;
            }
            objectWrapper.setPropertyValue(selection.alias(), columnResult);
        }
        return resultClass.cast(objectWrapper.getWrappedInstance());
    }


    private <R> R extractRowResult(InnerGenericRmSession session, ResultRow resultRow
            , SimpleStmt sqlWrapper, Class<R> resultClass) {

        final ObjectWrapper beanWrapper = createObjectWrapper(resultClass, session);

        final StatementType statementType = sqlWrapper.statementType();
        for (Selection selection : sqlWrapper.selectionList()) {
            // 1. obtain column result
            Object columnResult = extractColumnResult(resultRow, selection, statementType
                    , selection.mappingMeta().javaType());
            if (columnResult == null) {
                continue;
            }
            // 2. set bean property value.
            beanWrapper.setPropertyValue(selection.alias(), columnResult);
        }
        return getWrapperInstance(beanWrapper);
    }

    private <R> Mono<R> flatMapColumnResult(ResultRow resultRow, Selection selection, StatementType statementType
            , Class<R> columnClass) {
        return Mono.justOrEmpty(
                extractColumnResult(resultRow, selection, statementType, columnClass)
        );
    }

    /**
     * @param <R> column's {@link Selection} java type
     */
    @Nullable
    private <R> R extractColumnResult(ResultRow resultRow, Selection selection, StatementType statementType
            , Class<R> columnResultClass) {

        // 1. obtain column result
        Object columnResult = resultRow.getObject(selection.alias());
        if (columnResult == null) {
            return null;
        }
        MappingType mappingType = selection.mappingMeta();
        // 2. decode columnResult with mappingMeta
        columnResult = mappingType.decodeForReactive(columnResult, this.mappingContext);
        if (!mappingType.javaType().isInstance(columnResult)) {
            throw new MetaException("%s decodeForReactive return value isn't %s's instance."
                    , mappingType.getClass().getName()
                    , mappingType.javaType().getName());
        }

        if (selection instanceof FieldSelection) {
            FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
            if (fieldMeta.codec()) {
                // 3. decode cipher field
                columnResult = doDecodeResult(statementType, fieldMeta, columnResult);
            }
        }
        return columnResultClass.cast(columnResult);
    }

    private <R> Publisher<R> assertOptimisticLockWhenEmpty(SimpleStmt sqlWrapper) {
        Mono<R> mono;
        if (sqlWrapper.hasVersion()) {
            // throw optimistic lock exception
            mono = Mono.defer(() -> Mono.error(createOptimisticLockException(sqlWrapper.sql())));
        } else {
            mono = Mono.empty();
        }
        return mono;
    }

    private <R> Mono<List<R>> assertFirstSQLAndSecondSQLResultMatch(List<R> listOfSecond
            , Map<?, ?> wrapperMap, SimpleStmt simpleWrapper) {
        Mono<List<R>> mono;
        if (listOfSecond.size() == wrapperMap.size()) {
            mono = Mono.just(listOfSecond);
        } else {
            mono = Mono.error(createChildReturningNotMatchException(wrapperMap.size(), listOfSecond.size()
                    , simpleWrapper));
        }
        return mono;
    }


    private Object keyExtractor(ObjectWrapper beanWrapper, Selection primaryFieldSelection) {
        Object idValue = beanWrapper.getPropertyValue(primaryFieldSelection.alias());
        if (idValue == null) {
            throw new IllegalStateException(String.format(
                    "%s not found property[%s] value.", beanWrapper, primaryFieldSelection.alias()));
        }
        return idValue;
    }


    private <N extends Number> Mono<N> assertOptimisticLock(N updatedRows, GenericSimpleStmt sqlWrapper) {

        Mono<N> mono;
        if (sqlWrapper.hasVersion() && updatedRows.longValue() < 1L) {
            mono = Mono.error(createOptimisticLockException(sqlWrapper.sql()));
        } else {
            mono = Mono.just(updatedRows);
        }
        return mono;
    }


    private PreparedStatement bindParamGroupList(final PreparedStatement st, BatchSimpleStmt sqlWrapper) {
        final StatementType statementType = sqlWrapper.statementType();
        for (List<ParamValue> paramList : sqlWrapper.paramGroupList()) {
            bindParamList(st, statementType, paramList);
            st.addBatch();
        }
        return st;
    }


    private PreparedStatement bindParamList(final PreparedStatement st, StatementType statementType
            , List<ParamValue> paramList)
            throws ReactiveSQLException {

        final MappingContext mappingContext = this.mappingContext;
        final Database database = mappingContext.database();

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            ParamValue paramValue = paramList.get(i);
            Object value = paramValue.value();
            ParamMeta paramMeta = paramValue.paramMeta();
            SQLDataType sqlDataType = paramMeta.mappingMeta().sqlDataType(database);

            if (value == null) {
                st.bindNull(i + 1, sqlDataType.typeName());
            } else {
                if (paramMeta instanceof FieldMeta) {
                    FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
                    if (fieldMeta.codec()) {
                        value = doEncodeParam(statementType, fieldMeta, value);
                    }
                }
                value = paramValue.paramMeta().mappingMeta().encodeForReactive(value, mappingContext);
                st.bind(i + 1, sqlDataType.typeName(), value);
            }
        }
        return st;
    }


    private void printSQL(GenericSimpleStmt sqlWrapper) {
        if (this.sessionFactory.showSQL()) {
            LOG.info("Army will execute {}:\n{}", sqlWrapper.statementType()
                    , this.sessionFactory.dialect().showSQL(sqlWrapper));
        }
    }

}
