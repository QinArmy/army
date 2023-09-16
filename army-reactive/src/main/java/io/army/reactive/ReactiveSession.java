package io.army.reactive;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.MultiResultStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.session.Session;
import io.army.session.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    Mono<TransactionStatus> transactionStatus();

    /*-------------------below queryOne methods-------------------*/

    /**
     * @param <R> result row Java Type.
     */
    <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass);


    <R> Mono<R> queryOne(SimpleDqlStatement statement, Class<R> resultClass, StatementOption option);



    /*-------------------below queryOneOptional methods-------------------*/

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<Optional<R>> queryOneOptional(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Mono<R> queryOneOptional(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer);


    /*-------------------below queryOneObject methods-------------------*/

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> Mono<R> queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer);


    /*-------------------below queryOneRecord methods-------------------*/

    <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Mono<R> queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);



    /*-------------------below query methods-------------------*/

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass);


    <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer);


    /*-------------------below queryOptional methods-------------------*/


    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer);

    /*-------------------below queryObject methods-------------------*/

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer);


    /*-------------------below queryRecord methods-------------------*/

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);


    /*-------------------below save methods-------------------*/

    Mono<ResultStates> save(Object domain);

    /*-------------------below update methods-------------------*/

    Mono<ResultStates> update(SimpleDmlStatement dml);


    /*-------------------below batchSave methods-------------------*/

    <T> Flux<ResultStates> batchSave(List<T> domainList);


    /*-------------------below batchUpdate methods-------------------*/

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement);

    Flux<ResultStates> batchUpdate(BatchDmlStatement statement, boolean useMultiStmt);


    /*-------------------below batchQuery methods-------------------*/

    QueryResults batchQueryResults(BatchDqlStatement statement);

    QueryResults batchQueryResults(BatchDqlStatement statement, boolean useMultiStmt);

    /*-------------------below batchQueryAsFlux methods-------------------*/

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass);

    <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, boolean useMultiStmt);


    /*-------------------below batchQueryObject methods-------------------*/

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor);

    <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, boolean useMultiStmt);


    /*-------------------below batchQueryObject methods-------------------*/

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function);

    <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, boolean useMultiStmt);



    /*-------------------below multiStmt methods-------------------*/

    MultiResult multiStmt(MultiResultStatement statement);


}
