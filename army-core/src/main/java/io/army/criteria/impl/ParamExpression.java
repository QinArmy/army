package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

abstract class ParamExpression<E> extends OperationExpression<E> implements ValueExpression<E>, ParamValue {


    static <E> ParamExpression<E> strict(ParamMeta paramMeta, @Nullable E value) {
        if (value instanceof Expression || value instanceof Function || value instanceof Collection) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            throw new CriteriaException(String.format("Value[%s] couldn't create param expression.", value));
        }
        return new StrictParamExpression<>(paramMeta, value);
    }

    static <E> ParamExpression<E> strict(Expression<?> type, @Nullable E value) {
        final ParamMeta paramMeta;
        if (type instanceof GenericField) {
            paramMeta = (GenericField<?, ?>) type;
        } else {
            paramMeta = type.mappingType();
        }
        return strict(paramMeta, value);
    }

    static <E> ParamExpression<E> strict(E value) {
        Objects.requireNonNull(value);
        return new StrictParamExpression<>(_MappingFactory.getMapping(value.getClass()), value);
    }

    static <E> ParamExpression<E> optimizing(final Expression<?> type, final @Nullable E value) {
        final ParamMeta paramMeta;
        if (type instanceof GenericField) {
            paramMeta = (GenericField<?, ?>) type;
        } else {
            paramMeta = type.mappingType();
        }
        return new OptimizingParamExpression<>(paramMeta, value);
    }

    static <E> ParamExpression<E> optimizing(final MappingType type, final @Nullable E value) {
        return new OptimizingParamExpression<>(type, value);
    }

    final ParamMeta paramMeta;

    final E value;

    private ParamExpression(ParamMeta paramMeta, @Nullable E value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }

    /**
     * @return a simple java object or {@link java.util.Collection}
     */
    @Nullable
    public final E value() {
        return this.value;
    }

    public final ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public final MappingType mappingType() {
        return this.paramMeta.mappingType();
    }

    @Override
    public final boolean containsSubQuery() {
        // always false
        return false;
    }

    @Override
    public final String toString() {
        return " ?";
    }


    private static final class StrictParamExpression<E> extends ParamExpression<E> {

        private StrictParamExpression(ParamMeta paramMeta, @Nullable E value) {
            super(paramMeta, value);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }

    }


    private static final class OptimizingParamExpression<E> extends ParamExpression<E> {

        private OptimizingParamExpression(ParamMeta paramMeta, @Nullable E value) {
            super(paramMeta, value);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            if (this.value == null) {
                context.sqlBuilder()
                        .append(" NULL");
            } else {
                final ParamMeta paramMeta = this.paramMeta;
                if (paramMeta.mappingType() instanceof _ArmyNoInjectionMapping) {
                    context.sqlBuilder()
                            .append(Constant.SPACE)
                            .append(context.dialect().literal(paramMeta, this.value));
                } else {
                    context.appendParam(this);
                }

            }


        }


    }


}
