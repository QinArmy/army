package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;

final class NullKeyWord<E> extends AbstractNoNOperationExpression<E> {


    @Override
    protected void afterSpace(_SqlContext context) {

    }

    @Override
    public void appendSql(_SqlContext context) {
        context.sqlBuilder().append(" NULL");
    }

    @Override
    public void appendSortPart(_SqlContext context) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public MappingType mappingMeta() {
        return null;
    }


}
