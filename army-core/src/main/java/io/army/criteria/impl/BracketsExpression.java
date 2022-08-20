package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TableField;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;

final class BracketsExpression extends OperationExpression {

    static Expression bracket(final Expression expression) {
        final Expression result;
        if (expression instanceof BracketsExpression
                || expression instanceof TableField
                || expression instanceof SqlValueParam.SingleValue) {
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
                .append(_Constant.SPACE_LEFT_PAREN);

        this.expression.appendSql(context);

        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    @Override
    public TypeMeta typeMeta() {
        return this.expression.typeMeta();
    }

    @Override
    public String toString() {
        return String.format(" (%s )", this.expression);
    }


}
