package io.army.criteria.impl;

import io.army.criteria.CompoundPredicate;
import io.army.criteria.RowElement;

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
    public final CompoundPredicate equal(RowElement operand) {
        return null;
    }


    @Override
    public final CompoundPredicate less(RowElement operand) {
        return null;
    }

    @Override
    public final CompoundPredicate lessEqual(RowElement operand) {
        return null;
    }

    @Override
    public final CompoundPredicate greater(RowElement operand) {
        return null;
    }

    @Override
    public final CompoundPredicate greaterEqual(RowElement operand) {
        return null;
    }

    @Override
    public final CompoundPredicate notEqual(RowElement operand) {
        return null;
    }


}
