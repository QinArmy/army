package io.army.sync.executor;

import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;

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
public interface ExecutorProvider {

    ServerMeta createServerMeta() throws DataAccessException;


    /**
     * @throws UnsupportedOperationException throw when support only creating{@link RmExecutorFactory }
     * @throws IllegalStateException         throw when invoke this method before {@link #createServerMeta()}
     * @throws IllegalArgumentException      throw when {@link  MappingEnv#serverMeta()} not match.
     */
    LocalExecutorFactory createLocalFactory(ExecutorEnvironment env);

    /**
     * @throws UnsupportedOperationException throw when support only creating{@link LocalExecutorFactory }
     * @throws IllegalStateException         throw when invoke this method before {@link #createServerMeta()}
     * @throws IllegalArgumentException      throw when {@link  MappingEnv#serverMeta()} not match.
     */
    RmExecutorFactory createRmFactory(ExecutorEnvironment env);


}
