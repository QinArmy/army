package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


final class ConstantExpressionImpl<E> extends AbstractExpression<E> implements ConstantExpression<E> {

    private static final ConcurrentMap<Object, ConstantExpression<?>> CONSTANT_EXP_CACHE = createConstantExpCache();

    private static ConcurrentMap<Object, ConstantExpression<?>> createConstantExpCache() {
        ConcurrentMap<Object, ConstantExpression<?>> map = new ConcurrentHashMap<>();

        map.put(0, null);
        map.put(1, null);

        map.put(0L, null);
        map.put(1L, null);

        map.put(BigDecimal.ZERO, null);
        map.put(BigDecimal.ONE, null);
        map.put(new BigDecimal("0.00"), null);
        map.put(new BigDecimal("1.00"), null);

        map.put(new BigInteger("0"), null);
        map.put(new BigInteger("1"), null);
        return map;
    }

    @SuppressWarnings("unchecked")
    static <E> ConstantExpression<E> build(final @Nullable MappingType mappingType, final E constant) {

        MappingType type;
        if (mappingType == null) {
            type = MappingFactory.getDefaultMapping(constant.getClass());
        } else {
            type = mappingType;
        }

        final ConstantExpression<E> cacheExp = (ConstantExpression<E>) CONSTANT_EXP_CACHE.get(constant);

        ConstantExpression<E> exp;
        if (cacheExp != null && cacheExp.mappingType() == type) {
            exp = cacheExp;
        } else {
            exp = new ConstantExpressionImpl<>(type, constant);
            if (cacheExp == null && CONSTANT_EXP_CACHE.containsKey(constant)) {
                CONSTANT_EXP_CACHE.put(constant, exp);
            }
        }
        return exp;
    }

    private final MappingType mappingType;

    private final E constant;

    private ConstantExpressionImpl(MappingType mappingType, E constant) {
        this.mappingType = mappingType;
        this.constant = constant;
    }


    @Override
    public Selection as(String alias) {
        return new ConstantSelection<>(this, alias);
    }

    @Override
    protected void afterSpace(SQLContext context) {
        context.appendTextValue(mappingType, constant);
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

    /*################################## blow static inner method ##################################*/

    private static final class ConstantSelection<E> implements Selection {

        private final ConstantExpression<E> constant;

        private final String alias;

        private ConstantSelection(ConstantExpression<E> constant, String alias) {
            this.constant = constant;
            this.alias = alias;
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public MappingType mappingType() {
            return constant.mappingType();
        }

        @Override
        public void appendSQL(SQLContext context) {
            constant.appendSQL(context);
            context.stringBuilder()
                    .append(" AS ");
            context.quoteIfKeyAndAppend(this.alias);

        }

        @Override
        public String toString() {
            return this.constant.constant() + " AS " + this.alias;
        }
    }
}
