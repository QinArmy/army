package io.army.reactive;

import io.army.env.ArmyEnvironment;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.reactive.executor.ReactiveStmtExecutor;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.session.SessionException;
import io.army.session.SessionFactoryException;
import io.army.session._ArmySession;
import io.army.session._ArmySessionFactory;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * This class is a implementation of {@link ReactiveSessionFactory}
 *
 * @see ArmyReactiveLocalSession
 * @see ArmyReactiveRmSession
 * @since 1.0
 */
final class ArmyReactiveSessionFactory extends _ArmySessionFactory implements ReactiveSessionFactory {


    /**
     * @see ArmyReactiveFactorBuilder#buildAfterScanTableMeta(String, Object, ArmyEnvironment)
     */
    static ArmyReactiveSessionFactory create(ArmyReactiveFactorBuilder builder) {
        return new ArmyReactiveSessionFactory(builder);
    }


    private static final AtomicIntegerFieldUpdater<ArmyReactiveSessionFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmyReactiveSessionFactory.class, "factoryClosed");

    final ReactiveStmtExecutorFactory stmtExecutorFactory;


    private volatile int factoryClosed;

    /**
     * private constructor
     */
    private ArmyReactiveSessionFactory(ArmyReactiveFactorBuilder builder) throws SessionFactoryException {
        super(builder);
        this.stmtExecutorFactory = builder.stmtExecutorFactory;
        assert this.stmtExecutorFactory != null;
    }

    @Override
    public String driverSpiVendor() {
        return this.stmtExecutorFactory.driverSpiVendor();
    }

    @Override
    public boolean isReactive() {
        //always true
        return true;
    }

    @Override
    public Mono<ReactiveLocalSession> localSession() {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionFactoryClosed(this));
        }
        return new ArmyLocalBuilder(this).build();
    }

    @Override
    public Mono<ReactiveRmSession> rmSession() {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionFactoryClosed(this));
        }
        return new ArmyRmBuilder(this).build();
    }

    @Override
    public LocalSessionBuilder localBuilder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new ArmyLocalBuilder(this);
    }

    @Override
    public RmSessionBuilder rmBuilder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new ArmyRmBuilder(this);
    }

    @Override
    public boolean isClosed() {
        return this.factoryClosed != 0;
    }

    @Override
    public <T> Mono<T> close() {
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

        private ReactiveSessionBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


    }//ReactiveSessionBuilder


    static final class ArmyLocalBuilder extends ReactiveSessionBuilder<LocalSessionBuilder, Mono<ReactiveLocalSession>>
            implements LocalSessionBuilder {

        /**
         * private constructor
         */
        private ArmyLocalBuilder(ArmyReactiveSessionFactory sessionFactory) {
            super(sessionFactory);
        }

        @Override
        protected Mono<ReactiveLocalSession> createSession(String sessionName, boolean readonly) {
            return ((ArmyReactiveSessionFactory) this.factory).stmtExecutorFactory
                    .localExecutor(sessionName, readonly)
                    .map(this::createLocalSession)
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }


        @Override
        protected Mono<ReactiveLocalSession> handleError(SessionException cause) {
            return Mono.error(cause);
        }

        private ReactiveLocalSession createLocalSession(final ReactiveLocalStmtExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return ArmyReactiveLocalSession.create(this);
        }

    } // ArmyLocalBuilder


    static final class ArmyRmBuilder extends ReactiveSessionBuilder<RmSessionBuilder, Mono<ReactiveRmSession>>
            implements RmSessionBuilder {

        /**
         * private constructor
         */
        private ArmyRmBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


        @Override
        protected Mono<ReactiveRmSession> createSession(String sessionName, boolean readonly) {
            return ((ArmyReactiveSessionFactory) this.factory).stmtExecutorFactory
                    .rmExecutor(sessionName, readonly)
                    .map(this::createRmSession)
                    .onErrorMap(_ArmySession::wrapIfNeed);
        }

        @Override
        protected Mono<ReactiveRmSession> handleError(SessionException cause) {
            return Mono.error(cause);
        }

        private ReactiveRmSession createRmSession(final ReactiveRmStmtExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return ArmyReactiveRmSession.create(this);
        }


    } // ArmyRmBuilder

}
