package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

/**
 *
 * @param <T> entity java class
 * @see SingleUpdateAble
 */
public interface SetAbleOfSingleUpdate<T extends IDomain>  extends SingleUpdate{

    <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T,F> targetField, Expression<F> expression);


    <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T,F> targetField,@Nullable  F newValue);

}
