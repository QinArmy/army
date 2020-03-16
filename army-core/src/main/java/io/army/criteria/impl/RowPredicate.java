package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Row;
import io.army.criteria.RowSubQuery;
import io.army.criteria.SQLContext;

final class RowPredicate extends AbstractPredicate {

    private final Row row;

    private final DualOperator operator;

    private final RowSubQuery rowSubQuery;

    RowPredicate(Row row, DualOperator operator, RowSubQuery rowSubQuery) {
        this.row = row;
        this.operator = operator;
        this.rowSubQuery = rowSubQuery;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        row.appendSQL(context);
        context.stringBuilder()
                .append(" ")
                .append(operator.rendered())
                .append(" ");
        rowSubQuery.appendSQL(context);
    }


    @Override
    protected String beforeAs() {
        return row.toString() + " " + operator.rendered() + " " + rowSubQuery;
    }
}
