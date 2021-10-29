package io.army.sync;

import io.army.wrapper.SQLWrapper;

import java.util.List;

interface UpdateSQLExecutor {

    int update(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    long largeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    List<Integer> batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    List<Long> batchLargeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    <T> List<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass);

    static UpdateSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }
}
