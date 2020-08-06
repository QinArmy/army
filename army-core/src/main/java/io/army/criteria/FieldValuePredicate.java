package io.army.criteria;


import io.army.meta.FieldMeta;

public interface FieldValuePredicate extends FieldPredicate {

    FieldMeta<?, ?> fieldMeta();

    DualPredicateOperator operator();

    Object value();
}
