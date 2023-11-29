package io.army.sync.executor;

@Deprecated
public interface SyncRmExecutorFactory extends SyncExecutorFactory {

    SyncRmStmtExecutor createRmStmtExecutor();


}
