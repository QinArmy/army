package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
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
        final StringBuilder builder = context.sqlBuilder()
                .append(" CAST(");
        this.expression.appendSql(context);
        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }

    @Override
    public final MappingType mappingType() {
        return this.paramMeta.mappingType();
    }

    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public final boolean containsSubQuery() {
        return this.expression.containsSubQuery();
    }

    @Override
    public final String toString() {
        return String.format(" CAST(%s )", this.expression);
    }


}
