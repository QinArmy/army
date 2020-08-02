package io.army.boot.sync;

import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

interface SelectSQLExecutor {

    <T> List<T> select(InnerGenericRmSession session, SimpleSQLWrapper wrapper, Class<T> resultClass);

    static SelectSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new SelectSQLExecutorImpl(sessionFactory);
    }
}
