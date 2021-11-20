package io.army.criteria.impl;

import io.army.criteria.NamedParamExpression;
import io.army.criteria.SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

final class NamedParamExpressionImpl<E> extends AbstractNoNOperationExpression<E>
        implements NamedParamExpression<E> {

    static <E> NamedParamExpressionImpl<E> build(String name, ParamMeta paramMeta) {
        return new NamedParamExpressionImpl<>(name, paramMeta);
    }

    private final String name;

    private final ParamMeta paramMeta;

    private NamedParamExpressionImpl(String name, ParamMeta paramMeta) {
        this.name = name;
        this.paramMeta = paramMeta;
    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public Object value() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void afterSpace(SqlContext context) {
        context.sqlBuilder()
                .append("?");
        context.appendParam(this);

    }

    @Override
    public final MappingType mappingMeta() {
        throw new UnsupportedOperationException();
    }
}
