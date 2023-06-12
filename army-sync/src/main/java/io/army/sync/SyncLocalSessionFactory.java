package io.army.sync;

import io.army.criteria.Visible;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;
import io.army.dialect.DialectParserFactory;
import io.army.env.SyncKey;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.proxy._SessionCacheFactory;
import io.army.session.*;
import io.army.stmt.Stmt;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.LocalExecutorFactory;
import io.army.sync.executor.LocalStmtExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.ZoneId;

/**
 * This class is a implementation of {@link LocalSessionFactory}
 */
final class SyncLocalSessionFactory extends _ArmySessionFactory implements LocalSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLocalSessionFactory.class);

    final LocalExecutorFactory executorFactory;

    final DialectParser dialectParser;

    final _SessionCacheFactory sessionCacheFactory;

    final MappingEnv mappingEnv;

    private final SessionContext sessionContext;

    private final boolean supportSavePoints;

    private boolean closed;


    SyncLocalSessionFactory(LocalSessionFactoryBuilder builder) throws SessionFactoryException {
        super(builder);

        this.executorFactory = builder.executorFactory;
        assert this.executorFactory != null;
        final DialectEnv dialectEnv = builder.dialectEnv;
        assert dialectEnv != null;
        this.dialectParser = DialectParserFactory.createDialect(dialectEnv);
        this.mappingEnv = dialectEnv.mappingEnv();
        this.sessionContext = getSessionContext();
        //this.sessionCacheFactory = SessionCacheFactory.build(this);
        this.sessionCacheFactory = _SessionCacheFactory.create(this);
        this.supportSavePoints = this.mappingEnv.serverMeta().isSupportSavePoints()
                && this.executorFactory.supportSavePoints();
    }


    @Override
    public boolean isReactive() {
        return false;
    }

    @Override
    public ZoneId zoneId() {
        return this.mappingEnv.databaseZoneOffset();
    }


    @Override
    public void close() throws SessionFactoryException {
        synchronized (this) {
            if (this.closed) {
                return;
            }
            destroyArmyBeans();
            try {
                this.executorFactory.close();
            } catch (DataAccessException e) {
                String m = String.format("%s close occur error.%s", ExecutorFactory.class.getName(), e.getMessage());
                throw new SessionFactoryException(m, e);
            }
            this.closed = true;
        }
    }

    @Override
    public boolean isSupportSavePoints() {
        return this.supportSavePoints;
    }

    @Override
    public ServerMeta serverMeta() {
        return this.mappingEnv.serverMeta();
    }

    @Override
    public SessionContext currentSessionContext() throws SessionFactoryException {
        final SessionContext context = this.sessionContext;
        if (context == null) {
            String m = String.format("%s no specified %s.", this, SessionContext.class.getName());
            throw new SessionFactoryException(m);
        }
        return context;
    }

    @Override
    public LocalSessionFactory.SessionBuilder builder() {
        return new LocalSessionBuilder(this);
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }


    @Override
    public String toString() {
        return String.format("[%s name:%s,readonly:%s]"
                , LocalSessionFactory.class.getName(), this.name, this.readonly);
    }

    void printSqlIfNeed(final Stmt stmt) {
        super.printSqlLog(this.dialectParser, stmt, LOG);
    }

    /*################################## blow package method ##################################*/


    /*################################## blow private method ##################################*/


    private void destroyArmyBeans() {
//        ArmyBean armyBean = null;
//        try {
//            for (ArmyBean bean : this.env.getAllBean().values()) {
//                armyBean = bean;
//                bean.armyBeanDestroy();
//            }
//        } catch (Exception e) {
//            throw new SessionFactoryException(e, "ArmyBean destroy occur error,ArmyBean[%s].", armyBean);
//        }
    }


    @Nullable
    private SessionContext getSessionContext() {
        final String className;
        className = this.env.get(SyncKey.SESSION_CONTEXT);
        if (className == null) {
            return null;
        }

        try {
            final Class<?> clazz;
            clazz = Class.forName(className);
            final Method method;
            method = clazz.getDeclaredMethod("create", LocalSessionFactory.class);
            final int modifiers = method.getModifiers();
            if (!(Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    && clazz.isAssignableFrom(method.getReturnType()))) {
                throw noCurrentSessionContextFactoryMethod(className, null);
            }
            final Object context;
            context = method.invoke(null, this);
            if (context == null) {
                String m = String.format("%s return null", method);
                throw new SessionFactoryException(m);
            }
            return (SessionContext) context;
        } catch (ClassNotFoundException e) {
            String m = String.format("Create %s,Not found %s class.", SessionContext.class.getName(), className);
            throw new SessionFactoryException(m, e);
        } catch (NoSuchMethodException e) {
            throw noCurrentSessionContextFactoryMethod(className, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String m = String.format("Create %s occur error.%s", SessionContext.class.getName(), this);
            throw new SessionFactoryException(m, e);
        }
    }


    private static SessionFactoryException noCurrentSessionContextFactoryMethod(String className, @Nullable Throwable cause) {
        String m = String.format("%s don't definite public static %s create(%s) method."
                , className, className, LocalSessionFactory.class.getName());
        return new SessionFactoryException(m, cause);
    }


    /*################################## blow instance inner class  ##################################*/

    static final class LocalSessionBuilder implements LocalSessionFactory.SessionBuilder {

        final SyncLocalSessionFactory factory;

        String name;

        boolean readonly;

        LocalStmtExecutor stmtExecutor;

        Visible visible;

        private LocalSessionBuilder(SyncLocalSessionFactory factory) {
            this.factory = factory;
            this.readonly = factory.readonly;
        }

        @Override
        public SessionBuilder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        @Override
        public SessionBuilder readonly(boolean readonly) {
            this.readonly = readonly;
            return this;
        }

        @Override
        public SessionBuilder queryInsertMode(QueryInsertMode mode) {
            //TODO
            throw new UnsupportedOperationException();
        }

        @Override
        public SessionBuilder visibleMode(Visible visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public LocalSession build() throws SessionException {
            if (!this.readonly && this.factory.readonly) {
                String m = String.format("%s couldn't create non-readonly %s."
                        , this.factory, LocalSession.class.getName());
                throw new CreateSessionException(m);
            }
            try {
                this.stmtExecutor = this.factory.executorFactory.createLocalStmtExecutor();
            } catch (DataAccessException e) {
                throw new CreateSessionException("create session occur error.", e);
            }
            if (this.name == null) {
                this.name = "unnamed";
            }
            return new SyncLocalSession(this);

        }


    }//LocalSessionBuilder


}
