package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;

final class BetweenPredicate extends AbstractPredicate {

    private final Expression<?> left;

    private final Expression<?> center;

    private final Expression<?> right;

    BetweenPredicate(Expression<?> left, Expression<?> center, Expression<?> right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.stringBuilder();
        left.appendSQL(context);
        builder.append(" BETWEEN ");
        center.appendSQL(context);
        builder.append(" AND ");
        right.appendSQL(context);
    }



    @Override
    public String beforeAs() {
        return String.format("%s BETWEEN %s AND %s",left,center,right);
    }
}
