package io.army.sync.executor;


import io.army.session.DataAccessException;
import io.army.session.executor.StmtExecutorFactory;

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
public interface SyncExecutorFactory extends StmtExecutorFactory, AutoCloseable {


    MetaExecutor createMetaExecutor() throws DataAccessException;

    /**
     * <p>
     * close {@link SyncExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     * </p>
     */
    void close() throws DataAccessException;

}
