package io.army.criteria.impl;

import io.army.criteria.*;

import java.util.Iterator;
import java.util.List;

final class RowImpl implements Row {

    private final List<Expression<?>> columnList;

    RowImpl(List<Expression<?>> columnList) {
        for (Expression<?> expression : columnList) {
            if (expression instanceof ParamExpression) {
                throw new IllegalArgumentException("columnList of row can't contains ParamExpression.");
            }
        }
        this.columnList = columnList;
    }

    @Override
    public List<Expression<?>> columnList() {
        return columnList;
    }

    @Override
    public void appendSQL(SQLContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append("Row(");

        for (Iterator<Expression<?>> iterator = columnList.iterator(); iterator.hasNext(); ) {
            Expression<?> expression = iterator.next();
            expression.appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(")");
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ROW(");
        for (Iterator<Expression<?>> iterator = columnList.iterator(); iterator.hasNext(); ) {
            Expression<?> expression = iterator.next();
            builder.append(expression);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public IPredicate eq(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.EQ, rowSubQuery);
    }

    @Override
    public IPredicate notEq(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.NOT_EQ, rowSubQuery);
    }

    @Override
    public IPredicate lt(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.LT, rowSubQuery);
    }

    @Override
    public IPredicate le(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.LE, rowSubQuery);
    }

    @Override
    public IPredicate gt(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.GT, rowSubQuery);
    }

    @Override
    public IPredicate ge(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.GE, rowSubQuery);
    }

    @Override
    public IPredicate in(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.IN, rowSubQuery);
    }

    @Override
    public IPredicate notIn(RowSubQuery rowSubQuery) {
        return new RowPredicate(this, DualPredicateOperator.NOT_IN, rowSubQuery);
    }
}
