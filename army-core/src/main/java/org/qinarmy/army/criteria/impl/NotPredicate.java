package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.Expression;
import org.qinarmy.army.criteria.Predicate;

/**
 * created  on 2018/11/25.
 */
final class NotPredicate extends AbstractPredicate {

    private final Expression<?> origin;

    private final boolean not;

    private NotPredicate(Expression<?> origin, boolean not) {
        this.origin = origin;
        this.not = not;
    }

    static Predicate getNotPredicate(Expression<?> origin) {
        return origin instanceof NotPredicate
                ? new NotPredicate(((NotPredicate) origin).getOrigin(), false)
                : new NotPredicate(origin, true);
    }


    @Override
    public String toString() {
        return not ? (UnaryOperator.NOT.rendered() + " " + origin) : origin.toString();
    }

    Expression<?> getOrigin() {
        return origin;
    }
}
