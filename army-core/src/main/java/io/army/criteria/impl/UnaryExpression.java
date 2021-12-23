package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

/**
 * <p>
 * This class representing unary expression,unary expression always out outer bracket.
 * </p>
 * This class is a implementation of {@link Expression}.
 * The expression consist of a  {@link Expression} and a {@link UnaryOperator}.
 *
 * @param <E> expression result java type
 */
final class UnaryExpression<E> extends OperationExpression<E> {

    static <E> UnaryExpression<E> create(_Expression<E> expression, UnaryOperator operator) {
        switch (operator) {
            case INVERT:
            case NEGATED:
                return new UnaryExpression<>(expression, operator);
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
    }

    final _Expression<E> expression;

    private final UnaryOperator operator;

    private UnaryExpression(_Expression<E> expression, UnaryOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.expression.paramMeta();
    }

    @Override
    public void appendSql(final _SqlContext context) {
        // unary expression always out outer bracket.
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET);

        switch (this.operator) {
            case INVERT:
            case NEGATED: {
                builder.append(Constant.SPACE)
                        .append(this.operator.rendered());

                final _Expression<E> expression = this.expression;
                final boolean innerBracket = !(expression instanceof ValueExpression
                        || expression instanceof GenericField
                        || expression instanceof UnaryExpression
                        || expression instanceof BracketsExpression);

                if (innerBracket) {
                    builder.append(Constant.SPACE)
                            .append(Constant.LEFT_BRACKET);
                }
                // append expression
                expression.appendSql(context);

                if (innerBracket) {
                    builder.append(Constant.SPACE)
                            .append(Constant.RIGHT_BRACKET);
                }

            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);

        }

        // unary expression always out outer bracket.
        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET);
        switch (this.operator) {
            case INVERT:
            case NEGATED: {
                builder.append(Constant.SPACE)
                        .append(this.operator.rendered());

                final _Expression<E> expression = this.expression;
                final boolean needBracket = !(expression instanceof ValueExpression
                        || expression instanceof BracketsExpression);

                if (needBracket) {
                    builder.append(Constant.SPACE)
                            .append(Constant.LEFT_BRACKET);
                }
                // append expression
                builder.append(expression);

                if (needBracket) {
                    builder.append(Constant.SPACE)
                            .append(Constant.RIGHT_BRACKET);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);

        }
        return builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET)
                .toString();
    }


    @Override
    public boolean containsSubQuery() {
        return this.expression.containsSubQuery();
    }


}
