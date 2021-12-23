package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.function.Function;

/**
 * This class is a implementation of {@link Expression}.
 * The expression consist of a left {@link Expression} ,a {@link DualOperator} and right {@link Expression}.
 *
 * @param <E> expression result java type
 * @since 1.0
 */
final class DualExpression<E> extends OperationExpression<E> {

    static <C, E, O> DualExpression<E> functionCreate(Expression<E> left, DualOperator operator
            , Function<C, Expression<O>> function) {
        final Expression<O> functionResult;
        functionResult = function.apply(CriteriaContextStack.getCriteria());
        assert functionResult != null;
        return new DualExpression<>(left, operator, functionResult);
    }


    static <E> DualExpression<E> create(Expression<E> left, DualOperator operator, Expression<?> right) {
        return new DualExpression<>(left, operator, right);
    }

    private final _Expression<?> left;

    private final DualOperator operator;

    private final _Expression<?> right;


    private DualExpression(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = (_Expression<?>) left;
        this.operator = operator;
        this.right = (_Expression<?>) right;
    }


    @Override
    public ParamMeta paramMeta() {
        return this.left.paramMeta();
    }

    @Override
    public void appendSql(final _SqlContext context) {

        final _Expression<?> left = this.left, right = this.right;
        final boolean outerBracket, leftInnerBracket, rightInnerBracket;
        switch (this.operator) {
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case MOD:
                outerBracket = leftInnerBracket = rightInnerBracket = false;
                break;
            case LEFT_SHIFT:
            case RIGHT_SHIFT:
            case AND:
            case OR:
            case XOR: {
                outerBracket = true;
                leftInnerBracket = !(left instanceof ValueExpression
                        || left instanceof GenericField
                        || left instanceof BracketsExpression
                        || left instanceof UnaryExpression);

                rightInnerBracket = !(right instanceof ValueExpression
                        || right instanceof GenericField
                        || right instanceof BracketsExpression
                        || right instanceof UnaryExpression);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }

        final StringBuilder builder = context.sqlBuilder();

        if (outerBracket) {
            builder.append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);
        }

        if (leftInnerBracket) {
            builder.append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);
        }
        //1. append left expression
        left.appendSql(context);

        if (leftInnerBracket) {
            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

        //2. append operator
        builder.append(Constant.SPACE)
                .append(this.operator.rendered());

        if (rightInnerBracket) {
            builder.append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);
        }

        //3. append right expression
        right.appendSql(context);

        if (rightInnerBracket) {
            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

        if (outerBracket) {
            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }


    }

    @Override
    public boolean containsSubQuery() {
        return this.left.containsSubQuery()
                || this.right.containsSubQuery();
    }

    @Override
    public String toString() {
        return String.format("%s %s%s", this.left, this.operator.rendered(), this.right);
    }

    /*################################## blow private static inner class ##################################*/

}
