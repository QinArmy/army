package io.army.reactive;

import io.army.env.ArmyEnvironment;
import io.army.reactive.executor.ReactiveExecutorFactory;
import io.army.reactive.executor.ReactiveLocalExecutor;
import io.army.reactive.executor.ReactiveRmExecutor;
import io.army.reactive.executor.ReactiveStmtExecutor;
import io.army.session.*;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;

/**
 * This class is a implementation of {@link ReactiveSessionFactory}
 *
 * @see ArmyReactiveLocalSession
 * @see ArmyReactiveRmSession
 * @since 0.6.0
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

    final ReactiveExecutorFactory stmtExecutorFactory;


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
    public boolean isSync() {
        return false;
    }

    @Override
    public Mono<ReactiveLocalSession> localSession() {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionFactoryClosed(this));
        }
        return new LocalBuilder(this).build();
    }

    @Override
    public Mono<ReactiveRmSession> rmSession() {
        if (isClosed()) {
            return Mono.error(_Exceptions.sessionFactoryClosed(this));
        }
        return new RmBuilder(this).build();
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


    static final class LocalBuilder extends ReactiveSessionBuilder<LocalSessionBuilder, Mono<ReactiveLocalSession>>
            implements LocalSessionBuilder {

        /**
         * private constructor
         */
        private LocalBuilder(ArmyReactiveSessionFactory sessionFactory) {
            super(sessionFactory);
        }

        @Override
        protected Mono<ReactiveLocalSession> createSession(final String sessionName, final boolean readonly,
                                                           final Function<Option<?>, ?> optionFunc) {
            return Mono.defer(() ->
                    ((ArmyReactiveSessionFactory) this.factory).stmtExecutorFactory
                            .localExecutor(sessionName, readonly, optionFunc)
                            .map(this::createLocalSession)
                            .onErrorMap(_ArmySession::wrapIfNeed)
            );
        }


        @Override
        protected Mono<ReactiveLocalSession> handleError(SessionException cause) {
            return Mono.error(cause);
        }

        private ReactiveLocalSession createLocalSession(final ReactiveLocalExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return ArmyReactiveLocalSession.create(this);
        }

    } // LocalBuilder


    static final class RmBuilder extends ReactiveSessionBuilder<RmSessionBuilder, Mono<ReactiveRmSession>>
            implements RmSessionBuilder {

        /**
         * private constructor
         */
        private RmBuilder(ArmyReactiveSessionFactory factory) {
            super(factory);
        }


        @Override
        protected Mono<ReactiveRmSession> createSession(final String sessionName, final boolean readonly,
                                                        final Function<Option<?>, ?> optionFunc) {
            return Mono.defer(() ->
                    ((ArmyReactiveSessionFactory) this.factory).stmtExecutorFactory
                            .rmExecutor(sessionName, readonly, optionFunc)
                            .map(this::createRmSession)
                            .onErrorMap(_ArmySession::wrapIfNeed)
            );
        }

        @Override
        protected Mono<ReactiveRmSession> handleError(SessionException cause) {
            return Mono.error(cause);
        }

        private ReactiveRmSession createRmSession(final ReactiveRmExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return ArmyReactiveRmSession.create(this);
        }


    } // ArmyRmBuilder

}
