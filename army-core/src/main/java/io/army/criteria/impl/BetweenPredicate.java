package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SpecialPredicate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

class BetweenPredicate extends AbstractPredicate {

    static BetweenPredicate build(Expression<?> left, Expression<?> center, Expression<?> right) {
        BetweenPredicate predicate;
        if (containField(left, center, right)) {
            predicate = new SpecialBetweenPredicate(left, center, right);
        } else {
            predicate = new BetweenPredicate(left, center, right);
        }
        return predicate;
    }

    private static boolean containField(Expression<?> left, Expression<?> center, Expression<?> right) {
        return (left instanceof SpecialPredicate)
                || (center instanceof SpecialPredicate)
                || (right instanceof SpecialPredicate);
    }

    final Expression<?> left;

    final Expression<?> center;

    final Expression<?> right;

    private BetweenPredicate(Expression<?> left, Expression<?> center, Expression<?> right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        doAppendSQL(context);
    }

    final void doAppendSQL(SQLContext context) {
        left.appendSQL(context);
        StringBuilder builder = context.sqlBuilder()
                .append(" BETWEEN ");
        center.appendSQL(context);
        builder.append(" AND");
        right.appendSQL(context);
    }


    @Override
    public String beforeAs() {
        return String.format("%s BETWEEN %s AND %s", left, center, right);
    }

    @Override
    public boolean containsSubQuery() {
        return this.left.containsSubQuery()
                || this.center.containsSubQuery()
                || this.right.containsSubQuery();
    }

    /*################################## blow private static inner class ##################################*/

    private static final class SpecialBetweenPredicate extends BetweenPredicate implements SpecialPredicate {

        private SpecialBetweenPredicate(Expression<?> left, Expression<?> center, Expression<?> right) {
            super(left, center, right);
        }

        @Override
        protected void afterSpace(SQLContext context) {
            context.appendFieldPredicate(this);
        }

        @Override
        public void appendPredicate(SQLContext context) {
            doAppendSQL(context);
        }

        @Override
        public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.left.containsField(fieldMetas)
                    || this.center.containsField(fieldMetas)
                    || this.right.containsField(fieldMetas);
        }

        @Override
        public boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.left.containsFieldOf(tableMeta)
                    || this.center.containsFieldOf(tableMeta)
                    || this.right.containsFieldOf(tableMeta);
        }

        @Override
        public int containsFieldCount(TableMeta<?> tableMeta) {
            return this.left.containsFieldCount(tableMeta)
                    + this.center.containsFieldCount(tableMeta)
                    + this.right.containsFieldCount(tableMeta);
        }

    }


}
