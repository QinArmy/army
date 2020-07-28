package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util.Assert;

class UnaryPredicate extends AbstractPredicate {

    static UnaryPredicate build(UnaryOperator operator, SubQuery subQuery) {
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                return new UnaryPredicate(operator, subQuery);
            default:
                throw new IllegalArgumentException(
                        String.format("operator[%s] not in [EXISTS,NOT_EXISTS]", operator));
        }

    }

    static UnaryPredicate build(UnaryOperator operator, Expression<?> expression) {
        UnaryPredicate predicate;
        if (operator == UnaryOperator.NOT_EXISTS || operator == UnaryOperator.EXISTS) {
            throw new IllegalArgumentException(
                    String.format("operator[%s] can't in [EXISTS,NOT_EXISTS]", operator));
        } else if (expression instanceof FieldExpression) {
            predicate = new SpecialUnaryPredicate(operator, (FieldExpression<?>) expression);
        } else {
            predicate = new UnaryPredicate(operator, expression);
        }
        return predicate;
    }

    private final UnaryOperator operator;

    private final SelfDescribed expression;

    private UnaryPredicate(UnaryOperator operator, SelfDescribed expression) {
        Assert.notNull(expression, "expression required");

        this.operator = operator;
        this.expression = expression;
    }

    @Override
    protected void appendSQL(SQLContext context) {
        doAppendSQL(context);
    }

    final void doAppendSQL(SQLContext context) {
        switch (operator.position()) {
            case LEFT:
                context.sqlBuilder()
                        .append(operator.rendered());
                expression.appendSQL(context);
                break;
            case RIGHT:
                expression.appendSQL(context);
                context.sqlBuilder().append(operator.rendered());
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

    @Override
    public final boolean containsSubQuery() {
        return (this.expression instanceof SubQuery)
                || ((Expression<?>) this.expression).containsSubQuery();
    }

    /*################################## blow private static inner class ##################################*/

    private static final class SpecialUnaryPredicate extends UnaryPredicate implements IPredicate {

        private SpecialUnaryPredicate(UnaryOperator operator, FieldExpression<?> expression) {
            super(operator, expression);
        }

        @Override
        protected void appendSQL(SQLContext context) {
            context.appendFieldPredicate(this);
        }

        @Override
        public void appendPredicate(SQLContext context) {
            context.sqlBuilder()
                    .append(" ");
            doAppendSQL(context);
        }
    }
}
