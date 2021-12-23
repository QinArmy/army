package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;

final class BracketsExpression<E> extends OperationExpression<E> {

    static <E> Expression<E> bracket(final Expression<E> expression) {
        final Expression<E> result;
        if (expression instanceof BracketsExpression
                || expression instanceof UnaryExpression
                || expression instanceof GenericField) {
            result = expression;
        } else {
            result = new BracketsExpression<>(expression);
        }
        return result;
    }

    private final _Expression<E> expression;

    private BracketsExpression(Expression<E> expression) {
        this.expression = (_Expression<E>) expression;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET);

        this.expression.appendSql(context);

        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }


    @Override
    public ParamMeta paramMeta() {
        return this.expression.paramMeta();
    }

    @Override
    public String toString() {
        return String.format(" (%s )", this.expression);
    }

    @Override
    public boolean containsSubQuery() {
        return this.expression.containsSubQuery();
    }


}
