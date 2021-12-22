package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

final class BracketsExpression<E> extends OperationExpression<E> {

    static <E> BracketsExpression<E> bracket(final Expression<E> expression) {
        final BracketsExpression<E> result;
        if (expression instanceof BracketsExpression) {
            result = (BracketsExpression<E>) expression;
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
    public MappingType mappingType() {
        return this.expression.mappingType();
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
