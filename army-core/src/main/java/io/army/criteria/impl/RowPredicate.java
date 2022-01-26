package io.army.criteria.impl;

import io.army.criteria.ExpressionRow;
import io.army.criteria.SubQuery;
import io.army.dialect._SqlContext;

final class RowPredicate extends OperationPredicate {

    private final ExpressionRow row;

    private final DualOperator operator;

    private final SubQuery rowSubQuery;

    RowPredicate(ExpressionRow row, DualOperator operator, SubQuery rowSubQuery) {
        this.row = row;
        this.operator = operator;
        this.rowSubQuery = rowSubQuery;
    }


    @Override
    public void appendSql(_SqlContext context) {
//        row.appendSql(context);
//        context.sqlBuilder()
//                .append(" ")
//                .append(operator.rendered());
        //rowSubQuery.appendSql(context);
    }



    @Override
    public String toString() {
        return row + " " + operator.rendered() + " " + rowSubQuery;
    }
}
