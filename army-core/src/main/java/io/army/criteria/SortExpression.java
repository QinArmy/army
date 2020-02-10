package io.army.criteria;

import io.army.lang.Nullable;

/**
 * created  on 2018/10/9.
 */
public interface SortExpression<E>  extends Expression<E>{

    @Nullable
    Boolean ascExp();

}
