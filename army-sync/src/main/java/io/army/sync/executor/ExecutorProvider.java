package io.army.sync.executor;

import io.army.session.DataAccessException;
import io.army.session.UnsupportedDataSourceTypeException;

public interface ExecutorProvider {

    /**
     * @param dataSource
     * @param info
     * @return
     * @throws DataAccessException
     * @throws UnsupportedDataSourceTypeException
     */
    ExecutorFactory createFactory(Object dataSource, FactoryInfo info);

}
