package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingType;

final class SortExpressionImpl<E> extends AbstractNoNOperationExpression<E> implements SortExpression<E> {

    private final Expression<?> expression;

    private final boolean ascExp;

    SortExpressionImpl(Expression<?> expression, boolean ascExp) {
        this.expression = expression;
        this.ascExp = ascExp;
    }

    @Override
    public Boolean sortExp() {
        return this.ascExp;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        expression.appendSQL(context);

        if (this.ascExp) {
            context.stringBuilder()
                    .append(" ASC");
        } else {
            context.stringBuilder()
                    .append(" DESC");
        }
    }

    @Override
    public String toString() {
        String text = expression.toString();
        if (ascExp) {
            text += " ASC";
        } else {
            text += " DESC";
        }
        return text;
    }


    @Override
    public MappingType mappingType() {
        return expression.mappingType();
    }
}
