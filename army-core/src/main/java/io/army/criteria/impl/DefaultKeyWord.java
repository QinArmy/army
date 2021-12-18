package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;

public final class DefaultKeyWord<E> extends AbstractNoNOperationExpression<E> {

    static DefaultKeyWord<?> INSTANCE = new DefaultKeyWord<>();


    private DefaultKeyWord() {
    }


    @Override
    public MappingType mappingMeta() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    protected void afterSpace(_SqlContext context) {
        context.sqlBuilder().append(" DEFAULT");
    }


}
