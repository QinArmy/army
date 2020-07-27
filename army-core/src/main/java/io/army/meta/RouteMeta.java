package io.army.meta;

import io.army.sharding.Route;
import io.army.sharding.RouteType;

public interface RouteMeta extends Meta {

       RouteType routeType();

       boolean singleField();

       Class<? extends Route> routeClass();

       int fieldOrder();

}
