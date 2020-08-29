package io.army.sharding;


public interface TableRoute extends Route {

    boolean containTable(int tableIndex);

    int tableIndex(Object routeKey);

}
