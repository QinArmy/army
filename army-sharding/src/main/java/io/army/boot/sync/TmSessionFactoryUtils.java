package io.army.boot.sync;


import io.army.CreateSessionException;
import io.army.ErrorCode;
import io.army.SessionException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

abstract class TmSessionFactoryUtils {

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
//        List<XADataSource> dataSourceList = factionBuilder.dataSourceList();
//
//        if (dataSourceList == null || dataSourceList.size() < 2) {
//            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
//                    , "XADataSource list size must great than 1 .");
//        }
//        Map<Integer, Database> databaseMap = factionBuilder.databaseMap();
//        if (databaseMap == null) {
//            databaseMap = Collections.emptyMap();
//        }
//        List<RmSessionFactory> rmSessionFactoryList = new ArrayList<>(dataSourceList.size());
//        List<Database> databaseList = new ArrayList<>(dataSourceList.size());
//        boolean supportZone = true;
//
//        final Database defaultDatabase = readDatabase(tmFactory);
//        final int size = dataSourceList.size();
//
//        for (int i = 0; i < size; i++) {
//            Database database = databaseMap.get(i);
//            if (database == null) {
//                database = defaultDatabase;
//            }
//            RmSessionFactory rmSessionFactory = new RmSessionFactoryImpl(tmFactory, dataSourceList.get(i), i, database);
//            if (supportZone) {
//                supportZone = rmSessionFactory.supportZone();
//            }
//            rmSessionFactoryList.add(rmSessionFactory);
//            databaseList.add(rmSessionFactory.actualDatabase());
//
//        }
        return new RmSessionFactoryWrapper(null, null, false);
    }


    static Dialect createDialectForSync(XADataSource dataSource, @Nullable Database database
            , RmSessionFactoryImpl sessionFactory) {
//        try {
//            XAConnection xaConn = dataSource.getXAConnection();
//            Dialect dialect;
//            try (Connection conn = xaConn.getConnection()) {
//                dialect = createDialect(database, extractDatabase(conn.getMetaData()), sessionFactory);
//            }
//            xaConn.close();
//            return dialect;
//        } catch (SQLException e) {
//            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e, "get connection error.");
//        }
        return null;
    }

    static Connection getConnection(XAConnection xaConnection) throws SessionException {
        try {
            return xaConnection.getConnection();
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "XAConnection getConnection() error.");
        }
    }

    /*################################## blow private method ##################################*/

}
