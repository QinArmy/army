package io.army.criteria.impl;

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._SqlContext;
import io.army.util._Exceptions;

import java.util.Objects;

final class UnaryPredicate extends OperationPredicate {

    static UnaryPredicate fromSubQuery(UnaryOperator operator, SubQuery subQuery) {
        Objects.requireNonNull(subQuery);
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                return new UnaryPredicate(operator, (_SelfDescribed) subQuery);
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }

    }

    static UnaryPredicate create(final UnaryOperator operator, final _Expression expression) {
        if (expression instanceof SubQuery) {
            throw new IllegalArgumentException("expression couldn't be sub query.");
        }
        switch (operator) {
            case IS_NULL:
            case IS_NOT_NULL:
                return new UnaryPredicate(operator, expression);
            default:
                throw _Exceptions.unexpectedEnum(operator);
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
                        .append(this.operator.rendered());
            }
            break;
            case EXISTS:
            case NOT_EXISTS: {
                context.sqlBuilder()
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
                        .append(this.operator.rendered());
            }
            break;
            case EXISTS:
            case NOT_EXISTS: {
                builder.append(this.operator.rendered())
                        .append(this.expressionOrSubQuery);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }
        return builder.toString();
    }


    /*################################## blow private static inner class ##################################*/


}
