package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.Expression;
import org.qinarmy.army.criteria.ParamExpression;

import java.sql.JDBCType;

/**
 * created  on 2018/12/4.
 */
class ParamDualExpression<E> extends AbstractExpression<E> implements ParamExpression<E> {

    private final Expression<E> left;

    private final DualOperator operator;

    private final E right;


    public ParamDualExpression(Expression<E> left, DualOperator operator, E right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Expression<E> getExpression() {
        return left;
    }

    public DualOperator getOperator() {
        return operator;
    }

    @Override
    public E getParam() {
        return right;
    }

    @Override
    public JDBCType getJdbcType() {
        return null;
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.rendered() + " ?";
    }
}
