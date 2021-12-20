package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.ValueExpression;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

final class ParamExpression<E> extends NoNOperationExpression<E> implements ValueExpression<E>, ParamValue {


    static <E> ParamExpression<E> create(ParamMeta paramMeta, @Nullable E value) {
        if (value instanceof Expression || value instanceof Function || value instanceof Collection) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            throw new CriteriaException(String.format("Value[%s] couldn't create param expression.", value));
        }
        return new ParamExpression<>(paramMeta, value);
    }

    static <E> ParamExpression<E> create(E value) {
        Objects.requireNonNull(value);
        return create(_MappingFactory.getMapping(value.getClass()), value);
    }

    private final ParamMeta paramMeta;

    private final E value;

    private ParamExpression(ParamMeta paramMeta, @Nullable E value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }


    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public E value() {
        return this.value;
    }


    @Override
    public MappingType mappingType() {
        return this.paramMeta.mappingType();
    }


    @Override
    public void appendSql(final _SqlContext context) {
        context.appendParam(this);
    }

    @Override
    public String toString() {
        return " ?";
    }

}
