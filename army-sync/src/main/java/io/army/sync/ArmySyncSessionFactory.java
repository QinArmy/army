package io.army.sync;

import io.army.env.SyncKey;
import io.army.session.Option;
import io.army.session.SessionException;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;
import io.army.sync.executor.SyncExecutor;
import io.army.sync.executor.SyncExecutorFactory;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link SyncSessionFactory}.
 *
 * @since 0.6.0
 */
final class ArmySyncSessionFactory extends _ArmySessionFactory implements SyncSessionFactory {

    static ArmySyncSessionFactory create(ArmySyncFactoryBuilder builder) {
        return new ArmySyncSessionFactory(builder);
    }

    private static final AtomicIntegerFieldUpdater<ArmySyncSessionFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmySyncSessionFactory.class, "factoryClosed");

    final SyncExecutorFactory stmtExecutorFactory;

    final boolean sessionIdentifierEnable;

    final boolean buildInExecutor;
    final boolean jdbcDriver;
    private volatile int factoryClosed;

    /**
     * private constructor
     */
    private ArmySyncSessionFactory(ArmySyncFactoryBuilder builder) throws SessionFactoryException {
        super(builder);
        this.stmtExecutorFactory = builder.stmtExecutorFactory;
        assert this.stmtExecutorFactory != null;
        this.sessionIdentifierEnable = this.env.getOrDefault(SyncKey.SESSION_IDENTIFIER_ENABLE);
        this.buildInExecutor = this.stmtExecutorFactory.getClass().getPackage().getName().startsWith("io.army.jdbc.");
        this.jdbcDriver = this.buildInExecutor || this.stmtExecutorFactory.driverSpiVendor().equals("java.sql");
    }

    @Override
    public String driverSpiVendor() {
        return this.stmtExecutorFactory.driverSpiVendor();
    }

    @Override
    public boolean isReactive() {
        // always false
        return false;
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public SyncLocalSession localSession() {
        return localBuilder().build();
    }

    @Override
    public SyncLocalSession localSession(@Nullable String name, boolean readOnly) {
        return localBuilder()
                .name(name)
                .readonly(readOnly)
                .build();
    }

    @Override
    public SyncRmSession rmSession() {
        return rmBuilder().build();
    }

    @Override
    public SyncRmSession rmSession(@Nullable String name, boolean readOnly) {
        return rmBuilder()
                .name(name)
                .readonly(readOnly)
                .build();
    }


    @Override
    public LocalSessionBuilder localBuilder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new LocalBuilder(this);
    }

    @Override
    public RmSessionBuilder rmBuilder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new RmBuilder(this);
    }

    @Override
    public boolean isClosed() {
        return this.factoryClosed != 0;
    }

    @Override
    public void close() throws SessionFactoryException {
        if (!FACTORY_CLOSED.compareAndSet(this, 0, 1)) {
            return;
        }
        try {
            this.stmtExecutorFactory.close();
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    static abstract class SyncBuilder<B, R> extends ArmySessionBuilder<B, R> {

        SyncExecutor stmtExecutor;

        SyncBuilder(ArmySyncSessionFactory factory) {
            super(factory);
        }


        @Override
        protected final R handleError(SessionException cause) {
            throw cause;
        }


    } // SyncBuilder


    static final class LocalBuilder extends SyncBuilder<LocalSessionBuilder, SyncLocalSession>
            implements LocalSessionBuilder {

        private LocalBuilder(ArmySyncSessionFactory factory) {
            super(factory);
        }


        @Override
        protected SyncLocalSession createSession(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc) {
            this.stmtExecutor = ((ArmySyncSessionFactory) this.factory)
                    .stmtExecutorFactory.localExecutor(sessionName, readOnly, optionFunc);
            return ArmySyncLocalSession.create(this);
        }


    } // LocalBuilder

    static final class RmBuilder extends SyncBuilder<RmSessionBuilder, SyncRmSession>
            implements RmSessionBuilder {

        private RmBuilder(ArmySyncSessionFactory factory) {
            super(factory);
        }


        @Override
        protected SyncRmSession createSession(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc) {
            this.stmtExecutor = ((ArmySyncSessionFactory) this.factory).stmtExecutorFactory
                    .rmExecutor(sessionName, readOnly, optionFunc);
            return ArmySyncRmSession.create(this);
        }


    } // SyncRmSessionBuilder


}
