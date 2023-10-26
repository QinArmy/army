package io.army.sync.executor;

public interface RmExecutorFactory extends SyncExecutorFactory {

    RmStmtExecutor createRmStmtExecutor();


}
