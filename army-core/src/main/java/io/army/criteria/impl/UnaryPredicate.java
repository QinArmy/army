package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collection;

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
            predicate = new FieldUnaryPredicate(operator, (FieldExpression<?>) expression);
        } else {
            predicate = new UnaryPredicate(operator, expression);
        }
        return predicate;
    }

    private final UnaryOperator operator;

    final SelfDescribed expressionOrSubQuery;

    private UnaryPredicate(UnaryOperator operator, SelfDescribed expressionOrSubQuery) {
        Assert.notNull(expressionOrSubQuery, "expression required");

        this.operator = operator;
        this.expressionOrSubQuery = expressionOrSubQuery;
    }

    @Override
    public void appendSql(_SqlContext context) {
        this.doAppendSQL(context);
    }

    final void doAppendSQL(_SqlContext context) {
        switch (this.operator.position()) {
            case LEFT:
                context.sqlBuilder()
                        .append(" ")
                        .append(this.operator.rendered());
                this.expressionOrSubQuery.appendSql(context);
                break;
            case RIGHT:
                this.expressionOrSubQuery.appendSql(context);
                context.sqlBuilder()
                        .append(" ")
                        .append(this.operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        switch (this.operator.position()) {
            case LEFT:
                builder.append(this.operator.rendered())
                        .append(" ")
                        .append(this.expressionOrSubQuery);
                break;
            case RIGHT:
                builder.append(this.expressionOrSubQuery)
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
        return (this.expressionOrSubQuery instanceof SubQuery)
                || ((Expression<?>) this.expressionOrSubQuery).containsSubQuery();
    }

    /*################################## blow private static inner class ##################################*/

    private static final class FieldUnaryPredicate extends UnaryPredicate implements FieldPredicate {

        private FieldUnaryPredicate(UnaryOperator operator, FieldExpression<?> expression) {
            super(operator, expression);
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.appendFieldPredicate(this);
        }

        @Override
        public void appendPredicate(_SqlContext context) {
            this.doAppendSQL(context);
        }

        @Override
        public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return ((FieldExpression<?>) this.expressionOrSubQuery).containsField(fieldMetas);
        }

        @Override
        public boolean containsFieldOf(TableMeta<?> tableMeta) {
            return ((FieldExpression<?>) this.expressionOrSubQuery).containsFieldOf(tableMeta);
        }

        @Override
        public int containsFieldCount(TableMeta<?> tableMeta) {
            return ((FieldExpression<?>) this.expressionOrSubQuery).containsFieldCount(tableMeta);
        }
    }
}
