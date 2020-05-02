package io.army.criteria.impl;

import io.army.criteria.ParamExpression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.ParamWrapper;

final class ParamExpressionImp<E> extends AbstractNoNOperationExpression<E> implements ParamExpression<E> {

    static <E> ParamExpressionImp<E> build(@Nullable MappingMeta mappingType, @Nullable E value) {
        ParamExpressionImp<E> paramExpression;
        if (mappingType != null && value != null) {
            paramExpression = new ParamExpressionImp<>(mappingType, value);
        } else if (mappingType != null) {
            paramExpression = new ParamExpressionImp<>(mappingType);
        } else if (value != null) {
            paramExpression = new ParamExpressionImp<>(value);
        } else {
            throw new IllegalArgumentException("paramMeta then value all is null");
        }
        return paramExpression;
    }

    private final MappingMeta mappingType;

    private final E value;

    private ParamExpressionImp(MappingMeta mappingType, E value) {
        this.mappingType = mappingType;
        this.value = value;
    }

    private ParamExpressionImp(MappingMeta mappingType) {
        Assert.notNull(mappingType, "");
        this.mappingType = mappingType;
        this.value = null;
    }

    private ParamExpressionImp(E value) {
        Assert.notNull(value, "");
        this.value = value;
        this.mappingType = MappingFactory.getDefaultMapping(this.value.getClass());
    }


    @Override
    public E value() {
        return value;
    }

    @Override
    public MappingMeta paramMeta() {
        return mappingType;
    }

    @Override
    public Selection as(String alias) {
        return super.as(alias);
    }

    @Override
    protected void afterSpace(SQLContext context) {
        context.sqlBuilder().append("?");
        context.appendParam(ParamWrapper.build(mappingType, value));
    }

    @Override
    public String toString() {
        return "?";
    }

}
