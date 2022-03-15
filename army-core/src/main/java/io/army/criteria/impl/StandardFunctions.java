package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.mapping._MappingFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
abstract class StandardFunctions {

    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    StandardFunctions() {
        throw new UnsupportedOperationException();
    }



    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static Expression abs(Expression x) {
        return SQLFunctions.oneArgumentFunc("ABS", x.paramMeta().mappingType(), x);
    }

    public static Expression acos(Expression x) {
        return SQLFunctions.oneArgumentFunc("ACOS", _MappingFactory.getDefault(Double.class), x);
    }


    public static Expression asin(Expression x) {
        return SQLFunctions.oneArgumentFunc("ASIN", _MappingFactory.getDefault(Double.class), x);
    }

    public static Expression atan(Expression x) {
        return SQLFunctions.oneArgumentFunc("ATAN", _MappingFactory.getDefault(Double.class), x);
    }

    public static Expression atan(Expression one, Expression two) {
        return SQLFunctions.twoArgumentFunc("ATAN", _MappingFactory.getDefault(Double.class), one, two);
    }

    public static Expression cell(Expression x) {
        return SQLFunctions.oneArgumentFunc("CELL", _MappingFactory.getDefault(Integer.class), x);
    }

    public static Expression cellAsLong(Expression x) {
        return SQLFunctions.oneArgumentFunc("CELL", _MappingFactory.getDefault(Long.class), x);
    }

    public static Expression conv(Expression number, int fromBase, int toBase) {
//        return new SQLFunctions.ThreeArgumentFunc<>("CONV", _MappingFactory.getMapping(String.class), number
//                , literal(fromBase), literal(toBase));
        return null;
    }

    public static Expression cos(Expression x) {
        return SQLFunctions.oneArgumentFunc("COS", _MappingFactory.getDefault(Double.class), x);
    }

    public static Expression cot(Expression x) {
        return SQLFunctions.oneArgumentFunc("COT", _MappingFactory.getDefault(Double.class), x);
    }

    public static Expression crc32(Expression expression) {
        return SQLFunctions.oneArgumentFunc("CRC32", _MappingFactory.getDefault(Long.class), expression);
    }

    public static Expression degrees(Expression radian) {
        return SQLFunctions.oneArgumentFunc("DEGREES", _MappingFactory.getDefault(Double.class), radian);
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression exp(Expression index) {
        return SQLFunctions.oneArgumentFunc("EXP", _MappingFactory.getDefault(Double.class), index);
    }

    public static Expression floor(Expression number) {
        return SQLFunctions.oneArgumentFunc("FLOOR", _MappingFactory.getDefault(Integer.class), number);
    }

    public static Expression floorAsLong(Expression number) {
        return SQLFunctions.oneArgumentFunc("FLOOR", _MappingFactory.getDefault(Long.class), number);
    }

    public static Expression format(Expression number, Expression decimal) {
        return SQLFunctions.twoArgumentFunc("FORMAT", _MappingFactory.getDefault(String.class), number, decimal);
    }

    public static Expression format(Expression number, int decimal) {
        return SQLFunctions.twoArgumentFunc("FORMAT", _MappingFactory.getDefault(String.class)
                , number, SQLs.literal(decimal));
    }

    public static Expression hex(Expression number) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getDefault(String.class), number);
    }

    public static Expression hex(Number number) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getDefault(String.class), SQLs.literal(number));
    }

    public static Expression hex(String numberText) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getDefault(String.class), SQLs.literal(numberText));
    }

    public static Expression hexForText(Expression numberText) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getDefault(String.class), numberText);
    }

    /**
     * @see #exp(Expression)
     */
    public static Expression ln(Expression power) {
        return SQLFunctions.oneArgumentFunc("LN", _MappingFactory.getDefault(Double.class), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log(Expression power) {
        return SQLFunctions.oneArgumentFunc("LOG", _MappingFactory.getDefault(Double.class), power);
    }

    public static Expression log(Expression bottomNumber
            , Expression power) {
        return SQLFunctions.twoArgumentFunc("LOG", _MappingFactory.getDefault(Double.class), bottomNumber, power);
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log2(Expression power) {
        return log(SQLs.literal(2), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log10(Expression power) {
        return log(SQLs.literal(10), power);
    }

    public static Expression mod(Expression dividend
            , Expression divisor) {
        return SQLFunctions.twoArgumentFunc("MOD", dividend.paramMeta().mappingType(), dividend, divisor);
    }


    public static Expression pi() {
        return SQLFunctions.noArgumentFunc("PI", _MappingFactory.getDefault(Double.class));
    }


    /*################################## blow date time function method ##################################*/

    public static Expression now() {
        return SQLFunctions.noArgumentFunc("NOW", _MappingFactory.getDefault(LocalDateTime.class));
    }

    public static Expression currentDate() {
        return SQLFunctions.noArgumentFunc("CURRENT_DATE", _MappingFactory.getDefault(LocalDate.class));
    }

    /*################################## blow static inner class  ##################################*/




}
