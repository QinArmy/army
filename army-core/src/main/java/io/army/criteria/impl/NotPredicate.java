package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;

/**
 * this interface representing a {@code not} expression
 */
final class NotPredicate extends OperationPredicate {

    static IPredicate not(final OperationPredicate predicate) {
        final _Predicate notPredicate;
        if (predicate instanceof NotPredicate) {
            notPredicate = ((NotPredicate) predicate).predicate;
        } else {
            notPredicate = new NotPredicate(predicate);
        }
        return notPredicate;
    }

    private final OperationPredicate predicate;

    private NotPredicate(OperationPredicate predicate) {
        this.predicate = predicate;

    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(" NOT");

        final _Predicate predicate = this.predicate;
        final boolean noBracket = !(predicate instanceof OrPredicate);

        if (noBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        predicate.appendSql(context);

        if (noBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }

    @Override
    public String toString() {
        return String.format(" ( NOT%s )", this.predicate);
    }

    /*################################## blow private static inner class  ##################################*/


}
