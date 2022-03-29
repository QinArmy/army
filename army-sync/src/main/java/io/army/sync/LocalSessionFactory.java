package io.army.sync;

import io.army.dialect._Dialect;
import io.army.dialect._DialectFactory;
import io.army.env.SyncKey;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.proxy._SessionCacheFactory;
import io.army.session.*;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.StmtExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * This class is a implementation of {@link SessionFactory}
 */
final class LocalSessionFactory extends _AbstractSessionFactory implements SessionFactory {

    final ExecutorFactory executorFactory;

    final _Dialect dialect;

    final _SessionCacheFactory sessionCacheFactory;


    private final SessionContext sessionContext;

    private boolean closed;


    LocalSessionFactory(LocalSessionFactoryBuilder builder) throws SessionFactoryException {
        super(builder);

        this.executorFactory = Objects.requireNonNull(builder.executorFactory);
        this.dialect = _DialectFactory.createDialect(this);
        this.sessionContext = getCurrentSessionContext();
        //this.sessionCacheFactory = SessionCacheFactory.build(this);
        this.sessionCacheFactory = _SessionCacheFactory.create(this);
    }


    @Override
    public boolean isReactive() {
        return false;
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
    public SessionContext currentSessionContext() throws SessionFactoryException {
        final SessionContext context = this.sessionContext;
        if (context == null) {
            String m = String.format("%s no specified %s.", this, SessionContext.class.getName());
            throw new SessionFactoryException(m);
        }
        return context;
    }

    @Override
    public SessionFactory.SessionBuilder builder() {
        return new LocalSessionBuilder(this);
    }


    @Override
    public boolean factoryClosed() {
        return this.closed;
    }


    @Override
    public String toString() {
        return String.format("[%s name:%s,readonly:%s]"
                , SessionFactory.class.getName(), this.name, this.readonly);
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
    private SessionContext getCurrentSessionContext() {
        final String className;
        className = this.env.get(SyncKey.SESSION_CONTEXT);
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
                , className, className, SessionFactory.class.getName());
        return new SessionFactoryException(m, cause);
    }


    /*################################## blow instance inner class  ##################################*/

    static final class LocalSessionBuilder implements SessionFactory.SessionBuilder {

        final LocalSessionFactory sessionFactory;

        StmtExecutor stmtExecutor;

        String name;

        boolean readonly;

        private LocalSessionBuilder(LocalSessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
            this.readonly = sessionFactory.readonly;
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
        public Session build() throws SessionException {
            if (!this.readonly && this.sessionFactory.readonly) {
                String m = String.format("%s couldn't create non-readonly %s."
                        , this.sessionFactory, Session.class.getName());
                throw new CreateSessionException(m);
            }
            try {
                this.stmtExecutor = this.sessionFactory.executorFactory.createStmtExecutor();
            } catch (DataAccessException e) {
                throw new CreateSessionException("create session occur error.", e);
            }
            return new LocalSession(this);

        }


    }//LocalSessionBuilder


}
