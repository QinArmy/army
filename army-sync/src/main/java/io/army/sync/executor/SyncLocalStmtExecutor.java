package io.army.sync.executor;

import java.util.function.Function;

/**
 * <p>The instance of this interface is created by {@link SyncExecutorFactory#localExecutor(String, boolean, Function)}.
 *
 * @since 0.6.0
 */
public interface SyncLocalStmtExecutor extends SyncExecutor, SyncExecutor.LocalTransactionSpec {


}
