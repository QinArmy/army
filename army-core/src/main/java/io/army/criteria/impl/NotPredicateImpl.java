package io.army.criteria.impl;

import io.army.criteria.FieldPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLContext;

/**
 * this interface representing a {@code not} expression
 */
class NotPredicateImpl extends AbstractPredicate {

    static IPredicate build(final IPredicate predicate) {
        IPredicate notPredicate;
        if (predicate instanceof NotPredicateImpl) {
            notPredicate = ((NotPredicateImpl) predicate).predicate;
        } else if (predicate instanceof FieldPredicate) {
            notPredicate = new SpecialNotPredicate((FieldPredicate) predicate);
        } else {
            notPredicate = new NotPredicateImpl(predicate);
        }
        return notPredicate;
    }

    private final IPredicate predicate;

    private NotPredicateImpl(IPredicate predicate) {
        this.predicate = predicate;

    }

    @Override
    public void appendSQL(SQLContext context) {
        doAppendSQL(context);
    }

    final void doAppendSQL(SQLContext context) {
        context.sqlBuilder()
                .append(UnaryOperator.NOT.rendered());
        predicate.appendSQL(context);
    }

    @Override
    public String toString() {
        return UnaryOperator.NOT.rendered() + " " + predicate;
    }

    /*################################## blow private static inner class  ##################################*/

    private static final class SpecialNotPredicate extends NotPredicateImpl implements FieldPredicate {

        private SpecialNotPredicate(FieldPredicate predicate) {
            super(predicate);
        }

        @Override
        public void appendSQL(SQLContext context) {
            context.appendFieldPredicate(this);
        }


        @Override
        public void appendPredicate(SQLContext context) {
            context.sqlBuilder()
                    .append(" ");
            doAppendSQL(context);
        }
    }
}
