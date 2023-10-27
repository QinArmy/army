package io.army.sync;

import io.army.session.FactoryBuilderSupport;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;
import io.army.sync.executor.SyncStmtExecutorFactory;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class ArmySyncSessionFactory extends _ArmySessionFactory implements SyncSessionFactory {

    private static final AtomicIntegerFieldUpdater<ArmySyncSessionFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmySyncSessionFactory.class, "factoryClosed");

    private SyncStmtExecutorFactory stmtExecutorFactory;

    private volatile int factoryClosed;

    ArmySyncSessionFactory(FactoryBuilderSupport<?, ?> support) throws SessionFactoryException {
        super(support);
    }

    @Override
    public final String driverSpiVendor() {
        return null;
    }

    @Override
    public final boolean isReactive() {
        // always false
        return false;
    }


    @Override
    public final boolean isClosed() {
        return this.factoryClosed != 0;
    }

    @Override
    public final void close() throws SessionFactoryException {
        if (FACTORY_CLOSED.compareAndSet(this, 0, 1)) {
            this.stmtExecutorFactory.close();
        }
    }


}
