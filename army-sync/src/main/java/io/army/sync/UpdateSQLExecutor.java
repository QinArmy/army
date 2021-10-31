package io.army.sync;

import io.army.stmt.Stmt;

import java.util.List;

interface UpdateSQLExecutor {

    int update(InnerGenericRmSession session, Stmt stmt);

    long largeUpdate(InnerGenericRmSession session, Stmt stmt);

    List<Integer> batchUpdate(InnerGenericRmSession session, Stmt stmt);

    List<Long> batchLargeUpdate(InnerGenericRmSession session, Stmt stmt);

    <T> List<T> returningUpdate(InnerGenericRmSession session, Stmt stmt, Class<T> resultClass);

    static UpdateSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }
}
