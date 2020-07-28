package io.army.criteria.impl;

import io.army.criteria.ConvertExpression;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingMeta;

final class ConvertExpressionImpl<E> extends AbstractExpression<E> implements ConvertExpression<E> {

    private final Expression<?> original;

    private final MappingMeta convertType;

    ConvertExpressionImpl(Expression<?> original, MappingMeta convertType) {
        this.original = original;
        this.convertType = convertType;
    }

    @Override
    protected void appendSQL(SQLContext context) {
        original.appendSQL(context);
    }


    @Override
    public MappingMeta mappingMeta() {
        return convertType;
    }

    @Override
    public Expression<?> originalExp() {
        return original;
    }

    @Override
    public String toString() {
        return original.toString();
    }
}
