package io.army.sharding;

import io.army.criteria.NotFoundRouteException;
import io.army.meta.TableMeta;

public interface RouteContext {

    byte databaseIndex();

    Route route(TableMeta<?> table) throws NotFoundRouteException;


}
