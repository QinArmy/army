package io.army.boot.reactive;

import io.army.beans.ObjectWrapper;
import io.army.boot.GenericSQLExecutorSupport;
import io.army.codec.StatementType;
import io.army.criteria.Selection;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.stmt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.PreparedStatement;
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
        this.mappingContext = null;
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
            , BatchStmt sqlWrapper, Function<PreparedStatement, Flux<N>> executeFunction) {
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
//        Flux< ResultRow> resultRowFlux = session.createPreparedStatement(sqlWrapper.sql())
//                //2. bind param list
//                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
//                .doOnNext(st -> printSQL(sqlWrapper))
//                .cast(ResultRow.class)
        //3. execute sql
        // .flatMapMany(PreparedStatement::executeQuery)
        //4. assert optimistic lock
        // .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper));

        //5. map result
//        Flux<R> flux;
//        if (singleType(sqlWrapper.selectionList(), resultClass)) {
//            Selection selection = sqlWrapper.selectionList().get(0);
//            StatementType statementType = sqlWrapper.statementType();
//            // map single column result
//            flux = resultRowFlux.flatMap(row -> flatMapColumnResult(row, selection, statementType, resultClass));
//        } else {
//            // map complex result
//            flux = resultRowFlux.map(row -> extractRowResult(session, row, sqlWrapper, resultClass));
//        }
        return Flux.empty();
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
//        return session.createPreparedStatement(sqlWrapper.sql())
//                //2. bind param list
//                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramGroup()))
//                .doOnNext(st -> printSQL(sqlWrapper))
        //3. execute sql
        // .flatMapMany(PreparedStatement::executeQuery)
        // .map(row -> Optional.ofNullable(extractColumnResult(row, selection, statementType, columnClass)))
        ;
        return Flux.empty();
    }


    protected final <R> Flux<R> doReturningUpdate(InnerGenericRmSession session, Stmt stmt
            , Class<R> resultClass, boolean updateStatement) {
        Flux<R> flux;
        if (stmt instanceof SimpleStmt) {
            flux = doExecuteSimpleQuery(session, (SimpleStmt) stmt, resultClass);
        } else if (stmt instanceof PairStmt) {
            final PairStmt childSQLWrapper = (PairStmt) stmt;
            final SimpleStmt firstWrapper = updateStatement
                    ? childSQLWrapper.childStmt()
                    : childSQLWrapper.parentStmt();

            final SimpleStmt secondWrapper = updateStatement
                    ? childSQLWrapper.parentStmt()
                    : childSQLWrapper.childStmt();


            final boolean onlyIdReturning = onlyIdReturning(childSQLWrapper.parentStmt()
                    , childSQLWrapper.childStmt());

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
                // .flatMapMany(PreparedStatement::executeQuery)
                //4. assert optimistic lock
                // .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //5. map ResultRow to ObjectWrapper
                //  .map(resultRow -> mapFirstSQLResult(session, resultRow, sqlWrapper, resultClass, onlyIdReturning))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                //6. collect bean wrapper to java.util.Map with primaryKeyValue as key.
                // .collectMap(beanWrapper -> keyExtractor(beanWrapper, primaryFieldSelection))
                // make java.util.Map unmodifiable.
                //  .map(Collections::unmodifiableMap)
                .then(Mono.empty())
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
                // .flatMapMany(PreparedStatement::executeQuery)
                //4. assert optimistic lock
                //  .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //5. map ResultRow to T
                // .map(resultRow -> mapSecondSQLResult(resultRow, objectWrapperMap, sqlWrapper, resultClass))

                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                // .collectList()
                // 6. assert first sql updated row and second sql updated rows match
                // .flatMap(listOfSecond -> assertFirstSQLAndSecondSQLResultMatch(listOfSecond, objectWrapperMap, sqlWrapper))
                // .flatMapMany(Flux::fromIterable)
                .thenMany(Mono.empty())
                ;
    }



    private <N extends Number> Mono<N> assertOptimisticLock(N updatedRows, GenericSimpleStmt sqlWrapper) {

        Mono<N> mono;
        if (sqlWrapper.hasOptimistic() && updatedRows.longValue() < 1L) {
            mono = Mono.error(createOptimisticLockException(sqlWrapper.sql()));
        } else {
            mono = Mono.just(updatedRows);
        }
        return mono;
    }


    private PreparedStatement bindParamGroupList(final PreparedStatement st, BatchStmt sqlWrapper) {
//        final StatementType statementType = sqlWrapper.statementType();
//        for (List<ParamValue> paramList : sqlWrapper.groupList()) {
//            bindParamList(st, statementType, paramList);
//            st.addBatch();
//        }
        return st;
    }


    private PreparedStatement bindParamList(final PreparedStatement st, StatementType statementType
            , List<ParamValue> paramList) {

        final MappingContext mappingContext = this.mappingContext;
        final Database database = mappingContext.database();

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            ParamValue paramValue = paramList.get(i);
            Object value = paramValue.value();
            ParamMeta paramMeta = paramValue.paramMeta();
            //  SqlDataType sqlDataType = paramMeta.mappingMeta().sqlDataType(database);

            if (value == null) {
                //  st.bindNull(i + 1, sqlDataType.typeName());
            } else {
                if (paramMeta instanceof FieldMeta) {
                    FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
                    if (fieldMeta.codec()) {
                        value = doEncodeParam(statementType, fieldMeta, value);
                    }
                }
                //  value = paramValue.paramMeta().mappingMeta().encodeForReactive(value, mappingContext);
                // st.bind(i + 1, sqlDataType.typeName(), value);
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
