package io.army.sync;

import io.army.dialect.InsertException;
import io.army.stmt.Stmt;

import java.util.List;

/**
 *
 */
interface InsertSQLExecutor {


    void valueInsert(InnerGenericRmSession session, List<Stmt> stmtList) throws InsertException;

    int subQueryInsert(InnerGenericRmSession session, Stmt stmt) throws InsertException;

    long subQueryLargeInsert(InnerGenericRmSession session, Stmt stmt) throws InsertException;

    <T> List<T> returningInsert(InnerGenericRmSession session, Stmt stmt, Class<T> resultClass)
            throws InsertException;

    static InsertSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new InsertSQLExecutorIml(sessionFactory);
    }

}
