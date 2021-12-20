package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FuncExpression;
import io.army.criteria.GenericField;
import io.army.criteria.NamedParamExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Function;

abstract class AbstractSQLs {


    AbstractSQLs() {
        throw new UnsupportedOperationException();
    }


    @Deprecated
    public static <E> Expression<E> asNull(Class<?> nullTypeClass) {
        return ParamExpression.create(_MappingFactory.getMapping(nullTypeClass), null);
    }

    @Deprecated
    public static <E> Expression<E> asNull(MappingType mappingType) {
        return ParamExpression.create(mappingType, null);
    }


    public static <E> Expression<E> param(E param) {
        return ParamExpression.create(param);
    }

    @Deprecated
    public static <E> Expression<E> param(E param, ParamMeta paramMeta) {
        return ParamExpression.create(paramMeta, param);
    }

    public static <E> Expression<E> param(MappingType mappingType, @Nullable E value) {
        return ParamExpression.create(mappingType, value);
    }

    public static <E> Expression<E> param(final Expression<?> type, @Nullable E value) {
        final ParamMeta paramMeta;
        if (type instanceof GenericField) {
            paramMeta = (GenericField<?, ?>) type;
        } else {
            paramMeta = type.mappingType();
        }
        return ParamExpression.create(paramMeta, value);
    }

    public static <E> Expression<Collection<E>> collectionParam(Expression<?> type, Collection<E> value) {
        return CollectionParamExpression.create(type, value);
    }


    /**
     * @see SQLs#batchUpdate()
     * @see SQLs#batchUpdate(Object)
     * @see SQLs#batchDelete()
     * @see SQLs#batchDelete(Object)
     */
    public static <E> NamedParamExpression<E> namedParam(String name, ParamMeta paramMeta) {
        return NamedParamImpl.create(name, paramMeta);
    }

    /**
     * @see SQLs#batchUpdate()
     * @see SQLs#batchUpdate(Object)
     * @see SQLs#batchDelete()
     * @see SQLs#batchDelete(Object)
     */
    public static <E> NamedParamExpression<E> namedParam(GenericField<?, ?> field) {
        return NamedParamImpl.create(field.fieldName(), field);
    }


    @Deprecated
    public static ParamMeta obtainParamMeta(Expression<?> expression) {
        ParamMeta paramMeta;
        if (expression instanceof GenericField) {
            FieldMeta<?, ?> fieldMeta = ((GenericField<?, ?>) expression).fieldMeta();
            if (fieldMeta.codec()) {
                paramMeta = fieldMeta;
            } else {
                paramMeta = fieldMeta.mappingType();
            }
        } else {
            paramMeta = expression.mappingType();
        }
        return paramMeta;
    }

    /**
     * package method
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    static Expression<?> paramWithExp(Object value, Expression<?> expression) {
        final Expression<?> actualExp;
        if (value instanceof Expression) {
            actualExp = (Expression<?>) value;
        } else if (value instanceof Function) {
            actualExp = ((Function<Object, Expression<?>>) value).apply(CriteriaContextStack.pop().criteria());
        } else {
            actualExp = param(expression, value);
        }
        return actualExp;
    }


    public static <E> Expression<E> literal(E value) {
        return LiteralExpression.create(value);
    }

    public static <E> Expression<E> literal(ParamMeta paramMeta, E value) {
        return LiteralExpression.create(paramMeta, value);
    }


    @SuppressWarnings("unchecked")
    public static <E> Expression<E> defaultWord() {
        return (Expression<E>) DefaultWord.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <E> Expression<E> nullWord() {
        return (Expression<E>) NullWord.INSTANCE;
    }

    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static <E extends Number> FuncExpression<E> abs(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ABS", x.mappingType(), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<Double> acos(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ACOS", _MappingFactory.getMapping(Double.class), (_Expression<?>) x);
    }


    public static <E extends Number> Expression<Double> asin(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ASIN", _MappingFactory.getMapping(Double.class), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("ATAN", _MappingFactory.getMapping(Double.class), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> one, Expression<E> two) {
        return AbstractFunc.twoArgumentFunc("ATAN", _MappingFactory.getMapping(Double.class), (_Expression<?>) one, (_Expression<?>) two);
    }

    public static <E extends Number> Expression<Integer> cell(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("CELL", _MappingFactory.getMapping(Integer.class), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<Long> cellAsLong(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("CELL", _MappingFactory.getMapping(Long.class), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<String> conv(Expression<E> number, int fromBase, int toBase) {
        return new AbstractFunc.ThreeArgumentFunc<>("CONV", _MappingFactory.getMapping(String.class), number
                , literal(fromBase), literal(toBase));
    }

    public static <E extends Number> Expression<Double> cos(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("COS", _MappingFactory.getMapping(Double.class), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<Double> cot(Expression<E> x) {
        return AbstractFunc.oneArgumentFunc("COT", _MappingFactory.getMapping(Double.class), (_Expression<?>) x);
    }

    public static <E extends Number> Expression<Long> crc32(Expression<E> expression) {
        return AbstractFunc.oneArgumentFunc("CRC32", _MappingFactory.getMapping(Long.class), (_Expression<?>) expression);
    }

    public static <E extends Number> Expression<Double> degrees(Expression<E> radian) {
        return AbstractFunc.oneArgumentFunc("DEGREES", _MappingFactory.getMapping(Double.class), (_Expression<?>) radian);
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
                , number, literal(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), number);
    }

    public static Expression<String> hex(Number number) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), literal(number));
    }

    public static Expression<String> hex(String numberText) {
        return AbstractFunc.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), literal(numberText));
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
        return log(literal(2), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log10(Expression<E> power) {
        return log(literal(10), power);
    }

    public static <E extends Number> Expression<E> mod(Expression<E> dividend
            , Expression<E> divisor) {
        return AbstractFunc.twoArgumentFunc("MOD", dividend.mappingType(), dividend, divisor);
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

}
