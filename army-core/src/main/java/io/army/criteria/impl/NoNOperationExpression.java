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
    public final <C> IPredicate equalExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqual(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifEqual(Function<C, Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqual(Function<String, Object> operand, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqualLiteral(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqualLiteral(Function<String, Object> operand, String keyName) {
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
    public final <C> IPredicate lessThanExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThan(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifLessThan(Function<C, Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThan(Function<String, Object> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThanLiteral(Supplier<Object> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThanLiteral(Function<String, Object> function, String keyName) {
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
    public final <C> IPredicate lessEqualExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqual(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifLessEqual(Function<C, Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqual(Function<String, Object> operand, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqualLiteral(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqualLiteral(Function<String, Object> operand, String keyName) {
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
    public final <C> IPredicate greatThanExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThan(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifGreatThan(Function<C, Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThan(Function<String, Object> operand, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThanLiteral(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThanLiteral(Function<String, Object> operand, String keyName) {
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
    public final <C> IPredicate greatEqualExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqual(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifGreatEqual(Function<C, Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqual(Function<String, Object> operand, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqualLiteral(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqualLiteral(Function<String, Object> operand, String keyName) {
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
    public final <C> IPredicate notEqualExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqual(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifNotEqual(Function<C, Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqual(Function<String, Object> operand, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqualLiteral(Supplier<Object> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqualLiteral(Function<String, Object> operand, String keyName) {
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
    public final IPredicate ifBetween(Supplier<Object> firstOperand, Supplier<Object> secondOperand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetween(Function<String, Object> function, String firstKey, String secondKey) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetweenLiteral(Supplier<Object> firstOperand, Supplier<Object> secondOperand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetweenLiteral(Function<String, Object> function, String firstKey, String secondKey) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate between(Function<C, ExpressionPair> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifBetween(Function<C, ExpressionPair> function) {
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
    public final IPredicate in(Object collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inParam(Object collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate inExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifIn(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifIn(Supplier<Object> collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifInParam(Supplier<Object> collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifInParam(Function<C, Object> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifIn(Function<String, Object> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifInParam(Function<String, Object> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Object collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInParam(Object collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notInExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotIn(Supplier<Object> collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifNotIn(Function<C, Object> collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotIn(Function<String, Object> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotInParam(Supplier<Object> collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifNotInParam(Function<C, Object> collectionOrExp) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotInParam(Function<String, Object> function, String keyName) {
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
    public final <C> IPredicate likeExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate likeExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLike(Supplier<Object> pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifLike(Function<C, Object> pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLike(Function<String, Object> function, String keyName) {
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
    public final <C> IPredicate notLikeExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLikeExp(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotLike(Supplier<Object> pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate ifNotLike(Function<C, Object> pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotLike(Function<String, Object> function, String keyName) {
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
    public final <C> Expression modExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression modExp(Supplier<Expression> supplier) {
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
    public final <C> Expression multiplyExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression multiplyExp(Supplier<Expression> supplier) {
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
    public final <C> Expression plusExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plusExp(Supplier<Expression> supplier) {
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
    public final <C> Expression minusExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minusExp(Supplier<Expression> supplier) {
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
    public final <C> Expression divideExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divideExp(Supplier<Expression> supplier) {
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
    public final <C> Expression bitwiseAndExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAndExp(Supplier<Expression> supplier) {
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
    public final <C> Expression bitwiseOrExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOrExp(Supplier<Expression> supplier) {
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
    public final <C> Expression xorExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xorExp(Supplier<Expression> supplier) {
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
    public final <C> Expression rightShiftExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftExp(Supplier<Expression> supplier) {
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
    public final <C> Expression leftShiftExp(Function<C, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftExp(Supplier<Expression> supplier) {
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
