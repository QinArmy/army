package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

public interface _Predicate extends IPredicate, _Expression {


    boolean isOptimistic();

    boolean isIdsEquals(TableMeta<?> table, String alias);


}
