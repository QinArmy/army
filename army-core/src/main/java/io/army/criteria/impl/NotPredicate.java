package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;

import java.util.Objects;

/**
 * this interface representing a {@code not} expression
 */
final class NotPredicate<I extends Item> extends OperationPredicate<I> {

    static <I extends Item> OperationPredicate<I> not(final OperationPredicate<I> predicate) {
        final OperationPredicate<I> notPredicate;
        if (predicate instanceof NotPredicate) {
            notPredicate = ((NotPredicate<I>) predicate).predicate;
        } else {
            notPredicate = new NotPredicate<>(predicate);
        }
        return notPredicate;
    }

    private final OperationPredicate<I> predicate;

    private NotPredicate(OperationPredicate<I> predicate) {
        super(predicate.function);
        this.predicate = predicate;

    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder;
        builder = context.sqlBuilder()
                .append(" NOT");

        final _Predicate predicate = this.predicate;
        final boolean needBracket = !(predicate instanceof OrPredicate);

        if (needBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        this.predicate.appendSql(context);

        if (needBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.predicate);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof NotPredicate) {
            final NotPredicate<?> o = (NotPredicate<?>) obj;
            match = o.predicate.equals(this.predicate);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(" NOT");

        final _Predicate predicate = this.predicate;
        final boolean needBracket = !(predicate instanceof OrPredicate);

        if (needBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        builder.append(this.predicate);

        if (needBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }
        return builder.toString();
    }

    /*################################## blow private static inner class  ##################################*/


}
