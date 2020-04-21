package io.army.criteria.impl;

import io.army.criteria.*;

/**
 * created  on 2018/11/25.
 */
final class DualPredicate extends AbstractPredicate {

    static DualPredicate build(Expression<?> left, DualOperator operator, Expression<?> right) {
        return new DualPredicate(left, operator, right);
    }

    static DualPredicate build(Expression<?> left, SubQueryOperator operator, SubQuery subQuery) {
        switch (operator) {
            case IN:
            case NOT_IN:
                break;
            default:
                throw new IllegalArgumentException(String.format("operator[%s] not in[EXISTS,NOT_EXISTS].", operator));
        }
        return new DualPredicate(left, operator, subQuery);
    }

    private final SelfDescribed left;

    private final SQLOperator operator;

    private final SelfDescribed right;


    private DualPredicate(SelfDescribed left, SQLOperator operator, SelfDescribed right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    @Override
    protected void afterSpace(SQLContext context) {
        left.appendSQL(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered());
        right.appendSQL(context);
    }

    @Override
    public String beforeAs() {
        return String.format("%s %s %s",left,operator,right);
    }


}
