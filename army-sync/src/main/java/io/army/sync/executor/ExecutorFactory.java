package io.army.sync.executor;


import io.army.session.DataAccessException;

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
public interface ExecutorFactory {


    /**
     * @return true : underlying database driver provider save point api.
     */
    boolean supportSavePoints();

    MetaExecutor createMetaExecutor() throws DataAccessException;

    /**
     * <p>
     * close {@link ExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     * </p>
     */
    void close() throws DataAccessException;

}
