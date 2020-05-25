package io.army.boot;

import io.army.wrapper.SQLWrapper;

import java.util.List;

interface UpdateSQLExecutor {

    int update(InnerSession session, SQLWrapper sqlWrapper);

    int[] batchUpdate(InnerSession session, SQLWrapper sqlWrapper);

    long updateLarge(InnerSession session, SQLWrapper sqlWrapper);

    long[] batchUpdateLarge(InnerSession session, SQLWrapper sqlWrapper);

    <T> List<T> returningUpdate(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass);

    static UpdateSQLExecutor build(InnerSessionFactory sessionFactory) {
        return UpdateSQLExecutorImpl.build(sessionFactory);
    }
}
