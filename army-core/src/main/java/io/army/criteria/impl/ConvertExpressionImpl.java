package io.army.criteria.impl;

import io.army.criteria.ConvertExpression;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;

import java.util.List;

final class ConvertExpressionImpl<E> extends AbstractExpression<E> implements ConvertExpression<E> {

    private final Expression<?> original;

    private final MappingType convertType;

    ConvertExpressionImpl(Expression<?> original, MappingType convertType) {
        this.original = original;
        this.convertType = convertType;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        original.appendSQL(context);
    }


    @Override
    public MappingType mappingType() {
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
