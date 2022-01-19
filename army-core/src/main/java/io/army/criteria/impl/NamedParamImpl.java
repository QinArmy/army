package io.army.criteria.impl;

import io.army.criteria.NamedParam;
import io.army.criteria.NonNullNamedParam;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;

/**
 * <p>
 * This class is a implementation of {@link NamedParam}.
 * </p>
 *
 * @param <E> java type of named parameter
 */
class NamedParamImpl<E> extends OperationExpression<E>
        implements NamedParam<E> {

    static <E> NamedParam<E> nullable(String name, ParamMeta paramMeta) {
        return new NamedParamImpl<>(name, paramMeta);
    }

    static <E> NonNullNamedParam<E> nonNull(String name, ParamMeta paramMeta) {
        return new NonNullNamedParamImpl<>(name, paramMeta);
    }

    private final String name;

    private final ParamMeta paramMeta;

    private NamedParamImpl(String name, ParamMeta paramMeta) {
        this.name = name;
        this.paramMeta = paramMeta;
    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public final Object value() {
        throw new UnsupportedOperationException("Named parameter no value in non batch update (or batch delete) statement.");
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        context.appendParam(this);
    }

    @Override
    public final String toString() {
        return " ?:" + this.name;
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    /**
     * <p>
     * This class is a implementation of {@link NonNullNamedParam}.
     * </p>
     *
     * @param <E> java type of named parameter
     */
    private static final class NonNullNamedParamImpl<E> extends NamedParamImpl<E> implements NonNullNamedParam<E> {

        private NonNullNamedParamImpl(String name, ParamMeta paramMeta) {
            super(name, paramMeta);
        }


    }


}
