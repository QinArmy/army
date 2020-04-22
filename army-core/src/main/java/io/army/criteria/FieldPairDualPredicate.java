package io.army.criteria;

import io.army.meta.FieldExp;

public interface FieldPairDualPredicate extends IPredicate {

    FieldExp<?, ?> left();

    DualOperator operator();

    FieldExp<?, ?> right();

}
