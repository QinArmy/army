package io.army.criteria.impl;

import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

final class ParamExpressions<E> extends OperationExpression<E> implements ValueExpression<E>, ParamValue {


    static <E> ValueExpression<E> strict(final ParamMeta paramMeta, final @Nullable E value) {
        final ValueExpression<E> expression;
        if (value == null) {
            expression = new NullParamExpression<>(paramMeta, false);
        } else {
            expression = new ParamExpressions<>(paramMeta, value, false);
        }
        return expression;
    }


    static <E> ValueExpression<E> optimizing(final ParamMeta paramMeta, final @Nullable E value) {
        final ValueExpression<E> expression;
        if (value == null) {
            expression = new NullParamExpression<>(paramMeta, true);
        } else {
            expression = new ParamExpressions<>(paramMeta, value, true);
        }
        return expression;
    }

    private final ParamMeta paramMeta;

    private final E value;

    private final boolean optimizing;

    private ParamExpressions(ParamMeta paramMeta, E value, final boolean optimizing) {
        this.paramMeta = paramMeta;
        this.value = value;
        this.optimizing = optimizing && paramMeta.mappingType() instanceof _ArmyNoInjectionMapping;
    }


    public E value() {
        return this.value;
    }

    public ParamMeta paramMeta() {
        return this.paramMeta;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        if (this.optimizing) {
            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(context.dialect().literal(this.paramMeta, this.value));
        } else {
            context.appendParam(this);
        }

    }

    @Override
    public boolean containsSubQuery() {
        // always false
        return false;
    }

    @Override
    public String toString() {
        return " ?";
    }


    static final class NullParamExpression<E> extends NoNOperationExpression<E>
            implements ParamValue, ValueExpression<E> {

        private final ParamMeta paramMeta;

        private final boolean optimizing;

        private NullParamExpression(ParamMeta paramMeta, boolean optimizing) {
            this.paramMeta = paramMeta;
            this.optimizing = optimizing;
        }


        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            if (this.optimizing) {
                context.sqlBuilder()
                        .append(" NULL");
            } else {
                context.appendParam(this);
            }
        }

        @Override
        public E value() {
            // always null
            return null;
        }

        @Override
        public String toString() {
            return " NULL";
        }

    }


}
