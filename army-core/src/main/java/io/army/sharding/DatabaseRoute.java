package io.army.sharding;


public interface DatabaseRoute extends Route {

    boolean containsDatabase(int databaseIndex);

    int dataSourceRoute(Object routeKey);

}
