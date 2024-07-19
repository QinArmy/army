package io.army.util;

import io.army.meta.TableMeta;
import io.army.sync.SyncSession;

public abstract class SyncCriteria {

    private SyncCriteria() {
        throw new UnsupportedOperationException();
    }


    public static <T> long rowCount(Class<T> domainClass, SyncSession session) {
        return rowCountOf(session.tableMeta(domainClass), session);
    }

    public static <T> long rowCountOf(TableMeta<T> table, SyncSession session) {
        return session.queryOne(SQLStmts.rowCountStmtOf(table), Long.class);
    }

    public static <T> int rowCountAsInt(Class<T> domainClass, SyncSession session) {
        return rowCountAsIntOf(session.tableMeta(domainClass), session);
    }

    public static <T> int rowCountAsIntOf(TableMeta<T> table, SyncSession session) {
        return session.queryOne(SQLStmts.rowCountStmtOf(table), Integer.class);
    }


}
