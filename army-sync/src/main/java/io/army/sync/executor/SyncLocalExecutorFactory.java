package io.army.sync.executor;

import io.army.session.DataAccessException;

@Deprecated
public interface SyncLocalExecutorFactory extends SyncExecutorFactory {

    SyncLocalStmtExecutor createLocalStmtExecutor() throws DataAccessException;

}
