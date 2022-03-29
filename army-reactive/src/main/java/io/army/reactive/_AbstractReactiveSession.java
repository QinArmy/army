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
    public final <R> Mono<R> selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<R> selectOne(Select select, Class<R> resultClass, Visible visible) {
        return this.select(select, resultClass, visible)
                .take(2)
                .collectList()
                .flatMap(this::justOne);
    }

    @Override
    public final <R> Mono<Optional<R>> selectOneNullable(Select select, Class<R> resultClass) {
        return this.selectOneNullable(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<Optional<R>> selectOneNullable(Select select, Class<R> resultClass, Visible visible) {
        return this.selectNullable(select, resultClass, visible)
                .take(2)
                .collectList()
                .flatMap(this::justOne);
    }

    @Override
    public final Mono<Map<String, Object>> selectOneAsMap(Select select) {
        return this.selectOneAsMap(select, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Map<String, Object>> selectOneAsMap(Select select, Visible visible) {
        return this.selectOneAsMap(select, HashMap::new, visible);
    }

    @Override
    public final Mono<Map<String, Object>> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor) {
        return this.selectOneAsMap(select, mapConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Map<String, Object>> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        return this.selectAsMap(select, mapConstructor, visible)
                .take(2)
                .collectList()
                .flatMap(this::justOne);
    }

    @Override
    public final <R> Flux<R> select(Select select, Class<R> resultClass) {
        return this.select(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<Optional<R>> selectNullable(Select select, Class<R> resultClass) {
        return this.selectNullable(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsMap(Select select) {
        return this.selectAsMap(select, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsMap(Select select, Visible visible) {
        return this.selectAsMap(select, HashMap::new, visible);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor) {
        return this.selectAsMap(select, mapConstructor, Visible.ONLY_VISIBLE);
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
