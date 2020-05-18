package io.army.boot;

import io.army.wrapper.BatchUpdateSQLWrapper;
import io.army.wrapper.UpdateSQLWrapper;

import java.util.List;

interface UpdateSQLExecutor {

    int update(InnerSession session, UpdateSQLWrapper sqlWrapper);

    <T> List<T> returningUpdate(InnerSession session, UpdateSQLWrapper sqlWrapper, Class<T> resultClass);

    List<Integer> batchUpdate(InnerSession session, BatchUpdateSQLWrapper sqlWrapper);

    static UpdateSQLExecutor build(InnerSessionFactory sessionFactory) {
        return UpdateSQLExecutorImpl.build(sessionFactory);
    }
}
