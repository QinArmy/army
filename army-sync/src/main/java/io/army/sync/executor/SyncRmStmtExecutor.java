package io.army.sync.executor;

import io.army.session.Session;

/**
 * <p>The instance of this interface is created by {@link SyncStmtExecutorFactory#rmExecutor(String)} )}.
 *
 * @since 1.0
 */
public interface SyncRmStmtExecutor extends SyncStmtExecutor,
        SyncStmtExecutor.XaTransactionSpec,
        Session.XaTransactionSupportSpec {


}
