package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.TableField;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;

final class BracketsExpression extends OperationExpression {

    static Expression bracket(final Expression expression) {
        final Expression result;
        if (expression instanceof BracketsExpression
                || expression instanceof TableField
                || expression instanceof ValueExpression) {
            result = expression;
        } else {
            result = new BracketsExpression(expression);
        }
        return result;
    }

    private final ArmyExpression expression;

    private BracketsExpression(Expression expression) {
        this.expression = (ArmyExpression) expression;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_LEFT_BRACKET);

        this.expression.appendSql(context);

        builder.append(Constant.SPACE_RIGHT_BRACKET);
    }


    @Override
    public ParamMeta paramMeta() {
        return this.expression.paramMeta();
    }

    @Override
    public String toString() {
        return String.format(" (%s )", this.expression);
    }


}
