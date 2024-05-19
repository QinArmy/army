/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.CompoundPredicate;
import io.army.criteria.RowExpression;
import io.army.criteria.SQLColumnSet;

import java.util.function.BiFunction;

/**
 * <p>
 * This class is a abstract implementation of {@link io.army.criteria.RowExpression}.
 *
 * @since 0.6.0
 */
public abstract class OperationRowExpression extends OperationSQLExpression implements ArmyRowExpression {


    /**
     * package constructor
     */
    protected OperationRowExpression() {
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
    public final CompoundPredicate nullSafeEqual(SQLColumnSet operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NULL_SAFE_EQUAL, operand);
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

    @Override
    public final CompoundPredicate space(BiFunction<RowExpression, RowExpression, CompoundPredicate> funcRef,
                                         RowExpression right) {
        final CompoundPredicate predicate;
        predicate = funcRef.apply(this, right);
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return predicate;
    }


}
