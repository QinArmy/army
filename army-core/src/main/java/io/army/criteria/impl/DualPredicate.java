package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.IPredicate;
import io.army.criteria.RouteFieldCollectionPredicate;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.sharding.Route;
import io.army.util._Exceptions;

import java.util.Map;
import java.util.function.Function;

/**
 *
 */
class DualPredicate extends OperationPredicate {

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
            case IN:
            case NOT_IN: {
                predicate = new DualPredicate(left, operator, right);
            }
            break;
            default:
                predicate = new DualPredicate(left, operator, right);

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
    public final void appendSql(_SqlContext context) {
        this.left.appendSql(context);
        context.sqlBuilder()
                .append(this.operator.rendered());
        this.right.appendSql(context);
    }


    @Override
    public final boolean containsSubQuery() {
        return this.left.containsSubQuery() || this.right.containsSubQuery();
    }


    @Override
    public final String toString() {
        return String.format("%s %s%s", this.left, this.operator, this.right);
    }


    private static final class RouteFieldCollectionPredicateImpl extends DualPredicate
            implements RouteFieldCollectionPredicate {

        private RouteFieldCollectionPredicateImpl(Expression<?> left, DualOperator operator, Expression<?> right) {
            super(left, operator, right);
        }

        @Override
        public Map<Byte, IPredicate> tableSplit(Function<TableMeta<?>, Route> function) {
            return null;
        }

        @Override
        public Map<Byte, IPredicate> databaseSplit(Function<TableMeta<?>, Route> function) {
            return null;
        }
    }


}
