package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;

class BetweenPredicate extends OperationPredicate {

    static BetweenPredicate between(Expression<?> left, Expression<?> center, Expression<?> right) {
        return new BetweenPredicate(left, center, right);
    }

    final _Expression<?> left;

    final _Expression<?> center;

    final _Expression<?> right;

    private BetweenPredicate(Expression<?> left, Expression<?> center, Expression<?> right) {
        this.left = (_Expression<?>) left;
        this.center = (_Expression<?>) center;
        this.right = (_Expression<?>) right;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        this.left.appendSql(context);
        final StringBuilder builder = context.sqlBuilder()
                .append(" BETWEEN");
        this.center.appendSql(context);
        builder.append(" AND");
        this.right.appendSql(context);
    }

    @Override
    public String toString() {
        return String.format(" %s BETWEEN%s AND%s", left, center, right);
    }

    @Override
    public final boolean containsSubQuery() {
        return this.left.containsSubQuery()
                || this.center.containsSubQuery()
                || this.right.containsSubQuery();
    }

    /*################################## blow private static inner class ##################################*/


}
