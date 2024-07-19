/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.reactive;

import io.army.env.ArmyEnvironment;
import io.army.reactive.executor.ReactiveExecutor;
import io.army.reactive.executor.ReactiveExecutorFactory;
import io.army.reactive.executor.ReactiveLocalExecutor;
import io.army.reactive.executor.ReactiveRmExecutor;
import io.army.session.*;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
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

    final ReactiveExecutorFactory executorFactory;

    final boolean buildInExecutor;
    final boolean jdbdDriver;

    final boolean resultItemDriverSpi;

    private volatile int factoryClosed;

    /**
     * private constructor
     */
    private ArmyReactiveSessionFactory(ArmyReactiveFactorBuilder builder) throws SessionFactoryException {
        super(builder);
        this.executorFactory = builder.stmtExecutorFactory;
        assert this.executorFactory != null;

        this.buildInExecutor = this.executorFactory.getClass().getPackage().getName().startsWith("io.army.jdbd.");
        this.jdbdDriver = this.buildInExecutor || this.executorFactory.driverSpiName().equalsIgnoreCase("JDBD");

        if (this.jdbdDriver) {
            this.resultItemDriverSpi = true;
        } else {
            this.resultItemDriverSpi = this.executorFactory.isResultItemDriverSpi();
        }
    }

    @Override
    public String driverSpiName() {
        return this.executorFactory.driverSpiName();
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
    public boolean isResultItemDriverSpi() {
        return this.resultItemDriverSpi;
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

    @Nullable
    @Override
    public <T> T valueOf(Option<T> option) {
        try {
            return this.executorFactory.valueOf(option);
        } catch (Exception e) {
            throw wrapError(e);
        }
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
            return this.executorFactory.close();
        }
        return Mono.empty();
    }


    static abstract class ReactiveSessionBuilder<B, R> extends ArmySessionBuilder<ArmyReactiveSessionFactory, B, R> {

        ReactiveExecutor stmtExecutor;

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
                    this.factory.executorFactory
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
                    this.factory.executorFactory
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
