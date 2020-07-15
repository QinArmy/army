package io.army.boot;


import io.army.GenericSyncSession;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

abstract class AbstractGenericSession implements GenericSyncSession {

    @Nullable
    @Override
    public final <R extends IDomain> R get(TableMeta<R> tableMeta, Object id) {
        return this.get(tableMeta, id, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return this.getByUnique(tableMeta, propNameList, valueList, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass) {
        return this.select(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final int update(Update update) {
        return this.update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final void updateOne(Update update) {
        this.updateOne(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass) {
        return this.returningUpdate(update, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final int[] batchUpdate(Update update) {
        return this.batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long largeUpdate(Update update) {
        return this.largeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long[] batchLargeUpdate(Update update) {
        return this.batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final void insert(Insert insert) {
        this.insert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final int subQueryInsert(Insert insert) {
        return this.subQueryInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long subQueryLargeInsert(Insert insert) {
        return this.subQueryLargeInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass) {
        return this.returningInsert(insert, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final int delete(Delete delete) {
        return this.delete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass) {
        return this.returningDelete(delete, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final int[] batchDelete(Delete delete) {
        return this.batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long largeDelete(Delete delete) {
        return this.largeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long[] batchLargeDelete(Delete delete) {
        return this.batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }
}
