package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FieldExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

/**
 * This class is a implementation of {@link Expression}.
 * The expression consist of a  {@link Expression} and a {@link UnaryOperator}.
 *
 * @param <E> expression result java type
 */
class UnaryExpression<E> extends AbstractExpression<E> {

    static <E> UnaryExpression<E> build(_Expression<E> expression, UnaryOperator unaryOperator) {
        UnaryExpression<E> unaryExpression;
        if (expression instanceof FieldExpression) {
            unaryExpression = new FieldUnaryExpression<>(expression, unaryOperator);
        } else {
            unaryExpression = new UnaryExpression<>(expression, unaryOperator);
        }
        return unaryExpression;
    }

    final _Expression<E> expression;

    private final UnaryOperator operator;

    private UnaryExpression(_Expression<E> expression, UnaryOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }


    @Override
    public final MappingType mappingType() {
        return expression.mappingType();
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
                this.expression.appendSql(context);
                break;
            case RIGHT:
                this.expression.appendSql(context);
                context.sqlBuilder()
                        .append(" ")
                        .append(this.operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", this.operator));
        }
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        switch (this.operator.position()) {
            case LEFT:
                builder.append(this.operator.rendered())
                        .append(" ")
                        .append(this.expression);
                break;
            case RIGHT:
                builder.append(this.expression)
                        .append(" ")
                        .append(this.operator.rendered());
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

    private static final class FieldUnaryExpression<E> extends UnaryExpression<E> implements FieldExpression<E> {

        private FieldUnaryExpression(_Expression<E> expression, UnaryOperator operator) {
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
