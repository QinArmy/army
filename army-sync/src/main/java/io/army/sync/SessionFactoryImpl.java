package io.army.sync;

import io.army.ArmyKeys;
import io.army.SessionException;
import io.army.SessionFactoryException;
import io.army.bean.ArmyBean;
import io.army.cache.SessionCache;
import io.army.cache.SessionCacheFactory;
import io.army.dialect._Dialect;
import io.army.dialect._DialectFactory;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.session.AbstractSessionFactory;
import io.army.session.DataAccessException;
import io.army.sync.executor.ExecutorFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * This class is a implementation of {@link SessionFactory}
 */
final class SessionFactoryImpl extends AbstractSessionFactory implements SessionFactory {

    final ExecutorFactory executorFactory;

    final _Dialect dialect;

    private final SessionCacheFactory sessionCacheFactory;

    private final CurrentSessionContext currentSessionContext;


    private boolean closed;


    SessionFactoryImpl(FactoryBuilderImpl builder) throws SessionFactoryException {
        super(builder);

        this.executorFactory = Objects.requireNonNull(builder.executorFactory);
        this.dialect = _DialectFactory.createDialect(this);
        this.currentSessionContext = getCurrentSessionContext();
        this.sessionCacheFactory = SessionCacheFactory.build(this);
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
    public boolean supportSavePoints() {
        return this.dialect.supportSavePoint() && this.executorFactory.supportSavePoints();
    }

    @Override
    public ServerMeta serverMeta() {
        return this.executorFactory.serverMeta();
    }

    @Override
    public CurrentSessionContext currentSessionContext() throws SessionFactoryException {
        final CurrentSessionContext context = this.currentSessionContext;
        if (context == null) {
            String m = String.format("%s no specified %s.", this, CurrentSessionContext.class.getName());
            throw new SessionFactoryException(m);
        }
        return context;
    }

    @Override
    public SessionFactory.SessionBuilder builder() {
        return null;
    }


    @Override
    public boolean factoryClosed() {
        return this.closed;
    }


    @Override
    public String toString() {
        return String.format("%s[%s] readonly:%s", SessionFactory.class.getName(), this.name, this.readOnly);
    }

    /*################################## blow package method ##################################*/

    final SessionCache createSessionCache(Session session) {
        return this.sessionCacheFactory.createSessionCache(session);
    }


    /*################################## blow private method ##################################*/


    private void initializeArmyBeans() {
        ArmyBean armyBean = null;
        try {
            for (ArmyBean bean : this.env.getAllBean().values()) {
                armyBean = bean;
                bean.initializing(this);
            }
        } catch (Exception e) {
            throw new SessionFactoryException(e, "ArmyBean initializing occur error,ArmyBean[%s].", armyBean);
        }
    }

    private void destroyArmyBeans() {
        ArmyBean armyBean = null;
        try {
            for (ArmyBean bean : this.env.getAllBean().values()) {
                armyBean = bean;
                bean.armyBeanDestroy();
            }
        } catch (Exception e) {
            throw new SessionFactoryException(e, "ArmyBean destroy occur error,ArmyBean[%s].", armyBean);
        }
    }


    @Nullable
    private CurrentSessionContext getCurrentSessionContext() {
        final String className;
        className = this.env.get(ArmyKeys.CURRENT_SESSION_CONTEXT);
        if (className == null) {
            return null;
        }

        try {
            final Class<?> clazz;
            clazz = Class.forName(className);
            final Method method;
            method = clazz.getDeclaredMethod("create", SessionFactory.class);
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
            return (CurrentSessionContext) context;
        } catch (ClassNotFoundException e) {
            String m = String.format("Create %s,Not found %s class.", CurrentSessionContext.class.getName(), className);
            throw new SessionFactoryException(m, e);
        } catch (NoSuchMethodException e) {
            throw noCurrentSessionContextFactoryMethod(className, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String m = String.format("Create %s occur error.%s", CurrentSessionContext.class.getName(), this);
            throw new SessionFactoryException(m, e);
        }
    }

    /*################################## blow instance inner class  ##################################*/

    final class SessionBuilderImpl implements SessionFactory.SessionBuilder {

        private final SessionFactoryImpl sessionFactory;

        private boolean currentSession;

        private boolean readOnly = SessionFactoryImpl.this.readOnly;

        private boolean resetConnection = true;

        private SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
            this.sessionFactory = sessionFactory;
        }

        @Override
        public SessionBuilder name(String name) {
            return null;
        }

        @Override
        public SessionFactory.SessionBuilder currentSession(boolean current) {
            this.currentSession = current;
            return this;
        }

        @Override
        public final SessionBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public SessionBuilder resetConnection(boolean reset) {
            this.resetConnection = reset;
            return this;
        }

        public final boolean currentSession() {
            return currentSession;
        }

        public final boolean readOnly() {
            return this.readOnly;
        }

        public final boolean resetConnection() {
            return resetConnection;
        }

        @Override
        public Session build() throws SessionException {
//            final boolean current = this.currentSession;
//            try {
//                if (SessionFactoryImpl.this.readOnly && !this.readOnly) {
//                    throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR
//                            , "%s can't create create non-readonly TmSession.", SessionFactoryImpl.this);
//                }
//                final Session session = new SessionImpl(SessionFactoryImpl.this
//                        , SessionFactoryImpl.this.dataSource.getConnection(), this);
//                if (current) {
//                    SessionFactoryImpl.this.currentSessionContext.currentSession(session);
//                }
//                return session;
//            } catch (SQLException e) {
//                throw new CreateSessionException(ErrorCode.CANNOT_GET_CONN, e
//                        , "Could not create Army-managed session,because can't get connection.");
//            } catch (IllegalStateException e) {
//                if (current) {
//                    throw new CreateSessionException(ErrorCode.DUPLICATION_CURRENT_SESSION, e
//                            , "Could not create Army-managed session,because duplication current session.");
//                } else {
//                    throw new CreateSessionException(ErrorCode.ACCESS_ERROR, e
//                            , "Could not create Army-managed session.");
//                }
//
//            }
            return null;


        }


    }


    private static SessionFactoryException noCurrentSessionContextFactoryMethod(String className, @Nullable Throwable cause) {
        String m = String.format("%s don't definite public static %s create(%s) method."
                , className, className, SessionFactory.class.getName());
        return new SessionFactoryException(m, cause);
    }


}
