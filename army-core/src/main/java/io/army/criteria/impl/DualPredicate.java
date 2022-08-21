package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect._SqlContext;
import io.army.util._Exceptions;

import java.util.Objects;

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
                if (rightExp.isNullValue()) {
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
    public void appendSql(final _SqlContext context) {
        this.left.appendSql(context);

        final DualOperator operator = this.operator;
        context.sqlBuilder().append(operator.signText);

        final ArmyExpression right = this.right;
        switch (operator) {
            case IN:
            case NOT_IN: {
                if (right instanceof MultiValueExpression) {
                    ((MultiValueExpression) right).appendSqlWithParens(context);
                } else {
                    right.appendSql(context);
                }
            }
            break;
            default:
                right.appendSql(context);

        }//switch

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
        } else if (obj instanceof DualPredicate) {
            final DualPredicate p = (DualPredicate) obj;
            match = p.operator == this.operator
                    && p.left.equals(this.left)
                    && p.right.equals(this.right);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format(" %s%s%s", this.left, this.operator, this.right);
    }


}
