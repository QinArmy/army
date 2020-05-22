package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SpecialExpression;
import io.army.meta.FieldExpression;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;

import java.math.BigInteger;
import java.util.Collection;

class DualExpresion<E> extends AbstractExpression<E> {


    static <E> DualExpresion<E> build(Expression<?> left, DualOperator operator, Expression<?> right) {
        DualExpresion<E> dualExpresion;
        if (left instanceof SpecialExpression) {
            if (left instanceof FieldExpression) {
                dualExpresion = buildLeftField((FieldExpression<?, ?>) left, operator, right);
            } else {
                dualExpresion = new SpecialExpressionImpl<>(left, operator, right);
            }
        } else if (right instanceof SpecialExpression) {
            if (right instanceof FieldExpression) {
                dualExpresion = buildRightField(left, operator, (FieldExpression<?, ?>) right);
            } else {
                dualExpresion = new SpecialExpressionImpl<>(left, operator, right);
            }
        } else {
            dualExpresion = new DualExpresion<>(left, operator, right);
        }
        return dualExpresion;
    }

    private static <E> DualExpresion<E> buildLeftField(FieldExpression<?, ?> left, DualOperator operator
            , Expression<?> right) {
        DualExpresion<E> dualExpresion;
        if (right instanceof FieldExpression) {
            dualExpresion = buildFieldPair(left, operator, (FieldExpression<?, ?>) right);
        } else {
            dualExpresion = new SpecialExpressionImpl<>(left, operator, right);
        }
        return dualExpresion;
    }

    private static <E> DualExpresion<E> buildRightField(Expression<?> left, DualOperator operator
            , FieldExpression<?, ?> right) {
        DualExpresion<E> dualExpresion;
        if (left instanceof FieldExpression) {
            dualExpresion = buildFieldPair((FieldExpression<?, ?>) left, operator, right);
        } else {
            dualExpresion = new SpecialExpressionImpl<>(left, operator, right);
        }
        return dualExpresion;
    }

    private static <E> DualExpresion<E> buildFieldPair(FieldExpression<?, ?> left
            , DualOperator operator, FieldExpression<?, ?> right) {
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
    protected final void afterSpace(SQLContext context) {
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
    public String beforeAs() {
        return left + " " + operator.rendered() + " " + right;
    }

    /*################################## blow private static inner class ##################################*/

    private static class SpecialExpressionImpl<E> extends DualExpresion<E> implements SpecialExpression<E> {

        private SpecialExpressionImpl(Expression<?> left, DualOperator operator, Expression<?> right) {
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

    private static final class FieldPairExpressionImpl<E> extends SpecialExpressionImpl<E> {

        private FieldPairExpressionImpl(FieldExpression<?, ?> left, DualOperator operator
                , FieldExpression<?, ?> right) {
            super(left, operator, right);
        }


    }


}
