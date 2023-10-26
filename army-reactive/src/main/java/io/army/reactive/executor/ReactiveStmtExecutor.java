package io.army.reactive.executor;


import io.army.reactive.QueryResults;
import io.army.reactive.ReactiveCloseable;
import io.army.reactive.ReactiveStmtOption;
import io.army.session.CurrentRecord;
import io.army.session.Option;
import io.army.session.ResultItem;
import io.army.session.ResultStates;
import io.army.session.executor.StmtExecutor;
import io.army.stmt.*;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
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
 *
 * @see ReactiveStmtExecutorFactory
 * @since 1.0
 */
public interface ReactiveStmtExecutor extends StmtExecutor, ReactiveCloseable {


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

}
