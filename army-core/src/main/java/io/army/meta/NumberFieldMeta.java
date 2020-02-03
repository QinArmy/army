package io.army.meta;

import io.army.criteria.NumberExpression;
import io.army.domain.IDomain;

public interface NumberFieldMeta<T extends IDomain,F extends Number> extends FieldMeta<T,F> , NumberExpression<F> {

}
