package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;

import java.util.function.Function;

/**
 * This class is a implementation of {@link Expression}.
 * The expression consist of a left {@link Expression} ,a {@link DualOperator} and right {@link Expression}.
 *
 * @param <E> expression result java type
 * @since 1.0
 */
final class DualExpression<E> extends AbstractExpression<E> {

    static <C, E, O> DualExpression<E> create(Expression<E> left, DualOperator operator, Function<C, Expression<O>> function) {
        final Expression<O> functionResult;
        functionResult = function.apply(CriteriaContextStack.getCriteria());
        assert functionResult != null;
        return new DualExpression<>(left, operator, functionResult);
    }


    static <E> DualExpression<E> create(Expression<E> left, DualOperator operator, Expression<?> right) {
        return new DualExpression<>(left, operator, right);
    }

    private final _Expression<?> left;

    private final DualOperator operator;

    private final _Expression<?> right;


    private DualExpression(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = (_Expression<?>) left;
        this.operator = operator;
        this.right = (_Expression<?>) right;
    }

    @Override
    public MappingType mappingType() {
        return this.left.mappingType();
    }

    @Override
    public void appendSql(final _SqlContext context) {
        this.left.appendSql(context);
        context.sqlBuilder()
                .append(Constant.SPACE)
                .append(this.operator.rendered());
        this.right.appendSql(context);
    }

    @Override
    public boolean containsSubQuery() {
        return this.left.containsSubQuery()
                || this.right.containsSubQuery();
    }

    @Override
    public String toString() {
        return String.format("%s %s%s", this.left, this.operator.rendered(), this.right);
    }

    /*################################## blow private static inner class ##################################*/

}
