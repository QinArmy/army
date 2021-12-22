package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

/**
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
    public MappingType mappingType() {
        return this.expression.mappingType();
    }

    @Override
    public ParamMeta paramMeta() {
        return this.expression.paramMeta();
    }

    @Override
    public void appendSql(final _SqlContext context) {
        switch (this.operator) {
            case INVERT:
            case NEGATED: {
                context.sqlBuilder()
                        .append(Constant.SPACE)
                        .append(this.operator.rendered());
                this.expression.appendSql(context);
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
            case INVERT:
            case NEGATED: {
                builder
                        .append(Constant.SPACE)
                        .append(this.operator.rendered())
                        .append(this.expression);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);

        }
        return builder.toString();
    }


    @Override
    public boolean containsSubQuery() {
        return this.expression.containsSubQuery();
    }


}
