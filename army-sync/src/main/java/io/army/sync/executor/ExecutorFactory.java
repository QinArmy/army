package io.army.sync.executor;


public interface ExecutorFactory {

    MetaExecutor createMetaExecutor()throws Exception;

    SqlExecutor createSqlExecutor()throws Exception;


}
