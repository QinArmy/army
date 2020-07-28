package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.FieldExpression;
import io.army.meta.GenericField;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;

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
        if (left instanceof FieldExpression) {
            if (left instanceof GenericField) {
                dualExpresion = buildLeftField((GenericField<?, ?>) left, operator, right);
            } else {
                dualExpresion = new FieldExpressionImpl<>(left, operator, right);
            }
        } else if (right instanceof FieldExpression) {
            if (right instanceof GenericField) {
                dualExpresion = buildRightField(left, operator, (GenericField<?, ?>) right);
            } else {
                dualExpresion = new FieldExpressionImpl<>(left, operator, right);
            }
        } else {
            dualExpresion = new DualExpresion<>(left, operator, right);
        }
        return dualExpresion;
    }

    private static <E> DualExpresion<E> buildLeftField(GenericField<?, ?> left, DualOperator operator
            , Expression<?> right) {
        DualExpresion<E> dualExpresion;
        if (right instanceof GenericField) {
            dualExpresion = buildFieldPair(left, operator, (GenericField<?, ?>) right);
        } else {
            dualExpresion = new FieldExpressionImpl<>(left, operator, right);
        }
        return dualExpresion;
    }

    private static <E> DualExpresion<E> buildRightField(Expression<?> left, DualOperator operator
            , GenericField<?, ?> right) {
        DualExpresion<E> dualExpresion;
        if (left instanceof GenericField) {
            dualExpresion = buildFieldPair((GenericField<?, ?>) left, operator, right);
        } else {
            dualExpresion = new FieldExpressionImpl<>(left, operator, right);
        }
        return dualExpresion;
    }

    private static <E> DualExpresion<E> buildFieldPair(GenericField<?, ?> left
            , DualOperator operator, GenericField<?, ?> right) {
        return new FieldPairExpressionImpl<>(left, operator, right);
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
    public final MappingMeta mappingMeta() {
        return this.operator.bitOperator()
                ? MappingFactory.getDefaultMapping(BigInteger.class)
                : left.mappingMeta();
    }

    @Override
    public final void appendSQL(SQLContext context) {
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
            return this.left.containsFieldCount(tableMeta)
                    + this.right.containsFieldCount(tableMeta);
        }

    }

    private static final class FieldPairExpressionImpl<E> extends FieldExpressionImpl<E> {

        private FieldPairExpressionImpl(GenericField<?, ?> left, DualOperator operator
                , GenericField<?, ?> right) {
            super(left, operator, right);
        }


    }


}
