package io.army.criteria.impl;

import io.army.criteria.Expression;

/**
 * created  on 2018/11/25.
 */
class DualPredicate extends AbstractPredicate {

    private final Expression<?> left;

    private final DualOperator operator;

    private final Expression<?> right;


    DualPredicate(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.rendered() + " " + right.toString();
    }

    public Expression<?> getLeft() {
        return left;
    }

    public DualOperator getOperator() {
        return operator;
    }

    public Expression<?> getRight() {
        return right;
    }
}
