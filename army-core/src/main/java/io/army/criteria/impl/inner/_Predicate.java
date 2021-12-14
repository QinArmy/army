package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sharding.DatabaseRoute;
import io.army.sharding.TableRoute;

import java.util.List;

public interface _Predicate extends IPredicate, _Expression<Boolean> {

    @Nullable
    Byte databaseIndex(DatabaseRoute route, List<FieldMeta<?, ?>> routeFields);

    @Nullable
    Byte tableIndex(TableRoute route, List<FieldMeta<?, ?>> routeFields);

}
