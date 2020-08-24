package io.army.reactive;

import io.army.GenericSession;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GenericReactiveSession extends GenericSession {


    GenericReactiveSessionFactory sessionFactory();

    /**
     * @param <R> representing select result Java Type
     */
    <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id);

    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList, List<Object> valueList);

    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<R> selectOne(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<R> selectOne(Select select, Class<R> resultClass, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> select(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> select(Select select, Class<R> resultClass, Visible visible);

    Mono<Integer> subQueryInsert(Insert insert);

    Mono<Integer> subQueryInsert(Insert insert, Visible visible);

    Mono<Long> subQueryLargeInsert(Insert insert);

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