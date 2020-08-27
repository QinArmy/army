package io.army.boot.reactive;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericProxyReactiveSession;
import io.army.reactive.GenericReactiveSessionFactory;
import io.army.reactive.GenericReactiveTmSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

abstract class AbstractProxyReactiveSession<F extends GenericReactiveSessionFactory>
        implements GenericProxyReactiveSession {

    final F sessionFactory;

    final CurrentSessionContext currentSessionContext;

    AbstractProxyReactiveSession(F sessionFactory, CurrentSessionContext currentSessionContext) {
        this.sessionFactory = sessionFactory;
        this.currentSessionContext = currentSessionContext;
    }

    @Override
    public ArmyEnvironment environment() {
        return this.sessionFactory.environment();
    }

    @Nullable
    @Override
    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        return this.sessionFactory.tableMeta(domainClass);
    }

    @Override
    public Mono<Boolean> readOnly() {
        return this.currentSessionContext.currentSession()
                .map(GenericReactiveTmSession::readonly);
    }

    @Override
    public Mono<Boolean> hasTransaction() {
        return this.currentSessionContext.currentSession()
                .map(GenericReactiveTmSession::hasTransaction);
    }

    @Override
    public <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.get(tableMeta, id));
    }

    @Override
    public <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.getByUnique(tableMeta, propNameList, valueList));
    }

    @Override
    public Mono<Void> valueInsert(Insert insert) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.valueInsert(insert));
    }

    @Override
    public Mono<Void> valueInsert(Insert insert, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.valueInsert(insert, visible));
    }

    @Override
    public <R> Mono<R> selectOne(Select select, Class<R> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.selectOne(select, resultClass));
    }

    @Override
    public <R> Mono<R> selectOne(Select select, Class<R> resultClass, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.selectOne(select, resultClass, visible));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select, Class<? extends Map> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.selectOneAsUnmodifiableMap(select, resultClass));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select, Class<? extends Map> resultClass
            , final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.selectOneAsUnmodifiableMap(select, resultClass, visible));
    }

    @Override
    public <R> Flux<R> select(Select select, Class<R> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.select(select, resultClass));
    }

    @Override
    public <R> Flux<R> select(Select select, Class<R> resultClass, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.select(select, resultClass, visible));
    }

    @Override
    public <R> Flux<Optional<R>> selectOptional(Select select, Class<R> columnClass) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.selectOptional(select, columnClass));
    }

    @Override
    public <R> Flux<Optional<R>> selectOptional(Select select, Class<R> columnClass, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.selectOptional(select, columnClass, visible));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select, Class<? extends Map> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.selectAsUnmodifiableMap(select, resultClass));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select, Class<? extends Map> resultClass
            , final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.selectAsUnmodifiableMap(select, resultClass, visible));
    }

    @Override
    public <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.returningInsert(insert, resultClass));
    }

    @Override
    public <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.returningInsert(insert, resultClass, visible));
    }

    @Override
    public Mono<Integer> subQueryInsert(Insert insert) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.subQueryInsert(insert));
    }

    @Override
    public Mono<Integer> subQueryInsert(Insert insert, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.subQueryInsert(insert, visible));
    }

    @Override
    public Mono<Long> largeSubQueryInsert(Insert insert) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.largeSubQueryInsert(insert));
    }

    @Override
    public Mono<Long> largeSubQueryInsert(Insert insert, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.largeSubQueryInsert(insert, visible));
    }

    @Override
    public Mono<Integer> update(Update update) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.update(update));
    }

    @Override
    public Mono<Integer> update(Update update, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.update(update, visible));
    }

    @Override
    public Mono<Long> largeUpdate(Update update) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.largeUpdate(update));
    }

    @Override
    public Mono<Long> largeUpdate(Update update, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.largeUpdate(update, visible));
    }

    @Override
    public <R> Flux<R> returningUpdate(Update update, Class<R> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.returningUpdate(update, resultClass));
    }

    @Override
    public <R> Flux<R> returningUpdate(Update update, Class<R> resultClass, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.returningUpdate(update, resultClass, visible));
    }

    @Override
    public Mono<Integer> delete(Delete delete) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.delete(delete));
    }

    @Override
    public Mono<Integer> delete(Delete delete, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.delete(delete, visible));
    }

    @Override
    public Mono<Long> largeDelete(Delete delete) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.largeDelete(delete));
    }

    @Override
    public Mono<Long> largeDelete(Delete delete, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMap(session -> session.largeDelete(delete, visible));
    }

    @Override
    public <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.returningDelete(delete, resultClass));
    }

    @Override
    public <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .flatMapMany(session -> session.returningDelete(delete, resultClass, visible));
    }

    @Override
    public Mono<Void> flush() {
        return this.currentSessionContext.currentSession()
                .flatMap(GenericReactiveTmSession::flush);
    }
}
