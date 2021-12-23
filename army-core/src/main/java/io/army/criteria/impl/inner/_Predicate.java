package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.RouteContext;

public interface _Predicate extends IPredicate, _Expression<Boolean> {


    byte databaseIndex(TableMeta<?> table, RouteContext context);

    byte tableIndex(TableMeta<?> table, RouteContext context);

    @Nullable
    FieldMeta<?, ?> databaseRouteField(TableMeta<?> table);

    @Nullable
    FieldMeta<?, ?> tableRouteField(TableMeta<?> table);


}
