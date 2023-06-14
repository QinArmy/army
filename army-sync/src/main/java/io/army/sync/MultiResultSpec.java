package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.DataAccessException;

public interface MultiResultSpec extends AutoCloseable {

    State hasMore();

    long nextUpdate() throws DataAccessException;

    @Nullable
    <R> R nextOne(Class<R> resultClass);


    @Override
    void close() throws DataAccessException;

    enum State {

        NONE,
        UPDATE,
        QUERY
    }


}
