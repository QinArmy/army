package io.army.boot.reactive;

import io.army.wrapper.*;
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
    public final <N extends Number> Mono<N> update(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Class<N> resultClass) {
        Mono<? extends Number> mono;
        if (resultClass == Integer.class) {
            mono = internalUpdate(session, sqlWrapper, PreparedStatement::executeUpdate, "update");
        } else if (resultClass == Long.class) {
            mono = internalUpdate(session, sqlWrapper, PreparedStatement::executeLargeUpdate, "update");
        } else {
            mono = Mono.error(new IllegalArgumentException("ResultClass error"));
        }
        return mono.cast(resultClass);
    }

    @Override
    public final <N extends Number> Flux<N> batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Class<N> resultClass) {
        Flux<? extends Number> flux;
        if (resultClass == Integer.class) {
            flux = internalBatchUpdate(session, sqlWrapper, PreparedStatement::executeBatch);
        } else if (resultClass == Long.class) {
            flux = internalBatchUpdate(session, sqlWrapper, PreparedStatement::executeLargeBatch);
        } else {
            flux = Flux.error(new IllegalArgumentException("ResultClass error"));
        }
        return flux.cast(resultClass);
    }

    @Override
    public final <T> Flux<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Class<T> resultClass) {
        return doReturningUpdate(session, sqlWrapper, resultClass, true);
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
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleSQLWrapper, Function)
     */
    private <N extends Number> Mono<N> internalUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Function<PreparedStatement, Mono<N>> executeFunction, String methodName) {

        Mono<N> mono;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            mono = doExecuteUpdate(session, (SimpleSQLWrapper) sqlWrapper, executeFunction);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            final ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

            // 1. execute child update sql
            mono = doExecuteUpdate(session, childWrapper, executeFunction)
                    //2. execute parent update sql and assert parent updated rows and child match
                    .flatMap(childRows -> doParentUpdate(session, childSQLWrapper, executeFunction, childRows))
            ;
        } else {
            mono = Mono.error(createUnSupportedSQLWrapperException(sqlWrapper, methodName));
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
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleSQLWrapper, Function)
     */
    private <N extends Number> Mono<N> doParentUpdate(InnerGenericRmSession session, ChildSQLWrapper childSQLWrapper
            , Function<PreparedStatement, Mono<N>> executeFunction, N childRows) {
        Mono<N> mono;
        if (childRows.longValue() < 1L) {
            mono = Mono.just(childRows);
        } else {
            final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
            // 1. execute parent update sql.
            mono = doExecuteUpdate(session, childSQLWrapper.parentWrapper(), executeFunction)
                    // 2. assert parent updated rows and child match
                    .flatMap(parentRows -> assertParentChildUpdateMatch(parentRows, childRows, childWrapper))
            ;
        }
        return mono;
    }

    private <N extends Number> Mono<N> assertParentChildUpdateMatch(N parentRows, N childRows
            , SimpleSQLWrapper childWrapper) {
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
     * @see #doExecuteBatchUpdate(InnerGenericRmSession, BatchSimpleSQLWrapper, Function)
     */
    private <N extends Number> Flux<N> internalBatchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Function<PreparedStatement, Flux<N>> executeFunction) {

        Flux<N> flux;
        if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            flux = doExecuteBatchUpdate(session, (BatchSimpleSQLWrapper) sqlWrapper, executeFunction);
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            final ChildBatchSQLWrapper childSQLWrapper = (ChildBatchSQLWrapper) sqlWrapper;
            final BatchSimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

            // 1. execute child batch update sql
            flux = doExecuteBatchUpdate(session, childWrapper, executeFunction)
                    .collectList()
                    //2. execute parent update sql and assert parent updated rows and child match
                    .flatMapMany(list -> doParentBatchUpdate(session, childSQLWrapper, executeFunction, list))
            ;
        } else {
            flux = Flux.error(createUnSupportedSQLWrapperException(sqlWrapper, "batchUpdate"));
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
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleSQLWrapper, Function)
     */
    private <N extends Number> Flux<N> doParentBatchUpdate(InnerGenericRmSession session
            , ChildBatchSQLWrapper childSQLWrapper, Function<PreparedStatement, Flux<N>> executeFunction
            , List<N> childList) {

        final BatchSimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
        return // 1. execute parent update sql.
                doExecuteBatchUpdate(session, childSQLWrapper.parentWrapper(), executeFunction)
                        .collectList()
                        // 2. assert parent updated rows and child match
                        .flatMap(parentList -> assertParentChildBatchUpdateMatch(parentList, childList, childWrapper))
                        .flatMapMany(Flux::fromIterable)
                ;
    }


    private <N extends Number> Mono<List<N>> assertParentChildBatchUpdateMatch(List<N> parentRowsList
            , List<N> childRowsList, BatchSimpleSQLWrapper childWrapper) {
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
