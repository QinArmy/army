package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.FieldExpression;
import io.army.criteria._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.math.BigInteger;
import java.util.Collection;

/**
 * This class is a implementation of {@link Expression}.
 * The expression consist of a left {@link Expression} ,a {@link DualOperator} and right {@link Expression}.
 *
 * @param <E> expression result java type
 * @since 1.0
 */
class DualExpresion<E> extends AbstractExpression<E> {


    static <E> DualExpresion<E> build(Expression<?> left, DualOperator operator, Expression<?> right) {
        DualExpresion<E> dualExpresion;
        if (left instanceof FieldExpression || right instanceof FieldExpression) {
            dualExpresion = new FieldExpressionImpl<>(left, operator, right);
        } else {
            dualExpresion = new DualExpresion<>(left, operator, right);
        }
        return dualExpresion;
    }


    final Expression<?> left;

    final DualOperator operator;

    final Expression<?> right;


    private DualExpresion(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public final MappingType mappingMeta() {
        return this.operator.bitOperator()
                ? _MappingFactory.getMapping(BigInteger.class)
                : left.mappingMeta();
    }

    @Override
    public final void appendSQL(_SqlContext context) {
        left.appendSQL(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered());
        right.appendSQL(context);
    }

    @Override
    public final boolean containsSubQuery() {
        return this.left.containsSubQuery()
                || this.right.containsSubQuery();
    }

    @Override
    public String toString() {
        return left + " " + operator.rendered() + " " + right;
    }

    /*################################## blow private static inner class ##################################*/

    private static class FieldExpressionImpl<E> extends DualExpresion<E> implements FieldExpression<E> {

        private FieldExpressionImpl(Expression<?> left, DualOperator operator, Expression<?> right) {
            super(left, operator, right);
        }

        @Override
        public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.left.containsField(fieldMetas)
                    || this.right.containsField(fieldMetas);
        }

        @Override
        public final boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.left.containsFieldOf(tableMeta)
                    || this.right.containsFieldOf(tableMeta);
        }

        @Override
        public final int containsFieldCount(TableMeta<?> tableMeta) {
            return this.left.containsFieldCount(tableMeta) + this.right.containsFieldCount(tableMeta);
        }

    }

}
