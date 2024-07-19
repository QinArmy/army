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

package io.army.sync;

import io.army.env.SyncKey;
import io.army.option.Option;
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

    final SyncExecutorFactory executorFactory;

    final boolean sessionIdentifierEnable;

    final boolean buildInExecutor;
    final boolean jdbcDriver;

    final boolean resultItemDriverSpi;


    private volatile int factoryClosed;

    /**
     * private constructor
     */
    private ArmySyncSessionFactory(ArmySyncFactoryBuilder builder) throws SessionFactoryException {
        super(builder);
        this.executorFactory = builder.stmtExecutorFactory;
        assert this.executorFactory != null;
        this.sessionIdentifierEnable = this.env.getOrDefault(SyncKey.SESSION_IDENTIFIER_ENABLE);
        this.buildInExecutor = this.executorFactory.getClass().getPackage().getName().startsWith("io.army.jdbc.");
        this.jdbcDriver = this.buildInExecutor || this.executorFactory.driverSpiName().equalsIgnoreCase("JDBC");

        if (this.jdbcDriver) {
            this.resultItemDriverSpi = false;
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
        // always false
        return false;
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public boolean isResultItemDriverSpi() {
        return this.resultItemDriverSpi;
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
    public void close() throws SessionFactoryException {
        if (!FACTORY_CLOSED.compareAndSet(this, 0, 1)) {
            return;
        }
        try {
            this.executorFactory.close();
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    static abstract class SyncBuilder<B, R> extends ArmySessionBuilder<ArmySyncSessionFactory, B, R> {

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
            this.stmtExecutor = this.factory
                    .executorFactory.localExecutor(sessionName, readOnly, optionFunc);
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
            this.stmtExecutor = this.factory.executorFactory
                    .rmExecutor(sessionName, readOnly, optionFunc);
            return ArmySyncRmSession.create(this);
        }


    } // SyncRmSessionBuilder


}
