package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.Item;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util._StringUtils;

final class AndPredicate<I extends Item> extends OperationPredicate<I> {


    static <I extends Item> AndPredicate<I> create(OperationPredicate<I> left, @Nullable IPredicate right) {
        assert right != null;
        return new AndPredicate<>(left, (OperationPredicate<?>) right);
    }


    final OperationPredicate<I> left;

    final OperationPredicate<?> right;

    private AndPredicate(OperationPredicate<I> left, OperationPredicate<?> right) {
        super(left.function);
        this.left = left;
        this.right = right;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        this.left.appendSql(context);

        context.sqlBuilder().append(_Constant.SPACE_AND);

        this.right.appendSql(context);

    }


    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(this.left)
                .append(_Constant.SPACE_AND)
                .append(this.right)
                .toString();
    }


}
