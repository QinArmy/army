package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.mapping.postgre.array.PostgreMultiRangeArrayType;
import io.army.mapping.postgre.array.PostgreSingleRangeArrayType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * Package class This class is base class of below:
 *     <ul>
 *         <li>{@link PostgreRangeType}</li>
 *         <li>{@link PostgreSingleRangeArrayType}</li>
 *         <li>{@link PostgreMultiRangeArrayType}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public abstract class _ArmyPostgreRangeType extends _ArmyNoInjectionMapping {


    public final PostgreSqlType sqlType;

    protected final Class<?> javaType;

    protected final RangeFunction<Object, ?> rangeFunc;

    protected final PostgreSingleRangeType.MockRangeFunction<?> mockFunction;

    /**
     * <p>
     * package constructor
     * </p>
     */
    @SuppressWarnings("unchecked")
    protected _ArmyPostgreRangeType(final PostgreSqlType sqlType, final Class<?> javaType,
                                    final @Nullable RangeFunction<?, ?> rangeFunc) {
        final Class<?> underlyingType;
        if (javaType == String.class) {
            underlyingType = javaType;
        } else {
            underlyingType = ArrayUtils.underlyingComponent(javaType);
        }
        assert rangeFunc != null || underlyingType == String.class;
        this.sqlType = sqlType;
        this.javaType = javaType;
        this.rangeFunc = (RangeFunction<Object, ?>) rangeFunc;
        if (underlyingType == String.class || ArmyPostgreRange.class.isAssignableFrom(javaType)) {
            this.mockFunction = null;
        } else {
            this.mockFunction = PostgreRangeType.createMockFunction(javaType, boundJavaType(sqlType));
        }
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.Postgre) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return this.sqlType;
    }

    @Override
    public final boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (this.getClass().isInstance(type)) {
            match = ((_ArmyPostgreRangeType) type).sqlType == this.sqlType;
        } else {
            match = false;
        }
        return match;
    }


    protected final void serialize(final Object bound, final Consumer<String> appender) {
        switch (this.sqlType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY: {
                if (!(bound instanceof Integer)) {
                    throw boundTypeError(bound);
                }
                appender.accept(bound.toString());
            }
            break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY: {
                if (!(bound instanceof Long)) {
                    throw boundTypeError(bound);
                }
                appender.accept(bound.toString());
            }
            break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY: {
                if (!(bound instanceof BigDecimal)) {
                    throw boundTypeError(bound);
                }
                appender.accept(((BigDecimal) bound).toPlainString());
            }
            break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY: {
                if (!(bound instanceof LocalDate)) {
                    throw boundTypeError(bound);
                }
                appender.accept(bound.toString());
            }
            break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY: {
                if (!(bound instanceof LocalDateTime)) {
                    throw boundTypeError(bound);
                }
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
                appender.accept(((LocalDateTime) bound).format(_TimeUtils.DATETIME_FORMATTER_6));
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
            }
            break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY: {
                if (!(bound instanceof OffsetDateTime)) {
                    throw boundTypeError(bound);
                }
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
                appender.accept(((OffsetDateTime) bound).format(_TimeUtils.OFFSET_DATETIME_FORMATTER_6));
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.sqlType);
        }

    }

    protected final Object deserialize(final String text) {
        final Object value;
        switch (this.sqlType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY:
                value = Integer.parseInt(text);
                break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
                value = Long.parseLong(text);
                break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
                value = new BigDecimal(text);
                break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
                //TODO postgre date format?
                value = LocalDate.parse(text);
                break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
                value = LocalDateTime.parse(text, _TimeUtils.DATETIME_FORMATTER_6);
                break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:
                value = OffsetDateTime.parse(text, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.sqlType);
        }
        return value;
    }

    /**
     * army inner method
     */
    public PostgreMultiRangeType _fromMultiArray(PostgreMultiRangeArrayType type) {
        throw new UnsupportedOperationException();
    }

    /**
     * army inner method
     */
    public PostgreSingleRangeType _fromSingleArray(PostgreSingleRangeArrayType type) {
        throw new UnsupportedOperationException();
    }

    private MetaException boundTypeError(@Nullable Object bound) {
        String m = String.format("%s type return bound java type %s error.", this.javaType.getName(),
                _ClassUtils.safeClassName(bound));
        return new MetaException(m);
    }


    protected static Class<?> boundJavaType(final PostgreSqlType sqlType) {
        final Class<?> type;
        switch (sqlType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY:
                type = Integer.class;
                break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
                type = Long.class;
                break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
                type = BigDecimal.class;
                break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
                type = LocalDate.class;
                break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
                type = LocalDateTime.class;
                break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:
                type = OffsetDateTime.class;
                break;
            default:
                throw _Exceptions.unexpectedEnum(sqlType);
        }
        return type;
    }

    @Nullable
    protected static RangeFunction<?, ?> tryCreateDefaultRangeFunc(final Class<?> targetType, final Class<?> elementType) {
        RangeFunction<?, ?> rangeFunc;
        try {
            rangeFunc = PostgreRangeType.createRangeFunction(targetType, elementType, CREATE);
        } catch (Throwable e) {
            rangeFunc = null;
        }
        return rangeFunc;
    }

    protected static <T> TextFunction<?> multiRangeParseFunc(final Object nonNull, final RangeFunction<T, ?> rangeFunc,
                                                             final Function<String, T> parseFunc, final SqlType sqlType,
                                                             final MappingType type, final ErrorHandler handler) {
        return (str, offset, end) -> {
            char ch;
            if (offset + 5 == end && ((ch = str.charAt(offset)) == 'e' || ch == 'E')
                    && str.regionMatches(true, offset, PostgreRangeType.EMPTY, 0, 5)) {
                String m = "multi-range must be non-empty and non-null";
                throw handler.apply(type, sqlType, nonNull, new IllegalArgumentException(m));
            }
            return PostgreRangeType.parseNonEmptyRange(str, offset, end, rangeFunc, parseFunc);
        };
    }


    /**
     * @throws IllegalArgumentException            when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws CriteriaException                   when text error and handler throw this type.
     * @throws io.army.session.DataAccessException when text error and handler throw this type.
     */
    @SuppressWarnings("unchecked")
    static <T, R> R parseRange(final String text, final @Nullable RangeFunction<T, R> rangeFunc,
                               final Function<String, T> parseFunc, final SqlType sqlType,
                               final MappingType type, final ErrorHandler handler) {
        final Class<?> javaType;
        javaType = type.javaType();
        final Object value;
        if (rangeFunc == null) {
            if (javaType != String.class) {
                String m = String.format("function is null,but %s.javaType() isn't %s",
                        MappingType.class.getName(), String.class.getName());
                throw new IllegalArgumentException(m);
            }
            value = text;
        } else if (PostgreRangeType.EMPTY.equalsIgnoreCase(text)) {
            value = PostgreRangeType.emptyRange(javaType);
        } else {
            try {
                value = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), rangeFunc, parseFunc);
            } catch (Throwable e) {
                throw handler.apply(type, sqlType, text, e);
            }
        }
        return (R) value;
    }

    @SuppressWarnings("unchecked")
    protected static <T> void rangeToText(final Object nonNull, final BiConsumer<T, Consumer<String>> consumer,
                                          final MappingType type, final Consumer<String> appendConsumer) {
        if (nonNull instanceof ArmyPostgreRange) {
            final ArmyPostgreRange<T> range = (ArmyPostgreRange<T>) nonNull;
            if (range.isEmpty()) {
                appendConsumer.accept(PostgreRangeType.EMPTY);
            } else {
                if (range.isIncludeLowerBound()) {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_PAREN));
                }
                final T lowerBound, upperBound;
                lowerBound = range.getLowerBound();
                upperBound = range.getUpperBound();

                if (lowerBound != null) {
                    consumer.accept(lowerBound, appendConsumer);
                }
                appendConsumer.accept(String.valueOf(_Constant.COMMA));
                if (upperBound != null) {
                    consumer.accept(upperBound, appendConsumer);
                }

                if (range.isIncludeUpperBound()) {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_PAREN));
                }
            }
        } else {
            final PostgreRangeType.MockRangeFunction<T> mockFunction;
            if (type instanceof PostgreSingleRangeType) {
                mockFunction = (PostgreRangeType.MockRangeFunction<T>) ((PostgreSingleRangeType) type).mockFunction;
                assert mockFunction != null;
            } else if (type instanceof PostgreRangeType.UserDefinedRangeType) {
                mockFunction = ((PostgreRangeType.UserDefinedRangeType<T>) type).mockFunction();
            } else {
                String m = String.format("either %s is %s type or %s is %s type.",
                        type.javaType().getName(),
                        ArmyPostgreRange.class.getName(),
                        type,
                        PostgreRangeType.UserDefinedRangeType.class.getName()
                );
                throw new IllegalArgumentException(m);
            }
            if (mockFunction.isEmpty.apply(nonNull)) {
                appendConsumer.accept(PostgreRangeType.EMPTY);
            } else {
                if (mockFunction.isIncludeLowerBound.apply(nonNull)) {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_PAREN));
                }
                final T lowerBound, upperBound;
                lowerBound = mockFunction.getLowerBound.apply(nonNull);
                upperBound = mockFunction.getUpperBound.apply(nonNull);

                if (lowerBound != null) {
                    consumer.accept(lowerBound, appendConsumer);
                }
                appendConsumer.accept(String.valueOf(_Constant.COMMA));
                if (upperBound != null) {
                    consumer.accept(upperBound, appendConsumer);
                }

                if (mockFunction.isIncludeUpperBound.apply(nonNull)) {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_PAREN));
                }
            }

        }

    }

    @SuppressWarnings("unchecked")
    static <R> Function<Object, R> rangeBeanFunc(final Class<?> javaType, final String methodName,
                                                 final Class<R> resultType) {
        try {
            final Method method;
            method = javaType.getMethod(methodName);
            return instance -> {
                try {
                    final Object result;
                    result = method.invoke(instance);
                    if (!resultType.isInstance(result)) {
                        String m = String.format("%s isn't instance of %s.", _ClassUtils.safeClassName(result),
                                resultType.getName());
                        throw new MetaException(m);
                    }
                    return (R) result;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new MetaException(e.getMessage(), e);
        }
    }

    static Constructor<?> loadConstructor(final Class<?> javaType, final String methodName, final int colonIndex,
                                          final Class<?> elementType) throws MetaException {

        if (!javaType.getName().equals(methodName.substring(0, colonIndex))) {
            String m = String.format("%s isn't the constructor of %s", methodName, javaType.getName());
            throw new MetaException(m);
        }
        try {
            return javaType.getConstructor(boolean.class, elementType, elementType, boolean.class);
        } catch (NoSuchMethodException e) {
            String m = String.format("constructor[%s] error", methodName);
            throw new MetaException(m, e);
        }
    }

    /**
     * @param colonIndex -2 or positive
     */
    static Method loadFactoryMethod(final Class<?> javaType, final String methodName, final int colonIndex,
                                    final Class<?> elementType) {
        try {
            final Class<?> classOfMethod;
            final String name;
            if (colonIndex == -2) {
                classOfMethod = javaType;
                name = methodName;
            } else {
                classOfMethod = Class.forName(methodName.substring(0, colonIndex));
                name = methodName.substring(colonIndex + 2);
            }
            final Method method;
            method = classOfMethod.getMethod(name, boolean.class, elementType, elementType, boolean.class);
            final int modifier;
            modifier = method.getModifiers();
            if (!(Modifier.isPublic(modifier)
                    && Modifier.isStatic(modifier)
                    && javaType.isAssignableFrom(method.getReturnType()))) {
                String m = String.format("%s isn't public static factory method.", methodName);
                throw new MetaException(m);
            }
            return method;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new MetaException(e.getMessage(), e);
        }
    }

    static IllegalArgumentException nearbyError(String text) {
        return new IllegalArgumentException(String.format("'%s' nearby error.", text));
    }


}
