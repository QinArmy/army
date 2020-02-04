package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.mapping.*;

import java.time.LocalDateTime;


public abstract class SQLS {

    protected SQLS() {
        throw new UnsupportedOperationException();
    }

    public static <T extends IDomain> SingleSetAble<T> update(TableMeta<T> tableMeta) {
        return new SingleUpatableImpl<>(tableMeta);
    }

    public static <E> ParamExpression<E> asNull(MappingType mappingType) {
        return ParamExpressionImp.build(mappingType, null);
    }

    public static <E> ConstantExpression<E> constant(E value) {
        return ConstantExpressionImpl.build(null, value);
    }

    public static <E> ConstantExpression<E> constant(E value, @Nullable MappingType mappingType) {
        return ConstantExpressionImpl.build(mappingType, value);
    }


    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static <E extends Number> Expression<E> abs(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ABS", x.mappingType(), x);
    }

    public static <E extends Number> Expression<Double> acos(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ACOS", DoubleType.INSTANCE, x);
    }


    public static <E extends Number> Expression<Double> asin(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ASIN", DoubleType.INSTANCE, x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ATAN", DoubleType.INSTANCE, x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> one, Expression<E> two) {
        return new Funcs.TwoArgumentFunc<>("ATAN", DoubleType.INSTANCE, one, two);
    }

    public static <E extends Number> Expression<Integer> cell(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("CELL", IntegerType.INSTANCE, x);
    }

    public static <E extends Number> Expression<Long> cellAsLong(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("CELL", LongType.INSTANCE, x);
    }

    public static <E extends Number> Expression<String> conv(Expression<E> number, int fromBase, int toBase) {
        return new Funcs.ThreeArgumentFunc<>("CONV", StringType.INSTANCE, number, constant(fromBase), constant(toBase));
    }

    public static <E extends Number> Expression<Double> cos(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("COS", DoubleType.INSTANCE, x);
    }

    public static <E extends Number> Expression<Double> cot(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("COT", DoubleType.INSTANCE, x);
    }

    public static <E extends Number> Expression<Long> crc32(Expression<E> expression) {
        return new Funcs.OneArgumentFunc<>("CRC32", LongType.INSTANCE, expression);
    }

    public static <E extends Number> Expression<Double> degrees(Expression<E> radian) {
        return new Funcs.OneArgumentFunc<>("DEGREES", DoubleType.INSTANCE, radian);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> exp(Expression<E> index) {
        return new Funcs.OneArgumentFunc<>("EXP", DoubleType.INSTANCE, index);
    }

    public static <E extends Number> Expression<Integer> floor(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("FLOOR", IntegerType.INSTANCE, number);
    }

    public static <E extends Number> Expression<Integer> floorAsLong(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("FLOOR", LongType.INSTANCE, number);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, Expression<E> decimal) {
        return new Funcs.TwoArgumentFunc<>("FORMAT", StringType.INSTANCE, number, decimal);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, int decimal) {
        return new Funcs.TwoArgumentFunc<>("FORMAT", StringType.INSTANCE, number, constant(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("HEX", StringType.INSTANCE, number);
    }

    public static Expression<String> hex(Number number) {
        return new Funcs.OneArgumentFunc<>("HEX", StringType.INSTANCE, constant(number));
    }

    public static Expression<String> hex(String numberText) {
        return new Funcs.OneArgumentFunc<>("HEX", StringType.INSTANCE, constant(numberText));
    }

    public static Expression<String> hexForText(Expression<String> numberText) {
        return new Funcs.OneArgumentFunc<>("HEX", StringType.INSTANCE, numberText);
    }

    /**
     * @see #exp(Expression)
     */
    public static <E extends Number> Expression<Double> ln(Expression<E> power) {
        return new Funcs.OneArgumentFunc<>("LN", DoubleType.INSTANCE, power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log(Expression<E> power) {
        return new Funcs.OneArgumentFunc<>("LOG", DoubleType.INSTANCE, power);
    }

    public static <B extends Number, P extends Number> Expression<Double> log(Expression<B> bottomNumber
            , Expression<P> power) {
        return new Funcs.TwoArgumentFunc<>("LOG", DoubleType.INSTANCE, bottomNumber, power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log2(Expression<E> power) {
        return log(constant(2), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log10(Expression<E> power) {
        return log(constant(10), power);
    }

    public static <E extends Number> Expression<E> mod(Expression<E> dividend
            , Expression<E> divisor) {
        return new Funcs.TwoArgumentFunc<>("MOD", dividend.mappingType(), dividend, divisor);
    }


    public static Expression<Double> pi() {
        return new Funcs.NoArgumentFunc<>("PI", DoubleType.INSTANCE);
    }



    /*################################## blow date time function method ##################################*/

    public static Expression<LocalDateTime> now() {
        return new Funcs.NoArgumentFunc<>("NOW", LocalDateTimeType.INSTANCE);
    }


}
