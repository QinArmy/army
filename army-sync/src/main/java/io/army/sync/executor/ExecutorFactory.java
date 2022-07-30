package io.army.sync.executor;


import io.army.session.DataAccessException;

public interface ExecutorFactory {


    boolean supportSavePoints();

    MetaExecutor createMetaExecutor() throws DataAccessException;


    void close() throws DataAccessException;

}
