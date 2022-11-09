package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.dialect._SqlContext;

import java.util.Objects;

class BetweenPredicate<I extends Item> extends OperationPredicate<I> {

    static <I extends Item> BetweenPredicate<I> create(OperationExpression<I> left, Expression center
            , Expression right) {
        return new BetweenPredicate<>(left, center, right);
    }

    final ArmyExpression left;

    final ArmyExpression center;

    final ArmyExpression right;

    private BetweenPredicate(OperationExpression<I> left, Expression center, Expression right) {
        super(left.function);
        this.left = left;
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
    public int hashCode() {
        return Objects.hash(this.left, this.center, this.right);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof BetweenPredicate) {
            final BetweenPredicate<?> o = (BetweenPredicate<?>) obj;
            match = o.left.equals(this.left)
                    && o.center.equals(this.center)
                    && o.right.equals(this.right);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format(" %s BETWEEN%s AND%s", left, center, right);
    }


    /*################################## blow private static inner class ##################################*/


}
