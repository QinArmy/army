package io.army.criteria.impl;

import io.army.criteria.DualPredicateOperator;
import io.army.criteria.ExpressionRow;
import io.army.criteria.RowSubQuery;
import io.army.criteria.SQLContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

final class RowPredicate extends AbstractPredicate {

    private final ExpressionRow<?> row;

    private final DualPredicateOperator operator;

    private final RowSubQuery rowSubQuery;

    RowPredicate(ExpressionRow<?> row, DualPredicateOperator operator, RowSubQuery rowSubQuery) {
        this.row = row;
        this.operator = operator;
        this.rowSubQuery = rowSubQuery;
    }


    @Override
    public final void appendSQL(SQLContext context) {
        row.appendSQL(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered());
        rowSubQuery.appendSQL(context);
    }

    @Override
    public final boolean containsSubQuery() {
        return true;
    }

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        boolean contains = false;
        for (FieldMeta<?, ?> fieldMeta : this.row.columnList()) {
            if (fieldMetas.contains(fieldMeta)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        boolean contains = false;
        for (FieldMeta<?, ?> fieldMeta : this.row.columnList()) {
            if (fieldMeta.tableMeta() == tableMeta) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        int count = 0;
        for (FieldMeta<?, ?> fieldMeta : this.row.columnList()) {
            if (fieldMeta.tableMeta() == tableMeta) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return row.toString() + " " + operator.rendered() + " " + rowSubQuery;
    }
}
