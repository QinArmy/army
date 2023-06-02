package io.army.criteria.impl;

import io.army.criteria.CompoundPredicate;
import io.army.criteria.SQLColumnSet;

/**
 * <p>
 * This class is a abstract implementation of {@link io.army.criteria.RowExpression}.
 * </p>
 *
 * @since 1.0
 */
abstract class OperationRowExpression extends OperationSQLExpression implements ArmyRowExpression {


    /**
     * package constructor
     */
    OperationRowExpression() {
    }

    @Override
    public final CompoundPredicate equal(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, operand);
    }

    @Override
    public final CompoundPredicate notEqual(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, operand);
    }


    @Override
    public final CompoundPredicate less(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS, operand);
    }

    @Override
    public final CompoundPredicate lessEqual(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, operand);
    }

    @Override
    public final CompoundPredicate greater(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, operand);
    }

    @Override
    public final CompoundPredicate greaterEqual(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, operand);
    }




}
