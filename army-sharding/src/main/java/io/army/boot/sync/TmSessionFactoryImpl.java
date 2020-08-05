package io.army.boot.sync;

import io.army.*;
import io.army.boot.DomainValuesGenerator;
import io.army.cache.SessionCacheFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.DatabaseRoute;
import io.army.sharding.ShardingRoute;
import io.army.sharding.TableRoute;
import io.army.sync.ProxyTmSession;
import io.army.sync.TmSession;
import io.army.sync.TmSessionFactory;
import io.army.sync.TmSessionFactoryCloseException;
import io.army.tx.Isolation;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is a implementation of {@link TmSessionFactory}.
 * Transaction Manager (TM) {@link TmSessionFactory}.
 * <p>
 * this class run only below {@link ShardingMode}:
 *     <ul>
 *         <li>{@link ShardingMode#SHARDING}</li>
 *     </ul>
 * </p>
 */
class TmSessionFactoryImpl extends AbstractGenericSessionFactory implements InnerTmSessionFactory {

    private final Map<TableMeta<?>, DomainAdvice> domainAdviceMap;

    private final List<RmSessionFactory> rmSessionFactoryList;

    private final List<Database> actualDatabaseList;

    private final CurrentSessionContext currentSessionContext;

    private final int tableCountPerDatabase;

    private final Map<TableMeta<?>, ShardingRoute> shardingRouteMap;

    private final ProxyTmSession proxySession;

    private final SessionCacheFactory sessionCacheFactory;

    private final DomainValuesGenerator domainValuesGenerator;

    private final boolean supportZone;

    private final AtomicBoolean initFinished = new AtomicBoolean(false);


    private boolean closed;

    TmSessionFactoryImpl(TmSessionFactionBuilderImpl builder) {
        super(builder);
        Assert.isTrue(this.shardingMode == ShardingMode.SHARDING
                , () -> String.format("%s support only SHARDING ShardingMode", TmSessionFactoryImpl.this));

        this.domainAdviceMap = SyncSessionFactoryUtils.createDomainAdviceMap(builder.domainInterceptors());
        final TmSessionFactoryUtils.RmSessionFactoryWrapper wrapper = TmSessionFactoryUtils.createRmSessionFactoryMap(
                this, builder);
        this.rmSessionFactoryList = wrapper.rmSessionFactoryList;
        this.actualDatabaseList = wrapper.databaseList;
        this.supportZone = wrapper.supportZone;

        this.currentSessionContext = SyncSessionFactoryUtils.buildCurrentSessionContext(this);
        this.tableCountPerDatabase = builder.tableCountPerDatabase();
        this.shardingRouteMap = SyncSessionFactoryUtils.routeMap(this, ShardingRoute.class
                , this.rmSessionFactoryList.size(), this.tableCountPerDatabase);
        this.proxySession = new ProxyTmSessionImpl(this, this.currentSessionContext);

        this.sessionCacheFactory = SessionCacheFactory.build(this);
        this.domainValuesGenerator = DomainValuesGenerator.build(this);
    }


    @Override
    public SessionBuilder builder() {
        return new SessionBuilderImpl();
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public boolean supportZone() {
        return this.supportZone;
    }

    @Override
    public TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        TableRoute tableRoute = this.shardingRouteMap.get(tableMeta);
        if (tableRoute == null) {
            throw new NotFoundRouteException("TableMeta[%s] not found TableRoute.", tableMeta);
        }
        return tableRoute;
    }

    @Override
    public ProxyTmSession proxySession() {
        return this.proxySession;
    }

    @Override
    public List<Database> actualDatabaseList() {
        return this.actualDatabaseList;
    }

    @Override
    public int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    @Override
    public DatabaseRoute dataSourceRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        DatabaseRoute databaseRoute = this.shardingRouteMap.get(tableMeta);
        if (databaseRoute == null) {
            throw new NotFoundRouteException("TableMeta[%s] not found DatabaseRoute.", tableMeta);
        }
        return databaseRoute;
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
    public boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass) {
        return currentSessionContextClass.isInstance(this.currentSessionContext);
    }

    @Nullable
    @Override
    public GenericTmSessionFactory tmSessionFactory() {
        // always null
        return null;
    }

    @Override
    public void close() throws SessionFactoryException {
        if (this.closed) {
            return;
        }
        Map<Integer, SessionFactoryException> exceptionMap = null;
        for (RmSessionFactory rmSessionFactory : this.rmSessionFactoryList) {
            try {
                rmSessionFactory.close();
            } catch (SessionFactoryException e) {
                if (exceptionMap == null) {
                    exceptionMap = new HashMap<>();
                }
                exceptionMap.putIfAbsent(rmSessionFactory.databaseIndex(), e);
            }
        }
        if (CollectionUtils.isEmpty(exceptionMap)) {
            this.closed = true;
        } else {
            throw new TmSessionFactoryCloseException(exceptionMap, "%s close occur error.", this);
        }
    }

    @Override
    public String toString() {
        return "TmSessionFactory[" + this.name + "]";
    }

    /*################################## blow InnerTmSessionFactory method ##################################*/

    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @Override
    public CurrentSessionContext currentSessionContext() {
        return this.currentSessionContext;
    }

    @Override
    public SessionCacheFactory sessionCacheFactory() {
        return this.sessionCacheFactory;
    }

    @Override
    public List<RmSessionFactory> rmSessionFactoryList() {
        return this.rmSessionFactoryList;
    }

    /*################################## blow package method ##################################*/

    boolean initializeTmSessionFactory() {
        if (this.initFinished.get()) {
            return false;
        }
        synchronized (this.initFinished) {

            this.rmSessionFactoryList.parallelStream()
                    .forEach(RmSessionFactory::initialize);
            this.initFinished.compareAndSet(false, true);
        }
        return true;
    }

    /*################################## blow package instance inner class ##################################*/

    final class SessionBuilderImpl implements SessionBuilder {

        private String transactionName;

        private boolean current;

        private Isolation isolation;

        private boolean readOnly;

        private int timeout = 0;

        @Override
        public SessionBuilder transactionName(@Nullable String transactionName) {
            this.transactionName = transactionName;
            return this;
        }

        @Override
        public SessionBuilder currentSession(boolean current) {
            this.current = current;
            return this;
        }

        @Override
        public SessionBuilder isolation(Isolation isolation) {
            this.isolation = isolation;
            return this;
        }

        @Override
        public SessionBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public SessionBuilder timeout(int timeoutSeconds) {
            this.timeout = timeoutSeconds;
            return this;
        }

        public String transactionName() {
            return this.transactionName;
        }

        public boolean current() {
            return current;
        }

        public Isolation isolation() {
            return isolation;
        }

        public boolean readOnly() {
            return readOnly;
        }

        public int timeout() {
            return timeout;
        }

        @Override
        public TmSession build() throws SessionException {
            if (this.isolation == null || this.isolation.level < Isolation.REPEATABLE_READ.level) {
                throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR
                        , "isolation[%s] not great than or equals REPEATABLE_READ.", this.isolation);
            }
            if (TmSessionFactoryImpl.this.readOnly && !this.readOnly) {
                throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR
                        , "%s can't create create non-readonly TmSession.", TmSessionFactoryImpl.this);
            }
            if (this.timeout == 0) {
                throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, "not specified timeout");
            }
            return null;
        }
    }

}
