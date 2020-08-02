package io.army.boot.sync;


import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class SyncShardingSessionFactoryUtils extends SyncSessionFactoryUtils {

    static Map<String, RmSessionFactoryImpl> createSessionFactoryMap(TmSessionFactoryImpl factory
            , TmSessionFactionBuilderImpl factionBuilder) {
        List<XADataSource> dataSourceList = factionBuilder.dataSourceList();
        if (CollectionUtils.isEmpty(dataSourceList)) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , "XADataSource list must not empty.");
        }
        Map<Integer, Database> sqlDialectMap = factionBuilder.sqlDialectMap();
        if (sqlDialectMap == null) {
            sqlDialectMap = Collections.emptyMap();
        }
        final Database defaultSqlDialect = readDatabase(factory);
        final int size = dataSourceList.size();
        for (int i = 0; i < size; i++) {
            Database sqlDialect = sqlDialectMap.get(i);
            if (sqlDialect == null) {
                sqlDialect = defaultSqlDialect;
            }

        }
        return Collections.emptyMap();
    }


    static Dialect createDialectForSync(XADataSource dataSource, @Nullable Database database
            , RmSessionFactoryImpl sessionFactory) {
        try (Connection conn = dataSource.getXAConnection().getConnection()) {

            return createDialect(database, extractDatabase(conn), sessionFactory);
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e, "get connection error.");
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
