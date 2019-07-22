package io.army.criteria.impl;

import io.army.criteria.Expression;

import java.sql.JDBCType;

/**
 * created  on 2018/11/27.
 */
class DualConstantPredicate extends AbstractPredicate {


    private final Expression<?> left;

    private final DualOperator operator;

    private final Object right;

    public DualConstantPredicate(Expression<?> left, DualOperator operator, Object right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression<?> getLeft() {
        return left;
    }

    public DualOperator getOperator() {
        return operator;
    }

    @Override
    public Class<?> javaType() {
        return null;
    }

    @Override
    public JDBCType jdbcType() {
        return null;
    }

    public Object getRight() {
        return right;
    }
}
