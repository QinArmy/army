package io.army.sync.executor;


import io.army.session.DataAccessException;

public interface Executor extends AutoCloseable {


    @Override
    void close() throws DataAccessException;
}
