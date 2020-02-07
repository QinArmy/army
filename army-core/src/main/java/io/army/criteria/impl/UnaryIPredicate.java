package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.util.Assert;

final class UnaryIPredicate extends AbstractIPredicate {

    private final UnaryOperator operator;

    private final Expression<?> expression;

    UnaryIPredicate(UnaryOperator operator, Expression<?> expression) {
        Assert.notNull(expression, "expression required");

        this.operator = operator;
        this.expression = expression;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        switch (operator.position()) {
            case LEFT:
                context.stringBuilder().append(operator.rendered());
                expression.appendSQL(context);
                break;
            case RIGHT:
                expression.appendSQL(context);
                context.stringBuilder().append(operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (operator.position()) {
            case LEFT:
                builder.append(operator.rendered())
                        .append(expression);
                break;
            case RIGHT:
                builder.append(expression)
                        .append(" ")
                        .append(operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
        return builder.toString();
    }
}
