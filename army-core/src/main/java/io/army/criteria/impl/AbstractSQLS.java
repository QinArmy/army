package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.Expression;
import io.army.criteria.ParamExpression;
import io.army.criteria.SQLContext;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.meta.mapping.StringType;

import java.time.LocalDate;
import java.time.LocalDateTime;

abstract class AbstractSQLS {


    AbstractSQLS() {
        throw new UnsupportedOperationException();
    }


    public static <E> ParamExpression<E> asNull(Class<?> nullTypeClass) {
        return ParamExpressionImp.build(MappingFactory.getDefaultMapping(nullTypeClass), null);
    }

    public static <E> ParamExpression<E> asNull(MappingMeta mappingType) {
        return ParamExpressionImp.build(mappingType, null);
    }

    public static <E> ParamExpression<E> param(E param) {
        return ParamExpressionImp.build(null, param);
    }

    public static <E> ParamExpression<E> param(E param, MappingMeta mappingType) {
        return ParamExpressionImp.build(mappingType, param);
    }

    static <E> ParamExpression<E> param(E param, Expression<E> expression) {
        return ParamExpressionImp.build(expression.mappingType(), param);
    }

    public static <E> ConstantExpression<E> constant(E value) {
        return ConstantExpressionImpl.build(null, value);
    }

    public static <E> ConstantExpression<E> constant(E value, @Nullable MappingMeta mappingType) {
        return ConstantExpressionImpl.build(mappingType, value);
    }

    static <E> ConstantExpression<E> constant(E value, Expression<E> expression) {
        return ConstantExpressionImpl.build(expression.mappingType(), value);
    }

    @SuppressWarnings("unchecked")
    public static <E> Expression<E> defaultValue() {
        return (Expression<E>) DefaultValueExpression.INSTANCE;
    }

    public static TableMeta<Dual> dual() {
        return Dual.DualTableMeta.INSTANCE;
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
        return new Funcs.OneArgumentFunc<>("ACOS", MappingFactory.getDefaultMapping(Double.class), x);
    }


    public static <E extends Number> Expression<Double> asin(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ASIN", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ATAN", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> one, Expression<E> two) {
        return new Funcs.TwoArgumentFunc<>("ATAN", MappingFactory.getDefaultMapping(Double.class), one, two);
    }

    public static <E extends Number> Expression<Integer> cell(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("CELL", MappingFactory.getDefaultMapping(Integer.class), x);
    }

    public static <E extends Number> Expression<Long> cellAsLong(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("CELL", MappingFactory.getDefaultMapping(Long.class), x);
    }

    public static <E extends Number> Expression<String> conv(Expression<E> number, int fromBase, int toBase) {
        return new Funcs.ThreeArgumentFunc<>("CONV", MappingFactory.getDefaultMapping(String.class), number
                , constant(fromBase), constant(toBase));
    }

    public static <E extends Number> Expression<Double> cos(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("COS", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> cot(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("COT", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Long> crc32(Expression<E> expression) {
        return new Funcs.OneArgumentFunc<>("CRC32", MappingFactory.getDefaultMapping(Long.class), expression);
    }

    public static <E extends Number> Expression<Double> degrees(Expression<E> radian) {
        return new Funcs.OneArgumentFunc<>("DEGREES", MappingFactory.getDefaultMapping(Double.class), radian);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> exp(Expression<E> index) {
        return new Funcs.OneArgumentFunc<>("EXP", MappingFactory.getDefaultMapping(Double.class), index);
    }

    public static <E extends Number> Expression<Integer> floor(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("FLOOR", MappingFactory.getDefaultMapping(Integer.class), number);
    }

    public static <E extends Number> Expression<Long> floorAsLong(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("FLOOR", MappingFactory.getDefaultMapping(Long.class), number);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, Expression<E> decimal) {
        return new Funcs.TwoArgumentFunc<>("FORMAT", MappingFactory.getDefaultMapping(String.class), number, decimal);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, int decimal) {
        return new Funcs.TwoArgumentFunc<>("FORMAT", MappingFactory.getDefaultMapping(String.class)
                , number, constant(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), number);
    }

    public static Expression<String> hex(Number number) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), constant(number));
    }

    public static Expression<String> hex(String numberText) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), constant(numberText));
    }

    public static Expression<String> hexForText(Expression<String> numberText) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), numberText);
    }

    /**
     * @see #exp(Expression)
     */
    public static <E extends Number> Expression<Double> ln(Expression<E> power) {
        return new Funcs.OneArgumentFunc<>("LN", MappingFactory.getDefaultMapping(Double.class), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log(Expression<E> power) {
        return new Funcs.OneArgumentFunc<>("LOG", MappingFactory.getDefaultMapping(Double.class), power);
    }

    public static <B extends Number, P extends Number> Expression<Double> log(Expression<B> bottomNumber
            , Expression<P> power) {
        return new Funcs.TwoArgumentFunc<>("LOG", MappingFactory.getDefaultMapping(Double.class), bottomNumber, power);
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
        return new Funcs.NoArgumentFunc<>("PI", MappingFactory.getDefaultMapping(Double.class));
    }


    /*################################## blow date time function method ##################################*/

    public static Expression<LocalDateTime> now() {
        return new Funcs.NoArgumentFunc<>("NOW", MappingFactory.getDefaultMapping(LocalDateTime.class));
    }

    public static Expression<LocalDate> currentDate() {
        return new Funcs.NoArgumentFunc<>("CURRENT_DATE", MappingFactory.getDefaultMapping(LocalDate.class));
    }

    /*################################## blow static inner class  ##################################*/

    private static final class DefaultValueExpression<E> extends AbstractNoNOperationExpression<E> {

        private static final DefaultValueExpression<?> INSTANCE = new DefaultValueExpression<>();

        private DefaultValueExpression() {
        }

        @Override
        protected void afterSpace(SQLContext context) {
            context.sqlBuilder()
                    .append(" DEFAULT");
        }

        @Override
        public MappingMeta mappingType() {
            return StringType.build(String.class);
        }
    }
}
