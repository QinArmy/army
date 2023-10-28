package io.army.sync.executor;

import io.army.session.DataAccessException;

@Deprecated
public interface SyncLocalExecutorFactory extends SyncStmtExecutorFactory {

    SyncLocalStmtExecutor createLocalStmtExecutor() throws DataAccessException;

}
