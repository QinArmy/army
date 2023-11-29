package io.army.sync.executor;


import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.session.executor.ExecutorFactory;

import java.util.function.Function;

/**
 * <p>This interface representing blocking {@link SyncExecutor} factory.
 *
 * @since 1.0
 */
public interface SyncExecutorFactory extends ExecutorFactory, AutoCloseable {


    @Override
    MetaExecutor metaExecutor(Function<Option<?>, ?> optionFunc) throws DataAccessException;

    @Override
    SyncLocalStmtExecutor localExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc) throws DataAccessException;

    @Override
    SyncRmStmtExecutor rmExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc) throws DataAccessException;

    /**
     * <p>
     * close {@link SyncExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     * </p>
     */
    void close() throws DataAccessException;

}
