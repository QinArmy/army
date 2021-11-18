package io.army.boot.sync;


import io.army.*;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.AbstractSessionFactory;
import io.army.session.FactoryMode;
import io.army.session.GenericTmSessionFactory;
import io.army.sharding.TableRoute;
import io.army.sync.SessionFactory;
import io.army.tx.TransactionException;
import io.army.tx.XaTransactionOption;
import io.army.util.Assert;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * this class is a implementation of {@link SessionFactory}
 * Resource Manager (RM) {@link SessionFactory}.
 * <p>
 * this class run only below {@link FactoryMode}:
 *     <ul>
 *         <li>{@link FactoryMode#SHARDING}</li>
 *     </ul>
 * </p>
 */
final class RmSessionFactoryImpl extends AbstractSessionFactory
        implements InnerRmSessionFactory {

    private final TmSessionFactoryImpl tmSessionFactory;

    private final int databaseIndex;

    private final XADataSource dataSource;

    private final Dialect dialect;

    private final DomainValuesGenerator domainValuesGenerator;


    private final AtomicBoolean initFinished = new AtomicBoolean(false);

    private boolean closed;

    RmSessionFactoryImpl(TmSessionFactoryImpl sessionFactory, XADataSource dataSource, int databaseIndex
            , @Nullable Database database) {
        super(sessionFactory, databaseIndex);
        Assert.state(this.factoryMode == FactoryMode.SHARDING
                , () -> String.format("%s support only SHARDING ShardingMode", RmSessionFactoryImpl.class.getName()));
        Assert.notNull(dataSource, "dataSource required");

        this.tmSessionFactory = sessionFactory;
        this.databaseIndex = databaseIndex;
        this.dataSource = dataSource;
        this.dialect = TmSessionFactoryUtils.createDialectForSync(dataSource, database, this);

        // executor after dialect
        this.domainValuesGenerator = sessionFactory.domainValuesGenerator();
//        this.insertSQLExecutor = InsertSQLExecutor.build(this);
//        this.selectSQLExecutor = SelectSQLExecutor.build(this);
//        this.updateSQLExecutor = UpdateSQLExecutor.build(this);

    }


    @Override
    public final void close() throws SessionFactoryException {
        this.closed = true;
    }

    @Override
    public final boolean factoryClosed() {
        return this.closed;
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }


    @Override
    public final boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public final Database actualDatabase() {
        return this.dialect.database();
    }

    @Override
    public final int databaseIndex() {
        return this.databaseIndex;
    }

    @Override
    public final int tableCountPerDatabase() {
        return this.tmSessionFactory.tableCountPerDatabase();
    }

    @Override
    public final void initialize() {
        if (this.initFinished.get()) {
            return;
        }
        synchronized (this.initFinished) {
            if (this.initFinished.get()) {
                return;
            }
            migrationMeta();
            this.initFinished.compareAndSet(false, true);
        }
    }

    @Override
    public final DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @Override
    public boolean compareDefaultOnMigrating() {
        return this.compareDefaultOnMigrating;
    }

    @NonNull
    @Override
    public final GenericTmSessionFactory tmSessionFactory() {
        return this.tmSessionFactory;
    }

    @Override
    public final TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        return this.tmSessionFactory.tableRoute(tableMeta);
    }


    @Override
    public final RmSession build(XaTransactionOption option) throws SessionException {
        try {
            RmSession rmSession = new RmSessionImpl(this, dataSource.getXAConnection(), option);
            // start xa transaction,because invoker can directly use RmSession.
            rmSession.sessionTransaction().start();
            return rmSession;
        } catch (TransactionException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "XA transaction start error.");
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "getXAConnection() error.");
        }
    }

    /*################################## blow private method ##################################*/

    private void migrationMeta() {
//        String keyName = String.format(ArmyConfigConstant.MIGRATION_MODE, this.tmSessionFactory.name());
//        if (!this.env.getProperty(keyName, Boolean.class, Boolean.FALSE)) {
//            return;
//        }
//        XADataSource primary = SyncSessionFactoryUtils.obtainPrimaryDataSource(this.dataSource);
//        XAConnection xaConn = null;
//        try {
//            xaConn = primary.getXAConnection();
//            try (Connection conn = xaConn.getConnection()) {
//                // execute migration
//                SyncMetaMigrator.build()
//                        .migrate(conn, this);
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException_0(ErrorCode.CODEC_DATA_ERROR, e, "%s migration failure.", this);
//        } finally {
//            if (xaConn != null) {
//                closeXAConnection(xaConn);
//            }
//        }
    }

    private void closeXAConnection(XAConnection xaConn) {
        try {
            xaConn.close();
        } catch (SQLException e) {
            throw new DataAccessException_0(ErrorCode.CODEC_DATA_ERROR, e, "%s migration failure.", this);
        }
    }
}
