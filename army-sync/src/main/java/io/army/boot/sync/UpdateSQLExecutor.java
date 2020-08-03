package io.army.boot.sync;

import io.army.wrapper.SQLWrapper;

import java.util.List;

interface UpdateSQLExecutor {

    int update(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement);

    long largeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement);

    int[] batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement);

    long[] batchLargeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, boolean updateStatement);

    <T> List<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass
            , boolean updateStatement);

    static UpdateSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }
}
