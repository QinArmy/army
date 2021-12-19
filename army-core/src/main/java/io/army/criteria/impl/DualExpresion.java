package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.FieldExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
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


    static <E> DualExpresion<E> build(_Expression<?> left, DualOperator operator, _Expression<?> right) {
        DualExpresion<E> dualExpresion;
        if (left instanceof FieldExpression || right instanceof FieldExpression) {
            dualExpresion = new FieldExpressionImpl<>(left, operator, right);
        } else {
            dualExpresion = new DualExpresion<>(left, operator, right);
        }
        return dualExpresion;
    }


    final _Expression<?> left;

    final DualOperator operator;

    final _Expression<?> right;


    private DualExpresion(_Expression<?> left, DualOperator operator, _Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public final MappingType mappingType() {
        return this.operator.bitOperator()
                ? _MappingFactory.getMapping(BigInteger.class)
                : left.mappingType();
    }

    @Override
    public final void appendSql(_SqlContext context) {
        left.appendSql(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered());
        right.appendSql(context);
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

        private FieldExpressionImpl(_Expression<?> left, DualOperator operator, _Expression<?> right) {
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
