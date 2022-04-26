package io.army.reactive;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.util._Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class _AbstractReactiveSession implements ReactiveSession {

    protected _AbstractReactiveSession() {
    }


    @Override
    public final <R> Mono<R> queryOne(DqlStatement statement, Class<R> resultClass) {
        return this.queryOne(statement, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<R> queryOne(DqlStatement statement, Class<R> resultClass, Visible visible) {
        return this.query(statement, resultClass, visible)
                .take(2)
                .collectList()
                .flatMap(this::justOne);
    }

    @Override
    public final <R> Mono<Optional<R>> queryOneNullable(DqlStatement statement, Class<R> resultClass) {
        return this.queryOneNullable(statement, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<Optional<R>> queryOneNullable(DqlStatement statement, Class<R> resultClass, Visible visible) {
        return this.queryNullable(statement, resultClass, visible)
                .take(2)
                .collectList()
                .flatMap(this::justOne);
    }

    @Override
    public final Mono<Map<String, Object>> queryOneAsMap(DqlStatement statement) {
        return this.queryOneAsMap(statement, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Map<String, Object>> queryOneAsMap(DqlStatement statement, Visible visible) {
        return this.queryOneAsMap(statement, HashMap::new, visible);
    }

    @Override
    public final Mono<Map<String, Object>> queryOneAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor) {
        return this.queryOneAsMap(statement, mapConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Map<String, Object>> queryOneAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        return this.queryAsMap(statement, mapConstructor, visible)
                .take(2)
                .collectList()
                .flatMap(this::justOne);
    }

    @Override
    public final <R> Flux<R> query(DqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<Optional<R>> queryNullable(DqlStatement statement, Class<R> resultClass) {
        return this.queryNullable(statement, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> queryAsMap(DqlStatement statement) {
        return this.queryAsMap(statement, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> queryAsMap(DqlStatement statement, Visible visible) {
        return this.queryAsMap(statement, HashMap::new, visible);
    }

    @Override
    public final Flux<Map<String, Object>> queryAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor) {
        return this.queryAsMap(statement, mapConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> Mono<Void> save(T domain) {
        return this.save(domain, NullHandleMode.INSERT_DEFAULT, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> Mono<Void> save(T domain, NullHandleMode mode) {
        return this.save(domain, mode, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> update(DmlStatement dml) {
        return this.update(dml, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<R> returningUpdate(DmlStatement dml, Class<R> resultClass) {
        return this.returningUpdate(dml, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml) {
        return this.returningUpdateAsMap(dml, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Visible visible) {
        return this.returningUpdateAsMap(dml, HashMap::new, visible);
    }

    @Override
    public final Flux<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor) {
        return this.returningUpdateAsMap(dml, mapConstructor, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> Flux<Optional<R>> returningNullableUpdate(DmlStatement dml, Class<R> resultClass) {
        return this.returningNullableUpdate(dml, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> Mono<Void> batchSave(List<T> domainList) {
        return this.batchSave(domainList, NullHandleMode.INSERT_DEFAULT, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> Mono<Void> batchSave(List<T> domainList, NullHandleMode mode) {
        return this.batchSave(domainList, mode, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Long> batchUpdate(NarrowDmlStatement dml) {
        return this.batchUpdate(dml, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiResult multiStmt(List<Statement> statementList) {
        return this.multiStmt(statementList, Visible.ONLY_VISIBLE);
    }


    final <R> Mono<R> justOne(List<R> list) {
        final Mono<R> mono;
        switch (list.size()) {
            case 0:
                mono = Mono.empty();
                break;
            case 1:
                mono = Mono.just(list.get(0));
                break;
            default: {
                mono = Mono.error(_Exceptions.nonUnique(list));
            }
        }
        return mono;
    }


}
