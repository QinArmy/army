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

import io.army.annotation.SortOrder;
import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeFunction;
import io.army.mapping.NullType;
import io.army.meta.ParentTableMeta;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>
 * This class representing non-operation expression.
 * This class is base class of following : <ul>
 * <li>{@link SQLs#DEFAULT}</li>
 * <li>{@link SQLs#_ASTERISK_EXP}</li>
 * </ul>
 */
abstract class NonOperationExpression implements ArmyExpression {


    NonOperationExpression() {
    }


    @Override
    public final boolean isNullValue() {
        final boolean nullable;
        if (this instanceof UpdateTimePlaceHolderExpression) {
            nullable = false;
        } else if (this instanceof SqlValueParam.SingleAnonymousValue) {
            nullable = ((SqlValueParam.SingleAnonymousValue) this).value() == null;
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final CompoundPredicate equal(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate nullSafeEqual(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate equalAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate equalSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate equalAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate less(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate lessAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greater(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate greaterAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate greaterEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate is(SQLs.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate isNot(SQLs.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate is(SQLs.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate isNot(SQLs.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate isNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate isNotNull() {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate in(SQLColumnSet row) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notIn(SQLColumnSet row) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate like(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notLike(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression mod(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression times(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression plus(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression minus(Expression minuend) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression divide(Expression divisor) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression bitwiseAnd(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression bitwiseOr(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression bitwiseXor(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression rightShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression leftShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final <R extends UnaryResult> R space(Function<Expression, R> funcRef) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <T, R extends ResultExpression> R space(BiFunction<Expression, T, R> funcRef, T right) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords, R extends ResultExpression> R space(OptionalClauseOperator<M, Expression, R> funcRef, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords, R extends ResultExpression> R space(OptionalClauseOperator<M, Expression, R> funcRef, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <T> CompoundPredicate whiteSpace(BiFunction<Expression, T, CompoundPredicate> funcRef, T right) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords, T extends RightOperand> CompoundPredicate whiteSpace(TeFunction<Expression, M, T, CompoundPredicate> funcRef, M modifier, T right) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> CompoundPredicate whiteSpace(OptionalClauseOperator<M, Expression, CompoundPredicate> funcRef, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> CompoundPredicate whiteSpace(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression mapTo(TypeMeta typeMeta) {
        throw unsupportedOperation(this);
    }

    @Override
    public final boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
        // always false
        return false;
    }

    @Override
    public final Selection as(String selectionLabel) {
        if (this instanceof NullWord) {
            return ArmySelections.forExp(this, selectionLabel);
        }
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem asSortItem() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return this;
    }

    @Override
    public final SortItem asc() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SortOrder.ASC, null);
    }

    @Override
    public final SortItem desc() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SortOrder.DESC, null);
    }

    @Override
    public final SortItem ascSpace(@Nullable SQLs.NullsFirstLast firstLast) {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SortOrder.ASC, firstLast);
    }

    @Override
    public final SortItem descSpace(@Nullable SQLs.NullsFirstLast firstLast) {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SortOrder.DESC, firstLast);
    }

    String operationErrorMessage() {
        return String.format("%s don't support any operation.", this.getClass().getName());
    }

    /**
     * @see SQLs#NULL
     */
    static SQLs.WordNull nullWord() {
        return NullWord.INSTANCE;
    }

    static Expression updateTimePlaceHolder() {
        return UpdateTimePlaceHolderExpression.PLACEHOLDER;
    }


    static CriteriaException unsupportedOperation(NonOperationExpression expression) {
        return ContextStack.clearStackAndCriteriaError(expression.operationErrorMessage());
    }

    static RuntimeException nonOperationExpression(final @Nullable Expression expression) {
        final RuntimeException e;
        if (expression == null) {
            e = ContextStack.clearStackAndNullPointer();
        } else if (expression instanceof NonOperationExpression) {
            e = unsupportedOperation((NonOperationExpression) expression);
        } else {
            String m = String.format("%s isn't army expression", expression.getClass().getName());
            e = ContextStack.clearStackAndCriteriaError(m);
        }
        return e;
    }


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     *
     * @see SQLs#NULL
     */
    private static final class NullWord extends NonOperationExpression
            implements SqlValueParam.SingleAnonymousValue,
            ArmySimpleExpression,
            SQLs.WordNull,
            SQLs.ArmyKeyWord {

        private static final NullWord INSTANCE = new NullWord();


        private NullWord() {
        }

        @Override
        public String spaceRender() {
            return _Constant.SPACE_NULL;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE_NULL);
        }

        @Override
        public TypeMeta typeMeta() {
            return NullType.INSTANCE;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return _Constant.SPACE_NULL;
        }

        @Override
        String operationErrorMessage() {
            return "SQL key word NULL don't support operator";
        }


    } // NullWord


    private static final class UpdateTimePlaceHolderExpression extends NonOperationExpression
            implements ArmySimpleExpression {

        private static final UpdateTimePlaceHolderExpression PLACEHOLDER = new UpdateTimePlaceHolderExpression();

        private UpdateTimePlaceHolderExpression() {
        }


        @Override
        public TypeMeta typeMeta() {
            throw new UnsupportedOperationException("updateTime placeholder don't support this operation");
        }

        @Override
        public void appendSql(StringBuilder sqlBuilder, _SqlContext context) {
            throw new CriteriaException("SQLs.UPDATE_TIME_PLACEHOLDER present in error context");
        }


        @Override
        public String toString() {
            if (this != SQLs.UPDATE_TIME_PLACEHOLDER) {
                // no bug,never here
                throw new IllegalStateException();
            }
            return "SQLs.UPDATE_TIME_PLACEHOLDER";
        }


    } // UpdateTimePlaceHolderExpression


}
