package io.army.sync.executor;


import io.army.session.DataAccessException;
import io.army.session.executor.StmtExecutorFactory;

/**
 * <p>This interface representing blocking {@link SyncStmtExecutor} factory.
 *
 * @since 1.0
 */
public interface SyncStmtExecutorFactory extends StmtExecutorFactory, AutoCloseable {


    @Override
    MetaExecutor metaExecutor() throws DataAccessException;

    @Override
    SyncLocalStmtExecutor localExecutor(String sessionName, boolean readOnly) throws DataAccessException;

    @Override
    SyncRmStmtExecutor rmExecutor(String sessionName, boolean readOnly) throws DataAccessException;

    /**
     * <p>
     * close {@link SyncStmtExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     * </p>
     */
    void close() throws DataAccessException;

}
