package io.army.boot.reactive;

import io.army.dialect.InsertException;
import io.army.wrapper.*;
import io.jdbd.PreparedStatement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

final class InsertSQLExecutorImpl extends SQLExecutorSupport implements InsertSQLExecutor {

    InsertSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    public final Mono<Void> valueInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper)
            throws InsertException {
        return doValueInsert(session, sqlWrapper);
    }

    @Override
    public <N extends Number> Mono<N> subQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Class<N> resultClass) throws InsertException {

        Mono<? extends Number> mono;
        if (resultClass == Integer.class) {
            mono = internalSubQueryInsert(session, sqlWrapper, PreparedStatement::executeUpdate, "subQueryInsert");
        } else if (resultClass == Long.class) {
            mono = internalSubQueryInsert(session, sqlWrapper, PreparedStatement::executeLargeUpdate
                    , "subQueryInsert");
        } else {
            mono = Mono.error(new IllegalArgumentException("ResultClass error"));
        }
        return mono.cast(resultClass);
    }

    @Override
    public final <T> Flux<T> returningInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException {
        return doReturningUpdate(session, sqlWrapper, resultClass, false, "returningInsert");
    }


    /*################################## blow private method ##################################*/

    private Mono<Void> doValueInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) {
        Mono<Void> mono;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            final SimpleSQLWrapper simpleSQLWrapper = (SimpleSQLWrapper) sqlWrapper;
            // 1. execute insert sql
            mono = doExecuteUpdate(session, simpleSQLWrapper, PreparedStatement::executeUpdate)
                    // 2. assert  insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, simpleSQLWrapper));
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
            final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
            // 1. execute parent insert sql
            mono = doExecuteUpdate(session, parentWrapper, PreparedStatement::executeUpdate)
                    // 2. assert parent insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, parentWrapper))
                    //3. execute child insert sql
                    .then(doExecuteUpdate(session, childWrapper, PreparedStatement::executeUpdate))
                    // 4. assert child insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, childWrapper))
            ;
        } else if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
            final BatchSimpleSQLWrapper batchSQLWrapper = (BatchSimpleSQLWrapper) sqlWrapper;
            // 1. execute batch insert sql
            mono = doExecuteBatchUpdate(session, batchSQLWrapper, PreparedStatement::executeBatch)
                    // 2. assert each insert rows equals 1
                    .flatMap(insertRows -> assertValueInsertRows(insertRows, batchSQLWrapper))
                    .then();
        } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
            ChildBatchSQLWrapper batchSQLWrapper = (ChildBatchSQLWrapper) sqlWrapper;
            final BatchSimpleSQLWrapper parentWrapper = batchSQLWrapper.parentWrapper();
            final BatchSimpleSQLWrapper childWrapper = batchSQLWrapper.childWrapper();

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
            mono = Mono.error(createUnSupportedSQLWrapperException(sqlWrapper, "valueInsert"));
        }
        return mono;
    }

    private Mono<Void> doExecuteBatchChildValueInsert(InnerGenericRmSession session, BatchSimpleSQLWrapper childWrapper
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
            , BatchSimpleSQLWrapper childWrapper) {
        Mono<Void> mono;
        if (childRows.equals(parentRows)) {
            mono = Mono.empty();
        } else {
            mono = Mono.error(createBatchChildInsertNotMatchException(parentRows, childRows, childWrapper));
        }
        return mono;
    }

    private Mono<Void> assertValueInsertRows(Integer insertRows, GenericSimpleWrapper sqlWrapper) {
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
     * @see #doExecuteUpdate(InnerGenericRmSession, SimpleSQLWrapper, Function)
     */
    private <N extends Number> Mono<N> internalSubQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Function<PreparedStatement, Mono<N>> executeFunction, String methodName) {
        Mono<N> mono;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            mono = doExecuteUpdate(session, (SimpleSQLWrapper) sqlWrapper, executeFunction);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            final ChildSQLWrapper childSQLWrapper = ((ChildSQLWrapper) sqlWrapper);
            final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
            final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

            // 1. execute parent sub query insert
            mono = doExecuteUpdate(session, parentWrapper, executeFunction)
                    //2. execute child sub query insert
                    .flatMap(parentRows -> doChildSubQueryInsert(session, childWrapper, parentRows, executeFunction))
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
    private <N extends Number> Mono<N> doChildSubQueryInsert(InnerGenericRmSession session
            , SimpleSQLWrapper childWrapper, N parentRows, Function<PreparedStatement, Mono<N>> executeFunction) {
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
            , SimpleSQLWrapper childWrapper) {
        Mono<N> mono;
        if (childRows.equals(parentRows)) {
            mono = Mono.just(childRows);
        } else {
            mono = Mono.error(createChildSubQueryInsertNotMatchException(parentRows, childRows, childWrapper));
        }
        return mono;
    }
}
