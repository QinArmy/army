package io.army.sharding;


public interface DatabaseRoute extends Route {

    int dataSourceCount();

    int dataSourceRoute(Object routeKey);

}
