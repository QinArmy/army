package io.army.session;

import io.army.session.executor.StmtExecutor;

public interface StmtExecutorHolder {

    <T extends StmtExecutor> T getStmtExecutor(Class<T> typeClass);


}
