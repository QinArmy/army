package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.meta.FieldMeta;

import java.util.List;

public interface _StandardUpdate extends _Update, _SingleDml {

    /**
     * @return a unmodifiable list
     */
    @Override
    List<FieldMeta<?, ?>> targetFieldList();

    /**
     * @return a unmodifiable list
     */
    @Override
    List<Expression<?>> valueExpList();

}
