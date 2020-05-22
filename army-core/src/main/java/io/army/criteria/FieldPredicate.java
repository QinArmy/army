package io.army.criteria;

import io.army.meta.FieldExpression;

public interface FieldPredicate extends SpecialPredicate {

    FieldExpression<?, ?> fieldExp();

}
