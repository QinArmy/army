package io.army.boot.sync;


import io.army.CreateSessionException;
import io.army.ErrorCode;
import io.army.SessionException;
import io.army.SessionFactoryException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.util.Assert;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class TmSessionFactoryUtils extends SyncSessionFactoryUtils {

    static final class RmSessionFactoryWrapper {

        /**
         * a unmodifiable list
         */
        final List<RmSessionFactory> rmSessionFactoryList;

        /**
         * a unmodifiable list
         */
        final List<Database> databaseList;

        final boolean supportZone;

        private RmSessionFactoryWrapper(List<RmSessionFactory> rmSessionFactoryList, List<Database> databaseList
                , boolean supportZone) {
            this.rmSessionFactoryList = Collections.unmodifiableList(rmSessionFactoryList);
            this.databaseList = Collections.unmodifiableList(databaseList);
            this.supportZone = supportZone;
        }
    }

    /**
     * @return a unmodifiable object
     */
    static RmSessionFactoryWrapper createRmSessionFactoryMap(TmSessionFactoryImpl tmFactory
            , TmSessionFactionBuilderImpl factionBuilder) {
        List<XADataSource> dataSourceList = factionBuilder.dataSourceList();

        if (dataSourceList == null || dataSourceList.size() < 2) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , "XADataSource list size must great than 1 .");
        }
        Map<Integer, Database> databaseMap = factionBuilder.databaseMap();
        if (databaseMap == null) {
            databaseMap = Collections.emptyMap();
        }
        List<RmSessionFactory> rmSessionFactoryList = new ArrayList<>(dataSourceList.size());
        List<Database> databaseList = new ArrayList<>(dataSourceList.size());
        boolean supportZone = true;

        final Database defaultDatabase = readDatabase(tmFactory);
        final int size = dataSourceList.size();

        for (int i = 0; i < size; i++) {
            Database database = databaseMap.get(i);
            if (database == null) {
                database = defaultDatabase;
            }
            RmSessionFactory rmSessionFactory = new RmSessionFactoryImpl(tmFactory, dataSourceList.get(i), i, database);
            if (supportZone) {
                supportZone = rmSessionFactory.supportZone();
            }
            rmSessionFactoryList.add(rmSessionFactory);
            databaseList.add(rmSessionFactory.actualDatabase());

        }
        return new RmSessionFactoryWrapper(rmSessionFactoryList, databaseList, supportZone);
    }


    static Dialect createDialectForSync(XADataSource dataSource, @Nullable Database database
            , RmSessionFactoryImpl sessionFactory) {
        try (Connection conn = dataSource.getXAConnection().getConnection()) {

            return createDialect(database, extractDatabase(conn), sessionFactory);
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e, "get connection error.");
        }

    }

    static Connection getConnection(XAConnection xaConnection) throws SessionException {
        try {
            return xaConnection.getConnection();
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "XAConnection getConnection() error.");
        }
    }

    /*################################## blow private method ##################################*/

    private static RmSessionFactoryParams createSingleFactoryParams(
            SyncSessionFactoryParams.Sharding shardingParams, String factoryName) {
        RmSessionFactoryParams params = new RmSessionFactoryParams();

        DataSource dataSource = shardingParams.getDataSourceMap().get(factoryName);
        Assert.notNull(dataSource, () -> String.format("DataSource[%s] is null.", factoryName));
        params.setDataSource(dataSource);

        Database sqlDialect = shardingParams.getSqlDialectMap().get(factoryName);
        if (sqlDialect == null) {
            sqlDialect = shardingParams.getDefaultSqlDialect();
        }
        params.setSqlDialect(sqlDialect);
        return params;
    }
}
