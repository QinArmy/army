package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.SqlBuilder;
import io.army.dialect.SqlDialect;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ExpressionRowImpl<T extends IDomain> implements ExpressionRow<T> {

    private final List<FieldMeta<T, ?>> columnList;

    ExpressionRowImpl(List<FieldMeta<T, ?>> columnList) {
        List<FieldMeta<T, ?>> list = new ArrayList<>(columnList);
        this.columnList = Collections.unmodifiableList(list);
    }

    @Override
    public List<FieldMeta<T, ?>> columnList() {
        return columnList;
    }

    @Override
    public void appendSQL(SqlContext context) {
        SqlBuilder builder = context.sqlBuilder()
                .append(" ");
        SqlDialect sql = context.dql();
        if (sql.hasRowKeywords()) {
            builder.append("ROW");
        }
        builder.append("( ");
        int index = 0;
        for (FieldMeta<T, ?> fieldMeta : this.columnList) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(sql.quoteIfNeed(fieldMeta.fieldName()));
            index++;
        }
        builder.append(" )");
    }


    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ROW(");
        int index = 0;
        for (FieldMeta<T, ?> fieldMeta : this.columnList) {
            if(index > 0){
                builder.append(",");
            }
            builder.append(fieldMeta.fieldName());
            index++;
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
