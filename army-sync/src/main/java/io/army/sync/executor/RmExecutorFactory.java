package io.army.sync.executor;

public interface RmExecutorFactory extends ExecutorFactory {

    RmStmtExecutor createStmtExecutor();


}
