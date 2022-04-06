package io.army.criteria.impl;

import io.army.criteria.ExpressionRow;
import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._SqlContext;

import java.util.List;

final class ExpressionRowImpl implements ExpressionRow, _SelfDescribed {

    private final List<TableField<?>> columnList;

    public ExpressionRowImpl(List<TableField<?>> columnList) {
        this.columnList = columnList;
    }

    @Override
    public List<TableField<?>> fieldList() {
        return null;
    }

    @Override
    public void appendSql(_SqlContext context) {
//        StringBuilder builder = context.sqlBuilder()
//                .append(" ");
//        SqlDialect sql = context.dialect();
//        if (sql.hasRowKeywords()) {
//            builder.append("ROW");
//        }
//        builder.append("( ");
//        int index = 0;
//        for (FieldMeta<T, ?> fieldMeta : this.columnList) {
//            if (index > 0) {
//                builder.append(",");
//            }
//            builder.append(sql.quoteIfNeed(fieldMeta.fieldName()));
//            index++;
//        }
//        builder.append(" )");
    }


    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ROW(");
        int index = 0;
        for (TableField<?> fieldMeta : this.columnList) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(fieldMeta.fieldName());
            index++;
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public IPredicate eq(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.EQ, rowSubQuery);
    }

    @Override
    public IPredicate notEq(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.NOT_EQ, rowSubQuery);
    }

    @Override
    public IPredicate lt(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.LT, rowSubQuery);
    }

    @Override
    public IPredicate le(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.LE, rowSubQuery);
    }

    @Override
    public IPredicate gt(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.GT, rowSubQuery);
    }

    @Override
    public IPredicate ge(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.GE, rowSubQuery);
    }

    @Override
    public IPredicate in(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.IN, rowSubQuery);
    }

    @Override
    public IPredicate notIn(SubQuery rowSubQuery) {
        return new RowPredicate(this, DualOperator.NOT_IN, rowSubQuery);
    }
}
