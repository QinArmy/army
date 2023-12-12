package io.army.sync.executor;

import java.util.function.Function;

/**
 * <p>The instance of this interface is created by {@link SyncExecutorFactory#localExecutor(String, boolean, Function)}.
 *
 * @since 1.0
 */
public interface SyncLocalStmtExecutor extends SyncExecutor, SyncExecutor.LocalTransactionSpec {


}
