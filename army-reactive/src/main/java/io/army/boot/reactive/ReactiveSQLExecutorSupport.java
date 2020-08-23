package io.army.boot.reactive;

import io.army.DomainUpdateException;
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
import io.army.meta.mapping.MappingMeta;
import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.sqldatatype.SQLDataType;
import io.army.wrapper.*;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import io.jdbd.ResultRow;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is a base class of sql reactive executor implementation. This class provide common method.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see ReactiveInsertSQLExecutorImpl
 * @see ReactiveUpdateSQLExecutorImpl
 * @see ReactiveSelectSQLExecutorImpl
 */
abstract class ReactiveSQLExecutorSupport extends GenericSQLExecutorSupport {

    final GenericReactiveRmSessionFactory sessionFactory;

    final MappingContext mappingContext;

    ReactiveSQLExecutorSupport(GenericReactiveRmSessionFactory sessionFactory) {
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
            , SimpleSQLWrapper sqlWrapper, Function<PreparedStatement, Mono<N>> executeFunction) {
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
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
            , BatchSimpleSQLWrapper sqlWrapper, Function<PreparedStatement, Flux<N>> executeFunction) {
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamGroupList(st, sqlWrapper))
                //3. execute sql
                .flatMapMany(executeFunction)
                //4.assert optimistic Lock
                .flatMap(updatedRows -> assertOptimisticLock(updatedRows, sqlWrapper))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                ;
    }


    protected final <T> Flux<T> doExecuteSimpleQuery(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        //1. decide map function
        Function<ResultRow, T> resultFunction;
        if (singleType(sqlWrapper.selectionList(), resultClass)) {
            resultFunction = row -> extractColumnResult(row, sqlWrapper.selectionList().get(0)
                    , sqlWrapper.statementType(), resultClass);
        } else {
            resultFunction = row -> extractRowResult(session, row, sqlWrapper, resultClass);
        }
        //2. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //3. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
                //4. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                //5. assert optimistic lock
                .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //6. map result
                .map(resultFunction)
                ;
    }

    protected final <T> Flux<T> doReturningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Class<T> resultClass, boolean updateStatement, String methodName) {
        Flux<T> flux;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            flux = doExecuteSimpleQuery(session, (SimpleSQLWrapper) sqlWrapper, resultClass);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            final ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            final SimpleSQLWrapper firstWrapper = updateStatement
                    ? childSQLWrapper.childWrapper()
                    : childSQLWrapper.parentWrapper();

            final SimpleSQLWrapper secondWrapper = updateStatement
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
            flux = Flux.error(createUnSupportedSQLWrapperException(sqlWrapper, methodName));
        }
        return flux;
    }

    protected final <T> T extractRowResult(InnerGenericRmSession session, ResultRow resultRow
            , SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {

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

    /*################################## blow private method ##################################*/

    private Mono<Map<Object, ObjectWrapper>> doExecuteFirstSQLReturning(InnerGenericRmSession session
            , SimpleSQLWrapper sqlWrapper, Class<?> resultClass, boolean onlyIdReturning) {
        // assert first selection is primary field selection and obtain Selection
        final Selection primaryFieldSelection = obtainPrimaryFieldForReturning(sqlWrapper.selectionList());

        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
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

    private <T> Flux<T> doExecuteSecondSQLReturning(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<T> resultClass, Map<Object, ObjectWrapper> objectWrapperMap) {

        // assert first selection is primary field selection
        obtainPrimaryFieldForReturning(sqlWrapper.selectionList());

        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
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
            , SimpleSQLWrapper sqlWrapper, Class<?> resultClass, boolean onlyIdReturning) {

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
     * @param <T> row's  java type
     */
    private <T> T mapSecondSQLResult(ResultRow resultRow
            , Map<Object, ObjectWrapper> objectWrapperMap, SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {

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

    /**
     * @param <T> column's {@link Selection} java type
     */
    @Nullable
    private <T> T extractColumnResult(ResultRow resultRow, Selection selection, StatementType statementType
            , Class<T> columnResultClass) {

        // 1. obtain column result
        Object columnResult = resultRow.getObject(selection.alias());
        if (columnResult == null) {
            return null;
        }
        MappingMeta mappingMeta = selection.mappingMeta();
        // 2. decode columnResult with mappingMeta
        columnResult = mappingMeta.decodeForReactive(columnResult, this.mappingContext);
        assertDecodeForReactiveReturning(mappingMeta, columnResult);

        if (selection instanceof FieldSelection) {
            FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
            if (fieldMeta.codec()) {
                // 3. decode cipher field
                columnResult = doDecodeResult(statementType, fieldMeta, columnResult);
            }
        }
        return columnResultClass.cast(columnResult);
    }

    private <T> Publisher<T> assertOptimisticLockWhenEmpty(SimpleSQLWrapper sqlWrapper) {
        Mono<T> mono;
        if (sqlWrapper.hasVersion()) {
            // throw optimistic lock exception
            mono = Mono.defer(() -> Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql())));
        } else {
            mono = Mono.empty();
        }
        return mono;
    }

    private <T> Mono<List<T>> assertFirstSQLAndSecondSQLResultMatch(List<T> listOfSecond
            , Map<?, ?> wrapperMap, GenericSimpleWrapper simpleWrapper) {
        Mono<List<T>> mono;
        if (listOfSecond.size() == wrapperMap.size()) {
            mono = Mono.just(listOfSecond);
        } else {
            mono = Mono.error(new DomainUpdateException(
                    "%s updated rows[%s] and first sql[%s] not match.", simpleWrapper.sql()
                    , listOfSecond.size(), wrapperMap.size()));
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


    private void assertDecodeForReactiveReturning(MappingMeta mappingMeta, Object columnResult) {
        if (!mappingMeta.javaType().isInstance(columnResult)) {
            throw new MetaException("%s decodeForReactive return value isn't %s's instance."
                    , mappingMeta.getClass().getName()
                    , mappingMeta.javaType().getName());
        }
    }


    private <N extends Number> Mono<N> assertOptimisticLock(N updatedRows, GenericSimpleWrapper sqlWrapper) {
        final boolean hasVersion = sqlWrapper.hasVersion();
        if (!hasVersion) {
            return Mono.just(updatedRows);
        }
        Mono<N> mono = null;
        if (updatedRows instanceof Integer) {
            if (updatedRows.intValue() < 1) {
                mono = Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql()));
            }
        } else if (updatedRows instanceof Long) {
            if (updatedRows.longValue() < 1L) {
                mono = Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql()));
            }
        } else {
            throw new IllegalArgumentException("executeFunction error.");
        }
        return mono == null ? Mono.just(updatedRows) : mono;
    }


    private PreparedStatement bindParamGroupList(final PreparedStatement st, BatchSimpleSQLWrapper sqlWrapper) {
        final StatementType statementType = sqlWrapper.statementType();
        for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
            bindParamList(st, statementType, paramList);
            st.addBatch();
        }
        return st;
    }


    private PreparedStatement bindParamList(final PreparedStatement st, StatementType statementType
            , List<ParamWrapper> paramList)
            throws ReactiveSQLException {

        final MappingContext mappingContext = this.mappingContext;
        final Database database = mappingContext.database();

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            ParamWrapper paramWrapper = paramList.get(i);
            Object value = paramWrapper.value();
            ParamMeta paramMeta = paramWrapper.paramMeta();
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
                value = paramWrapper.paramMeta().mappingMeta().encodeForReactive(value, mappingContext);
                st.bind(i + 1, sqlDataType.typeName(), value);
            }
        }
        return st;
    }


}
