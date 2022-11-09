package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.Objects;

/**
 * This class is a implementation of {@link Expression}.
 * The expression consist of a left {@link Expression} ,a {@link DualOperator} and right {@link Expression}.
 *
 * @since 1.0
 */
final class DualExpression<I extends Item> extends OperationExpression<I> {


    static <I extends Item> DualExpression<I> create(final OperationExpression<I> left, final DualOperator operator
            , Expression right) {
        final ArmyExpression rightExp = (ArmyExpression) right;
        switch (operator) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case BITWISE_AND:
            case BITWISE_OR:
            case XOR:
            case RIGHT_SHIFT:
            case LEFT_SHIFT: {
                if (rightExp.isNullValue()) {
                    throw _Exceptions.operatorRightIsNullable(operator);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new DualExpression<>(left, operator, rightExp);
    }

    private final ArmyExpression left;

    private final DualOperator operator;

    private final ArmyExpression right;


    private DualExpression(OperationExpression<I> left, DualOperator operator, ArmyExpression right) {
        super(left.function);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    @Override
    public TypeMeta typeMeta() {
        return this.left.typeMeta();
    }

    @Override
    public void appendSql(final _SqlContext context) {

        final _Expression left = this.left, right = this.right;
        final boolean outerBracket, leftInnerBracket, rightInnerBracket;
        switch (this.operator) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
                outerBracket = leftInnerBracket = rightInnerBracket = false;
                break;
            case LEFT_SHIFT:
            case RIGHT_SHIFT:
            case BITWISE_AND:
            case BITWISE_OR:
            case XOR: {
                outerBracket = true;
                leftInnerBracket = !(left instanceof SqlValueParam.SingleValue
                        || left instanceof TableField
                        || left instanceof BracketsExpression);

                rightInnerBracket = !(right instanceof SqlValueParam.SingleValue
                        || right instanceof TableField
                        || right instanceof BracketsExpression);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.operator);
        }

        final StringBuilder builder = context.sqlBuilder();

        if (outerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }

        if (leftInnerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }
        //1. append left expression
        left.appendSql(context);

        if (leftInnerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        //2. append operator
        builder.append(this.operator.spaceOperator);

        if (rightInnerBracket) {
            builder.append(_Constant.SPACE_LEFT_PAREN);
        }

        //3. append right expression
        right.appendSql(context);

        if (rightInnerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        if (outerBracket) {
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    }

    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.operator, this.right);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof DualExpression) {
            DualExpression<?> o = (DualExpression<?>) obj;
            match = o.left.equals(this.left)
                    && o.operator == this.operator
                    && o.right.equals(this.right);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format("%s %s%s", this.left, this.operator.spaceOperator, this.right);
    }

    /*################################## blow private static inner class ##################################*/

}
