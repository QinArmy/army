package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.Objects;
import java.util.function.Function;

final class UnaryPredicate<I extends Item> extends OperationPredicate<I> {

    static UnaryPredicate<Selection> fromSubQuery(UnaryOperator operator, @Nullable SubQuery subQuery) {
        assert subQuery != null;
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                return new UnaryPredicate<>(subQuery, operator, SQLs::_identity);
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }

    }

    static <I extends Item> UnaryPredicate<I> create(final UnaryOperator operator
            , final OperationExpression<I> expression) {
        if (expression instanceof SubQuery) {
            throw new IllegalArgumentException("expression couldn't be sub query.");
        }
        switch (operator) {
            case IS_NULL:
            case IS_NOT_NULL:
                return new UnaryPredicate<>(operator, expression);
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
    }

    private final UnaryOperator operator;

    private final _SelfDescribed expressionOrSubQuery;

    private UnaryPredicate(UnaryOperator operator, OperationExpression<I> expression) {
        super(expression.function);
        this.operator = operator;
        this.expressionOrSubQuery = expression;
    }

    private UnaryPredicate(SubQuery query, UnaryOperator operator, Function<Selection, I> function) {
        super(function);
        this.operator = operator;
        this.expressionOrSubQuery = (_SelfDescribed) query;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final _SelfDescribed expressionOrSubQuery = this.expressionOrSubQuery;
        switch (this.operator) {
            case IS_NOT_NULL:
            case IS_NULL: {
                final boolean innerBracket;
                innerBracket = !(expressionOrSubQuery instanceof DataField
                        || expressionOrSubQuery instanceof SqlValueParam.SingleValue);

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
    public int hashCode() {
        return Objects.hash(this.operator, this.expressionOrSubQuery);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof UnaryPredicate) {
            final UnaryPredicate<?> o = (UnaryPredicate<?>) obj;
            match = o.operator == this.operator
                    && o.expressionOrSubQuery.equals(this.expressionOrSubQuery);
        } else {
            match = false;
        }
        return match;
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
