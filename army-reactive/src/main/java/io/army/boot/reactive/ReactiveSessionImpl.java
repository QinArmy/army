package io.army.boot.reactive;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.jdbd.DatabaseSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

final class ReactiveSessionImpl extends AbstractGenericReactiveRmSession<DatabaseSession, ReactiveSessionFactory>
        implements ReactiveSession, InnerGenericRmSession {


    ReactiveSessionImpl(ReactiveSessionFactory sessionFactory, DatabaseSession databaseSession, boolean readOnly) {
        super(sessionFactory, databaseSession, readOnly);
    }

    @Override
    public final <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id, Visible visible) {
        return null;
    }

    @Override
    public final <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return null;
    }

    @Override
    public Mono<Void> valueInsert(Insert insert) {
        return this.valueInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public Mono<Void> valueInsert(Insert insert, final Visible visible) {
        assertSessionActive(true);
        // 1. parse value insert
        return Flux.fromIterable(this.dialect.valueInsert(insert, null, visible))
                //2. assert transaction for child domain value insert
                .doOnNext(this::assertForValueInsert)
                //3. execute value insert
                .flatMap(sqlWrapper -> this.insertSQLExecutor.valueInsert(this, sqlWrapper))
                .then()
                ;
    }

    @Override
    public Flux<Integer> batchUpdate(Update update) {
        return this.batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Integer> batchUpdate(Update update, final Visible visible) {
        assertSessionActive(true);
        return this.updateSQLExecutor
                .batchUpdate(this, parseUpdate(update, visible));
    }

    @Override
    public Flux<Long> batchLargeUpdate(Update update) {
        return this.batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Long> batchLargeUpdate(Update update, final Visible visible) {
        assertSessionActive(true);
        return this.updateSQLExecutor
                .batchLargeUpdate(this, parseUpdate(update, visible));
    }

    @Override
    public Flux<Integer> batchDelete(Delete delete) {
        return this.batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Integer> batchDelete(Delete delete, Visible visible) {
        assertSessionActive(true);
        return this.updateSQLExecutor
                .batchUpdate(this, parseDelete(delete, visible));
    }

    @Override
    public Flux<Long> batchLargeDelete(Delete delete) {
        return this.batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Long> batchLargeDelete(Delete delete, Visible visible) {
        assertSessionActive(true);
        return this.updateSQLExecutor
                .batchLargeUpdate(this, parseDelete(delete, visible));
    }
}
