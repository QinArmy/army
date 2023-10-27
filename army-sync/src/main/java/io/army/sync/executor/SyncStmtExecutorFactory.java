package io.army.sync.executor;


import io.army.session.DataAccessException;
import io.army.session.executor.StmtExecutorFactorySpec;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link SyncLocalExecutorFactory}</li>
 *         <li>{@link SyncRmExecutorFactory}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SyncStmtExecutorFactory extends StmtExecutorFactorySpec, AutoCloseable {


    MetaExecutor createMetaExecutor() throws DataAccessException;


    @Override
    MetaExecutor metaExecutor(String name) throws DataAccessException;

    @Override
    SyncLocalStmtExecutor localExecutor(String name) throws DataAccessException;

    @Override
    SyncRmStmtExecutor rmExecutor(String name) throws DataAccessException;

    /**
     * <p>
     * close {@link SyncStmtExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     * </p>
     */
    void close() throws DataAccessException;

}
