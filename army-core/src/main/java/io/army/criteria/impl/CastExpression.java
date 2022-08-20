package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;

class CastExpression extends OperationExpression {

    static CastExpression cast(Expression expression, TypeMeta paramMeta) {
        return new CastExpression(expression, paramMeta);
    }

    private final _Expression expression;

    private final TypeMeta paramMeta;

    private CastExpression(Expression expression, TypeMeta paramMeta) {
        this.expression = (_Expression) expression;
        this.paramMeta = paramMeta;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        this.expression.appendSql(context);
    }


    @Override
    public TypeMeta typeMeta() {
        return this.paramMeta;
    }


    @Override
    public final String toString() {
        return String.format(" CAST(%s )", this.expression);
    }


}
