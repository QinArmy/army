package io.army.sync.executor;

import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.executor.StmtExecutorFactoryProviderSpec;

/**
 * <p>
 * This interface representing provider of bloc executor.
 * This implementation of this interface must declared :
 * <pre>
 *      <code>
 *          public static {implementation class of ExecutorProvider} create(Object){
 *
 *          }
 *      </code>
 *  </pre>
 * </p>
 */
public interface SyncStmtExecutorFactoryProvider extends StmtExecutorFactoryProviderSpec {

    @Override
    ServerMeta createServerMeta(Dialect usedDialect) throws DataAccessException;


    /**
     * @throws UnsupportedOperationException throw when support only creating{@link RmExecutorFactory }
     * @throws IllegalStateException         throw when invoke this method before {@link #createServerMeta()}
     * @throws IllegalArgumentException      throw when {@link  MappingEnv#serverMeta()} not match.
     */
    @Override
    LocalExecutorFactory createFactory(ExecutorEnv env);

    /**
     * @throws UnsupportedOperationException throw when support only creating{@link LocalExecutorFactory }
     * @throws IllegalStateException         throw when invoke this method before {@link #createServerMeta()}
     * @throws IllegalArgumentException      throw when {@link  MappingEnv#serverMeta()} not match.
     */
    @Override
    RmExecutorFactory createRmFactory(ExecutorEnv env);


}
