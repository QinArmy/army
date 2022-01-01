package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.NamedParam;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
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
abstract class SQLUtils {

    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    SQLUtils() {
        throw new UnsupportedOperationException();
    }


    public static <E> Expression<E> param(E value) {
        Objects.requireNonNull(value);
        return ParamExpressions.optimizing(_MappingFactory.getMapping(value.getClass()), value);
    }

    public static <E> Expression<E> param(MappingType mappingType, @Nullable E value) {
        return ParamExpressions.optimizing(mappingType, value);
    }

    public static <E> Expression<E> param(final Expression<?> type, @Nullable E value) {
        return ParamExpressions.optimizing(type.paramMeta(), value);
    }

    /**
     * package method
     */
    @SuppressWarnings("unchecked")
    static Expression<?> paramWithExp(final Expression<?> type, final @Nullable Object value) {
        final Expression<?> resultExpression;
        if (value instanceof Expression) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            resultExpression = (Expression<?>) value;
        } else if (value instanceof Function) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            resultExpression = ((Function<Object, Expression<?>>) value).apply(CriteriaContextStack.getCriteria());
        } else if (value instanceof Supplier) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            resultExpression = (Expression<?>) ((Supplier<?>) value).get();
        } else {
            // use optimizing param expression
            resultExpression = ParamExpressions.optimizing(type.paramMeta(), value);
        }
        return resultExpression;
    }

    public static <E> Expression<E> strictParam(final MappingType type, final @Nullable E value) {
        return ParamExpressions.strict(type, value);
    }

    public static <E> Expression<E> strictParam(final Expression<?> type, final @Nullable E value) {
        return ParamExpressions.strict(type.paramMeta(), value);
    }


    public static <E> Expression<Collection<E>> collectionParam(Expression<?> type, Collection<E> value) {
        return CollectionParamExpression.optimizing(type, value);
    }

    public static <E> Expression<Collection<E>> strictCollectionParam(Expression<?> type, Collection<E> value) {
        return CollectionParamExpression.strict(type, value);
    }


    /**
     * @see SQLs#standardBatchUpdate()
     * @see SQLs#standardBatchUpdate(Object)
     * @see SQLs#standardBatchDelete()
     * @see SQLs#standardBatchDelete(Object)
     */
    public static <E> NamedParam<E> namedParam(String name, ParamMeta paramMeta) {
        return NamedParamImpl.named(name, paramMeta);
    }

    /**
     * @see SQLs#standardBatchUpdate()
     * @see SQLs#standardBatchUpdate(Object)
     * @see SQLs#standardBatchDelete()
     * @see SQLs#standardBatchDelete(Object)
     */
    public static <E> NamedParam<E> namedParam(GenericField<?, ?> field) {
        return NamedParamImpl.named(field.fieldName(), field);
    }


    /**
     * @see SQLs#standardBatchUpdate()
     * @see SQLs#standardBatchUpdate(Object)
     * @see SQLs#standardBatchDelete()
     * @see SQLs#standardBatchDelete(Object)
     */
    public static <E> NamedParam<E> nonNullNamedParam(String name, ParamMeta paramMeta) {
        return NamedParamImpl.nonNull(name, paramMeta);
    }

    /**
     * @see SQLs#standardBatchUpdate()
     * @see SQLs#standardBatchUpdate(Object)
     * @see SQLs#standardBatchDelete()
     * @see SQLs#standardBatchDelete(Object)
     */
    public static <E> NamedParam<E> nonNullNamedParam(GenericField<?, ?> field) {
        return NamedParamImpl.nonNull(field.fieldName(), field);
    }

    public static <E> Expression<E> literal(E value) {
        Objects.requireNonNull(value);
        return LiteralExpression.literal(_MappingFactory.getMapping(value.getClass()), value);
    }

    public static <E> Expression<E> literal(ParamMeta paramMeta, E value) {
        return LiteralExpression.literal(paramMeta, value);
    }

    /**
     * Only used to update set clause.
     */
    @SuppressWarnings("unchecked")
    public static <E> Expression<E> defaultWord() {
        return (Expression<E>) SQLs.DefaultWord.INSTANCE;
    }

    /**
     * Only used to update set clause.
     */
    @SuppressWarnings("unchecked")
    public static <E> Expression<E> nullWord() {
        return (Expression<E>) SQLs.NullWord.INSTANCE;
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
        return new SQLFunctions.ThreeArgumentFunc<>("CONV", _MappingFactory.getMapping(String.class), number
                , literal(fromBase), literal(toBase));
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
                , number, literal(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), number);
    }

    public static Expression<String> hex(Number number) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), literal(number));
    }

    public static Expression<String> hex(String numberText) {
        return SQLFunctions.oneArgumentFunc("HEX", _MappingFactory.getMapping(String.class), literal(numberText));
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


    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * </p>
     *
     * @param <E> The java type The expression thant reference kwy word {@code DEFAULT}
     */
    private static final class DefaultWord<E> extends NoNOperationExpression<E> {

        private static DefaultWord<?> INSTANCE = new DefaultWord<>();

        private DefaultWord() {
        }


        @Override
        public ParamMeta paramMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" DEFAULT");
        }

        @Override
        public String toString() {
            return " DEFAULT";
        }

    }// DefaultWord


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     * </p>
     *
     * @param <E> The java type The expression thant reference kwy word {@code NULL}
     */
    private static final class NullWord<E> extends NoNOperationExpression<E> {

        private static final NullWord<?> INSTANCE = new NullWord<>();


        private NullWord() {
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.sqlBuilder().append(" NULL");
        }

        @Override
        public ParamMeta paramMeta() {
            throw unsupportedOperation();
        }

        @Override
        public String toString() {
            return " NULL";
        }


    }// NullWord


}
