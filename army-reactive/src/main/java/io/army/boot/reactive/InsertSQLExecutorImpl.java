package io.army.boot.reactive;

import io.army.dialect.InsertException;
import io.army.stmt.*;
import io.jdbd.PreparedStatement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

final class InsertSQLExecutorImpl extends SQLExecutorSupport implements InsertSQLExecutor {

    InsertSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    public final Mono<Void> valueInsert(InnerGenericRmSession session, Stmt stmt)
            throws InsertException {
        return doValueInsert(session, stmt);
    }

    @Override
    public <N extends Number> Mono<N> subQueryInsert(InnerGenericRmSession session, Stmt stmt
            , Class<N> resultClass) throws InsertException {

        Mono<? extends Number> mono;
        if (resultClass == Integer.class) {
            mono = internalSubQueryInsert(session, stmt, PreparedStatement::executeUpdate, "subQueryInsert");
        } else if (resultClass == Long.class) {
            mono = internalSubQueryInsert(session, stmt, PreparedStatement::executeLargeUpdate
                    , "subQueryInsert");
        } else {
            mono = Mono.error(new IllegalArgumentException("ResultClass error"));
        }
        return mono.cast(resultClass);
    }

    @Override
    public final <T> Flux<T> returningInsert(InnerGenericRmSession session, Stmt stmt, Class<T> resultClass)
            throws InsertException {
        return doReturningUpdate(session, stmt, resultClass, false, "returningInsert");
    }


    /*################################## blow private method ##################################*/

    private Mono<Void> doValueInsert(InnerGenericRmSession session, Stmt stmt) {
        Mono<Void> mono;
        if (stmt instanceof SimpleStmt) {
            final SimpleStmt simpleSQLWrapper = (SimpleStmt) stmt;
            // 1. execute insert sql
            mono = doExecuteUpdate(session, simpleSQLWrapper, PreparedStatement::executeUpdate)
                    // 2. assert  insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, simpleSQLWrapper));
        } else if (stmt instanceof PairStmt) {
            PairStmt childSQLWrapper = (PairStmt) stmt;
            final SimpleStmt parentWrapper = childSQLWrapper.parentStmt();
            final SimpleStmt childWrapper = childSQLWrapper.childStmt();
            // 1. execute parent insert sql
            mono = doExecuteUpdate(session, parentWrapper, PreparedStatement::executeUpdate)
                    // 2. assert parent insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, parentWrapper))
                    //3. execute child insert sql
                    .then(doExecuteUpdate(session, childWrapper, PreparedStatement::executeUpdate))
                    // 4. assert child insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, childWrapper))
            ;
        } else if (stmt instanceof BatchSimpleStmt) {
            final BatchSimpleStmt batchSQLWrapper = (BatchSimpleStmt) stmt;
            // 1. execute batch insert sql
            mono = doExecuteBatchUpdate(session, batchSQLWrapper, PreparedStatement::executeBatch)
                    // 2. assert each insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, batchSQLWrapper))
                    .then();
        } else if (stmt instanceof ChildBatchStmt) {
            ChildBatchStmt batchSQLWrapper = (ChildBatchStmt) stmt;
            final BatchSimpleStmt parentWrapper = batchSQLWrapper.parentWrapper();
            final BatchSimpleStmt childWrapper = batchSQLWrapper.childWrapper();

            // 1. execute parent batch insert sql
            mono = doExecuteBatchUpdate(session, parentWrapper, PreparedStatement::executeBatch)
                    // 2. assert each parent insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, parentWrapper))
                    // 3. statistics parent insert count
                    .count()
                    // 4. execute child batch insert sql
                    .flatMap(parentRows -> doExecuteBatchChildValueInsert(session, childWrapper, parentRows))
            ;
        } else {
            mono = Mono.error(createUnSupportedSQLWrapperException(stmt, "valueInsert"));
        }
        return mono;
    }

    private Mono<Void> doExecuteBatchChildValueInsert(InnerGenericRmSession session, BatchSimpleStmt childWrapper
            , Long parentRows) {
        // 1. execute child  batch insert sql
        return doExecuteBatchUpdate(session, childWrapper, PreparedStatement::executeBatch)
                // 2. assert each child insert rows equals 1
                .flatMap(insertRows -> assertValueInsertRows(insertRows, childWrapper))
                // 3. statistics child insert count
                .count()
                // 4. assert parentRows and childRows match
                .flatMap(childRows -> assertParentChildBatchMatch(parentRows, childRows, childWrapper))
                ;
    }

    private Mono<Void> assertParentChildBatchMatch(Long parentRows, Long childRows
            , BatchSimpleStmt childWrapper) {
        Mono<Void> mono;
        if (childRows.equals(parentRows)) {
            mono = Mono.empty();
        } else {
            mono = Mono.error(createBatchChildInsertNotMatchException(parentRows, childRows, childWrapper));
        }
        return mono;
    }

    private Mono<Void> assertValueInsertRows(Integer insertRows, GenericSimpleStmt sqlWrapper) {
        Mono<Void> mono;
        if (insertRows != 1) {
            mono = Mono.error(createValueInsertException(insertRows, sqlWrapper));
        } else {
            mono = Mono.empty();
        }
        return mono;
    }


    /**
     * @param executeFunction {@link PreparedStatement}'s execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeUpdate()}</li>
     *                          <li>{@link PreparedStatement#executeLargeUpdate()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleStmt, Function)
     */
    private <N extends Number> Mono<N> internalSubQueryInsert(InnerGenericRmSession session, Stmt stmt
            , Function<PreparedStatement, Mono<N>> executeFunction, String methodName) {
        Mono<N> mono;
        if (stmt instanceof SimpleStmt) {
            mono = doExecuteUpdate(session, (SimpleStmt) stmt, executeFunction);
        } else if (stmt instanceof PairStmt) {
            final PairStmt childSQLWrapper = ((PairStmt) stmt);
            final SimpleStmt parentWrapper = childSQLWrapper.parentStmt();
            final SimpleStmt childWrapper = childSQLWrapper.childStmt();

            // 1. execute parent sub query insert
            mono = doExecuteUpdate(session, parentWrapper, executeFunction)
                    //2. execute child sub query insert
                    .flatMap(parentRows -> doChildSubQueryInsert(session, childWrapper, parentRows, executeFunction))
            ;
        } else {
            mono = Mono.error(createUnSupportedSQLWrapperException(stmt, methodName));
        }
        return mono;
    }

    /**
     * @param executeFunction {@link PreparedStatement}'s execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeUpdate()}</li>
     *                          <li>{@link PreparedStatement#executeLargeUpdate()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleStmt, Function)
     */
    private <N extends Number> Mono<N> doChildSubQueryInsert(InnerGenericRmSession session
            , SimpleStmt childWrapper, N parentRows, Function<PreparedStatement, Mono<N>> executeFunction) {
        Mono<N> mono;
        if (parentRows.longValue() < 1L) {
            mono = Mono.just(parentRows);
        } else {
            // 1. execute child sub query insert
            mono = doExecuteUpdate(session, childWrapper, executeFunction)
                    //2. assert child insert rows and parent match
                    .flatMap(childRows -> assertParentChildSubQueryInsertMatch(parentRows, childRows, childWrapper))
            ;
        }
        return mono;
    }


    private <N extends Number> Mono<N> assertParentChildSubQueryInsertMatch(N parentRows, N childRows
            , SimpleStmt childWrapper) {
        Mono<N> mono;
        if (childRows.equals(parentRows)) {
            mono = Mono.just(childRows);
        } else {
            mono = Mono.error(createChildSubQueryInsertNotMatchException(parentRows, childRows, childWrapper));
        }
        return mono;
    }
}
