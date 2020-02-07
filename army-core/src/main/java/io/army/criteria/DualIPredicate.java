package io.army.criteria;


public interface DualIPredicate extends IPredicate {

    Expression<?> leftExpression();

    DualOperator dualOperator();

    Expression<?> rightExpression();

}
