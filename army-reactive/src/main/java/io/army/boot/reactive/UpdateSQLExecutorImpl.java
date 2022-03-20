package io.army.boot.reactive;

import io.army.stmt.BatchStmt;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.jdbd.stmt.PreparedStatement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    UpdateSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final <N extends Number> Mono<N> update(InnerGenericRmSession session, Stmt stmt
            , Class<N> resultClass) {
//        Mono<? extends Number> mono;
//        if (resultClass == Integer.class) {
//            mono = internalUpdate(session, stmt, PreparedStatement::executeUpdate, "update");
//        } else if (resultClass == Long.class) {
//            mono = internalUpdate(session, stmt, PreparedStatement::executeLargeUpdate, "update");
//        } else {
//            mono = Mono.error(new IllegalArgumentException("ResultClass error"));
//        }
//        return mono.cast(resultClass);
        return Mono.empty();
    }

    @Override
    public final <N extends Number> Flux<N> batchUpdate(InnerGenericRmSession session, Stmt stmt
            , Class<N> resultClass) {
//        Flux<? extends Number> flux;
//        if (resultClass == Integer.class) {
//            flux = internalBatchUpdate(session, stmt, PreparedStatement::executeBatch);
//        } else if (resultClass == Long.class) {
//            flux = internalBatchUpdate(session, stmt, PreparedStatement::executeLargeBatch);
//        } else {
//            flux = Flux.error(new IllegalArgumentException("ResultClass error"));
//        }
//        return flux.cast(resultClass);
        return Flux.empty();
    }

    @Override
    public final <T> Flux<T> returningUpdate(InnerGenericRmSession session, Stmt stmt
            , Class<T> resultClass) {
        return doReturningUpdate(session, stmt, resultClass, true);
    }

    /*################################## blow private method ##################################*/

    /**
     * @param executeFunction {@link PreparedStatement}'s execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeUpdate()}</li>
     *
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleStmt, Function)
     */
    private <N extends Number> Mono<N> internalUpdate(InnerGenericRmSession session, Stmt stmt
            , Function<PreparedStatement, Mono<N>> executeFunction, String methodName) {

//        Mono<N> mono;
//        if (stmt instanceof SimpleStmt) {
//            mono = doExecuteUpdate(session, (SimpleStmt) stmt, executeFunction);
//        } else if (stmt instanceof PairStmt) {
//            final PairStmt childSQLWrapper = (PairStmt) stmt;
//            final SimpleStmt childWrapper = childSQLWrapper.childStmt();
//
//            // 1. execute child update sql
//            mono = doExecuteUpdate(session, childWrapper, executeFunction)
//                    //2. execute parent update sql and assert parent updated rows and child match
//                    .flatMap(childRows -> doParentUpdate(session, childSQLWrapper, executeFunction, childRows))
//            ;
//        } else {
//            mono = Mono.error(createUnSupportedSQLWrapperException(stmt, methodName));
//        }
        return Mono.empty();
    }

    /**
     * @param executeFunction {@link PreparedStatement}'s execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeUpdate()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleStmt, Function)
     */
    private <N extends Number> Mono<N> doParentUpdate(InnerGenericRmSession session, PairStmt childSQLWrapper
            , Function<PreparedStatement, Mono<N>> executeFunction, N childRows) {
//        Mono<N> mono;
//        if (childRows.longValue() < 1L) {
//            mono = Mono.just(childRows);
//        } else {
//            final SimpleStmt childWrapper = childSQLWrapper.childStmt();
//            // 1. execute parent update sql.
//            mono = doExecuteUpdate(session, childSQLWrapper.parentStmt(), executeFunction)
//                    // 2. assert parent updated rows and child match
//                    .flatMap(parentRows -> assertParentChildUpdateMatch(parentRows, childRows, childWrapper))
//            ;
//        }
        return Mono.empty();
    }

    private <N extends Number> Mono<N> assertParentChildUpdateMatch(N parentRows, N childRows
            , SimpleStmt childWrapper) {
        Mono<N> mono;
        if (parentRows.equals(childRows)) {
            mono = Mono.just(childRows);
        } else {
            mono = Mono.error(createParentUpdateNotMatchException(parentRows, childRows, childWrapper));
        }
        return mono;
    }

    /**
     * @param executeFunction {@link PreparedStatement}'s execute batch update method ,must be below:
     *                        <ul>
     *
     *
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Flux<Integer> or Flux<Long>}
     * @see #doExecuteBatchUpdate(InnerGenericRmSession, BatchStmt, Function)
     */
    private <N extends Number> Flux<N> internalBatchUpdate(InnerGenericRmSession session, Stmt stmt
            , Function<PreparedStatement, Flux<N>> executeFunction) {

//        Flux<N> flux;
//        if (stmt instanceof BatchSimpleStmt) {
//            flux = doExecuteBatchUpdate(session, (BatchSimpleStmt) stmt, executeFunction);
//        } else if (stmt instanceof PairBatchStmt) {
//            final PairBatchStmt childSQLWrapper = (PairBatchStmt) stmt;
//            final BatchSimpleStmt childWrapper = childSQLWrapper.childStmt();
//
//            // 1. execute child batch update sql
//            flux = doExecuteBatchUpdate(session, childWrapper, executeFunction)
//                    .collectList()
//                    //2. execute parent update sql and assert parent updated rows and child match
//                    .flatMapMany(list -> doParentBatchUpdate(session, childSQLWrapper, executeFunction, list))
//            ;
//        } else {
//            flux = Flux.error(createUnSupportedSQLWrapperException(stmt, "batchUpdate"));
//        }
        return Flux.empty();
    }


}
