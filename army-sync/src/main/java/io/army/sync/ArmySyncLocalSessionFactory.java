package io.army.sync;

import io.army.env.SyncKey;
import io.army.lang.Nullable;
import io.army.proxy._SessionCacheFactory;
import io.army.session.DataAccessException;
import io.army.session.SessionFactoryException;
import io.army.sync.executor.SyncLocalExecutorFactory;
import io.army.sync.executor.SyncLocalStmtExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This class is a implementation of {@link SyncLocalSessionFactory}
 */
final class ArmySyncLocalSessionFactory extends ArmySyncSessionFactory implements SyncLocalSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncLocalSessionFactory.class);

    final SyncLocalExecutorFactory executorFactory;

    final boolean buildInExecutor;

    final _SessionCacheFactory sessionCacheFactory;

    private final SessionContext sessionContext;

    private final boolean supportSavePoints;

    private boolean closed;


    ArmySyncLocalSessionFactory(LocalSessionFactoryBuilder builder) throws SessionFactoryException {
        super(builder);

        this.executorFactory = builder.executorFactory;
        assert this.executorFactory != null;
        this.buildInExecutor = isBuildInExecutor(this.executorFactory);


        this.sessionContext = getSessionContext();
        //this.sessionCacheFactory = SessionCacheFactory.build(this);
        this.sessionCacheFactory = _SessionCacheFactory.create(this);
        this.supportSavePoints = this.mappingEnv.serverMeta().isSupportSavePoints()
                && this.executorFactory.supportSavePoints();

    }


    @Override
    public SessionBuilder builder() {
        return new LocalSessionBuilder(this);
    }

    @Override
    public String toString() {
        return String.format("%s[name:%s,hash:%s,readonly:%s]",
                SyncLocalSessionFactory.class.getName(),
                this.name,
                System.identityHashCode(this),
                this.readonly
        );
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
            method = clazz.getDeclaredMethod("create", SyncLocalSessionFactory.class);
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


    private static boolean isBuildInExecutor(final SyncLocalExecutorFactory executorFactory) {
        return executorFactory.getClass().getName().startsWith("io.army.jdbc.");
    }


    private static SessionFactoryException noCurrentSessionContextFactoryMethod(String className, @Nullable Throwable cause) {
        String m = String.format("%s don't definite public static %s create(%s) method."
                , className, className, SyncLocalSessionFactory.class.getName());
        return new SessionFactoryException(m, cause);
    }


    /*################################## blow instance inner class  ##################################*/

    static final class LocalSessionBuilder extends ArmySessionBuilder<SessionBuilder, SyncLocalSession>
            implements SyncLocalSessionFactory.SessionBuilder {


        SyncLocalStmtExecutor stmtExecutor;


        private LocalSessionBuilder(ArmySyncLocalSessionFactory factory) {
            super(factory);
        }


        @Override
        protected SyncLocalSession createSession(String name) {
            try {
                //  this.stmtExecutor = this.factory.executorFactory.createLocalStmtExecutor();

                return new ArmySyncLocalSession(this);
            } catch (DataAccessException e) {
                throw createExecutorError(e);
            }
        }

        @Override
        protected SyncLocalSession handleError(Throwable cause) {
            return null;
        }
    }//LocalSessionBuilder


}
