package io.army.sync.executor;

@Deprecated
public interface SyncRmExecutorFactory extends SyncStmtExecutorFactory {

    SyncRmStmtExecutor createRmStmtExecutor();


}
