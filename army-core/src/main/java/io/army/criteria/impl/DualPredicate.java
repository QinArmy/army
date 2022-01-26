package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect._SqlContext;
import io.army.util._Exceptions;

/**
 *
 */
final class DualPredicate extends OperationPredicate {


    static DualPredicate create(final ArmyExpression left, final DualOperator operator, final Expression right) {
        final DualPredicate predicate;
        switch (operator) {
            case EQ:
            case NOT_EQ:
            case LT:
            case LE:
            case GT:
            case GE:
            case LIKE:
            case NOT_LIKE:
            case IN:
            case NOT_IN: {
                final ArmyExpression rightExp = (ArmyExpression) right;
                if (rightExp.isNullableValue()) {
                    throw _Exceptions.operatorRightIsNullable(operator);
                }
                predicate = new DualPredicate(left, operator, rightExp);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
        return predicate;
    }


    /*################################## blow instance member ##################################*/

    final ArmyExpression left;

    final DualOperator operator;

    final ArmyExpression right;

    private DualPredicate(ArmyExpression left, DualOperator operator, ArmyExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void appendSql(_SqlContext context) {
        this.left.appendSql(context);
        context.sqlBuilder()
                .append(this.operator.rendered());
        this.right.appendSql(context);
    }


    @Override
    public String toString() {
        return String.format(" %s%s%s", this.left, this.operator, this.right);
    }


}
