package io.army.sync.executor;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.executor.StmtExecutorFactoryProviderSpec;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing provider of blocking executor.
 */
public interface SyncStmtExecutorFactoryProvider extends StmtExecutorFactoryProviderSpec {

    @Override
    ServerMeta createServerMeta(Dialect usedDialect, @Nullable Function<String, Database> func) throws DataAccessException;


    /**
     * @throws IllegalStateException    throw when invoke this method before {@link #createServerMeta(Dialect, Function)}
     * @throws IllegalArgumentException throw when {@link  MappingEnv#serverMeta()} not match.
     */
    @Override
    SyncStmtExecutorFactory createFactory(ExecutorEnv env) throws DataAccessException;



}
