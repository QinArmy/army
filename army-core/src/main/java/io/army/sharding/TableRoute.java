package io.army.sharding;


public interface TableRoute extends Route {

    int tableCount();

    int tableRoute(Object routeKey);

}
