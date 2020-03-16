package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

final class SortExpressionImpl<E> extends AbstractExpression<E> {

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
    protected String beforeAs() {
        String text = expression.toString();
        if(ascExp){
            text += " ASC";
        }else {
            text += " DESC";
        }
        return  text;
    }

    @Override
    public MappingType mappingType() {
        return expression.mappingType();
    }
}
