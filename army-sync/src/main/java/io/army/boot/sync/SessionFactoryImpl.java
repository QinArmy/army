package io.army.boot.sync;

import io.army.*;
import io.army.boot.DomainValuesGenerator;
import io.army.boot.migratioin.SyncMetaMigrator;
import io.army.cache.SessionCacheFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.TableRoute;
import io.army.sync.ProxySession;
import io.army.sync.Session;
import io.army.sync.SessionFactory;
import io.army.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link SessionFactory}
 */
class SessionFactoryImpl extends AbstractGenericSessionFactory
        implements InnerSessionFactory {

    private static final EnumSet<ShardingMode> SUPPORT_SHARDING_SET = EnumSet.of(
            ShardingMode.NO_SHARDING
            , ShardingMode.SINGLE_DATABASE_SHARDING);

    private final DataSource dataSource;

    private final Dialect dialect;

    private final Map<TableMeta<?>, DomainAdvice> domainAdviceMap;

    private final int tableCountPerDatabase;

    private final SessionCacheFactory sessionCacheFactory;

    private final DomainValuesGenerator domainValuesGenerator;

    private final InsertSQLExecutor insertSQLExecutor;

    private final SelectSQLExecutor selectSQLExecutor;

    private final UpdateSQLExecutor updateSQLExecutor;

    private final ProxySession proxySession;

    private final CurrentSessionContext currentSessionContext;

    private final Map<TableMeta<?>, TableRoute> tableRouteMap;

    private final AtomicBoolean initFinished = new AtomicBoolean(false);

    private boolean closed;


    SessionFactoryImpl(SessionFactoryBuilderImpl factoryBuilder)
            throws SessionFactoryException {
        super(factoryBuilder);

        if (!SUPPORT_SHARDING_SET.contains(this.shardingMode)) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , "ShardingMode[%s] is supported by %s.", getClass().getName());
        }

        DataSource dataSource = factoryBuilder.dataSource();
        Assert.notNull(dataSource, "dataSource required");

        this.dataSource = dataSource;
        this.dialect = SyncSessionFactoryUtils.createDialectForSync(dataSource, this);
        this.domainAdviceMap = SyncSessionFactoryUtils.createDomainAdviceMap(
                factoryBuilder.domainInterceptors());
        this.tableCountPerDatabase = factoryBuilder.tableCountPerDatabase();
        SyncSessionFactoryUtils.assertTableCountOfSharding(this.tableCountPerDatabase, this);

        this.currentSessionContext = SyncSessionFactoryUtils.buildCurrentSessionContext(this);
        this.proxySession = new ProxySessionImpl(this, this.currentSessionContext);
        this.tableRouteMap = SyncSessionFactoryUtils.routeMap(this, TableRoute.class
                , 1, this.tableCountPerDatabase);
        this.sessionCacheFactory = SessionCacheFactory.build(this);

        // executor after dialect
        this.domainValuesGenerator = DomainValuesGenerator.build(this);
        this.insertSQLExecutor = InsertSQLExecutor.build(this);
        this.selectSQLExecutor = SelectSQLExecutor.build(this);
        this.updateSQLExecutor = UpdateSQLExecutor.build(this);
    }


    @Override
    public void close() throws SessionFactoryException {
        this.closed = true;
    }

    @Override
    public int databaseIndex() {
        // always 0
        return 0;
    }

    @Override
    public int tableCountOfSharding() {
        return this.tableCountPerDatabase;
    }

    @Override
    public ProxySession proxySession() {
        return this.proxySession;
    }

    @Override
    public SessionFactory.SessionBuilder builder() {
        return new SessionBuilderImpl();
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

    @Override
    public CurrentSessionContext currentSessionContext() {
        return this.currentSessionContext;
    }

    @Override
    public SessionCacheFactory sessionCacheFactory() {
        return this.sessionCacheFactory;
    }

    @Nullable
    @Override
    public GenericTmSessionFactory tmSessionFactory() {
        // always null
        return null;
    }

    /*################################## blow GenericRmSessionFactory method ##################################*/

    @Override
    public boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public Database actualDatabase() {
        return this.dialect.database();
    }

    /*################################## blow InnerGenericRmSessionFactory method ##################################*/

    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    @Override
    public InsertSQLExecutor insertSQLExecutor() {
        return this.insertSQLExecutor;
    }

    @Override
    public SelectSQLExecutor selectSQLExecutor() {
        return this.selectSQLExecutor;
    }

    @Override
    public UpdateSQLExecutor updateSQLExecutor() {
        return this.updateSQLExecutor;
    }

    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        TableRoute tableRoute = this.tableRouteMap.get(tableMeta);
        if (tableRoute == null) {
            throw new NotFoundRouteException("TableMeta[%s] not found table route.", tableMeta);
        }
        return tableRoute;
    }

    @Override
    public String toString() {
        return "SessionFactory[" + this.name + "]";
    }

    boolean initializeSessionFactory() throws DataAccessException {
        if (this.initFinished.get()) {
            return false;
        }
        synchronized (this.initFinished) {
            migrationMeta();
            this.initFinished.compareAndSet(false, true);
        }
        return true;
    }

    /*################################## blow private method ##################################*/

    private void migrationMeta() {
        String keyName = String.format(ArmyConfigConstant.MIGRATION_META, this.name);
        if (!this.env.getProperty(keyName, Boolean.class, Boolean.FALSE)) {
            return;
        }
        DataSource primary = SyncSessionFactoryUtils.obtainPrimaryDataSource(this.dataSource);
        try (Connection conn = primary.getConnection()) {
            // execute migration
            SyncMetaMigrator.build()
                    .migrate(conn, this);
        } catch (SQLException e) {
            throw new DataAccessException(ErrorCode.CODEC_DATA_ERROR, e, "%s migration failure.", this);
        }
    }

    /*################################## blow instance inner class  ##################################*/

    final class SessionBuilderImpl implements SessionFactory.SessionBuilder {

        private boolean currentSession;

        private boolean resetConnection = true;

        private SessionBuilderImpl() {
        }

        @Override
        public SessionFactory.SessionBuilder currentSession(boolean current) {
            this.currentSession = current;
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

        public final boolean resetConnection() {
            return resetConnection;
        }

        @Override
        public Session build() throws SessionException {
            final boolean current = this.currentSession;
            try {
                final Session session = new SessionImpl(SessionFactoryImpl.this
                        , SessionFactoryImpl.this.dataSource.getConnection(), SessionBuilderImpl.this);
                if (current) {
                    SessionFactoryImpl.this.currentSessionContext.currentSession(session);
                }
                return session;
            } catch (SQLException e) {
                throw new CreateSessionException(ErrorCode.CANNOT_GET_CONN, e
                        , "Could not create Army-managed session,because can't get connection.");
            } catch (IllegalStateException e) {
                if (current) {
                    throw new CreateSessionException(ErrorCode.DUPLICATION_CURRENT_SESSION, e
                            , "Could not create Army-managed session,because duplication current session.");
                } else {
                    throw new CreateSessionException(ErrorCode.ACCESS_ERROR, e
                            , "Could not create Army-managed session.");
                }

            }
        }
    }


}
