package io.army.criteria.impl;

import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

abstract class ParamExpression<E> extends OperationExpression<E> implements ValueExpression<E>, ParamValue {


    static <E> ValueExpression<E> strict(final ParamMeta paramMeta, final @Nullable E value) {
        final ValueExpression<E> expression;
        if (value == null) {
            expression = new StrictNullExpression<>(paramMeta);
        } else {
            expression = new StrictParamExpression<>(paramMeta, value);
        }
        return expression;
    }


    static <E> ValueExpression<E> optimizing(final ParamMeta paramMeta, final @Nullable E value) {
        final ValueExpression<E> expression;
        if (value == null) {
            expression = new OptimizingNullExpression<>(paramMeta);
        } else {
            expression = new OptimizingParamExpression<>(paramMeta, value);
        }
        return expression;
    }

    final ParamMeta paramMeta;

    final E value;


    private ParamExpression(ParamMeta paramMeta, E value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }


    public final E value() {
        return this.value;
    }

    public final ParamMeta paramMeta() {
        return this.paramMeta;
    }


    @Override
    public final String toString() {
        return " ?";
    }


    private static final class StrictParamExpression<E> extends ParamExpression<E> implements _StrictParam {

        private StrictParamExpression(ParamMeta paramMeta, E value) {
            super(paramMeta, value);
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.appendParam(this);
        }

    }// StrictParamExpression

    static final class StrictNullExpression<E> extends NoNOperationExpression<E>
            implements _StrictParam, ValueExpression<E> {

        private final ParamMeta paramMeta;

        private StrictNullExpression(ParamMeta paramMeta) {
            this.paramMeta = paramMeta;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }

        @Override
        public E value() {
            //always null
            return null;
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.appendParam(this);
        }

    }//StrictNullExpression

    private static final class OptimizingParamExpression<E> extends ParamExpression<E> {

        private OptimizingParamExpression(ParamMeta paramMeta, E value) {
            super(paramMeta, value);
        }

        @Override
        public void appendSql(_SqlContext context) {
            if (this.paramMeta.mappingType() instanceof _ArmyNoInjectionMapping) {
                context.sqlBuilder()
                        .append(Constant.SPACE)
                        .append(context.dialect().literal(this.paramMeta, this.value));
            } else {
                context.appendParam(this);
            }
        }

    }//OptimizingParamExpression


    static final class OptimizingNullExpression<E> extends NoNOperationExpression<E>
            implements ParamValue, ValueExpression<E> {

        private final ParamMeta paramMeta;


        private OptimizingNullExpression(ParamMeta paramMeta) {
            this.paramMeta = paramMeta;
        }


        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(Constant.SPACE_NULL);
        }

        @Override
        public E value() {
            // always null
            return null;
        }

        @Override
        public String toString() {
            return Constant.SPACE_NULL;
        }

    }


}
