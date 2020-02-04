package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;

import java.util.List;

final class TernaryPredicate extends AbstractPredicate {

    private final TernaryOperator ternaryOperator;

    private final Expression<?> left;

    private final Expression<?> center;

    private final Expression<?> right;

    TernaryPredicate(TernaryOperator ternaryOperator, Expression<?> left
            , Expression<?> center, Expression<?> right) {
        this.ternaryOperator = ternaryOperator;
        this.left = left;
        this.center = center;
        this.right = right;
    }

    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        ternaryOperator.appendSQL(builder, paramWrapperList, left, center, right);
    }
}
