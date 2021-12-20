package io.army.criteria.impl;

import io.army.criteria.NamedParamExpression;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

final class NamedParamImpl<E> extends NoNOperationExpression<E>
        implements NamedParamExpression<E> {

    static <E> NamedParamImpl<E> create(String name, ParamMeta paramMeta) {
        return new NamedParamImpl<>(name, paramMeta);
    }

    private final String name;

    private final ParamMeta paramMeta;

    private NamedParamImpl(String name, ParamMeta paramMeta) {
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
    protected void afterSpace(_SqlContext context) {
        context.sqlBuilder()
                .append("?");
        context.appendParam(this);

    }

    @Override
    public final MappingType mappingType() {
        throw new UnsupportedOperationException();
    }
}
