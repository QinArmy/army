package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;

class CastExpression<E> extends OperationExpression<E> {

    static <O> CastExpression<O> cast(Expression<?> expression, ParamMeta paramMeta) {
        return new CastExpression<>(expression, paramMeta);
    }

    private final _Expression<?> expression;

    private final ParamMeta paramMeta;

    private CastExpression(Expression<?> expression, ParamMeta paramMeta) {
        this.expression = (_Expression<?>) expression;
        this.paramMeta = paramMeta;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        this.expression.appendSql(context);
    }


    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }


    @Override
    public final String toString() {
        return String.format(" CAST(%s )", this.expression);
    }


}
