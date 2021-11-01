package io.army.boot.reactive;

import io.army.stmt.*;
import io.jdbd.PreparedStatement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    UpdateSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final <N extends Number> Mono<N> update(InnerGenericRmSession session, Stmt stmt
            , Class<N> resultClass) {
        Mono<? extends Number> mono;
        if (resultClass == Integer.class) {
            mono = internalUpdate(session, stmt, PreparedStatement::executeUpdate, "update");
        } else if (resultClass == Long.class) {
            mono = internalUpdate(session, stmt, PreparedStatement::executeLargeUpdate, "update");
        } else {
            mono = Mono.error(new IllegalArgumentException("ResultClass error"));
        }
        return mono.cast(resultClass);
    }

    @Override
    public final <N extends Number> Flux<N> batchUpdate(InnerGenericRmSession session, Stmt stmt
            , Class<N> resultClass) {
        Flux<? extends Number> flux;
        if (resultClass == Integer.class) {
            flux = internalBatchUpdate(session, stmt, PreparedStatement::executeBatch);
        } else if (resultClass == Long.class) {
            flux = internalBatchUpdate(session, stmt, PreparedStatement::executeLargeBatch);
        } else {
            flux = Flux.error(new IllegalArgumentException("ResultClass error"));
        }
        return flux.cast(resultClass);
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
     *                          <li>{@link PreparedStatement#executeLargeUpdate()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleStmt, Function)
     */
    private <N extends Number> Mono<N> internalUpdate(InnerGenericRmSession session, Stmt stmt
            , Function<PreparedStatement, Mono<N>> executeFunction, String methodName) {

        Mono<N> mono;
        if (stmt instanceof SimpleStmt) {
            mono = doExecuteUpdate(session, (SimpleStmt) stmt, executeFunction);
        } else if (stmt instanceof PairStmt) {
            final PairStmt childSQLWrapper = (PairStmt) stmt;
            final SimpleStmt childWrapper = childSQLWrapper.childStmt();

            // 1. execute child update sql
            mono = doExecuteUpdate(session, childWrapper, executeFunction)
                    //2. execute parent update sql and assert parent updated rows and child match
                    .flatMap(childRows -> doParentUpdate(session, childSQLWrapper, executeFunction, childRows))
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
    private <N extends Number> Mono<N> doParentUpdate(InnerGenericRmSession session, PairStmt childSQLWrapper
            , Function<PreparedStatement, Mono<N>> executeFunction, N childRows) {
        Mono<N> mono;
        if (childRows.longValue() < 1L) {
            mono = Mono.just(childRows);
        } else {
            final SimpleStmt childWrapper = childSQLWrapper.childStmt();
            // 1. execute parent update sql.
            mono = doExecuteUpdate(session, childSQLWrapper.parentStmt(), executeFunction)
                    // 2. assert parent updated rows and child match
                    .flatMap(parentRows -> assertParentChildUpdateMatch(parentRows, childRows, childWrapper))
            ;
        }
        return mono;
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
     *                          <li>{@link PreparedStatement#executeBatch()}</li>
     *                          <li>{@link PreparedStatement#executeLargeBatch()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Flux<Integer> or Flux<Long>}
     * @see #doExecuteBatchUpdate(InnerGenericRmSession, BatchSimpleStmt, Function)
     */
    private <N extends Number> Flux<N> internalBatchUpdate(InnerGenericRmSession session, Stmt stmt
            , Function<PreparedStatement, Flux<N>> executeFunction) {

        Flux<N> flux;
        if (stmt instanceof BatchSimpleStmt) {
            flux = doExecuteBatchUpdate(session, (BatchSimpleStmt) stmt, executeFunction);
        } else if (stmt instanceof ChildBatchStmt) {
            final ChildBatchStmt childSQLWrapper = (ChildBatchStmt) stmt;
            final BatchSimpleStmt childWrapper = childSQLWrapper.childWrapper();

            // 1. execute child batch update sql
            flux = doExecuteBatchUpdate(session, childWrapper, executeFunction)
                    .collectList()
                    //2. execute parent update sql and assert parent updated rows and child match
                    .flatMapMany(list -> doParentBatchUpdate(session, childSQLWrapper, executeFunction, list))
            ;
        } else {
            flux = Flux.error(createUnSupportedSQLWrapperException(stmt, "batchUpdate"));
        }
        return flux;
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
    private <N extends Number> Flux<N> doParentBatchUpdate(InnerGenericRmSession session
            , ChildBatchStmt childSQLWrapper, Function<PreparedStatement, Flux<N>> executeFunction
            , List<N> childList) {

        final BatchSimpleStmt childWrapper = childSQLWrapper.childWrapper();
        return // 1. execute parent update sql.
                doExecuteBatchUpdate(session, childSQLWrapper.parentWrapper(), executeFunction)
                        .collectList()
                        // 2. assert parent updated rows and child match
                        .flatMap(parentList -> assertParentChildBatchUpdateMatch(parentList, childList, childWrapper))
                        .flatMapMany(Flux::fromIterable)
                ;
    }


    private <N extends Number> Mono<List<N>> assertParentChildBatchUpdateMatch(List<N> parentRowsList
            , List<N> childRowsList, BatchSimpleStmt childWrapper) {
        if (parentRowsList.size() != childRowsList.size()) {
            return Mono.error(createParentBatchUpdateNotMatchException(parentRowsList.size()
                    , childRowsList.size(), childWrapper));
        }
        final int size = parentRowsList.size();
        for (int i = 0; i < size; i++) {
            if (!parentRowsList.get(i).equals(childRowsList.get(i))) {
                return Mono.error(createParentUpdateNotMatchException(parentRowsList.get(i)
                        , childRowsList.get(i), childWrapper));
            }
        }
        return Mono.just(childRowsList);
    }


}
