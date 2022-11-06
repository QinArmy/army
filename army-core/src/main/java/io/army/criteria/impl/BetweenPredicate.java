package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect._SqlContext;

class BetweenPredicate extends OperationPredicate {

    static BetweenPredicate create(Expression left, Expression center, Expression right) {
        return new BetweenPredicate(left, center, right);
    }

    final ArmyExpression left;

    final ArmyExpression center;

    final ArmyExpression right;

    private BetweenPredicate(Expression left, Expression center, Expression right) {
        this.left = (ArmyExpression) left;
        this.center = (ArmyExpression) center;
        this.right = (ArmyExpression) right;
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


    /*################################## blow private static inner class ##################################*/


}
