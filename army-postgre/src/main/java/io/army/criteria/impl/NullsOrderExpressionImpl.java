package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingType;

final class NullsOrderExpressionImpl<E> extends AbstractNoNOperationExpression<E> implements NullsOrderExpression<E> {

    private final Expression<E> expression;

    private final Nulls nulls;

    NullsOrderExpressionImpl(Expression<E> expression, Nulls nulls) {
        this.expression = expression;
        this.nulls = nulls;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        expression.appendSQL(context);
        context.sqlBuilder()
                .append(" NULLS ")
                .append(nulls.name())
        ;
    }

    @Override
    public Nulls nulls() {
        return this.nulls;
    }

    @Override
    public MappingType mappingType() {
        return this.expression.mappingType();
    }

    @Override
    public String toString() {
        return expression.toString() + " NULLS " + nulls.name();
    }
}
