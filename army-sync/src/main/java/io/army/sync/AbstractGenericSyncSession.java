package io.army.sync;

import io.army.NonUniqueException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.AbstractGenericSession;

import java.util.List;

abstract class AbstractGenericSyncSession extends AbstractGenericSession implements GenericSyncSession {


    @Nullable
    @Override
    public final <R extends IDomain> R get(TableMeta<R> tableMeta, Object id) {
        return this.get(tableMeta, id, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue) {
        return this.getByUnique(tableMeta, fieldMeta, fieldValue, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass, final Visible visible) {
        List<R> list = select(select, resultClass, visible);
        R r;
        if (list.size() == 1) {
            r = list.get(0);
        } else if (list.size() == 0) {
            r = null;
        } else {
            throw new NonUniqueException("select result[%s] more than 1.", list.size());
        }
        return r;
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
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass) {
        return this.returningUpdate(update, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long largeUpdate(Update update) {
        return this.largeUpdate(update, Visible.ONLY_VISIBLE);
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
    public final long largeDelete(Delete delete) {
        return this.largeDelete(delete, Visible.ONLY_VISIBLE);
    }

}
