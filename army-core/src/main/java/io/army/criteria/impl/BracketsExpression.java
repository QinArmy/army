package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TableField;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;

final class BracketsExpression<I extends Item> extends OperationExpression<I> {

    static <I extends Item> OperationExpression<I> bracket(final OperationExpression<I> expression) {
        final OperationExpression<I> result;
        if (expression instanceof BracketsExpression
                || expression instanceof TableField
                || expression instanceof SqlValueParam.SingleValue) {
            result = expression;
        } else {
            result = new BracketsExpression<>(expression);
        }
        return result;
    }

    private final ArmyExpression expression;

    private BracketsExpression(OperationExpression<I> expression) {
        super(expression.function);
        this.expression = expression;
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
