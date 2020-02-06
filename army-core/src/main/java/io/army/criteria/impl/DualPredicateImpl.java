package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.DualPredicate;
import io.army.criteria.Expression;
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
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        left.appendSQL(sql,builder,paramWrapperList);
        builder.append(operator.rendered());
        builder.append(" ");
        right.appendSQL(sql,builder,paramWrapperList);
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
