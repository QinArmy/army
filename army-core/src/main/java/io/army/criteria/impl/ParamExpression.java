package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

final class ParamExpression extends OperationExpression implements ValueExpression, ParamValue {


    static ValueExpression create(final ParamMeta paramMeta, final @Nullable Object value) {
        final ValueExpression expression;
        if (value == null) {
            expression = new NullExpression(paramMeta);
        } else {
            expression = new ParamExpression(paramMeta, value);
        }
        return expression;
    }

    final ParamMeta paramMeta;

    final Object value;


    private ParamExpression(ParamMeta paramMeta, Object value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }


    public Object value() {
        return this.value;
    }

    public ParamMeta paramMeta() {
        return this.paramMeta;
    }


    @Override
    public void appendSql(_SqlContext context) {
        context.appendParam(this);
    }

    @Override
    public String toString() {
        return " ?";
    }


    static final class NullExpression extends NoNOperationExpression
            implements _StrictParam, ValueExpression {

        private final ParamMeta paramMeta;

        private NullExpression(ParamMeta paramMeta) {
            this.paramMeta = paramMeta;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.appendParam(this);
        }

    }//StrictNullExpression


}
