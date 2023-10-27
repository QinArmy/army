package io.army.reactive;

import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.session._ArmyFactoryBuilder;

abstract class ArmyReactiveFactorBuilder extends _ArmyFactoryBuilder {


    ReactiveStmtExecutorFactory stmtExecutorFactory;

}
