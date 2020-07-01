package io.army.boot;


import io.army.dialect.SQLDialect;
import io.army.util.Assert;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

abstract class SyncShardingSessionFactoryUtils extends SyncSessionFactoryUtils {

    public static Map<String, RmSessionFactoryImpl> createSessionFactoryMap(TmSessionFactoryImpl factory
            , SyncSessionFactoryParams.Sharding factoryParams) {

        return Collections.emptyMap();
    }

    /*################################## blow private method ##################################*/

    private static RmSessionFactoryParams createSingleFactoryParams(
            SyncSessionFactoryParams.Sharding shardingParams, String factoryName) {
        RmSessionFactoryParams params = new RmSessionFactoryParams();

        DataSource dataSource = shardingParams.getDataSourceMap().get(factoryName);
        Assert.notNull(dataSource, () -> String.format("DataSource[%s] is null.", factoryName));
        params.setDataSource(dataSource);

        SQLDialect sqlDialect = shardingParams.getSqlDialectMap().get(factoryName);
        if (sqlDialect == null) {
            sqlDialect = shardingParams.getDefaultSqlDialect();
        }
        params.setSqlDialect(sqlDialect);
        return params;
    }
}
