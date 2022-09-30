package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

/**
 * <p>
 * This class representing unary expression,unary expression always out outer bracket.
 * </p>
 * This class is a implementation of {@link Expression}.
 * The expression consist of a  {@link Expression} and a {@link UnaryOperator}.
 */
final class UnaryExpression extends OperationExpression {

    static UnaryExpression create(ArmyExpression expression, UnaryOperator operator) {
        switch (operator) {
            case INVERT:
            case NEGATE: {
                if (expression.isNullValue()) {
                    throw _Exceptions.operatorRightIsNullable(operator);
                }
            }
            return new UnaryExpression(expression, operator);
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
    }

    final ArmyExpression expression;

    private final UnaryOperator operator;

    private UnaryExpression(ArmyExpression expression, UnaryOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    public TypeMeta typeMeta() {
        return this.expression.typeMeta();
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final boolean outerBracket;
        switch (this.operator) {
            case NEGATE:
                outerBracket = false;
                break;
            case INVERT:
                outerBracket = true;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }
        final StringBuilder builder = context.sqlBuilder();

        if (outerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }

        builder.append(this.operator.rendered());

        final _Expression expression = this.expression;
        final boolean innerBracket = !(expression instanceof SqlValueParam.SingleValue
                || expression instanceof DataField
                || expression instanceof BracketsExpression);

        if (innerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        // append expression
        expression.appendSql(context);

        if (innerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        if (outerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }

    @Override
    public String toString() {
        final boolean outerBracket;
        switch (this.operator) {
            case NEGATE:
                outerBracket = false;
                break;
            case INVERT:
                outerBracket = true;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }

        final StringBuilder builder = new StringBuilder();

        if (outerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        builder.append(_Constant.SPACE)
                .append(this.operator.rendered());

        final _Expression expression = this.expression;
        final boolean innerBracket = !(expression instanceof SqlValueParam.SingleValue
                || expression instanceof TableField
                || expression instanceof BracketsExpression);

        if (innerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        // append expression
        builder.append(expression);

        if (innerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        if (outerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }
        return builder.toString();
    }


}
