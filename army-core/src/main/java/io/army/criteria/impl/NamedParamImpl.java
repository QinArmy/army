package io.army.criteria.impl;

import io.army.criteria.NamedParam;
import io.army.criteria.NonNullNamedParam;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.Objects;

/**
 * <p>
 * This class is a implementation of {@link NamedParam}.
 * </p>
 */
class NamedParamImpl extends OperationExpression implements NamedParam {

    static NamedParam nullable(String name, ParamMeta paramMeta) {
        Objects.requireNonNull(name);
        return new NamedParamImpl(name, paramMeta);
    }

    static NonNullNamedParam nonNull(String name, ParamMeta paramMeta) {
        Objects.requireNonNull(name);
        return new NonNullNamedParamImpl(name, paramMeta);
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
        throw _Exceptions.namedParamInNonBatch(this);
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        context.appendParam(this);
    }

    @Override
    public final String toString() {
        return " ?:" + this.name;
    }


    /**
     * <p>
     * This class is a implementation of {@link NonNullNamedParam}.
     * </p>
     *
     */
    private static final class NonNullNamedParamImpl extends NamedParamImpl implements NonNullNamedParam {

        private NonNullNamedParamImpl(String name, ParamMeta paramMeta) {
            super(name, paramMeta);
        }


    }


}
