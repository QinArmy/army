package io.army.reactive;

import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.reactive.executor.StmtExecutor;
import io.army.session.DriverSpi;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class ArmyReactiveSessionFactory extends _ArmySessionFactory implements ReactiveSessionFactory {

    final ReactiveStmtExecutorFactory stmtExecutorFactory;

    final DriverSpi driverSpi;

    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    ArmyReactiveSessionFactory(ArmyReactiveFactorBuilder builder) throws SessionFactoryException {
        super(builder);
        this.stmtExecutorFactory = builder.stmtExecutorFactory;
        assert this.stmtExecutorFactory != null;
        this.driverSpi = DriverSpi.from(this.stmtExecutorFactory.driverSpiVendor());
    }


    @Override
    public final boolean isReactive() {
        //always true
        return true;
    }

    @Override
    public final boolean isClosed() {
        return this.factoryClosed.get();
    }

    @Override
    public final <T> Mono<T> close() {
        return Mono.defer(this::closeFactory);
    }


    private <T> Mono<T> closeFactory() {
        if (!this.factoryClosed.compareAndSet(false, true)) {
            return Mono.empty();
        }
        return this.stmtExecutorFactory.close();
    }

    static abstract class ReactiveSessionBuilder<B, R> extends ArmySessionBuilder<B, R> {

        StmtExecutor stmtExecutor;

        ReactiveSessionBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


    }//ReactiveSessionBuilder

}
