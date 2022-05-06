package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.stmt.StrictParamValue;

final class ParamExpression extends OperationExpression implements ValueExpression, StrictParamValue {


    static ValueExpression create(final ParamMeta paramMeta, final @Nullable Object value) {
        return new ParamExpression(paramMeta, value);
    }

    final ParamMeta paramMeta;

    final Object value;


    private ParamExpression(ParamMeta paramMeta, @Nullable Object value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }


    @Nullable
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


}
