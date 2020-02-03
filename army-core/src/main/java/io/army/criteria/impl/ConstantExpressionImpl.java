package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.lang.Nullable;


final class ConstantExpressionImpl<E> extends AbstractExpression<E> implements ConstantExpression<E> {

    private static final ConstantExpressionImpl<?> NULL = new ConstantExpressionImpl<>(null);

    @SuppressWarnings("unchecked")
    static <E> ConstantExpressionImpl<E> build(@Nullable E constant) {
        ConstantExpressionImpl<E> expression;
        if (constant == null) {
            expression = (ConstantExpressionImpl<E>) NULL;
        } else {
            expression = new ConstantExpressionImpl<>(constant);
        }
        return expression;
    }

    private final E constant;

    private ConstantExpressionImpl(@Nullable E constant) {
        this.constant = constant;
    }

    @Override
    public Class<?> javaType() {
        return constant == null
                ? void.class
                : constant.getClass();
    }

    @Nullable
    @Override
    public E constant() {
        return constant;
    }
}
