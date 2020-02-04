package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.MappingType;

import java.util.List;

final class ConvertExpression<E> extends AbstractExpression<E> {

    private final Expression<?> actual;

    private final MappingType convertType;

    ConvertExpression(Expression<?> actual, MappingType convertType) {
        this.actual = actual;
        this.convertType = convertType;
    }


    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        actual.appendSQL(builder, paramWrapperList);
    }

    @Override
    public MappingType mappingType() {
        return convertType;
    }
}
