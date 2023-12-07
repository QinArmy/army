package io.army.session.executor;

import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;

public abstract class ExecutorFactorySupport {

    final ArmyEnvironment armyEnv;

    final boolean sqlLogDynamic;

    final SqlLogMode sqlLogMode;

    protected ExecutorFactorySupport(ArmyEnvironment armyEnv) {
        this.armyEnv = armyEnv;
        this.sqlLogDynamic = armyEnv.getOrDefault(ArmyKey.SQL_LOG_DYNAMIC);
        this.sqlLogMode = armyEnv.getOrDefault(ArmyKey.SQL_LOG_MODE);
    }


}
