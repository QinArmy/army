package io.army.criteria.impl;

import io.army.criteria.ColumnSubQuery;
import io.army.criteria.DualPredicateOperator;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collection;

class ColumnSubQueryPredicate extends AbstractPredicate {

    static ColumnSubQueryPredicate build(Expression<?> operand, DualPredicateOperator operator
            , SubQueryOperator subQueryOperator, ColumnSubQuery<?> subQuery) {
        Assert.isTrue(operator.relational(), "operator isn't relational.");

        switch (subQueryOperator) {
            case ALL:
            case ANY:
            case SOME:
                return new RelationColumnSubQueryPredicate(operand, operator, subQueryOperator, subQuery);
            default:
                throw new IllegalArgumentException(String.format("SubQueryOperator[%s] error.", subQueryOperator));
        }
    }

    static ColumnSubQueryPredicate build(Expression<?> operand, DualPredicateOperator operator
            , ColumnSubQuery<?> subQuery) {
        switch (operator) {
            case IN:
            case NOT_IN:
                return new ColumnSubQueryPredicate(operand, operator, subQuery);
            default:
                throw new IllegalArgumentException(String.format("operator[%s] error.", operator));
        }
    }

    private final Expression<?> operand;

    private final DualPredicateOperator operator;

    private final ColumnSubQuery<?> subQuery;

    private ColumnSubQueryPredicate(Expression<?> operand, DualPredicateOperator operator, ColumnSubQuery<?> subQuery) {

        this.operand = operand;
        this.operator = operator;
        this.subQuery = subQuery;
    }


    @Override
    public void appendSQL(SQLContext context) {
        this.operand.appendSQL(context);
        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(this.operator.rendered());
        SubQueryOperator subQueryOperator = subQueryOperator();
        if (subQueryOperator != null) {
            builder.append(" ")
                    .append(subQueryOperator.rendered());
        }
        this.subQuery.appendSQL(context);
    }

    @SuppressWarnings("all")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("(")
                .append(operand)
                .append(" ")
                .append(operator.rendered());

        SubQueryOperator subQueryOperator = subQueryOperator();
        if (subQueryOperator != null) {
            builder.append(" ")
                    .append(subQueryOperator.rendered());
        }
        builder.append(" ")
                .append(subQuery)
                .append(")")
                .toString();

        return builder.toString();

    }

    @Nullable
    SubQueryOperator subQueryOperator() {
        return null;
    }

    @Override
    public final boolean containsSubQuery() {
        return true;
    }

    @Override
    public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return this.operand.containsField(fieldMetas);
    }

    @Override
    public final boolean containsFieldOf(TableMeta<?> tableMeta) {
        return this.operand.containsFieldOf(tableMeta);
    }

    @Override
    public final int containsFieldCount(TableMeta<?> tableMeta) {
        return this.operand.containsFieldCount(tableMeta);
    }

    private static final class RelationColumnSubQueryPredicate extends ColumnSubQueryPredicate {

        private final SubQueryOperator subQueryOperator;

        public RelationColumnSubQueryPredicate(Expression<?> operand, DualPredicateOperator operator
                , SubQueryOperator subQueryOperator, ColumnSubQuery<?> subQuery) {
            super(operand, operator, subQuery);
            this.subQueryOperator = subQueryOperator;
        }

        @Override
        SubQueryOperator subQueryOperator() {
            return this.subQueryOperator;
        }
    }
}
