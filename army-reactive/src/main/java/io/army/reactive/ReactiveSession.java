package io.army.reactive;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.session.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
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
public interface ReactiveSession extends Session, ReactiveCloseable {

    @Override
    ReactiveSessionFactory sessionFactory();

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @throws SessionException emit(not throw) when database driver emit error.
     */
    Mono<TransactionInfo> transactionInfo();

    Mono<? extends ReactiveSession> setTransactionCharacteristics(TransactionOption option);

    Mono<?> setSavePoint();

    Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint);

    Mono<? extends ReactiveSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    /*-------------------below query methods-------------------*/

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);


    /*-------------------below queryOptional methods-------------------*/


    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);

    /*-------------------below queryObject methods-------------------*/

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option);


    /*-------------------below queryRecord methods-------------------*/

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option);


    /*-------------------below save methods-------------------*/

    Mono<ResultStates> save(Object domain);

    /*-------------------below update methods-------------------*/

    Mono<ResultStates> update(SimpleDmlStatement statement);

    Mono<ResultStates> update(SimpleDmlStatement statement, ReactiveStmtOption option);


    /*-------------------below batchSave methods-------------------*/

    <T> Mono<ResultStates> batchSave(List<T> domainList);


    /*-------------------below batchUpdate methods-------------------*/

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement);

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement, ReactiveStmtOption option);


    /*-------------------below batchQuery methods-------------------*/

//    QueryResults batchQueryResults(BatchDqlStatement statement);
//
//    QueryResults batchQueryResults(BatchDqlStatement statement, ReactiveOption option);

    /*-------------------below batchQueryAsFlux methods-------------------*/

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass);

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, ReactiveStmtOption option);

    /*-------------------below batchQueryObject methods-------------------*/

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, ReactiveStmtOption option);


    /*-------------------below batchQueryObject methods-------------------*/

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, ReactiveStmtOption option);

    /*-------------------below multiStmt methods-------------------*/

//    MultiResult multiStmt(MultiResultStatement statement);

//    Flux<ResultItem> execute(DqlStatement statement);


//    Mono<ReactiveCursor> declareCursor(DeclareCursor statement);


}
