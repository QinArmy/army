package io.army.criteria;


public interface DualIPredicate extends IPredicate {

    Expression<?> leftExpression();

    DualOperator operator();

    Expression<?> rightExpression();

}
