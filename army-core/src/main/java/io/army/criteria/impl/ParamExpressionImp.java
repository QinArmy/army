package io.army.criteria.impl;

import io.army.criteria.ParamExpression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.dialect.ParamWrapper;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

final class ParamExpressionImp<E> extends AbstractNoNOperationExpression<E> implements ParamExpression<E> {

    static <E> ParamExpressionImp<E> build(@Nullable MappingType mappingType, @Nullable E value) {
        ParamExpressionImp<E> paramExpression;
        if (mappingType != null && value != null) {
            paramExpression = new ParamExpressionImp<>(mappingType, value);
        } else if (mappingType != null) {
            paramExpression = new ParamExpressionImp<>(mappingType);
        } else if (value != null) {
            paramExpression = new ParamExpressionImp<>(value);
        } else {
            throw new IllegalArgumentException("mappingType then value all is null");
        }
        return paramExpression;
    }

    private final MappingType mappingType;

    private final E value;

    private ParamExpressionImp(MappingType mappingType, E value) {
        this.mappingType = mappingType;
        this.value = value;
    }

    private ParamExpressionImp(MappingType mappingType) {
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
    public MappingType mappingType() {
        return mappingType;
    }

    @Override
    public Selection as(String alias) {
        return super.as(alias);
    }

    @Override
    protected void afterSpace(SQLContext context) {
        context.stringBuilder().append("?");
        context.appendParam(ParamWrapper.build(mappingType, value));
    }

    @Override
    public String toString() {
        return "?";
    }

    @Override
    public Boolean sortExp() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }
}
