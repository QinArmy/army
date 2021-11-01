package io.army.sync.executor;


public interface ExecutorFactory {

    MetaExecutor createMetaExecutor()throws Exception;

    StmtExecutor createSqlExecutor()throws Exception;


}
