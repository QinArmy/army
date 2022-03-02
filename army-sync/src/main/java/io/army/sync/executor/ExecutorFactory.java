package io.army.sync.executor;


import io.army.meta.ServerMeta;

public interface ExecutorFactory {

    /**
     * @return always same instance.
     */
    ServerMeta serverMeta();

    MetaExecutor createMetaExecutor();

    StmtExecutor createStmtExecutor();


}
