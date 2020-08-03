package io.army.sharding;


public interface DatabaseRoute extends Route {

    int dataSourceRoute(Object routeKey);

}
