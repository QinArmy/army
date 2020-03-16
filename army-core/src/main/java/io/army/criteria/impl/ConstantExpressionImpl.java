package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.SQLContext;
import io.army.dialect.DialectUtils;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.util.List;


final class ConstantExpressionImpl<E> extends AbstractExpression<E> implements ConstantExpression<E> {


    static <E> ConstantExpressionImpl<E> build(@Nullable MappingType mappingType, E constant) {
        MappingType type;
        if (mappingType == null) {
            type = MappingFactory.getDefaultMapping(constant.getClass());
        } else {
            type = mappingType;
        }
        return new ConstantExpressionImpl<>(type, constant);
    }

    private final MappingType mappingType;

    private final E constant;

    private ConstantExpressionImpl(MappingType mappingType, E constant) {
        this.mappingType = mappingType;
        this.constant = constant;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        context.appendTextValue(mappingType,constant);
    }

    @Override
    public MappingType mappingType() {
        return mappingType;
    }

    @Override
    public E constant() {
        return constant;
    }

    @Override
    public String beforeAs() {
        return mappingType.nonNullTextValue(constant);
    }
}
