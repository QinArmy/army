package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SortExpression;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

final class SortExpressionImpl<E> extends AbstractExpression<E> implements SortExpression<E> {

    private final Expression<?> expression;

    private final Boolean ascExp;

    SortExpressionImpl(Expression<?> expression, @Nullable Boolean ascExp) {
        Assert.isTrue(!(expression instanceof SortExpression), "expression error");
        this.expression = expression;
        this.ascExp = ascExp;
    }

    @Nullable
    @Override
    public Boolean ascExp() {
        return ascExp;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        expression.appendSQL(context);

        if (Boolean.TRUE.equals(ascExp)) {
            context.stringBuilder()
                    .append(" ASC");
        } else if (Boolean.FALSE.equals(ascExp)) {
            context.stringBuilder()
                    .append(" DESC");
        }
    }

    @Override
    public MappingType mappingType() {
        return expression.mappingType();
    }
}
