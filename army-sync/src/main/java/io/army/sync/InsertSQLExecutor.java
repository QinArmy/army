package io.army.sync;

import io.army.dialect.InsertException;
import io.army.wrapper.SQLWrapper;

import java.util.List;

/**
 *
 */
interface InsertSQLExecutor {


    void valueInsert(InnerGenericRmSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    int subQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException;

    long subQueryLargeInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException;

    <T> List<T> returningInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException;

    static InsertSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new InsertSQLExecutorIml(sessionFactory);
    }

}
