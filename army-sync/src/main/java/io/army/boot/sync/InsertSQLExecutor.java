package io.army.boot.sync;

import io.army.dialect.InsertException;
import io.army.wrapper.SQLWrapper;

import java.util.List;

/**
 *
 */
interface InsertSQLExecutor {


    void valueInsert(InnerSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    int subQueryInsert(InnerSession session, SQLWrapper sqlWrapper) throws InsertException;

    long subQueryLargeInsert(InnerSession session, SQLWrapper sqlWrapper) throws InsertException;

    <T> List<T> returningInsert(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException;

    static InsertSQLExecutor build(InnerSyncSessionFactory sessionFactory) {
        return new InsertSQLExecutorIml(sessionFactory);
    }

}
