package io.army.reactive;

import io.army.reactive.executor.ReactiveStmtExecutor;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class ArmyReactiveSessionFactory extends _ArmySessionFactory implements ReactiveSessionFactory {


    private static final AtomicIntegerFieldUpdater<ArmyReactiveSessionFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveSessionFactory.class, "factoryClosed");

    final ReactiveStmtExecutorFactory stmtExecutorFactory;


    private volatile int factoryClosed;

    ArmyReactiveSessionFactory(ArmyReactiveFactorBuilder builder) throws SessionFactoryException {
        super(builder);
        this.stmtExecutorFactory = builder.stmtExecutorFactory;
        assert this.stmtExecutorFactory != null;
    }

    @Override
    public final String driverSpiVendor() {
        return this.stmtExecutorFactory.driverSpiVendor();
    }

    @Override
    public final boolean isReactive() {
        //always true
        return true;
    }

    @Override
    public final boolean isClosed() {
        return this.factoryClosed != 0;
    }

    @Override
    public final <T> Mono<T> close() {
        return Mono.defer(this::closeFactory);
    }


    private <T> Mono<T> closeFactory() {
        if (FACTORY_CLOSED.compareAndSet(this, 0, 1)) {
            return this.stmtExecutorFactory.close();
        }
        return Mono.empty();
    }

    static abstract class ReactiveSessionBuilder<B, R> extends ArmySessionBuilder<B, R> {

        ReactiveStmtExecutor stmtExecutor;

        ReactiveSessionBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


    }//ReactiveSessionBuilder

}
