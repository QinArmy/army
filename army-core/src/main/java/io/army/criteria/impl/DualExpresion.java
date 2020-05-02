package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingMeta;

final class DualExpresion<E> extends AbstractExpression<E> {

    protected final Expression<?> left;

    protected final DualOperator operator;

    protected final Expression<?> right;


    DualExpresion(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public MappingMeta mappingType() {
        return left.mappingType();
    }

    @Override
    protected void afterSpace(SQLContext context) {
        left.appendSQL(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered())
                .append(" ");
        right.appendSQL(context);
    }

    @Override
    public String beforeAs() {
        return left + " " + operator.rendered() + " " + right;
    }
}
