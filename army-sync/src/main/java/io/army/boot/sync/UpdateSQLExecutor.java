package io.army.boot.sync;

import io.army.wrapper.BatchSimpleSQLWrapper;
import io.army.wrapper.SQLWrapper;

import java.util.List;
import java.util.Map;

interface UpdateSQLExecutor {

    int update(InnerSession session, SQLWrapper sqlWrapper, boolean updateStatement);

    long largeUpdate(InnerSession session, SQLWrapper sqlWrapper, boolean updateStatement);

    /**
     * @param <V> return map's value java type.
     * @return a unmodifiable map, key : key of {@linkplain BatchSimpleSQLWrapper#paramGroupMap()}
     * * ,value : batch update rows of named param.
     */
    <V extends Number> Map<Integer, V> batchUpdate(InnerSession session, SQLWrapper sqlWrapper
            , Class<V> mapValueClass, boolean updateStatement);

    <T> List<T> returningUpdate(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass
            , boolean updateStatement);

    static UpdateSQLExecutor build(InnerSyncSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }
}
