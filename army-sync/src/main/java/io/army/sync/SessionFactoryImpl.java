package io.army.sync;

import io.army.SessionException;
import io.army.SessionFactoryException;
import io.army.advice.sync.DomainAdvice;
import io.army.beans.ArmyBean;
import io.army.boot.DomainValuesGenerator;
import io.army.cache.SessionCache;
import io.army.cache.SessionCacheFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect._Dialect;
import io.army.dialect._DialectFactory;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.session.AbstractSessionFactory;
import io.army.session.FactoryMode;
import io.army.sharding.TableRoute;
import io.army.sync.executor.ExecutorFactory;
import io.army.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

/**
 * This class is a implementation of {@link SessionFactory}
 */
class SessionFactoryImpl extends AbstractSessionFactory implements SessionFactory {

    final ExecutorFactory executorFactory;

     final _Dialect dialect;

    private final Map<TableMeta<?>, DomainAdvice> domainAdviceMap;

    private final SessionCacheFactory sessionCacheFactory;

    private final DomainValuesGenerator domainValuesGenerator;

    private final ProxySession proxySession;

    private final CurrentSessionContext currentSessionContext;

    private final Map<TableMeta<?>, TableRoute> tableRouteMap;


    private boolean closed;


    SessionFactoryImpl(FactoryBuilderImpl builder) throws SessionFactoryException {
        super(builder);

        this.executorFactory = Objects.requireNonNull(builder.executorFactory);
        this.dialect = _DialectFactory.createDialect(null);//must after  this.executorFactory
        this.domainAdviceMap = CollectionUtils.unmodifiableMap(builder.domainAdviceMap);
        this.currentSessionContext = builder.currentSessionContext;

        this.proxySession = new ProxySessionImpl(this, this.currentSessionContext);
        this.tableRouteMap = SyncSessionFactoryUtils.routeMap(this, TableRoute.class
                , 1, this.tableCountPerDatabase);
        this.sessionCacheFactory = SessionCacheFactory.build(this);
        this.domainValuesGenerator = DomainValuesGenerator.build(this);

    }


    @Override
    public void close() throws SessionFactoryException {
        destroyArmyBeans();
        this.closed = true;
    }

    @Override
    public FactoryMode factoryMode() {
        return null;
    }

    @Override
    public ServerMeta serverMeta() {
        return this.executorFactory.serverMeta();
    }

    @Override
    public ProxySession proxySession() {
        return this.proxySession;
    }

    @Override
    public SessionFactory.SessionBuilder builder() {
        return null;
    }

    @Override
    public Map<TableMeta<?>, DomainAdvice> domainInterceptorMap() {
        return this.domainAdviceMap;
    }

    @Nullable
    @Override
    public DomainAdvice domainInterceptorList(TableMeta<?> tableMeta) {
        return this.domainAdviceMap.get(tableMeta);
    }

    @Override
    public boolean hasCurrentSession() {
        return this.currentSessionContext.hasCurrentSession();
    }



    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @Override
    public boolean factoryClosed() {
        return this.closed;
    }


    @Override
    public String toString() {
        return String.format("%s[%s]", SessionFactory.class.getName(), this.name);
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


}
