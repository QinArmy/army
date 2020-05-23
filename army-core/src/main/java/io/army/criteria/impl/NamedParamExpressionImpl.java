package io.army.criteria.impl;

import io.army.criteria.NamedParamExpression;
import io.army.criteria.SQLContext;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;

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
    protected void afterSpace(SQLContext context) {
        context.sqlBuilder()
                .append("?");
        context.appendParam(this);

    }

    @Override
    public final MappingMeta mappingMeta() {
        throw new UnsupportedOperationException();
    }
}
