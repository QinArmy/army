package io.army.reactive;

import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.session.FactoryBuilderSupport;

abstract class ArmyReactiveFactorBuilder extends FactoryBuilderSupport {


    ReactiveStmtExecutorFactory stmtExecutorFactory;

}
