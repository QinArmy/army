package io.army.sync.executor;

import io.army.session.DataAccessException;

public interface LocalExecutorFactory extends SyncExecutorFactory {

    SyncLocalStmtExecutor createLocalStmtExecutor() throws DataAccessException;

}
