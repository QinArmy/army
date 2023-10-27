package io.army.sync.executor;

import io.army.session.Session;

public interface SyncRmStmtExecutor extends SyncStmtExecutor,
        SyncStmtExecutor.XaTransactionSpec,
        Session.XaTransactionSupportSpec {


}
