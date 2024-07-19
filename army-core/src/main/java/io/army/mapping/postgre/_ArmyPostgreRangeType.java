/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.executor.DataAccessException;
import io.army.function.TextFunction;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionType;
import io.army.mapping.postgre.array.PostgreMultiRangeArrayType;
import io.army.mapping.postgre.array.PostgreSingleRangeArrayType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util.ClassUtils;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * Package class This class is base class of below:
 * <ul>
 *     <li>{@link PostgreRangeType}</li>
 *     <li>{@link PostgreSingleRangeArrayType}</li>
 *     <li>{@link PostgreMultiRangeArrayType}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public abstract class _ArmyPostgreRangeType extends _ArmyNoInjectionType {


    public final PostgreType dataType;

    protected final Class<?> javaType;

    protected final Class<?> underlyingJavaType;

    protected final RangeFunction<Object, ?> rangeFunc;

    protected final PostgreSingleRangeType.MockRangeFunction<?> mockFunction;

    /**
     * <p>
     * package constructor
     */
    @SuppressWarnings("unchecked")
    protected _ArmyPostgreRangeType(final PostgreType dataType, final Class<?> javaType,
                                    final @Nullable RangeFunction<?, ?> rangeFunc) {

        if (javaType == String.class) {
            this.underlyingJavaType = javaType;
        } else {
            this.underlyingJavaType = ArrayUtils.underlyingComponent(javaType);
        }
        assert rangeFunc != null || this.underlyingJavaType == String.class;
        this.dataType = dataType;
        this.javaType = javaType;
        this.rangeFunc = (RangeFunction<Object, ?>) rangeFunc;
        if (this.underlyingJavaType == String.class || ArmyPostgreRange.class.isAssignableFrom(javaType)) {
            this.mockFunction = null;
        } else {
            this.mockFunction = PostgreRangeType.createMockFunction(javaType, boundJavaType(dataType));
        }
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return this.dataType;
    }

    @Override
    public final boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (this.getClass().isInstance(type)) {
            match = ((_ArmyPostgreRangeType) type).dataType == this.dataType;
        } else {
            match = false;
        }
        return match;
    }


    protected final void serialize(final Object bound, final StringBuilder builder) {
        switch (this.dataType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY: {
                if (!(bound instanceof Integer)) {
                    throw boundTypeError(bound);
                }
                builder.append(bound);
            }
            break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY: {
                if (!(bound instanceof Long)) {
                    throw boundTypeError(bound);
                }
                builder.append(bound);
            }
            break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY: {
                if (!(bound instanceof BigDecimal)) {
                    throw boundTypeError(bound);
                }
                builder.append(((BigDecimal) bound).toPlainString());
            }
            break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY: {
                if (!(bound instanceof LocalDate)) {
                    throw boundTypeError(bound);
                }
                builder.append(bound);
            }
            break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY: {
                if (!(bound instanceof LocalDateTime)) {
                    throw boundTypeError(bound);
                }
                builder.append(_Constant.DOUBLE_QUOTE);
                builder.append(((LocalDateTime) bound).format(_TimeUtils.DATETIME_FORMATTER_6));
                builder.append(_Constant.DOUBLE_QUOTE);
            }
            break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY: {
                if (!(bound instanceof OffsetDateTime)) {
                    throw boundTypeError(bound);
                }
                builder.append(_Constant.DOUBLE_QUOTE);
                builder.append(((OffsetDateTime) bound).format(_TimeUtils.OFFSET_DATETIME_FORMATTER_6));
                builder.append(_Constant.DOUBLE_QUOTE);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.dataType);
        }

    }

    @Nullable
    protected final Object deserialize(final String text) {
        if (PostgreRangeType.INFINITY.equals(text)) {
            return null;
        }
        final Object value;
        switch (this.dataType) {
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
                throw _Exceptions.unexpectedEnum(this.dataType);
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
                ClassUtils.safeClassName(bound));
        return new MetaException(m);
    }


    protected static Class<?> boundJavaType(final PostgreType sqlType) {
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
                                                             final Function<String, T> parseFunc, final DataType dataType,
                                                             final MappingType type, final ErrorHandler handler) {
        return (str, offset, end) -> {
            char ch;
            if (offset + 5 == end && ((ch = str.charAt(offset)) == 'e' || ch == 'E')
                    && str.regionMatches(true, offset, PostgreRangeType.EMPTY, 0, 5)) {
                String m = "multi-range must be non-empty and non-null";
                throw handler.apply(type, dataType, nonNull, new IllegalArgumentException(m));
            }
            return PostgreRangeType.parseNonEmptyRange(str, offset, end, rangeFunc, parseFunc);
        };
    }


    /**
     * @throws IllegalArgumentException            when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws CriteriaException                   when text error and handler throw this type.
     * @throws DataAccessException when text error and handler throw this type.
     */
    @SuppressWarnings("unchecked")
    static <T, R> R parseRange(final String text, final @Nullable RangeFunction<T, R> rangeFunc,
                               final Function<String, T> parseFunc, final DataType dataType,
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
                throw handler.apply(type, dataType, text, e);
            }
        }
        return (R) value;
    }

    @SuppressWarnings("unchecked")
    protected static <T> void rangeToText(final Object nonNull, final BiConsumer<T, StringBuilder> consumer,
                                          final MappingType type, final StringBuilder builder) {
        if (nonNull instanceof ArmyPostgreRange) {
            final ArmyPostgreRange<T> range = (ArmyPostgreRange<T>) nonNull;
            if (range.isEmpty()) {
                builder.append(PostgreRangeType.EMPTY);
            } else {
                if (range.isIncludeLowerBound()) {
                    builder.append(_Constant.LEFT_SQUARE_BRACKET);
                } else {
                    builder.append(_Constant.LEFT_PAREN);
                }
                final T lowerBound, upperBound;
                lowerBound = range.getLowerBound();
                upperBound = range.getUpperBound();

                if (lowerBound != null) {
                    consumer.accept(lowerBound, builder);
                }
                builder.append(_Constant.COMMA);
                if (upperBound != null) {
                    consumer.accept(upperBound, builder);
                }

                if (range.isIncludeUpperBound()) {
                    builder.append(_Constant.RIGHT_SQUARE_BRACKET);
                } else {
                    builder.append(_Constant.RIGHT_PAREN);
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
                builder.append(PostgreRangeType.EMPTY);
            } else {
                if (mockFunction.isIncludeLowerBound.apply(nonNull)) {
                    builder.append(_Constant.LEFT_SQUARE_BRACKET);
                } else {
                    builder.append(_Constant.LEFT_PAREN);
                }
                final T lowerBound, upperBound;
                lowerBound = mockFunction.getLowerBound.apply(nonNull);
                upperBound = mockFunction.getUpperBound.apply(nonNull);

                if (lowerBound != null) {
                    consumer.accept(lowerBound, builder);
                }
                builder.append(_Constant.COMMA);
                if (upperBound != null) {
                    consumer.accept(upperBound, builder);
                }

                if (mockFunction.isIncludeUpperBound.apply(nonNull)) {
                    builder.append(_Constant.RIGHT_SQUARE_BRACKET);
                } else {
                    builder.append(_Constant.RIGHT_PAREN);
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
                        String m = String.format("%s isn't instance of %s.", ClassUtils.safeClassName(result),
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
