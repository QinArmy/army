package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.TableField;

import javax.annotation.Nullable;

import io.army.meta.ChildTableMeta;

public interface _Predicate extends IPredicate, _Expression {

    @Nullable
    _Predicate getIdPredicate();


    boolean isOptimistic();

    @Nullable
    TableField findParentId(ChildTableMeta<?> child, String alias);


}
