package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.SelfDescribed;
import io.army.criteria.SubQuery;
import io.army.util.Assert;

final class UnaryPredicate extends AbstractPredicate {

    static UnaryPredicate build(UnaryOperator operator, SubQuery subQuery) {
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("operator[%s] not in[EXISTS,NOT_EXISTS]", operator));
        }
        return new UnaryPredicate(operator, subQuery);
    }

    static UnaryPredicate build(UnaryOperator operator, SelfDescribed expression) {
        return new UnaryPredicate(operator, expression);
    }

    private final UnaryOperator operator;

    private final SelfDescribed expression;

    private UnaryPredicate(UnaryOperator operator, SelfDescribed expression) {
        Assert.notNull(expression, "expression required");

        this.operator = operator;
        this.expression = expression;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        switch (operator.position()) {
            case LEFT:
                context.stringBuilder()
                        .append(operator.rendered());
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
