package io.army.boot;

import io.army.SessionFactory;
import io.army.wrapper.SelectSQLWrapper;

import java.util.List;

interface SelectSQLExecutor extends SQLExecutor {

    <T> List<T> select(InnerSession session, SelectSQLWrapper wrapper, Class<T> resultClass);


    static SelectSQLExecutor build(SessionFactory sessionFactory) {
        return new SelectSQLExecutorImpl(sessionFactory);
    }
}
