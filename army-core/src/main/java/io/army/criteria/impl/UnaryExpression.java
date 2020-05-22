package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SpecialExpression;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;

import java.util.Collection;

class UnaryExpression<E> extends AbstractExpression<E> {

    static <E> UnaryExpression<E> build(Expression<E> expression, UnaryOperator unaryOperator) {
        UnaryExpression<E> unaryExpression;
        if (expression instanceof SpecialExpression) {
            unaryExpression = new SpecialUnaryExpression<>((SpecialExpression<E>) expression, unaryOperator);
        } else {
            unaryExpression = new UnaryExpression<>(expression, unaryOperator);
        }
        return unaryExpression;
    }

    final Expression<E> expression;

    final UnaryOperator operator;

    private UnaryExpression(Expression<E> expression, UnaryOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }


    @Override
    public final MappingMeta mappingMeta() {
        return expression.mappingMeta();
    }


    @Override
    protected final void afterSpace(SQLContext context) {
        switch (this.operator.position()) {
            case LEFT:
                context.sqlBuilder()
                        .append(this.operator.rendered());
                this.expression.appendSQL(context);
                break;
            case RIGHT:
                this.expression.appendSQL(context);
                context.sqlBuilder()
                        .append(" ")
                        .append(this.operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
    }

    @Override
    public final String beforeAs() {
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
        return this.expression.containsSubQuery();
    }

    /*################################## blow private static inner class ##################################*/

    private static final class SpecialUnaryExpression<E> extends UnaryExpression<E> implements SpecialExpression<E> {

        private SpecialUnaryExpression(SpecialExpression<E> expression, UnaryOperator operator) {
            super(expression, operator);
        }

        @Override
        public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.expression.containsField(fieldMetas);
        }

        @Override
        public final boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.expression.containsFieldOf(tableMeta);
        }

        @Override
        public final int containsFieldCount(TableMeta<?> tableMeta) {
            return this.expression.containsFieldCount(tableMeta);
        }
    }
}
