package io.army.sync.executor;

import io.army.session.Session;

import java.util.function.Function;

/**
 * <p>The instance of this interface is created by {@link SyncExecutorFactory#rmExecutor(String, boolean, Function)}.
 *
 * @since 0.6.0
 */
public interface SyncRmStmtExecutor extends SyncExecutor,
        SyncExecutor.XaTransactionSpec,
        Session.XaTransactionSupportSpec {


}
