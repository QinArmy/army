package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.TableField;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;

public interface _Predicate extends IPredicate, _Expression {

    boolean isIdPredicate();


    boolean isOptimistic();

    @Nullable
    TableField findParentId(ChildTableMeta<?> child, String alias);


}
