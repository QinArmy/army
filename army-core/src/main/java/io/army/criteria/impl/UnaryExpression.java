package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingType;

final class UnaryExpression<E> extends AbstractExpression<E> {

    static <E> UnaryExpression<E> build(Expression<E> expression, UnaryOperator unaryOperator) {
        return new UnaryExpression<>(expression, unaryOperator);
    }

    private final Expression<E> expression;

    private final UnaryOperator operator;

    private UnaryExpression(Expression<E> expression, UnaryOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }


    @Override
    public MappingType mappingType() {
        return expression.mappingType();
    }


    @Override
    protected void afterSpace(SQLContext context) {
        switch (operator.position()) {
            case LEFT:
                context.sqlBuilder()
                        .append(operator.rendered());
                expression.appendSQL(context);
                break;
            case RIGHT:
                expression.appendSQL(context);
                context.sqlBuilder()
                        .append(" ")
                        .append(operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
    }

    @Override
    public String beforeAs() {
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
