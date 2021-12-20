package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.criteria.ValueExpression;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
import io.army.util.ArrayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>
 * This class representing sql literal expression.
 * </p>
 *
 * @param <E> The java type of sql literal.
 */
final class LiteralExpression<E> extends AbstractExpression<E> implements ValueExpression<E> {

    private static final ConcurrentMap<Object, LiteralExpression<?>> CONSTANT_EXP_CACHE = new ConcurrentHashMap<>();

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
    static <E> LiteralExpression<E> create(final ParamMeta paramMeta, final E constant) {
        Objects.requireNonNull(paramMeta);
        Objects.requireNonNull(constant);
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
        final LiteralExpression<E> cacheExp = (LiteralExpression<E>) CONSTANT_EXP_CACHE.get(constant);

        LiteralExpression<E> exp;
        if (cacheExp != null && cacheExp.mappingType() == actualParamMeta.mappingType()) {
            exp = cacheExp;
        } else {
            exp = new LiteralExpression<>(actualParamMeta, constant);
            if (cacheExp == null && CONSTANT_KEYS.contains(constant)) {
                CONSTANT_EXP_CACHE.put(constant, exp);
            }
        }
        return exp;
    }

    static <E> LiteralExpression<E> create(final E constant) {
        Objects.requireNonNull(constant);
        return create(_MappingFactory.getMapping(constant.getClass()), constant);
    }

    private final ParamMeta paramMeta;

    private final E constant;

    private LiteralExpression(ParamMeta paramMeta, E constant) {
        this.paramMeta = paramMeta;
        this.constant = constant;
    }


    @Override
    public Selection as(String alias) {
        return new ExpressionSelection(this, alias);
    }

    @Override
    public void appendSql(_SqlContext context) {
        context.appendConstant(this.paramMeta, this.constant);
    }

    @Override
    public MappingType mappingType() {
        return this.paramMeta.mappingType();
    }

    @Override
    public E value() {
        return this.constant;
    }

    @Override
    public boolean containsSubQuery() {
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(this.constant);
    }


}
