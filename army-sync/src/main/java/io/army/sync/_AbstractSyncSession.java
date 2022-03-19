package io.army.sync;

import io.army.NonUniqueException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class _AbstractSyncSession implements SyncSession {

    protected _AbstractSyncSession() {
    }

    @Nullable
    @Override
    public final <R extends IDomain> R get(TableMeta<R> table, Object id) {
        return this.get(table, id, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value) {
        return this.getByUnique(table, field, value, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }


    @Nullable
    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass, final Visible visible) {
        final List<R> list;
        list = this.select(select, resultClass, ArrayList::new, visible);
        final R result;
        switch (list.size()) {
            case 1:
                result = list.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw new NonUniqueException("select result[%s] more than 1.", list.size());
        }
        return result;
    }


    @Override
    public final Map<String, Object> selectOneAsMap(Select select) {
        return this.selectOneAsMap(select, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Map<String, Object> selectOneAsMap(Select select, Visible visible) {
        return this.selectOneAsMap(select, HashMap::new, visible);
    }

    @Override
    public final Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor) {
        return this.selectOneAsMap(select, mapConstructor, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        final List<Map<String, Object>> list;
        list = this.selectAsMap(select, mapConstructor, ArrayList::new, visible);
        final Map<String, Object> result;
        switch (list.size()) {
            case 1:
                result = list.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw new NonUniqueException("select result[%s] more than 1.", list.size());
        }
        return result;
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass) {
        return this.select(select, resultClass, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.select(select, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return this.select(select, resultClass, ArrayList::new, visible);
    }


    @Override
    public final List<Map<String, Object>> selectAsMap(Select select) {
        return this.selectAsMap(select, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> selectAsMap(Select select, Visible visible) {
        return this.selectAsMap(select, HashMap::new, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.selectAsMap(select, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> void save(T domain) {
        this.save(domain, NullHandleMode.INSERT_DEFAULT);
    }

    @Override
    public final long insert(Insert insert) {
        return this.insert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass) {
        return this.returningInsert(insert, resultClass, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.returningInsert(insert, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible) {
        return this.returningInsert(insert, resultClass, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningInsertAsMap(Insert insert) {
        return this.returningInsertAsMap(insert, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> returningInsertAsMap(Insert insert, Visible visible) {
        return this.returningInsertAsMap(insert, HashMap::new, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningInsertAsMap(Insert insert, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.returningInsertAsMap(insert, mapConstructor, listConstructor, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass) {
        return this.returningUpdate(update, resultClass, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.returningUpdate(update, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        return this.returningUpdate(update, resultClass, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningUpdateAsMap(Update update) {
        return this.returningUpdateAsMap(update, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> returningUpdateAsMap(Update update, Visible visible) {
        return this.returningUpdateAsMap(update, HashMap::new, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningUpdateAsMap(Update update, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.returningUpdateAsMap(update, mapConstructor, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long update(Update update) {
        return this.update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(Update update) {
        return this.batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long delete(Delete delete) {
        return this.delete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass) {
        return this.returningDelete(delete, resultClass, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.returningDelete(delete, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        return this.returningDelete(delete, resultClass, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningDeleteAsMap(Delete delete) {
        return this.returningDeleteAsMap(delete, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> returningDeleteAsMap(Delete delete, Visible visible) {
        return this.returningDeleteAsMap(delete, HashMap::new, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningDeleteAsMap(Delete delete, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.returningDeleteAsMap(delete, mapConstructor, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> void batchSave(List<T> domainList) {
        this.batchSave(domainList, NullHandleMode.INSERT_DEFAULT);
    }

    @Override
    public final List<Long> batchDelete(Delete delete) {
        return this.batchDelete(delete, Visible.ONLY_VISIBLE);
    }


    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }


}