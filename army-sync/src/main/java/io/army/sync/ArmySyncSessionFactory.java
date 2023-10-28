package io.army.sync;

import io.army.session.SessionException;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;
import io.army.sync.executor.SyncStmtExecutor;
import io.army.sync.executor.SyncStmtExecutorFactory;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * <p>This class is a implementation of {@link SyncSessionFactory}.
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmySyncLocalSessionFactory}</li>
 *     <li>{@link ArmySyncRmSessionFactory}</li>
 * </ul>
 *
 * @since 1.0
 */
abstract class ArmySyncSessionFactory extends _ArmySessionFactory implements SyncSessionFactory {

    private static final AtomicIntegerFieldUpdater<ArmySyncSessionFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmySyncSessionFactory.class, "factoryClosed");

    final SyncStmtExecutorFactory stmtExecutorFactory;

    final boolean buildInExecutor;
    final boolean jdbcDriver;

    private volatile int factoryClosed;

    ArmySyncSessionFactory(ArmySyncFactoryBuilder<?, ?> builder) throws SessionFactoryException {
        super(builder);
        this.stmtExecutorFactory = builder.stmtExecutorFactory;
        assert this.stmtExecutorFactory != null;
        this.buildInExecutor = this.stmtExecutorFactory.getClass().getPackage().getName().startsWith("io.army.jdbc.");
        this.jdbcDriver = this.buildInExecutor || this.stmtExecutorFactory.driverSpiVendor().equals("java.sql");
    }

    @Override
    public final String driverSpiVendor() {
        return this.stmtExecutorFactory.driverSpiVendor();
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
        if (!FACTORY_CLOSED.compareAndSet(this, 0, 1)) {
            return;
        }
        try {
            this.stmtExecutorFactory.close();
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    static abstract class SyncSessionBuilder<B, R> extends ArmySessionBuilder<B, R> {

        SyncStmtExecutor stmtExecutor;

        SyncSessionBuilder(ArmySyncSessionFactory factory) {
            super(factory);
        }


        @Override
        protected final R handleError(SessionException cause) {
            throw cause;
        }


    } // SyncSessionBuilder


}
