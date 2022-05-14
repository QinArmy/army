package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

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
abstract class NonOperationExpression implements ArmyExpression {


    NonOperationExpression() {
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
    public final <C> IPredicate equalExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalSome(Supplier<? extends SubQuery> subQuery) {
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
    public final <C> IPredicate lessThanExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanAll(Supplier<? extends SubQuery> supplier) {
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
    public final <C> IPredicate lessEqualExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<? extends SubQuery> supplier) {
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
    public final <C> IPredicate greatThanExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanAll(Supplier<? extends SubQuery> supplier) {
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
    public final <C> IPredicate greatEqualExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<? extends SubQuery> supplier) {
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
    public final <C> IPredicate notEqualExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAll(Supplier<? extends SubQuery> supplier) {
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
    public final <C> IPredicate between(Function<C, ExpressionPair> function) {
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
    public final IPredicate in(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inParam(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inNamed(String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate inExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInParam(Object operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInNamed(String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notInExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Object pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate likeExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate likeExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notLikeExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLikeExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression modExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression modExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Object multiplicand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression timesLiteral(Object multiplicand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression timesNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression timesExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression timesExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression plusExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plusExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression minusExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minusExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression divideExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divideExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression bitwiseAndExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAndExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression bitwiseOrExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOrExp(Supplier<? extends Expression> supplier) {
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
    public final <C> Expression xorExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xorExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression inversion() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftLiteral(Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression rightShiftExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftExp(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftLiteral(Object bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression leftShiftExp(Function<C, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftExp(Supplier<? extends Expression> supplier) {
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
