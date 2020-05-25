package io.army.boot;

import io.army.dialect.InsertException;
import io.army.wrapper.SQLWrapper;

import java.util.List;

/**
 *
 */
interface InsertSQLExecutor {


    void insert(InnerSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    <T> List<T> returningInsert(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException;

    static InsertSQLExecutor build(InnerSessionFactory sessionFactory) {
        return new InsertSQLExecutorIml(sessionFactory);
    }

}
