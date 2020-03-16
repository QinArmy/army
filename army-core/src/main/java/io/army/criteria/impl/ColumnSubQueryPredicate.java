package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util.Assert;

final class ColumnSubQueryPredicate extends AbstractPredicate {

    private final Expression<?> operand;

    private final DualOperator operator;

    private final KeyOperator keyOperator;

    private final ColumnSubQuery<?> columnSubQuery;

    ColumnSubQueryPredicate(Expression<?> operand, DualOperator operator
            , KeyOperator keyOperator, ColumnSubQuery<?> columnSubQuery) {

        Assert.isTrue(operator.relational()
                        || operator == DualOperator.IN
                        || operator == DualOperator.NOT_IN
                , "operator error.");

        this.operand = operand;
        this.operator = operator;
        this.keyOperator = keyOperator;
        this.columnSubQuery = columnSubQuery;
    }


    @Override
    protected void afterSpace(SQLContext context) {
        operand.appendSQL(context);
        context.stringBuilder()
                .append(" ")
                .append(operator.rendered())
                .append(" ")
                .append(keyOperator.rendered())
                .append(" ");
        columnSubQuery.appendSQL(context);
    }

    @SuppressWarnings("all")
    @Override
    protected String beforeAs() {
        return new StringBuilder()
                .append("(")
                .append(operand)
                .append(" ")
                .append(operator.rendered())
                .append(" ")
                .append(keyOperator.rendered())
                .append(" ")
                .append(columnSubQuery)
                .append(")")
                .toString();

    }
}
