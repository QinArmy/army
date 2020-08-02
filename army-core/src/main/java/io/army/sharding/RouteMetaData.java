package io.army.sharding;

import io.army.ShardingMode;

public interface RouteMetaData {

    ShardingMode shardingMode();

    int databaseCount();

    int tableContPerDatabase();


}
