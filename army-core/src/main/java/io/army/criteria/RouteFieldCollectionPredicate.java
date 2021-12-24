package io.army.criteria;


import io.army.meta.TableMeta;
import io.army.sharding.Route;

import java.util.Map;
import java.util.function.Function;

public interface RouteFieldCollectionPredicate extends IPredicate {


    Map<Byte, IPredicate> tableSplit(Function<TableMeta<?>, Route> function);

    Map<Byte, IPredicate> databaseSplit(Function<TableMeta<?>, Route> function);


}
