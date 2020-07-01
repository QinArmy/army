package io.army.boot;

import io.army.wrapper.SQLWrapper;

import java.util.List;

interface UpdateSQLExecutor {

    int update(InnerSession session, SQLWrapper sqlWrapper);

    int[] batchUpdate(InnerSession session, SQLWrapper sqlWrapper);

    long largeUpdate(InnerSession session, SQLWrapper sqlWrapper);

    long[] batchLargeUpdate(InnerSession session, SQLWrapper sqlWrapper);

    <T> List<T> returningUpdate(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass);

    static UpdateSQLExecutor build(InnerSyncSessionFactory sessionFactory) {
        return UpdateSQLExecutorImpl.build(sessionFactory);
    }
}
