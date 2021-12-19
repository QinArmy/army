package io.army.criteria.impl;

import io.army.criteria.GenericField;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

final class UnaryPredicate extends AbstractPredicate {

    static UnaryPredicate create(UnaryOperator operator, SubQuery subQuery) {
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                return new UnaryPredicate(operator, subQuery);
            default:
                throw new IllegalArgumentException(
                        String.format("operator[%s] not in [EXISTS,NOT_EXISTS]", operator));
        }

    }

    static UnaryPredicate create(final UnaryOperator operator, final _Expression<?> expression) {
        if (expression instanceof SubQuery) {
            throw new IllegalArgumentException("expression couldn't be sub query.");
        }
        if (expression instanceof GenericField
                && _MetaBridge.VISIBLE.equals(((GenericField<?, ?>) expression).fieldName())) {
            throw _Exceptions.visibleFieldNoPredicate((GenericField<?, ?>) expression);
        }
        switch (operator) {
            case IS_NULL:
            case IS_NOT_NULL:
                return new UnaryPredicate(operator, expression);
            default:
                throw new IllegalArgumentException("operator error");
        }
    }

    private final UnaryOperator operator;

    private final _SelfDescribed expressionOrSubQuery;

    private UnaryPredicate(UnaryOperator operator, _SelfDescribed expressionOrSubQuery) {
        this.operator = operator;
        this.expressionOrSubQuery = expressionOrSubQuery;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        switch (this.operator) {
            case IS_NOT_NULL:
            case IS_NULL: {
                this.expressionOrSubQuery.appendSql(context);
                context.sqlBuilder()
                        .append(Constant.SPACE)
                        .append(this.operator.rendered());
            }
            break;
            case EXISTS:
            case NOT_EXISTS: {
                context.sqlBuilder()
                        .append(Constant.SPACE)
                        .append(this.operator.rendered());
                this.expressionOrSubQuery.appendSql(context);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }

    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        switch (this.operator) {
            case IS_NOT_NULL:
            case IS_NULL: {
                builder.append(this.expressionOrSubQuery)
                        .append(Constant.SPACE)
                        .append(this.operator.rendered());
            }
            break;
            case EXISTS:
            case NOT_EXISTS: {
                builder.append(Constant.SPACE)
                        .append(this.operator.rendered())
                        .append(this.expressionOrSubQuery);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }
        return builder.toString();
    }

    @Override
    public boolean containsSubQuery() {
        return (this.expressionOrSubQuery instanceof SubQuery)
                || ((_Expression<?>) this.expressionOrSubQuery).containsSubQuery();
    }

    /*################################## blow private static inner class ##################################*/


}
