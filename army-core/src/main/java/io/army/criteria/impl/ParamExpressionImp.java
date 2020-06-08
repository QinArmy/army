package io.army.criteria.impl;

import io.army.criteria.ParamExpression;
import io.army.criteria.SQLContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;

final class ParamExpressionImp<E> extends AbstractNoNOperationExpression<E> implements ParamExpression<E> {


    static <E> ParamExpressionImp<E> build(@Nullable ParamMeta paramMeta, @Nullable E value) {
        ParamExpressionImp<E> paramExpression;
        if (paramMeta != null && value != null) {
            paramExpression = new ParamExpressionImp<>(paramMeta, value);
        } else if (paramMeta != null) {
            paramExpression = new ParamExpressionImp<>(paramMeta);
        } else if (value != null) {
            paramExpression = new ParamExpressionImp<>(value);
        } else {
            throw new IllegalArgumentException("paramMeta then value all is null");
        }
        return paramExpression;
    }

    private final ParamMeta paramMeta;

    private final E value;

    private ParamExpressionImp(ParamMeta paramMeta, E value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }

    private ParamExpressionImp(ParamMeta paramMeta) {
        Assert.notNull(paramMeta, "");
        this.paramMeta = paramMeta;
        this.value = null;
    }

    private ParamExpressionImp(E value) {
        Assert.notNull(value, "");
        this.value = value;
        this.paramMeta = MappingFactory.getDefaultMapping(this.value.getClass());
    }


    @Override
    public final E value() {
        return value;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public final MappingMeta mappingMeta() {
        return this.paramMeta.mappingMeta();
    }


    @Override
    protected void afterSpace(SQLContext context) {
        context.sqlBuilder().append("?");
        context.appendParam(this);
    }

    @Override
    public String toString() {
        return "?";
    }

}
