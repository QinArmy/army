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

package io.army.session;

import io.army.ArmyException;
import io.army.criteria.Visible;
import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;
import io.army.dialect.DialectParserFactory;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.mapping.MappingEnv;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.util.*;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * <p>A abstract implementation of  {@link SessionFactory}.
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@code  io.army.reactive.ArmyReactiveSessionFactory}</li>
 *     <li>{@code io.army.sync.ArmySyncSessionFactory}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public abstract class _ArmySessionFactory implements SessionFactory {

    protected static final ConcurrentMap<String, Boolean> FACTORY_MAP = _Collections.concurrentHashMap(3);

    public final ArmyEnvironment env;

    protected final String name;

    protected final SchemaMeta schemaMeta;

    public final Map<Class<?>, TableMeta<?>> tableMap;

    protected final boolean readonly;

    protected final DialectParser dialectParser;

    protected final MappingEnv mappingEnv;

    public final Database serverDatabase;

    private final AllowMode queryInsertMode;

    private final Map<String, Boolean> queryInsertWhiteMap;

    private final AllowMode visibleMode;

    private final AllowMode driverSpiMode;

    private final Map<String, Boolean> visibleWhiteMap;

    private final Map<String, Boolean> driverSpiWhiteMap;

    final boolean sqlLogDynamic;

    final SqlLogMode sqlLogMode;

    public final boolean sqlExecutionCostTime;

    final boolean sqlParsingCostTime;


    protected _ArmySessionFactory(final _ArmyFactoryBuilder<?, ?> support) throws SessionFactoryException {
        final String name = _Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.containsKey(name)) {
            throw new SessionFactoryException(String.format("factory name[%s] duplication", name));
        }
        this.env = env;

        this.name = name;
        this.driverSpiMode = env.getOrDefault(ArmyKey.DRIVER_SPI_MODE);


        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMap = Objects.requireNonNull(support.tableMap);

        this.readonly = env.getOrDefault(ArmyKey.READ_ONLY);
        final DialectEnv dialectEnv = support.dialectEnv;
        assert dialectEnv != null;
        this.dialectParser = DialectParserFactory.createDialect(dialectEnv);
        this.mappingEnv = dialectEnv.mappingEnv();
        this.serverDatabase = this.mappingEnv.serverMeta().serverDatabase();

        this.queryInsertMode = env.getOrDefault(ArmyKey.QUERY_INSERT_MODE);
        this.queryInsertWhiteMap = createWhitMap(this.queryInsertMode, env, ArmyKey.QUERY_INSERT_SESSION_WHITE_LIST);
        this.visibleMode = env.getOrDefault(ArmyKey.VISIBLE_MODE);
        this.visibleWhiteMap = createWhitMap(this.visibleMode, env, ArmyKey.VISIBLE_SESSION_WHITE_LIST);

        this.driverSpiWhiteMap = createWhitMap(this.driverSpiMode, env, ArmyKey.DRIVER_SPI_SESSION_WHITE_LIST);


        this.sqlLogDynamic = env.getOrDefault(ArmyKey.SQL_LOG_DYNAMIC);
        this.sqlLogMode = env.getOrDefault(ArmyKey.SQL_LOG_MODE);

        if (!this.sqlLogDynamic && this.sqlLogMode == SqlLogMode.OFF) {
            this.sqlParsingCostTime = this.sqlExecutionCostTime = false;
        } else {
            this.sqlParsingCostTime = env.getOrDefault(ArmyKey.SQL_PARSING_COST_TIME);
            this.sqlExecutionCostTime = env.getOrDefault(ArmyKey.SQL_EXECUTION_COST_TIME);
        }


    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final ArmyEnvironment environment() {
        return this.env;
    }


    @Override
    public final SchemaMeta schemaMeta() {
        return this.schemaMeta;
    }

    @Override
    public final ZoneOffset zoneOffset() {
        return this.mappingEnv.zoneOffset();
    }

    @Override
    public final ServerMeta serverMeta() {
        return this.mappingEnv.serverMeta();
    }

    @Override
    public final boolean isSupportSavePoints() {
        return this.mappingEnv.serverMeta().isSupportSavePoints();
    }

    @Override
    public final Map<Class<?>, TableMeta<?>> tableMap() {
        return this.tableMap;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T> TableMeta<T> getTable(Class<T> domainClass) {
        return (TableMeta<T>) this.tableMap.get(domainClass);
    }

    @Override
    public final AllowMode visibleMode() {
        return this.visibleMode;
    }

    @Override
    public final AllowMode queryInsertMode() {
        return this.queryInsertMode;
    }

    @Override
    public final boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public final String toString() {
        return _StringUtils.builder(90)
                .append(getClass().getName())
                .append("[name:")
                .append(this.name)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }


    /*-------------------below protected methods -------------------*/


    private boolean isSessionDontSupportQueryInsert(final String sessionName) {
        final boolean dontSupport;
        switch (this.queryInsertMode) {
            case SUPPORT:
                dontSupport = false;
                break;
            case NEVER:
                dontSupport = true;
                break;
            case WHITE_LIST:
                dontSupport = this.queryInsertWhiteMap.get(sessionName) == null;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.queryInsertMode);

        }
        return dontSupport;
    }

    private boolean isSessionDontSupportVisible(final String sessionName) {
        final boolean dontSupport;
        switch (this.visibleMode) {
            case SUPPORT:
                dontSupport = false;
                break;
            case NEVER:
                dontSupport = true;
                break;
            case WHITE_LIST:
                dontSupport = this.visibleWhiteMap.get(sessionName) == null;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.visibleMode);

        }
        return dontSupport;
    }


    /*################################## blow protected static methods ##################################*/


    protected static Throwable wrapErrorIfNeed(final Throwable cause) {
        if (cause instanceof Exception) {
            return wrapError((Exception) cause);
        }
        return cause;
    }

    protected static SessionFactoryException wrapError(final Exception cause) {
        final SessionFactoryException error;
        if (cause instanceof SessionFactoryException) {
            error = (SessionFactoryException) cause;
        } else {
            error = new SessionFactoryException("unknown error," + cause.getMessage(), cause);
        }
        return error;
    }


    /*################################## blow private static method ##################################*/

    private static Function<ArmyException, RuntimeException> exceptionFunction(
            @Nullable Function<ArmyException, RuntimeException> function) {
        if (function == null) {
            function = e -> e;
        }
        return function;
    }


    /**
     * @return a unmodified map
     */
    private static Map<String, Boolean> createWhitMap(final AllowMode mode, final ArmyEnvironment env,
                                                      final ArmyKey<String> key) {
        final Map<String, Boolean> whiteMap;
        switch (mode) {
            case NEVER:
            case SUPPORT:
                whiteMap = _Collections.emptyMap();
                break;
            case WHITE_LIST:
                whiteMap = _StringUtils.whiteMap(env.get(key));
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return whiteMap;
    }


    @SuppressWarnings("unchecked")
    public static abstract class ArmySessionBuilder<F extends _ArmySessionFactory, B, R>
            implements SessionFactory.SessionBuilderSpec<B, R> {

        public final F factory;

        String name;

        boolean readonly;
        boolean allowQueryInsert;

        Visible visible = Visible.ONLY_VISIBLE;

        private Map<Option<?>, Object> dataSourceOptionMap;

        public ArmySessionBuilder(F factory) {
            this.factory = factory;
            this.readonly = factory.readonly;
        }

        @Override
        public final B name(@Nullable String name) {
            if (this.name != null) {
                throw new IllegalStateException("name non-null");
            }
            this.name = name;
            return (B) this;
        }

        @Override
        public final B readonly(boolean readonly) {
            this.readonly = readonly;
            return (B) this;
        }

        @Override
        public final B allowQueryInsert(boolean allow) {
            this.allowQueryInsert = allow;
            return (B) this;
        }

        @Override
        public final B visibleMode(@Nullable Visible visible) {
            if (visible == null) {
                throw new NullPointerException("visible non-ull");
            }
            this.visible = visible;
            return (B) this;
        }

        @Override
        public final <T> B dataSourceOption(final Option<T> option, final @Nullable T value) {
            Map<Option<?>, Object> map = this.dataSourceOptionMap;
            if (value == null && map == null) {
                return (B) this;
            }

            if (map == null) {
                this.dataSourceOptionMap = map = _Collections.hashMap();
            }
            if (value == null) {
                map.remove(option);
            } else {
                map.put(option, value);
            }
            return (B) this;
        }

        @Override
        public final R build() throws SessionException {
            String sessionName = this.name;
            if (sessionName == null) {
                this.name = sessionName = "unnamed";
            }
            Visible visible = this.visible;
            if (visible == null) {
                this.visible = visible = Visible.ONLY_VISIBLE;
            }

            final _ArmySessionFactory factory = this.factory;
            final boolean readonly = this.readonly;
            try {
                if (!readonly && factory.readonly) {
                    String m = String.format("%s is read only.", factory);
                    throw new CreateSessionException(m);
                } else if (visible != Visible.ONLY_VISIBLE && factory.isSessionDontSupportVisible(sessionName)) {
                    String m = String.format("%s don't allow to create the session[%s] that allow %s.",
                            factory, sessionName, Visible.class.getName());
                    throw new CreateSessionException(m);
                } else if (this.allowQueryInsert && factory.isSessionDontSupportQueryInsert(sessionName)) {
                    String m = String.format("%s don't allow to create the session[%s] that allow query insert statement.",
                            factory, sessionName);
                    throw new CreateSessionException(m);
                }
            } catch (SessionException e) {
                return handleError(e);
            }

            return createSession(sessionName, readonly, _FunctionUtils.mapFunc(this.dataSourceOptionMap));
        }

        public final boolean inOpenDriverSpi() {
            final boolean match;
            final _ArmySessionFactory factory = this.factory;
            switch (factory.driverSpiMode) {
                case NEVER:
                    match = false;
                    break;
                case SUPPORT:
                    match = true;
                    break;
                case WHITE_LIST: {
                    final String sessionName = this.name;
                    assert sessionName != null;
                    match = factory.driverSpiWhiteMap.containsKey(sessionName);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(factory.driverSpiMode);
            }
            return match;
        }


        protected abstract R createSession(String sessionName, boolean readonly, Function<Option<?>, ?> optionFunc);

        protected abstract R handleError(SessionException cause);


    } //ArmySessionBuilder


}
