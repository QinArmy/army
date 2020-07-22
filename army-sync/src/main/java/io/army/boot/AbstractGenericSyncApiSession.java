package io.army.boot;


import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.sync.GenericSyncApiSession;

import java.util.List;
import java.util.Map;

/**
 *
 */
abstract class AbstractGenericSyncApiSession extends AbstractGenericSyncSession implements GenericSyncApiSession {


    @Override
    public final Map<Integer, Integer> batchUpdate(Update update) {
        return this.batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Map<Integer, Long> batchLargeUpdate(Update update) {
        return this.batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    public final void valueInsert(Insert insert) {
        this.valueInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass) {
        return this.returningInsert(insert, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Map<Integer, Integer> batchDelete(Delete delete) {
        return this.batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Map<Integer, Long> batchLargeDelete(Delete delete) {
        return this.batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }
}
