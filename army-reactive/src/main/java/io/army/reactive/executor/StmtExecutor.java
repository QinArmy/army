package io.army.reactive.executor;


import io.army.reactive.Closeable;
import io.army.reactive.MultiResult;
import io.army.reactive.QueryResults;
import io.army.reactive.StatementOption;
import io.army.session.*;
import io.army.stmt.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public interface StmtExecutor extends Closeable, OptionSpec {

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
     * @throws DataAccessException throw when session have closed.
     */
    long sessionIdentifier() throws DataAccessException;

    boolean inTransaction() throws DataAccessException;

    Mono<TransactionStatus> transactionStatus();

    Mono<? extends StmtExecutor> setTransactionCharacteristics(TransactionOption option);

    Mono<Object> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<? extends StmtExecutor> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<? extends StmtExecutor> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<ResultStates> insert(SimpleStmt stmt, StatementOption option);

    Mono<ResultStates> update(SimpleStmt stmt, StatementOption option);

    Flux<ResultStates> batchUpdate(BatchStmt stmt, StatementOption option);

    <R> Flux<R> query(SimpleStmt stmt, Class<R> resultClass, StatementOption option);

    <R> Flux<R> queryObject(SimpleStmt stmt, Supplier<R> constructor, StatementOption option);

    <R> Flux<R> queryRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, StatementOption option);

    <R> Mono<Integer> secondQuery(TwoStmtQueryStmt stmt, List<R> resultList, StatementOption option);

    <R> Flux<R> batchQuery(BatchStmt stmt, Class<R> resultClass, StatementOption option);

    <R> Flux<R> batchQueryObject(BatchStmt stmt, Supplier<R> constructor, StatementOption option);

    <R> Flux<R> batchQueryRecord(BatchStmt stmt, Function<CurrentRecord, R> function, StatementOption option);

    <R> Mono<Integer> secondBatchQuery(TwoStmtBatchQueryStmt stmt, List<R> resultList, StatementOption option);

    QueryResults batchQuery(BatchStmt stmt, StatementOption option);

    MultiResult multiStmt(MultiStmt stmt, StatementOption option);


}
