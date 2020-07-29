package io.army.boot.sync;

import io.army.*;
import io.army.boot.DomainValuesGenerator;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.TableRoute;
import io.army.sync.ProxySession;
import io.army.sync.Session;
import io.army.sync.SessionFactory;
import io.army.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * this class is a implementation of {@link SessionFactory}.
 * this class run only below:
 * <ul>
 *     <li>{@link ShardingMode#NO_SHARDING}</li>
 *     <li>{@link ShardingMode#SAME_SCHEMA_SHARDING}</li>
 * </ul>
 */
class SingleDatabaseSessionFactory extends AbstractSyncSessionFactory
        implements SessionFactory, InnerRmSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SingleDatabaseSessionFactory.class);

    private final DataSource dataSource;

    private final SQLDialect actualSQLDialect;

    private final Dialect dialect;

    private final ProxySession proxySession;

    private final CurrentSessionContext currentSessionContext;

    private final DomainValuesGenerator domainValuesGenerator;

    private final InsertSQLExecutor insertSQLExecutor;

    private final SelectSQLExecutor selectSQLExecutor;

    private final UpdateSQLExecutor updateSQLExecutor;

    private boolean closed;


    SingleDatabaseSessionFactory(SyncSessionFactoryParams.Single factoryParams)
            throws SessionFactoryException {
        super(factoryParams);
        DataSource dataSource = factoryParams.getDataSource();
        Assert.notNull(dataSource, "dataSource required");

        this.dataSource = dataSource;
        this.currentSessionContext = SyncSessionFactoryUtils.buildCurrentSessionContext(this);
        this.proxySession = new ProxySessionImpl(this, this.currentSessionContext);

        this.dialect = SyncSessionFactoryUtils.createDialect(dataSource, this);
        this.actualSQLDialect = this.dialect.sqlDialect();
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
    public ProxySession proxySession() {
        return this.proxySession;
    }

    @Override
    public SessionFactory.SessionBuilder builder() {
        return new SessionBuilderImpl();
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
    public InsertSQLExecutor insertSQLExecutor() {
        return insertSQLExecutor;
    }

    @Override
    public SelectSQLExecutor selectSQLExecutor() {
        return this.selectSQLExecutor;
    }

    @Override
    public UpdateSQLExecutor updateSQLExecutor() {
        return this.updateSQLExecutor;
    }


    @Nullable
    @Override
    public GenericTmSessionFactory tmSessionFactory() {
        // always null
        return null;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public Dialect dialect() {
        return dialect;
    }


    @Override
    public boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public SQLDialect actualSQLDialect() {
        return this.actualSQLDialect;
    }

    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @Override
    public TableRoute tableRoute(TableMeta<?> tableMeta) {
        return null;
    }

    @Override
    public String toString() {
        return "SessionFactory[" + this.name + "]";
    }

    void initSessionFactory() throws DataAccessException {
        migrationMeta();
    }

    /*################################## blow private method ##################################*/

    private void migrationMeta() {
        String keyName = String.format(ArmyConfigConstant.MIGRATION_META, this.name);
        if (!this.env.getProperty(keyName, Boolean.class, Boolean.FALSE)) {
            return;
        }
        DataSource primary = SyncSessionFactoryUtils.obtainPrimaryDataSource(this.dataSource);
        try (Connection conn = primary.getConnection()) {
            new DefaultSessionFactoryInitializer(conn, this.tableMetaMap, this.dialect)
                    .onStartup();
        } catch (SQLException e) {
            throw new DataAccessException(ErrorCode.CODEC_DATA_ERROR, e, "init session factory failure.");
        }
    }

    /*################################## blow instance inner class  ##################################*/

    private final class SessionBuilderImpl implements SessionFactory.SessionBuilder {

        private boolean currentSession;

        private boolean noResetConnection;

        @Override
        public SessionFactory.SessionBuilder currentSession(boolean current) {
            this.currentSession = current;
            return this;
        }

        @Override
        public SessionBuilder resetConnection(boolean reset) {
            this.noResetConnection = reset;
            return this;
        }

        @Override
        public Session build() throws SessionException {
            final boolean current = this.currentSession;
            try {
                final Session session = new SessionImpl(SingleDatabaseSessionFactory.this
                        , SingleDatabaseSessionFactory.this.dataSource.getConnection()
                        , current, noResetConnection);
                if (current) {
                    SingleDatabaseSessionFactory.this.currentSessionContext.currentSession(session);
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
