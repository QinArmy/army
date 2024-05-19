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

import io.army.criteria.CompoundExpression;
import io.army.criteria.CompoundPredicate;
import io.army.criteria.Expression;
import io.army.criteria.SqlField;
import io.army.criteria.impl.inner._Selection;
import io.army.function.TeNamedOperator;

import java.util.function.BiFunction;

/**
 * <p>
 * This class is a implementation of {@link SqlField},and This class is base class of below:
 * <ul>
 *     <li>{@link TableFieldMeta}</li>
 *     <li>{@link QualifiedFieldImpl}</li>
 *     <li>{@link  CriteriaContexts.ImmutableDerivedField}</li>
 *     <li>{@code   CriteriaContexts.MutableDerivedField}</li>
 * </ul>
 */
public abstract class OperationDataField extends OperationExpression.OperationDefiniteExpression implements SqlField,
        _Selection {

    @Override
    public final CompoundPredicate equal(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate less(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate lessEqual(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate greater(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate greaterEqual(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate notEqual(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate like(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate notLike(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate in(TeNamedOperator<SqlField> namedOperator, int size) {
        return Expressions.inPredicate(this, false, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final CompoundPredicate notIn(TeNamedOperator<SqlField> namedOperator, int size) {
        return Expressions.inPredicate(this, true, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final CompoundExpression mod(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.MOD, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression plus(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.PLUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression minus(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.MINUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression times(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.TIMES, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression divide(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.DIVIDE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression bitwiseAnd(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression bitwiseOr(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression xor(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression rightShift(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression leftShift(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }



}
