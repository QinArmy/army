package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.dialect._SqlContext;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

import java.util.function.Function;

/**
 *
 */
final class DualPredicate extends OperationPredicate {

    static <C, E, O> DualPredicate create(final Expression<E> left, DualOperator operator
            , Function<C, Expression<O>> expOrSubQuery) {
        final Expression<O> functionResult;
        functionResult = expOrSubQuery.apply(CriteriaContextStack.getCriteria());
        assert functionResult != null;
        return create(left, operator, functionResult);
    }

    static DualPredicate create(final Expression<?> left, final DualOperator operator, final Expression<?> right) {
        if (left instanceof GenericField
                && _MetaBridge.VISIBLE.equals((((GenericField<?, ?>) left).fieldName()))) {
            throw _Exceptions.visibleFieldNoPredicate((GenericField<?, ?>) left);
        } else if (right instanceof GenericField
                && _MetaBridge.VISIBLE.equals((((GenericField<?, ?>) right).fieldName()))) {
            throw _Exceptions.visibleFieldNoPredicate((GenericField<?, ?>) right);
        }

        final DualPredicate predicate;
        switch (operator) {
            case NOT_EQ:
            case LT:
            case LE:
            case GT:
            case GE:
            case EQ:
            case LIKE:
            case NOT_LIKE:
            case IN:
            case NOT_IN:
                predicate = new DualPredicate(left, operator, right);
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
        return predicate;
    }


    /*################################## blow instance member ##################################*/

    final ArmyExpression<?> left;

    final DualOperator operator;

    final ArmyExpression<?> right;

    private DualPredicate(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = (ArmyExpression<?>) left;
        this.operator = operator;
        this.right = (ArmyExpression<?>) right;
    }

    @Override
    public void appendSql(_SqlContext context) {
        this.left.appendSql(context);
        context.sqlBuilder()
                .append(this.operator.rendered());
        this.right.appendSql(context);
    }


    @Override
    public boolean containsSubQuery() {
        return this.left.containsSubQuery() || this.right.containsSubQuery();
    }


    @Override
    public String toString() {
        return String.format("%s %s%s", this.left, this.operator, this.right);
    }


}
