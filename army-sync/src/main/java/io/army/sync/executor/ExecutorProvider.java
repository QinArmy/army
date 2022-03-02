package io.army.sync.executor;

import io.army.session.DataAccessException;
import io.army.session.UnsupportedDataSourceTypeException;

/**
 * <p>
 * This interface representing provider of bloc executor.
 * This implementation of this interface must declared :
 * <pre>
 *      <code>
 *          public static {implementation class of ExecutorProvider} getInstance(){
 *
 *          }
 *      </code>
 *  </pre>
 * </p>
 */
public interface ExecutorProvider {

    /**
     * @param dataSource
     * @param info
     * @return
     * @throws DataAccessException
     * @throws UnsupportedDataSourceTypeException
     */
    ExecutorFactory createFactory(Object dataSource, FactoryInfo info);

    ExecutorFactory createXaFactory(Object dataSource, FactoryInfo info);

}
