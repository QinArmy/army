package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.Item;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;

import java.util.Objects;

final class OrPredicate<I extends Item> extends OperationPredicate<I> {

    static <I extends Item> OperationPredicate<I> create(OperationPredicate<I> left, IPredicate right) {
        return new OrPredicate<>(left, (OperationPredicate<?>) right);
    }


    private final OperationPredicate<I> left;

    private final OperationPredicate<?> right;

    private OrPredicate(OperationPredicate<I> left, OperationPredicate<?> right) {
        super(left.function);
        this.left = left;
        this.right = right;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder;
        builder = context.sqlBuilder().append(_Constant.SPACE_LEFT_PAREN);// outer left paren

        final OperationPredicate<?> left = this.left, right = this.right;
        final boolean leftInnerParen, rightInnerParen;
        leftInnerParen = left instanceof NotPredicate || left instanceof AndPredicate;
        if (leftInnerParen) {
            builder.append(_Constant.SPACE_LEFT_PAREN); //left inner left bracket
        }
        left.appendSql(context);
        if (leftInnerParen) {
            builder.append(_Constant.SPACE_RIGHT_PAREN); //left inner left bracket
        }

        builder.append(_Constant.SPACE_OR);

        rightInnerParen = right instanceof NotPredicate || right instanceof AndPredicate;
        if (rightInnerParen) {
            builder.append(_Constant.SPACE_LEFT_PAREN); // inner left bracket
        }

        right.appendSql(context);

        if (rightInnerParen) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);// inner right bracket
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN); // outer right paren
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof OrPredicate) {
            final OrPredicate<?> o = (OrPredicate<?>) obj;
            match = o.left.equals(this.left) && o.right.equals(this.right);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(128)
                .append(_Constant.SPACE_LEFT_PAREN);

        final OperationPredicate<?> left = this.left, right = this.right;
        final boolean leftInnerParen, rightInnerParen;
        leftInnerParen = left instanceof NotPredicate || left instanceof AndPredicate;
        if (leftInnerParen) {
            builder.append(_Constant.SPACE_LEFT_PAREN); //left inner left bracket
        }
        builder.append(left);
        if (leftInnerParen) {
            builder.append(_Constant.SPACE_RIGHT_PAREN); //left inner left bracket
        }

        builder.append(_Constant.SPACE_OR);

        rightInnerParen = right instanceof NotPredicate || right instanceof AndPredicate;
        if (rightInnerParen) {
            builder.append(_Constant.SPACE_LEFT_PAREN); // inner left bracket
        }

        builder.append(right);

        if (rightInnerParen) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);// inner right bracket
        }

        return builder.append(_Constant.SPACE_RIGHT_PAREN)
                .toString();
    }


}
