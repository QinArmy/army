package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SQLOperator;
import io.army.meta.mapping.MappingType;

final class SortUsingOperatorExpressionImpl<E> extends AbstractNoNOperationExpression<E>
        implements SortUsingOperatorExpression<E> {

    private final Expression<E> expression;

    private final SQLOperator operator;

    SortUsingOperatorExpressionImpl(Expression<E> expression, SQLOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        expression.appendSQL(context);
        context.sqlBuilder()
                .append(" USING ")
                .append(operator.rendered())
        ;
    }

    @Override
    public MappingType mappingType() {
        return expression.mappingType();
    }

    @Override
    public String toString() {
        return expression.toString() + " USING " + operator.rendered();
    }
}
