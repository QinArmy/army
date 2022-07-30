package io.army.sync.executor;

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
     * @throws UnsupportedOperationException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    LocalExecutorFactory createLocalFactory(ExecutorEnvironment env);

    RmExecutorFactory createRmFactory(ExecutorEnvironment env);


}
