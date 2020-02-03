package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

public interface SingleSetAble<T extends IDomain> extends Updatable {

    <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T,F> targetField, Expression<F> expression);


    <F> WhereAbleOfSingleUpdate<T> set(FieldMeta<T,F> targetField,@Nullable  F newValue);

}
