package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.Selection;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
import io.army.util.ArrayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


final class ConstantExpressionImpl<E> extends AbstractExpression<E> implements ConstantExpression<E> {

    private static final ConcurrentMap<Object, ConstantExpression<?>> CONSTANT_EXP_CACHE = new ConcurrentHashMap<>();

    private static final Set<Object> CONSTANT_KEYS = ArrayUtils.asUnmodifiableSet(
            0,
            1,
            0L,
            1L,

            BigDecimal.ONE,
            BigDecimal.ZERO,
            new BigDecimal("0.00"),
            new BigDecimal("1.00"),

            new BigInteger("0"),
            new BigInteger("1")
    );


    @SuppressWarnings("unchecked")
    static <E> ConstantExpression<E> build(final @Nullable ParamMeta paramMeta, final E constant) {

        ParamMeta actualParamMeta;
        if (paramMeta == null) {
            actualParamMeta = _MappingFactory.getMapping(constant.getClass());
        } else {
            if (paramMeta.mappingType().javaType() != constant.getClass()) {
                throw new IllegalArgumentException(String.format("constant's class[%s] and paramMeta[%s] not match."
                        , constant.getClass().getName(), paramMeta.getClass().getName()));
            }
            actualParamMeta = paramMeta;
        }
        final ConstantExpression<E> cacheExp = (ConstantExpression<E>) CONSTANT_EXP_CACHE.get(constant);

        ConstantExpression<E> exp;
        if (cacheExp != null && cacheExp.mappingType() == actualParamMeta.mappingType()) {
            exp = cacheExp;
        } else {
            exp = new ConstantExpressionImpl<>(actualParamMeta, constant);
            if (cacheExp == null && CONSTANT_KEYS.contains(constant)) {
                CONSTANT_EXP_CACHE.put(constant, exp);
            }
        }
        return exp;
    }

    private final ParamMeta paramMeta;

    private final E constant;

    private ConstantExpressionImpl(ParamMeta paramMeta, E constant) {
        this.paramMeta = paramMeta;
        this.constant = constant;
    }


    @Override
    public final Selection as(String alias) {
        return new ExpressionSelection(this, alias);
    }

    @Override
    public final void appendSql(_SqlContext context) {
        context.appendConstant(this.paramMeta.mappingType(), this.constant);
    }

    @Override
    public final MappingType mappingType() {
        return this.paramMeta.mappingType();
    }

    @Override
    public final E value() {
        return this.constant;
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    @Override
    public final String toString() {
        return String.valueOf(this.constant);
    }


}
