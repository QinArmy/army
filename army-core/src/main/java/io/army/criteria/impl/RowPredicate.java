package io.army.criteria.impl;

import io.army.criteria.ExpressionRow;
import io.army.criteria.GenericField;
import io.army.criteria.RowSubQuery;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

final class RowPredicate extends AbstractPredicate {

    private final ExpressionRow<?> row;

    private final DualOperator operator;

    private final RowSubQuery rowSubQuery;

    RowPredicate(ExpressionRow<?> row, DualOperator operator, RowSubQuery rowSubQuery) {
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
    public final boolean containsSubQuery() {
        return true;
    }

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        boolean contains = false;
        for (GenericField<?, ?> fieldMeta : this.row.columnList()) {
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
        for (GenericField<?, ?> fieldMeta : this.row.columnList()) {
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
        for (GenericField<?, ?> fieldMeta : this.row.columnList()) {
            if (fieldMeta.tableMeta() == tableMeta) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return row + " " + operator.rendered() + " " + rowSubQuery;
    }
}
