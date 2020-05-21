package io.army.criteria;



public interface FieldValuePredicate extends FieldPredicate {

    DualOperator operator();

    Object value();
}
