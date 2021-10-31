package io.army.sync;

import io.army.stmt.SimpleStmt;

import java.util.List;

interface SelectSQLExecutor {

    <T> List<T> select(InnerGenericRmSession session, SimpleStmt wrapper, Class<T> resultClass);

    static SelectSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new SelectSQLExecutorImpl(sessionFactory);
    }
}
