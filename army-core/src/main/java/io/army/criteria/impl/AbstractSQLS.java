package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.Expression;
import io.army.criteria.ParamExpression;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

abstract class AbstractSQLS {

     AbstractSQLS() {
        throw new UnsupportedOperationException();
    }


    public static <E> ParamExpression<E> asNull(Class<?> nullTypeClass) {
        return ParamExpressionImp.build(MappingFactory.getDefaultMapping(nullTypeClass), null);
    }

    public static <E> ParamExpression<E> asNull(MappingType mappingType) {
        return ParamExpressionImp.build(mappingType, null);
    }

    public static <E> ParamExpression<E> param(E param) {
        return ParamExpressionImp.build(null, param);
    }

    public static <E> ParamExpression<E> param(E param, MappingType mappingType) {
        return ParamExpressionImp.build(mappingType, param);
    }

    static <E> ParamExpression<E> param(E param, Expression<E> expression) {
        return ParamExpressionImp.build(expression.mappingType(), param);
    }

    public static <E> ConstantExpression<E> constant(E value) {
        return ConstantExpressionImpl.build(null, value);
    }

    public static <E> ConstantExpression<E> constant(E value, @Nullable MappingType mappingType) {
        return ConstantExpressionImpl.build(mappingType, value);
    }

    static <E> ConstantExpression<E> constant(E value, Expression<E> expression) {
        return ConstantExpressionImpl.build(expression.mappingType(), value);
    }

}
