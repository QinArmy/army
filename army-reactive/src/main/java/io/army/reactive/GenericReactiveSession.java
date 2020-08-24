package io.army.reactive;

import io.army.GenericSession;
import io.army.criteria.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 *
 */
public interface GenericReactiveSession extends GenericSession {


    GenericReactiveSessionFactory sessionFactory();

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<R> selectOne(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<R> selectOne(Select select, Class<R> resultClass, Visible visible);

    Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select);

    Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> select(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> select(Select select, Class<R> resultClass, Visible visible);

    Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select);

    Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    Mono<Integer> subQueryInsert(Insert insert);

    Mono<Integer> subQueryInsert(Insert insert, Visible visible);

    Mono<Long> largeSubQueryInsert(Insert insert);

    Mono<Long> largeSubQueryInsert(Insert insert, Visible visible);

    /**
     * @param update will start singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    Mono<Integer> update(Update update);

    Mono<Integer> update(Update update, Visible visible);

    Mono<Long> largeUpdate(Update update);

    Mono<Long> largeUpdate(Update update, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> Flux<R> returningUpdate(Update update, Class<R> resultClass);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> Flux<R> returningUpdate(Update update, Class<R> resultClass, Visible visible);

    Mono<Integer> delete(Delete delete);

    Mono<Integer> delete(Delete delete, Visible visible);

    Mono<Long> largeDelete(Delete delete);

    Mono<Long> largeDelete(Delete delete, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible);

}
