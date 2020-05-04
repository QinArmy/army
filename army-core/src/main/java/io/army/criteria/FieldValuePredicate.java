package io.army.criteria;

import io.army.meta.FieldExp;

public interface FieldValuePredicate extends IPredicate {

    FieldExp<?, ?> fieldExp();

    DualOperator operator();

    Object value();
}
