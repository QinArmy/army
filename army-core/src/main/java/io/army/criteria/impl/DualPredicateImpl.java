package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.DualPredicate;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;

import java.util.List;

/**
 * created  on 2018/11/25.
 */
final class DualPredicateImpl extends AbstractPredicate implements DualPredicate {

    private final Expression<?> left;

    private final DualOperator operator;

    private final Expression<?> right;


    DualPredicateImpl(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    @Override
    protected void afterSpace(SQLContext context) {
        left.appendSQL(context);
        context.stringBuilder()
                .append(" ")
                .append(operator.rendered())
                .append(" ");
        right.appendSQL(context);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s",left,operator,right);
    }

    @Override
    public Expression<?> leftExpression() {
        return left;
    }

    @Override
    public DualOperator dualOperator() {
        return operator;
    }

    @Override
    public Expression<?> rightExpression() {
        return right;
    }
}
