package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingMeta;

final class BracketsExpression<E> extends AbstractExpression<E> {

    private final Expression<E> exp;

    BracketsExpression(Expression<E> exp) {
        this.exp = exp;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("(");
        exp.appendSQL(context);
        builder.append(")");
    }


    @Override
    public MappingMeta mappingType() {
        return exp.mappingType();
    }

    @Override
    public String beforeAs() {
        return "(" + exp + ")";
    }
}
