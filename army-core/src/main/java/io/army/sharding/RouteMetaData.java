package io.army.sharding;

import io.army.session.FactoryMode;

public interface RouteMetaData {

    FactoryMode shardingMode();

    int databaseCount();

    int tableContPerDatabase();


}
