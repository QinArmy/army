package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

/**
 * created  on 2019-02-22.
 */
final class ExpressionSelection<E> extends AbstractExpression<E> implements Selection {

    private final Expression<E> expression;

    private final String alias;

    ExpressionSelection(Expression<E> expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public String alias() {
        return alias;
    }

    @Override
    public MappingType mappingType() {
        return expression.mappingType();
    }

    @Override
    protected void afterSpace(SQLContext context) {
        expression.appendSQL(context);
        context.stringBuilder()
                .append(" AS ")
                .append(alias);
    }

    @Override
    public String toString() {
        return expression.toString() + " AS " + alias;
    }
}
