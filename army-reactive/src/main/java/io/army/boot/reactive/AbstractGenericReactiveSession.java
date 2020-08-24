package io.army.boot.reactive;

import io.army.NonUniqueException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveSession;
import io.army.tx.reactive.GenericReactiveTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

abstract class AbstractGenericReactiveSession implements GenericReactiveSession {

    final boolean readOnly;

    AbstractGenericReactiveSession(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public final boolean readonly() {
        GenericReactiveTransaction tx = obtainTransaction();
        return this.readOnly || (tx != null && tx.readOnly());
    }

    @Override
    public final <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id) {
        return this.get(tableMeta, id, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return this.getByUnique(tableMeta, propNameList, valueList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<R> selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<R> selectOne(Select select, Class<R> resultClass, final Visible visible) {
        return this.select(select, resultClass, visible)
                .collectList()
                .flatMap(this::mapMono)
                ;
    }


    @Override
    public final Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select) {
        return this.selectOneAsUnmodifiableMap(select, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select, final Visible visible) {
        return this.selectOne(select, Map.class, visible)
                .map(this::castMap)
                ;
    }

    @Override
    public final <R> Flux<R> select(Select select, Class<R> resultClass) {
        return this.select(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select) {
        return this.selectAsUnmodifiableMap(select, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select, final Visible visible) {
        return this.select(select, Map.class, visible)
                .map(this::castMap)
                ;
    }


    @Override
    public final <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass) {
        return this.returningInsert(insert, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Integer> subQueryInsert(Insert insert) {
        return this.subQueryInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> largeSubQueryInsert(Insert insert) {
        return this.largeSubQueryInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Integer> update(Update update) {
        return this.update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> largeUpdate(Update update) {
        return this.largeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<R> returningUpdate(Update update, Class<R> resultClass) {
        return this.returningUpdate(update, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Integer> delete(Delete delete) {
        return this.delete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> largeDelete(Delete delete) {
        return this.largeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass) {
        return this.returningDelete(delete, resultClass, Visible.ONLY_VISIBLE);
    }

    /*################################## blow package method ##################################*/

    @Nullable
    abstract GenericReactiveTransaction obtainTransaction();

    /*################################## blow private method ##################################*/

    private <R> Mono<R> mapMono(List<R> list) {
        Mono<R> mono;
        if (list.size() > 1) {
            mono = Mono.error(new NonUniqueException("select result[%s] more than 1.", list.size()));
        } else if (list.size() == 1) {
            mono = Mono.just(list.get(0));
        } else {
            mono = Mono.empty();
        }
        return mono;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }


}
