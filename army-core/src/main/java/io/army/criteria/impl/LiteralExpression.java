package io.army.criteria.impl;

import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.NonNull;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;

import java.util.Objects;
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


    static <E> LiteralExpression<E> literal(final ParamMeta paramMeta, final E literal) {
        return new LiteralExpression<>(paramMeta, literal);
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
    public void appendSql(final _SqlContext context) {
        context.sqlBuilder()
                .append(Constant.SPACE)
                .append(context.dialect().literal(this.paramMeta, this.literal));
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
