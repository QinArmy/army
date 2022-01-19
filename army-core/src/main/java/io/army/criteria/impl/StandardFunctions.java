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
    public static <E extends Number> Expression<E> abs(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("ABS", x.paramMeta().mappingType(), x);
    }

    public static <E extends Number> Expression<Double> acos(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("ACOS", _MappingFactory.getMapping(Double.class), x);
    }


    public static <E extends Number> Expression<Double> asin(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("ASIN", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("ATAN", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> one, Expression<E> two) {
        return SQLFunctions.twoArgumentFunc("ATAN", _MappingFactory.getMapping(Double.class), one, two);
    }

    public static <E extends Number> Expression<Integer> cell(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("CELL", _MappingFactory.getMapping(Integer.class), x);
    }

    public static <E extends Number> Expression<Long> cellAsLong(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("CELL", _MappingFactory.getMapping(Long.class), x);
    }

    public static <E extends Number> Expression<String> conv(Expression<E> number, int fromBase, int toBase) {
//        return new SQLFunctions.ThreeArgumentFunc<>("CONV", _MappingFactory.getMapping(String.class), number
//                , literal(fromBase), literal(toBase));
        return null;
    }

    public static <E extends Number> Expression<Double> cos(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("COS", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> cot(Expression<E> x) {
        return SQLFunctions.oneArgumentFunc("COT", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Long> crc32(Expression<E> expression) {
        return SQLFunctions.oneArgumentFunc("CRC32", _MappingFactory.getMapping(Long.class), expression);
    }

    public static <E extends Number> Expression<Double> degrees(Expression<E> radian) {
        return SQLFunctions.oneArgumentFunc("DEGREES", _MappingFactory.getMapping(Double.class), radian);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> exp(Expression<E> index) {
        return SQLFunctions.oneArgumentFunc("EXP", _MappingFactory.getMapping(Double.class), index);
    }

    public static <E extends Number> Expression<Integer> floor(Expression<E> number) {
        return SQLFunctions.oneArgumentFunc("FLOOR", _MappingFactory.getMapping(Integer.class), number);
    }

    public static <E extends Number> Expression<Long> floorAsLong(Expression<E> number) {
        return SQLFunctions.oneArgumentFunc("FLOOR", _MappingFactory.getMapping(Long.class), number);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, Expression<E> decimal) {
        return SQLFunctions.twoArgumentFunc("FORMAT", _MappingFactory.getMapping(String.class), number, decimal);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, int decimal) {
        return SQLFunctions.twoArgumentFunc("FORMAT", _MappingFactory.getMapping(String.class)
                , number, SQLs.literal(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), number);
    }

    public static Expression<String> hex(Number number) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), SQLs.literal(number));
    }

    public static Expression<String> hex(String numberText) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), SQLs.literal(numberText));
    }

    public static Expression<String> hexForText(Expression<String> numberText) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), numberText);
    }

    /**
     * @see #exp(Expression)
     */
    public static <E extends Number> Expression<Double> ln(Expression<E> power) {
        return SQLFunctions.oneArgumentFunc("LN", _MappingFactory.getMapping(Double.class), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log(Expression<E> power) {
        return SQLFunctions.oneArgumentFunc("LOG", _MappingFactory.getMapping(Double.class), power);
    }

    public static <B extends Number, P extends Number> Expression<Double> log(Expression<B> bottomNumber
            , Expression<P> power) {
        return SQLFunctions.twoArgumentFunc("LOG", _MappingFactory.getMapping(Double.class), bottomNumber, power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log2(Expression<E> power) {
        return log(SQLs.literal(2), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log10(Expression<E> power) {
        return log(SQLs.literal(10), power);
    }

    public static <E extends Number> Expression<E> mod(Expression<E> dividend
            , Expression<E> divisor) {
        return SQLFunctions.twoArgumentFunc("MOD", dividend.paramMeta().mappingType(), dividend, divisor);
    }


    public static Expression<Double> pi() {
        return SQLFunctions.noArgumentFunc("PI", _MappingFactory.getMapping(Double.class));
    }


    /*################################## blow date time function method ##################################*/

    public static Expression<LocalDateTime> now() {
        return SQLFunctions.noArgumentFunc("NOW", _MappingFactory.getMapping(LocalDateTime.class));
    }

    public static Expression<LocalDate> currentDate() {
        return SQLFunctions.noArgumentFunc("CURRENT_DATE", _MappingFactory.getMapping(LocalDate.class));
    }

    /*################################## blow static inner class  ##################################*/




}
