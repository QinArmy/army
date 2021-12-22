package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.NonNull;
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
final class LiteralExpression<E> extends OperationExpression<E> implements ValueExpression<E> {

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
    static <E> LiteralExpression<E> literal(final ParamMeta paramMeta, final E literal) {
        ParamMeta actualParamMeta;
        if (paramMeta == null) {
            actualParamMeta = _MappingFactory.getMapping(literal.getClass());
        } else {
            if (paramMeta.mappingType().javaType() != literal.getClass()) {
                throw new IllegalArgumentException(String.format("constant's class[%s] and paramMeta[%s] not match."
                        , literal.getClass().getName(), paramMeta.getClass().getName()));
            }
            actualParamMeta = paramMeta;
        }
        final LiteralExpression<E> cacheExp = (LiteralExpression<E>) CONSTANT_EXP_CACHE.get(literal);

        LiteralExpression<E> exp;
        if (cacheExp != null && cacheExp.mappingType() == actualParamMeta.mappingType()) {
            exp = cacheExp;
        } else {
            exp = new LiteralExpression<>(actualParamMeta, literal);
            if (cacheExp == null && CONSTANT_KEYS.contains(literal)) {
                CONSTANT_EXP_CACHE.put(literal, exp);
            }
        }
        return exp;
    }

    static <E> LiteralExpression<E> literal(final E constant) {
        Objects.requireNonNull(constant);
        return literal(_MappingFactory.getMapping(constant.getClass()), constant);
    }

    private final ParamMeta paramMeta;

    private final E literal;

    private LiteralExpression(ParamMeta paramMeta, E literal) {
        this.paramMeta = paramMeta;
        this.literal = literal;
    }


    @Override
    public Selection as(String alias) {
        return new ExpressionSelection(this, alias);
    }

    @Override
    public void appendSql(final _SqlContext context) {
        context.sqlBuilder()
                .append(Constant.SPACE)
                .append(context.dialect().literal(this.paramMeta, this.literal));
    }

    @Override
    public MappingType mappingType() {
        return this.paramMeta.mappingType();
    }

    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @NonNull
    @Override
    public E value() {
        return this.literal;
    }


    @Override
    public boolean containsSubQuery() {
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(this.literal);
    }


}
