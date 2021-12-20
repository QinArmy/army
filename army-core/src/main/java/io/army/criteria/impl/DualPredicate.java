package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

import java.util.function.Function;

/**
 *
 */
final class DualPredicate extends AbstractPredicate {

    static <C, E, O> DualPredicate create(final Expression<E> left, DualOperator operator
            , Function<C, Expression<O>> expOrSubQuery) {
        final Expression<O> functionResult;
        functionResult = expOrSubQuery.apply(CriteriaContextStack.getCriteria());
        assert functionResult != null;
        return create(left, operator, functionResult);
    }

    static DualPredicate create(Expression<?> left, DualOperator operator, Expression<?> right) {
        if (operator == DualOperator.EQ) {
            if (left instanceof GenericField && _MetaBridge.VISIBLE.equals((((GenericField<?, ?>) left).fieldName()))) {
                throw _Exceptions.visibleFieldNoPredicate((GenericField<?, ?>) left);
            }
            if (right instanceof GenericField && _MetaBridge.VISIBLE.equals(((GenericField<?, ?>) right).fieldName())) {
                throw _Exceptions.visibleFieldNoPredicate((GenericField<?, ?>) right);
            }
        }
        return new DualPredicate(left, operator, right);
    }


    @Deprecated
    @Nullable
    static DualPredicate ifParamCreate(Expression<?> left, DualOperator operator, @Nullable Object right) {
        final DualPredicate predicate;
        if (right == null) {
            predicate = null;
        } else {
            predicate = create(left, operator, SQLs.param(left, right));
        }
        return predicate;
    }

    @Nullable
    static <C, O> DualPredicate ifFunctionCreate(Expression<?> left, DualOperator operator
            , Function<C, Expression<O>> function) {

        final DualPredicate predicate;
        final Expression<O> functionResult;
        functionResult = function.apply(CriteriaContextStack.getCriteria());
        if (functionResult == null) {
            predicate = null;
        } else {
            predicate = create(left, operator, functionResult);
        }
        return predicate;
    }


    /*################################## blow instance member ##################################*/

    final _Expression<?> left;

    final DualOperator operator;

    final _Expression<?> right;

    private DualPredicate(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = (_Expression<?>) left;
        this.operator = operator;
        this.right = (_Expression<?>) right;
    }

    @Override
    public void appendSql(_SqlContext context) {
        this.left.appendSql(context);
        context.sqlBuilder()
                .append(Constant.SPACE)
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
