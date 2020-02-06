package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;

import java.util.List;

final class BracketsExpression<E> extends AbstractExpression<E> {

    private final Expression<E> exp;

    BracketsExpression(Expression<E> exp) {
        this.exp = exp;
    }

    @Override
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        builder.append("( ");
        exp.appendSQL(sql,builder, paramWrapperList);
        builder.append(")");
    }

    @Override
    public MappingType mappingType() {
        return exp.mappingType();
    }
}
