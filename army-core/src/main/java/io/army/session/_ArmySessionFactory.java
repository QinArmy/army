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
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

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
 * @since 1.0
 */
public abstract class _ArmySessionFactory implements SessionFactory {

    protected static final ConcurrentMap<String, Boolean> FACTORY_MAP = _Collections.concurrentHashMap(3);

    protected final String name;

    public final ArmyEnvironment env;

    protected final SchemaMeta schemaMeta;

    public final Map<Class<?>, TableMeta<?>> tableMap;

    protected final boolean readonly;

    protected final DialectParser dialectParser;

    protected final MappingEnv mappingEnv;

    public final Database serverDatabase;

    private final AllowMode queryInsertMode;

    private final Map<String, Boolean> queryInsertWhiteMap;

    private final AllowMode visibleMode;

    private final Map<String, Boolean> visibleWhiteMap;

    final boolean sqlLogDynamic;

    final SqlLogMode sqlLogMode;


    protected _ArmySessionFactory(final _ArmyFactoryBuilder<?, ?> support) throws SessionFactoryException {
        final String name = _Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.containsKey(name)) {
            throw new SessionFactoryException(String.format("factory name[%s] duplication", name));
        }
        this.name = name;
        this.env = env;
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

        this.sqlLogDynamic = env.getOrDefault(ArmyKey.SQL_LOG_DYNAMIC);
        this.sqlLogMode = env.getOrDefault(ArmyKey.SQL_LOG_MODE);

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
        return _StringUtils.builder(48)
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
    public static abstract class ArmySessionBuilder<B, R> implements SessionFactory.SessionBuilderSpec<B, R> {

        public final _ArmySessionFactory factory;

        String name;

        boolean readonly;
        boolean allowQueryInsert;

        Visible visible = Visible.ONLY_VISIBLE;

        public ArmySessionBuilder(_ArmySessionFactory factory) {
            this.factory = factory;
            this.readonly = factory.readonly;
        }

        @Override
        public final B name(@Nullable String name) {
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
        public final B visibleMode(Visible visible) {
            this.visible = visible;
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

            try {
                if (!this.readonly && factory.readonly) {
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

            return this.createSession(sessionName);
        }

        protected abstract R createSession(String name);

        protected abstract R handleError(SessionException cause);


        protected CreateSessionException createExecutorError(DataAccessException e) {
            String m = String.format("create executor for session[%s] occur error.", this.name);
            return new CreateSessionException(m, e);
        }


    } //ArmySessionBuilder


}
