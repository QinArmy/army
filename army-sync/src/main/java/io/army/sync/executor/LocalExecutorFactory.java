package io.army.sync.executor;

import io.army.session.DataAccessException;

public interface LocalExecutorFactory extends ExecutorFactory {

    LocalStmtExecutor createStmtExecutor() throws DataAccessException;

}
