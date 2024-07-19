package io.army.util;

import io.army.meta.TableMeta;
import io.army.sync.SyncSession;

public abstract class SyncCriteria {

    private SyncCriteria() {
        throw new UnsupportedOperationException();
    }

    public static <T> long rowCountOf(TableMeta<T> table, SyncSession session) {
        return session.queryOne(SQLStmts.rowCountStmtOf(table), Long.class);
    }


}
