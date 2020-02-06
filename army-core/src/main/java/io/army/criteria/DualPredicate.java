package io.army.criteria;


public interface DualPredicate extends Predicate {

    Expression<?> leftExpression();

    DualOperator dualOperator();

    Expression<?> rightExpression();

}
