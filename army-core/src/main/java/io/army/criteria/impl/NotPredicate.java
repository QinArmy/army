package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;

/**
 * this interface representing a {@code not} expression
 */
final class NotPredicate extends AbstractPredicate {

    static IPredicate not(final _Predicate predicate) {
        final _Predicate notPredicate;
        if (predicate instanceof NotPredicate) {
            notPredicate = ((NotPredicate) predicate).predicate;
        } else {
            notPredicate = new NotPredicate(predicate);
        }
        return notPredicate;
    }

    private final _Predicate predicate;

    private NotPredicate(_Predicate predicate) {
        this.predicate = predicate;

    }

    @Override
    public void appendSql(_SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(" ( NOT");

        this.predicate.appendSql(context);

        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }

    @Override
    public boolean containsSubQuery() {
        return this.predicate.containsSubQuery();
    }

    @Override
    public String toString() {
        return String.format(" ( NOT%s )", this.predicate);
    }

    /*################################## blow private static inner class  ##################################*/


}
