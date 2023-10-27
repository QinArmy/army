package io.army.sync;

import io.army.session._ArmyFactoryBuilder;
import io.army.sync.executor.SyncStmtExecutorFactory;

abstract class ArmySyncFactoryBuilder<B, R> extends _ArmyFactoryBuilder<B, R> {

    SyncStmtExecutorFactory stmtExecutorFactory;

    ArmySyncFactoryBuilder() {
    }


}
