package io.army.boot;

import io.army.SessionFactory;

import java.util.List;

interface SelectSQLExecutor {

    <T> List<T> select(InnerSession session, SelectSQLWrapper wrapper, Class<T> resultClass);


    static SelectSQLExecutor build(SessionFactory sessionFactory) {
        return new SelectSQLExecutorImpl(sessionFactory);
    }
}
