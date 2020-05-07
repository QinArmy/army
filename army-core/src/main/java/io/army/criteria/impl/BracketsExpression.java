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
        builder.append("( ");
        exp.appendSQL(context);
        builder.append(" )");
    }

    @Override
    public MappingMeta mappingMeta() {
        return exp.mappingMeta();
    }

    @Override
    public String beforeAs() {
        return "(" + exp + ")";
    }
}
