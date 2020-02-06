package io.army.criteria.impl;

import io.army.criteria.ConvertExpression;
import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
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
    protected void appendSQLBeforeWhitespace(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        original.appendSQL(builder, paramWrapperList);
    }

    @Override
    public MappingType mappingType() {
        return convertType;
    }

    @Override
    public Expression<?> originalExp() {
        return original;
    }


}
