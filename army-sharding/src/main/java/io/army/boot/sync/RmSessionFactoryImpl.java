package io.army.boot.sync;


import io.army.*;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.TableRoute;
import io.army.sync.SessionFactory;
import io.army.tx.XaTransactionOption;
import io.army.util.Assert;

import javax.sql.XADataSource;
import java.sql.SQLException;


/**
 * this class is a implementation of {@link SessionFactory}
 * Resource Manager (RM) {@link SessionFactory}.
 * <p>
 * this class run only below {@link io.army.ShardingMode}:
 *     <ul>
 *         <li>{@link io.army.ShardingMode#SHARDING}</li>
 *     </ul>
 * </p>
 */
final class RmSessionFactoryImpl extends AbstractGenericSessionFactory
        implements InnerRmSessionFactory {

    private final TmSessionFactoryImpl tmSessionFactory;

    private final int databaseIndex;

    private final XADataSource dataSource;

    private final Dialect dialect;

    private final DomainValuesGenerator domainValuesGenerator;

    private final InsertSQLExecutor insertSQLExecutor;

    private final SelectSQLExecutor selectSQLExecutor;

    private final UpdateSQLExecutor updateSQLExecutor;

    private boolean closed;

    RmSessionFactoryImpl(TmSessionFactoryImpl sessionFactory, XADataSource dataSource, int databaseIndex
            , @Nullable Database database) {
        super(sessionFactory, databaseIndex);
        Assert.state(this.shardingMode == ShardingMode.SHARDING
                , () -> String.format("%s support only SHARDING ShardingMode", RmSessionFactoryImpl.class.getName()));
        Assert.notNull(dataSource, "dataSource required");

        this.tmSessionFactory = sessionFactory;
        this.databaseIndex = databaseIndex;
        this.dataSource = dataSource;
        this.dialect = SyncShardingSessionFactoryUtils.createDialectForSync(dataSource, database, this);

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
    public boolean closed() {
        return this.closed;
    }

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
    public boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public Database actualDatabase() {
        return this.dialect.database();
    }

    @Override
    public int databaseIndex() {
        return this.databaseIndex;
    }

    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @NonNull
    @Override
    public GenericTmSessionFactory tmSessionFactory() {
        return this.tmSessionFactory;
    }

    @Override
    public TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        TableRoute tableRoute;
        tableRoute = this.tmSessionFactory.shardingRouteMap.get(tableMeta);
        if (tableRoute == null) {
            throw new NotFoundRouteException("TableMeta[%s] not found table route.", tableMeta);
        }
        return tableRoute;
    }


    @Override
    public final RmSession build(XaTransactionOption option) throws SessionException {
        try {
            return new RmSessionImpl(this, dataSource.getXAConnection(), option);
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "getXAConnection() error.");
        }
    }
}
