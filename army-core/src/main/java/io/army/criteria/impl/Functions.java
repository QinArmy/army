package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FuncExpression;
import io.army.criteria.SelectionSpec;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is util class used to create standard sql element :
 * <ul>
 *     <li>statement parameter</li>
 *     <li>sql literal</li>
 *     <li>standard sql function</li>
 * </ul>
 * </p>
 *
 * @see SQLs
 */
abstract class Functions {

    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    Functions() {
        throw new UnsupportedOperationException();
    }


    interface _NullTreatmentClause<NR> {

        NR respectNulls();

        NR ignoreNulls();

    }

    public interface _FromFirstLastClause<FR> {
        FR fromFirst();

        FR fromLast();

    }


    interface _CaseEndClause {

        SelectionSpec end();
    }

    interface _CaseElseClause extends _CaseEndClause {
        _CaseEndClause elseExp(Expression expression);

        _CaseEndClause elseExp(Supplier<? extends Expression> supplier);

        _CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        _CaseEndClause ifElse(Supplier<? extends Expression> supplier);

        _CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }

    interface _CaseThenClause {

        _CaseWhenSpec then(Expression expression);

        _CaseWhenSpec then(Supplier<? extends Expression> supplier);

        _CaseWhenSpec then(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseWhenSpec then(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }


    interface _CaseWhenClause {

        _CaseThenClause when(Expression expression);

        _CaseThenClause when(Supplier<? extends Expression> supplier);

        _CaseThenClause when(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseThenClause when(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        _CaseThenClause ifWhen(Supplier<? extends Expression> supplier);

        _CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }

    interface _CaseWhenSpec extends _CaseWhenClause, _CaseElseClause {

    }





    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static FuncExpression abs(Object expr) {
        final ArmyExpression expression;
        expression = SQLFunctions.funcParam(expr);
        return SQLFunctions.oneArgOptionFunc("ABS", null, expression, null, expression.paramMeta());
    }

    public static Expression acos(Expression x) {
        throw new UnsupportedOperationException();
    }


    public static Expression asin(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression atan(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression atan(Expression one, Expression two) {
        throw new UnsupportedOperationException();
    }

    public static Expression cell(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression cellAsLong(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression conv(Expression number, int fromBase, int toBase) {
        throw new UnsupportedOperationException();
    }

    public static Expression cos(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression cot(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression crc32(Expression expression) {
        throw new UnsupportedOperationException();
    }

    public static Expression degrees(Expression radian) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression exp(Expression index) {
        throw new UnsupportedOperationException();
    }

    public static Expression floor(Expression number) {
        throw new UnsupportedOperationException();
    }

    public static Expression floorAsLong(Expression number) {
        throw new UnsupportedOperationException();
    }

    public static Expression format(Expression number, Expression decimal) {
        throw new UnsupportedOperationException();
    }

    public static Expression format(Expression number, int decimal) {
        throw new UnsupportedOperationException();
    }

    public static Expression hex(Expression number) {
        throw new UnsupportedOperationException();
    }

    public static Expression hex(Number number) {
        throw new UnsupportedOperationException();
    }

    public static Expression hex(String numberText) {
        throw new UnsupportedOperationException();
    }

    public static Expression hexForText(Expression numberText) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #exp(Expression)
     */
    public static Expression ln(Expression power) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log(Expression power) {
        throw new UnsupportedOperationException();
    }

    public static Expression log(Expression bottomNumber
            , Expression power) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log2(Expression power) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log10(Expression power) {
        throw new UnsupportedOperationException();
    }

    public static Expression mod(Expression dividend
            , Expression divisor) {
        throw new UnsupportedOperationException();
    }


    public static Expression pi() {
        throw new UnsupportedOperationException();
    }


    /*################################## blow date time function method ##################################*/

    public static Expression now() {
        throw new UnsupportedOperationException();
    }

    public static Expression currentDate() {
        throw new UnsupportedOperationException();
    }

    public static _CaseWhenClause caseFunc() {
        return SQLFunctions.caseFunc(null);
    }

    /*################################## blow static inner class  ##################################*/


    /*-------------------below package method -------------------*/

    static ParamMeta _returnType(final ArmyExpression keyExpr, final ArmyExpression valueExpr
            , BiFunction<MappingType, MappingType, MappingType> function) {
        final ParamMeta keyType, valueType;
        keyType = keyExpr.paramMeta();
        valueType = valueExpr.paramMeta();
        final ParamMeta returnType;
        if (keyType instanceof ParamMeta.Delay || valueType instanceof ParamMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta(keyType, valueType, function);
        } else {
            returnType = function.apply(keyType.mappingType(), valueType.mappingType());
        }
        return returnType;
    }

}
