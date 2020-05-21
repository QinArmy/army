package io.army.criteria;

import io.army.meta.FieldExp;

public interface FieldPredicate extends IPredicate {

    FieldExp<?, ?> fieldExp();

    void appendPredicate(SQLContext context);
}
