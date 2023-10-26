package io.army.sync.executor;


import io.army.session.DataAccessException;
import io.army.session.executor.StmtExecutorFactorySpec;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link LocalExecutorFactory}</li>
 *         <li>{@link RmExecutorFactory}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SyncExecutorFactory extends StmtExecutorFactorySpec, AutoCloseable {


    MetaExecutor createMetaExecutor() throws DataAccessException;


    @Override
    MetaExecutor metaExecutor(String name);

    @Override
    LocalStmtExecutor localExecutor(String name);

    @Override
    RmStmtExecutor rmExecutor(String name);

    /**
     * <p>
     * close {@link SyncExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     * </p>
     */
    void close() throws DataAccessException;

}
