package io.army.sync.executor;


import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;

public interface ExecutorFactory {

    /**
     * @return always same instance.
     */
    ServerMeta serverMeta();

    boolean supportSavePoints();

    MetaExecutor createMetaExecutor() throws DataAccessException;

    StmtExecutor createStmtExecutor() throws DataAccessException;

    void close() throws DataAccessException;

}
