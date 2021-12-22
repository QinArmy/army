package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.Route;
import io.army.sharding.RouteContext;

import java.util.function.Function;

public interface _Predicate extends IPredicate, _Expression<Boolean> {


    byte databaseIndex(Function<TableMeta<?>, Route> function);

    byte tableIndex(TableMeta<?> table, RouteContext context);

    @Nullable
    FieldMeta<?, ?> databaseRouteField();

    @Nullable
    FieldMeta<?, ?> tableRouteField(TableMeta<?> table);


}
