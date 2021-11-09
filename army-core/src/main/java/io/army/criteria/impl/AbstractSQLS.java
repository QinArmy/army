package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.GenericField;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Function;

abstract class AbstractSQLS {


    AbstractSQLS() {
        throw new UnsupportedOperationException();
    }


    public static <E> ParamExpression<E> asNull(Class<?> nullTypeClass) {
        return ParamExpressionImp.build(_MappingFactory.getMapping(nullTypeClass), null);
    }

    public static <E> ParamExpression<E> asNull(MappingType mappingType) {
        return ParamExpressionImp.build(mappingType, null);
    }

    public static <E> ParamExpression<E> param(E param) {
        return ParamExpressionImp.build(null, param);
    }

    public static <E> ParamExpression<E> param(E param, ParamMeta paramMeta) {
        return ParamExpressionImp.build(paramMeta, param);
    }

    /**
     * @see Sqls#batchSingleUpdate(TableMeta)
     * @see Sqls#batchSingleUpdate(TableMeta, Object)
     * @see Sqls#batchSingleDelete()
     * @see Sqls#batchSingleDelete(Object)
     */
    public static <E> NamedParamExpression<E> namedParam(String name, ParamMeta paramMeta) {
        return NamedParamExpressionImpl.build(name, paramMeta);
    }

    public static ParamMeta obtainParamMeta(Expression<?> expression) {
        ParamMeta paramMeta;
        if (expression instanceof GenericField) {
            FieldMeta<?, ?> fieldMeta = ((GenericField<?, ?>) expression).fieldMeta();
            if (fieldMeta.codec()) {
                paramMeta = fieldMeta;
            } else {
                paramMeta = fieldMeta.mappingMeta();
            }
        } else {
            paramMeta = expression.mappingMeta();
        }
        return paramMeta;
    }

    /**
     * package method
     */
    @SuppressWarnings("unchecked")
    static Expression<?> paramWithExp(Object value, Expression<?> expression) {
        Expression<?> actualExp;
        if (value instanceof Expression) {
            actualExp = (Expression<?>) value;
        } else if (value instanceof Function) {
            actualExp = ((Function<Object, Expression<?>>) value).apply(CriteriaContextHolder.getContext().criteria());
        } else {
            actualExp = ParamExpressionImp.build(obtainParamMeta(expression), value);
        }
        return actualExp;
    }

    /**
     * @see MappingType#toConstant(ParamMeta, Object)
     */
    public static <E> ConstantExpression<E> constant(E value) {
        return ConstantExpressionImpl.build(null, value);
    }

    /**
     * @see MappingType#toConstant(ParamMeta, Object)
     */
    public static <E> ConstantExpression<E> constant(E value, @Nullable ParamMeta paramMeta) {
        return ConstantExpressionImpl.build(paramMeta, value);
    }


    @SuppressWarnings("unchecked")
    public static <E> Expression<E> defaultValue() {
        return (Expression<E>) DefaultValueExpression.INSTANCE;
    }

    public static TableMeta<Dual> dual() {
        return Dual.DualTableMeta.INSTANCE;
    }

    public static <E> Expression<Collection<E>> collection(ParamMeta paramMeta, Collection<E> collection) {
        return CollectionExpressionImpl.build(paramMeta, collection);
    }

    public static <E> Expression<Collection<E>> collectionWithExp(Expression<E> expression, Collection<E> collection) {
        return CollectionExpressionImpl.build(obtainParamMeta(expression), collection);
    }

    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static <E extends Number> FuncExpression<E> abs(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ABS", x.mappingMeta(), x);
    }

    public static <E extends Number> Expression<Double> acos(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ACOS", _MappingFactory.getMapping(Double.class), x);
    }


    public static <E extends Number> Expression<Double> asin(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ASIN", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ATAN", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> one, Expression<E> two) {
        return AbstractFunc.twoArgumentFunc("ATAN", _MappingFactory.getMapping(Double.class), one, two);
    }

    public static <E extends Number> Expression<Integer> cell(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("CELL", _MappingFactory.getMapping(Integer.class), x);
    }

    public static <E extends Number> Expression<Long> cellAsLong(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("CELL", _MappingFactory.getMapping(Long.class), x);
    }

    public static <E extends Number> Expression<String> conv(Expression<E> number, int fromBase, int toBase) {
        return new AbstractFunc.ThreeArgumentFunc<>("CONV", _MappingFactory.getMapping(String.class), number
                , constant(fromBase), constant(toBase));
    }

    public static <E extends Number> Expression<Double> cos(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("COS", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> cot(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("COT", _MappingFactory.getMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Long> crc32(Expression<E> expression) {
        return AbstractFunc.oneArgumentFunc("CRC32", _MappingFactory.getMapping(Long.class), expression);
    }

    public static <E extends Number> Expression<Double> degrees(Expression<E> radian) {
        return AbstractFunc.oneArgumentFunc("DEGREES", _MappingFactory.getMapping(Double.class), radian);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> exp(Expression<E> index) {
        return AbstractFunc.oneArgumentFunc("EXP", _MappingFactory.getMapping(Double.class), index);
    }

    public static <E extends Number> Expression<Integer> floor(Expression<E> number) {
        return AbstractFunc.oneArgumentFunc("FLOOR", _MappingFactory.getMapping(Integer.class), number);
    }

    public static <E extends Number> Expression<Long> floorAsLong(Expression<E> number) {
        return AbstractFunc.oneArgumentFunc("FLOOR", _MappingFactory.getMapping(Long.class), number);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, Expression<E> decimal) {
        return AbstractFunc.twoArgumentFunc("FORMAT", _MappingFactory.getMapping(String.class), number, decimal);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, int decimal) {
        return AbstractFunc.twoArgumentFunc("FORMAT", _MappingFactory.getMapping(String.class)
                , number, constant(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), number);
    }

    public static Expression<String> hex(Number number) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), constant(number));
    }

    public static Expression<String> hex(String numberText) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), constant(numberText));
    }

    public static Expression<String> hexForText(Expression<String> numberText) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), numberText);
    }

    /**
     * @see #exp(Expression)
     */
    public static <E extends Number> Expression<Double> ln(Expression<E> power) {
        return AbstractFunc.oneArgumentFunc("LN", _MappingFactory.getMapping(Double.class), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log(Expression<E> power) {
        return AbstractFunc.oneArgumentFunc("LOG", _MappingFactory.getMapping(Double.class), power);
    }

    public static <B extends Number, P extends Number> Expression<Double> log(Expression<B> bottomNumber
            , Expression<P> power) {
        return AbstractFunc.twoArgumentFunc("LOG", _MappingFactory.getMapping(Double.class), bottomNumber, power);
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
        return AbstractFunc.twoArgumentFunc("MOD", dividend.mappingMeta(), dividend, divisor);
    }


    public static Expression<Double> pi() {
        return AbstractFunc.noArgumentFunc("PI", _MappingFactory.getMapping(Double.class));
    }


    /*################################## blow date time function method ##################################*/

    public static Expression<LocalDateTime> now() {
        return AbstractFunc.noArgumentFunc("NOW", _MappingFactory.getMapping(LocalDateTime.class));
    }

    public static Expression<LocalDate> currentDate() {
        return AbstractFunc.noArgumentFunc("CURRENT_DATE", _MappingFactory.getMapping(LocalDate.class));
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
        public MappingType mappingMeta() {
            return StringType.build(String.class);
        }
    }
}
