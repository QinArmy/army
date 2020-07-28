package io.army.criteria.impl;

import io.army.criteria.DualPredicateOperator;
import io.army.criteria.ExpressionRow;
import io.army.criteria.RowSubQuery;
import io.army.criteria.SQLContext;

final class RowPredicate extends AbstractPredicate {

    private final ExpressionRow row;

    private final DualPredicateOperator operator;

    private final RowSubQuery rowSubQuery;

    RowPredicate(ExpressionRow row, DualPredicateOperator operator, RowSubQuery rowSubQuery) {
        this.row = row;
        this.operator = operator;
        this.rowSubQuery = rowSubQuery;
    }

    @Override
    protected void appendSQL(SQLContext context) {
        row.appendSQL(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered())
                .append(" ");
        rowSubQuery.appendSQL(context);
    }


    @Override
    protected String toString() {
        return row.toString() + " " + operator.rendered() + " " + rowSubQuery;
    }
}
