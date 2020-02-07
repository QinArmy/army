package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;

import java.util.List;

final class BracketsExpression<E> extends AbstractExpression<E> {

    private final Expression<E> exp;

    BracketsExpression(Expression<E> exp) {
        this.exp = exp;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.stringBuilder();
        builder.append("(");
        exp.appendSQL(context);
        builder.append(")");
    }


    @Override
    public MappingType mappingType() {
        return exp.mappingType();
    }

    @Override
    public String toString() {
        return "(" + exp + ")";
    }
}
