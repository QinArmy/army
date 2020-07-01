package io.army.boot;

import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

interface SelectSQLExecutor {

    <T> List<T> select(InnerSession session, SimpleSQLWrapper wrapper, Class<T> resultClass);

    static SelectSQLExecutor build(InnerSyncSessionFactory sessionFactory) {
        return new SelectSQLExecutorImpl(sessionFactory);
    }
}
