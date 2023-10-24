package io.army.reactive;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.session.*;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This interface representing a reactive database session.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link ReactiveLocalSession}</li>
 *     <li>{@link ReactiveRmSession}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface ReactiveSession extends Session, Closeable {

    @Override
    ReactiveSessionFactory sessionFactory();

    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * </p>
     *
     * @throws SessionException throw when session have closed.
     */
    long sessionIdentifier() throws SessionException;

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @throws SessionException emit(not throw) when database driver emit error.
     */
    Mono<TransactionInfo> transactionInfo();

    Mono<? extends ReactiveSession> setTransactionCharacteristics(TransactionOption option);

    Mono<Object> setSavePoint();

    Mono<Object> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    /*-------------------below query methods-------------------*/

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass);


    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, ReactiveOption option);

    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer, ReactiveOption option);


    /*-------------------below queryOptional methods-------------------*/


    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer);


    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, ReactiveOption option);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer, ReactiveOption option);

    /*-------------------below queryObject methods-------------------*/

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveOption option);

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer, ReactiveOption option);


    /*-------------------below queryRecord methods-------------------*/

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveOption option);

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer, ReactiveOption option);


    /*-------------------below save methods-------------------*/

    Mono<ResultStates> save(Object domain);

    /*-------------------below update methods-------------------*/

    Mono<ResultStates> update(SimpleDmlStatement dml);

    Mono<ResultStates> update(SimpleDmlStatement dml, ReactiveOption option);


    /*-------------------below batchSave methods-------------------*/

    <T> Mono<ResultStates> batchSave(List<T> domainList);


    /*-------------------below batchUpdate methods-------------------*/

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement);

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement, ReactiveOption option);


    /*-------------------below batchQuery methods-------------------*/

//    QueryResults batchQueryResults(BatchDqlStatement statement);
//
//    QueryResults batchQueryResults(BatchDqlStatement statement, ReactiveOption option);

    /*-------------------below batchQueryAsFlux methods-------------------*/

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass);

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, ReactiveOption option);

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer, ReactiveOption option);


    /*-------------------below batchQueryObject methods-------------------*/

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, ReactiveOption option);

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer, ReactiveOption option);


    /*-------------------below batchQueryObject methods-------------------*/

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);


    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, ReactiveOption option);

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer, ReactiveOption option);

    /*-------------------below multiStmt methods-------------------*/

//    MultiResult multiStmt(MultiResultStatement statement);

//    Flux<ResultItem> execute(DqlStatement statement);


//    Mono<ReactiveCursor> declareCursor(DeclareCursor statement);


}
