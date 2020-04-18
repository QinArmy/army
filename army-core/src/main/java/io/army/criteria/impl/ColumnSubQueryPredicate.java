package io.army.criteria.impl;

import io.army.criteria.ColumnSubQuery;
import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.util.Assert;

final class ColumnSubQueryPredicate extends AbstractPredicate {

    static ColumnSubQueryPredicate build(Expression<?> operand, DualOperator operator
            , SubQueryOperator subQueryOperator, ColumnSubQuery<?> subQuery) {
        switch (subQueryOperator) {
            case ALL:
            case ANY:
            case SOME:
                break;
            default:
                throw new IllegalArgumentException(String.format("subQueryOperator[%s] error.", subQuery));
        }
        return new ColumnSubQueryPredicate(operand, operator, subQueryOperator, subQuery);
    }

    private final Expression<?> operand;

    private final DualOperator operator;

    private final SubQueryOperator keyOperator;

    private final ColumnSubQuery<?> subQuery;

    private ColumnSubQueryPredicate(Expression<?> operand, DualOperator operator
            , SubQueryOperator keyOperator, ColumnSubQuery<?> subQuery) {

        Assert.isTrue(operator.relational(), "operator isn't relational operator.");

        this.operand = operand;
        this.operator = operator;
        this.keyOperator = keyOperator;
        this.subQuery = subQuery;
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
        subQuery.appendSQL(context);
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
                .append(subQuery)
                .append(")")
                .toString();

    }
}
