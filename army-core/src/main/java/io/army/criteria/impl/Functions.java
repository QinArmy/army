package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FuncExpression;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

import java.util.function.BiFunction;

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
