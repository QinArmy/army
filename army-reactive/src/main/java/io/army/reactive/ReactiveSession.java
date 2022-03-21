package io.army.reactive;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.GenericSession;
import io.army.session.SessionException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface ReactiveSession extends GenericSession {

    @Override
    SessionFactory sessionFactory();


    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> get(TableMeta<R> table, Object id);

    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> get(TableMeta<R> table, Object id, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value);

    <R extends IDomain> Mono<R> getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value, Visible visible);

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
    <R> Mono<Optional<R>> selectOneNullable(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Mono<Optional<R>> selectOneNullable(Select select, Class<R> resultClass, Visible visible);

    Mono<Map<String, Object>> selectOneAsMap(Select select);

    Mono<Map<String, Object>> selectOneAsMap(Select select, Visible visible);

    Mono<Map<String, Object>> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor);

    Mono<Map<String, Object>> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> select(Select select, Class<R> resultClass);


    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<R> select(Select select, Class<R> resultClass, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> selectNullable(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> selectNullable(Select select, Class<R> resultClass, Visible visible);


    Flux<Map<String, Object>> selectAsMap(Select select);

    Flux<Map<String, Object>> selectAsMap(Select select, Visible visible);

    Flux<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor);

    Flux<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor, Visible visible);

    <T extends IDomain> Mono<Void> save(T domain);

    <T extends IDomain> Mono<Void> save(T domain, NullHandleMode mode);

    <T extends IDomain> Mono<Void> save(T domain, NullHandleMode mode, Visible visible);

    Mono<Long> update(DmlStatement dml);

    Mono<Long> update(DmlStatement dml, Visible visible);

    <R> Flux<R> returningUpdate(DmlStatement dml, Class<R> resultClass);

    <R> Flux<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Visible visible);

    Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml);

    Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Visible visible);

    Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor);

    Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor
            , Visible visible);

    <R> Flux<Optional<R>> returningNullableUpdate(DmlStatement dml, Class<R> resultClass);

    <R> Flux<Optional<R>> returningNullableUpdate(DmlStatement dml, Class<R> resultClass, Visible visible);


    <T extends IDomain> Mono<Void> batchSave(List<T> domainList);

    <T extends IDomain> Mono<Void> batchSave(List<T> domainList, NullHandleMode mode);

    <T extends IDomain> Mono<Void> batchSave(List<T> domainList, NullHandleMode mode, Visible visible);

    Flux<Long> batchUpdate(NarrowDmlStatement dml);

    Flux<Long> batchUpdate(NarrowDmlStatement dml, Visible visible);

    MultiResult multiStmt(List<Statement> statementList);

    MultiResult multiStmt(List<Statement> statementList, Visible visible);

    MultiResult call(CallableStatement callable);


    Mono<Void> flush() throws SessionException;


}
