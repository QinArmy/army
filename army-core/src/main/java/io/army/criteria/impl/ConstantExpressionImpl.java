package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
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

        ParamMeta type;
        if (paramMeta == null) {
            type = MappingFactory.getDefaultMapping(constant.getClass());
        } else {
            type = paramMeta;
        }

        final ConstantExpression<E> cacheExp = (ConstantExpression<E>) CONSTANT_EXP_CACHE.get(constant);

        ConstantExpression<E> exp;
        if (cacheExp != null && cacheExp.mappingMeta() == type) {
            exp = cacheExp;
        } else {
            exp = new ConstantExpressionImpl<>(type, constant);
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
    public Selection as(String alias) {
        return new DefaultSelection(this, alias);
    }

    @Override
    protected void appendSQL(SQLContext context) {
        context.appendTextValue(this.paramMeta.mappingMeta(), this.constant);
    }

    @Override
    public MappingMeta mappingMeta() {
        return this.paramMeta.mappingMeta();
    }

    @Override
    public E value() {
        return constant;
    }

    @Override
    public String toString() {
        return this.paramMeta.mappingMeta().nonNullTextValue(constant);
    }


}
