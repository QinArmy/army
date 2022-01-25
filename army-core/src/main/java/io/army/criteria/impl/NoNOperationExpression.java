package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SQLs.DefaultWord}</li>
 *         <li>{@link SQLs.NullWord}</li>
 *         <li>{@link CollectionParamExpression}</li>
 *         <li>{@link ParamExpression.NullExpression}</li>
 *     </ul>
 * </p>
 */
abstract class NoNOperationExpression implements ArmyExpression {


    NoNOperationExpression() {
    }


    @Override
    public final Selection as(String alias) {
        if (!(this instanceof ParamValue)) {
            throw unsupportedOperation();
        }
        return new ExpressionSelection(this, alias);
    }

    @Override
    public final boolean isNullableValue() {
        final boolean nullable;
        if (this instanceof ValueExpression) {
            nullable = ((ValueExpression) this).value() == null;
        } else {
            nullable = this instanceof SQLs.NullWord;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqual(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqualLiteral(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equal(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equal(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalSome(Supplier<SubQuery> subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThan(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThan(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThanLiteral(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThan(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThan(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqual(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqualLiteral(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqual(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThan(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThan(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThanLiteral(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThan(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThan(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualNamed(String operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqual(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqualLiteral(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqual(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqualLiteral(@Nullable Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqual(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Object firstOperand, Object secondOperand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate betweenLiteral(Object firstOperand, Object secondOperand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstOperand, @Nullable Object secondOperand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetweenLiteral(@Nullable Object firstOperand, @Nullable Object secondOperand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenWrapper> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate isNull() {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate isNotNull() {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate in(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate inParam(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifIn(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifInParam(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate in(Expression parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate in(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notIn(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notInParam(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifNotIn(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifNotInParam(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Expression values) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notIn(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression modLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression modNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression mod(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression multiply(Object multiplicand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression multiplyLiteral(Object multiplicand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression multiplyNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression multiply(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression multiply(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Object augend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plusLiteral(Object augend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plusNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression plus(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Object minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minusLiteral(Object minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minusNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression minus(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Object divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divideLiteral(Object divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divideNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression divide(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression negate() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAndLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAndNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression bitwiseAnd(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOrLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOrNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression bitwiseOr(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xorLiteral(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xorNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression xor(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression inversion() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(@Nullable Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftLiteral(@Nullable Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftNamed(@Nullable String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression rightShift(@Nullable Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(@Nullable Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(@Nullable Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftLiteral(@Nullable Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftNamed(@Nullable String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression leftShift(@Nullable Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(@Nullable Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Object pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLike(@Nullable Object pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate like(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotLike(@Nullable Object pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notLike(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression asType(Class<?> convertType) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression asType(ParamMeta paramMeta) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bracket() {
        throw unsupportedOperation();
    }

    @Override
    public final SortItem asc() {
        throw unsupportedOperation();
    }

    @Override
    public final SortItem desc() {
        throw unsupportedOperation();
    }



    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
