package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.NamedParam;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
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
        final _SelfDescribed expressionOrSubQuery = this.expressionOrSubQuery;
        switch (this.operator) {
            case IS_NOT_NULL:
            case IS_NULL: {
                final boolean innerBracket;
                innerBracket = !(expressionOrSubQuery instanceof DataField
                        || expressionOrSubQuery instanceof ValueExpression
                        || expressionOrSubQuery instanceof NamedParam);

                final StringBuilder sqlBuilder = context.sqlBuilder();
                if (innerBracket) {
                    sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                }
                expressionOrSubQuery.appendSql(context);
                if (innerBracket) {
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
                }
                sqlBuilder.append(this.operator.rendered());
            }
            break;
            case EXISTS:
            case NOT_EXISTS: {
                context.sqlBuilder()
                        .append(this.operator.rendered());
                expressionOrSubQuery.appendSql(context);
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
