package io.army.sharding;

import io.army.criteria.NotFoundRouteException;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;

@Deprecated
public interface RouteContext {

    FactoryMode factoryMode();

    byte databaseIndex();

    Route route(TableMeta<?> table) throws NotFoundRouteException;


}
