package io.army.sync.executor;

public interface SyncRmExecutorFactory extends SyncStmtExecutorFactory {

    SyncRmStmtExecutor createRmStmtExecutor();


}
