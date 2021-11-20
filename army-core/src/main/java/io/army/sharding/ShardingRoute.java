package io.army.sharding;

public interface ShardingRoute extends Route {

    RouteResult route(Object key);

}
