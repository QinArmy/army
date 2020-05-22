package io.army.criteria;



public interface FieldValuePredicate extends FieldPredicate {

    DualPredicateOperator operator();

    Object value();
}
