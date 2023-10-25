package io.army.reactive;

import io.army.reactive.executor.StmtExecutor;
import io.army.reactive.executor.StmtExecutorFactory;
import io.army.session.DriverSpi;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;

abstract class ArmyReactiveSessionFactory extends _ArmySessionFactory implements ReactiveSessionFactory {

    final StmtExecutorFactory stmtExecutorFactory;

    final DriverSpi driverSpi;

    ArmyReactiveSessionFactory(ArmyReactiveFactorBuilder builder) throws SessionFactoryException {
        super(builder);
        this.stmtExecutorFactory = builder.stmtExecutorFactory;
        assert this.stmtExecutorFactory != null;
        this.driverSpi = DriverSpi.from(this.stmtExecutorFactory.driverSpiVendor());
    }


    static abstract class ReactiveSessionBuilder<B, R> extends ArmySessionBuilder<B, R> {

        StmtExecutor stmtExecutor;

        ReactiveSessionBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


    }//ReactiveSessionBuilder

}
