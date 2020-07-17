package io.army.sharding;


public interface DataSourceRoute extends Route {

    int dataSourceCount();

    int dataSourceRoute(Object routeKey);

}
