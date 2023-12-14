package io.army.session.executor;

import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;

public abstract class ExecutorFactorySupport {

    final ArmyEnvironment armyEnv;

    public final boolean sqlLogDynamic;

    public final SqlLogMode sqlLogMode;

    protected ExecutorFactorySupport(ArmyEnvironment armyEnv) {
        this.armyEnv = armyEnv;
        this.sqlLogDynamic = armyEnv.getOrDefault(ArmyKey.SQL_LOG_DYNAMIC);
        this.sqlLogMode = armyEnv.getOrDefault(ArmyKey.SQL_LOG_MODE);
    }


}
