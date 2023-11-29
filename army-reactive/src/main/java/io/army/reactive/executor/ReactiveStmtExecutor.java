package io.army.reactive.executor;


import io.army.meta.ChildTableMeta;
import io.army.reactive.ReactiveCloseable;
import io.army.reactive.ReactiveStmtOption;
import io.army.session.*;
import io.army.session.executor.StmtExecutor;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultStates;
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
 *     <li>{@link ReactiveLocalExecutor}</li>
 *     <li>{@link ReactiveRmExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @see ReactiveExecutorFactory
 * @since 1.0
 */
public interface ReactiveStmtExecutor extends StmtExecutor, ReactiveCloseable {

    Mono<TransactionInfo> transactionInfo();

    Mono<Void> setTransactionCharacteristics(TransactionOption option);

    Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<Void> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<Void> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<ResultStates> insert(SimpleStmt stmt, ReactiveStmtOption option);

    Mono<ResultStates> update(SimpleStmt stmt, ReactiveStmtOption option, Function<Option<?>, ?> optionFunc);

    Flux<ResultStates> batchUpdate(BatchStmt stmt, ReactiveStmtOption option);

    <R> Flux<R> query(SingleSqlStmt stmt, Class<R> resultClass, ReactiveStmtOption option);

    <R> Flux<Optional<R>> queryOptional(SingleSqlStmt stmt, Class<R> resultClass, ReactiveStmtOption option);

    <R> Flux<R> queryObject(SingleSqlStmt stmt, Supplier<R> constructor, ReactiveStmtOption option);

    <R> Flux<R> queryRecord(SingleSqlStmt stmt, Function<CurrentRecord, R> function, ReactiveStmtOption option);

    <R> Flux<R> secondQuery(TwoStmtQueryStmt stmt, ReactiveStmtOption option, List<R> resultList);

    <R> Flux<R> pairBatchQuery(PairBatchStmt stmt, Class<R> resultClass, ReactiveStmtOption option,
                               ChildTableMeta<?> childTable);

    <R> Flux<R> pairBatchQueryObject(PairBatchStmt stmt, Supplier<R> constructor, ReactiveStmtOption option,
                                     ChildTableMeta<?> childTable);

    <R> Flux<R> pairBatchQueryRecord(PairBatchStmt stmt, Function<CurrentRecord, R> function, ReactiveStmtOption option,
                                     ChildTableMeta<?> childTable);


    /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 1.0
     */
    interface LocalTransactionSpec {

        Mono<TransactionInfo> startTransaction(TransactionOption option, HandleMode mode);

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

        Flux<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc);


    }


}
