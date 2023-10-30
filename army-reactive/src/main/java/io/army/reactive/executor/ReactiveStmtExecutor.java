package io.army.reactive.executor;


import io.army.reactive.QueryResults;
import io.army.reactive.ReactiveStmtOption;
import io.army.session.*;
import io.army.session.executor.StmtExecutor;
import io.army.stmt.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>This interface representing reactive {@link StmtExecutor}.
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@link ReactiveLocalStmtExecutor}</li>
 *     <li>{@link ReactiveRmStmtExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @see ReactiveStmtExecutorFactory
 * @since 1.0
 */
public interface ReactiveStmtExecutor extends StmtExecutor {

    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * <br/>
     *
     * @return session identifier
     * @throws DataAccessException throw when underlying database session have closed.
     */
    long sessionIdentifier() throws DataAccessException;

    Mono<TransactionInfo> transactionInfo();

    Mono<Void> setTransactionCharacteristics(TransactionOption option);

    Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<Void> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<Void> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<ResultStates> insert(SimpleStmt stmt, ReactiveStmtOption option);

    Mono<ResultStates> update(SimpleStmt stmt, ReactiveStmtOption option);

    Flux<ResultStates> batchUpdate(BatchStmt stmt, ReactiveStmtOption option);

    <R> Flux<R> query(SimpleStmt stmt, Class<R> resultClass, ReactiveStmtOption option);

    <R> Flux<Optional<R>> queryOptional(SimpleStmt stmt, Class<R> resultClass, ReactiveStmtOption option);

    <R> Flux<R> queryObject(SimpleStmt stmt, Supplier<R> constructor, ReactiveStmtOption option);

    <R> Flux<R> queryRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, ReactiveStmtOption option);

    <R> Flux<R> secondQuery(TwoStmtQueryStmt stmt, List<R> resultList, ReactiveStmtOption option);

    <R> Flux<R> batchQuery(BatchStmt stmt, Class<R> resultClass, ReactiveStmtOption option);

    <R> Flux<R> batchQueryObject(BatchStmt stmt, Supplier<R> constructor, ReactiveStmtOption option);

    <R> Flux<R> batchQueryRecord(BatchStmt stmt, Function<CurrentRecord, R> function, ReactiveStmtOption option);

    <R> Flux<R> secondBatchQuery(TwoStmtBatchQueryStmt stmt, List<R> resultList, ReactiveStmtOption option);

    QueryResults batchQueryResults(BatchStmt stmt, ReactiveStmtOption option);


    Flux<ResultItem> execute(GenericSimpleStmt stmt, ReactiveStmtOption option);


    Flux<ResultItem> executeMultiStmt(List<GenericSimpleStmt> stmtList, ReactiveStmtOption option);


    <T> Mono<T> close();

    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 1.0
     */
    interface LocalTransactionSpec {

        Mono<TransactionInfo> startTransaction(TransactionOption option);

        Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc);

        Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc);

    }


    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 1.0
     */
    interface XaTransactionSpec {

        Mono<TransactionInfo> start(Xid xid, int flags, TransactionOption option);

        Mono<TransactionInfo> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        Mono<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc);

        Mono<Void> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

        Mono<Void> rollback(Xid xid, Function<Option<?>, ?> optionFunc);


        Mono<Void> forget(Xid xid, Function<Option<?>, ?> optionFunc);

        Mono<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc);


    }


}
