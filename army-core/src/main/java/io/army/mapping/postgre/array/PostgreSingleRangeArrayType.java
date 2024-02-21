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

package io.army.mapping.postgre.array;

import io.army.annotation.Mapping;
import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.mapping.*;
import io.army.mapping.array.PostgreArrays;
import io.army.mapping.postgre.*;
import io.army.meta.MetaException;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class representing army build-in postgre range array type.
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 * @since 0.6.0
 */
public class PostgreSingleRangeArrayType extends _ArmyPostgreRangeType implements MappingType.SqlArrayType {


    /**
     * @param javaType one dimension or higher dimension array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param param    from {@link Mapping#params()} ,it's the name of <ul>
     *                 <li>{@link PostgreType#INT4RANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#INT8RANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#NUMRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#DATERANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSTZRANGE_ARRAY}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param error.
     */
    public static PostgreSingleRangeArrayType from(final Class<?> javaType, final String param) throws MetaException {
        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }
        if (isNotSingleRangeArrayType(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        }
        return from(javaType, sqlType);
    }


    /**
     * @param javaType one dimension or higher dimension array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  valid instance <ul>
     *                 <li>{@link PostgreType#INT4RANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#INT8RANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#NUMRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#DATERANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSTZRANGE_ARRAY}</li>
     *                 </ul>
     */
    public static PostgreSingleRangeArrayType from(final Class<?> javaType, final PostgreType sqlType)
            throws IllegalArgumentException {

        final RangeFunction<?, ?> rangeFunc;
        final Class<?> componentType;

        final PostgreSingleRangeArrayType instance;
        if (isNotSingleRangeArrayType(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (javaType == String[].class) {
            instance = linearInstance(sqlType);
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreSingleRangeArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == String.class) {
            instance = new PostgreSingleRangeArrayType(sqlType, javaType, null);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(componentType, boundJavaType(sqlType))) == null) {
            throw errorJavaType(PostgreSingleRangeArrayType.class, javaType);
        } else {
            instance = new PostgreSingleRangeArrayType(sqlType, javaType, rangeFunc);
        }
        return instance;
    }


    /**
     * @param javaType one dimension or higher dimension non-string array class
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  valid instance <ul>
     *                 <li>{@link PostgreType#INT4RANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#INT8RANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#NUMRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#DATERANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSTZRANGE_ARRAY}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType or sqlType error
     */
    public static PostgreSingleRangeArrayType fromFunc(final Class<?> javaType,
                                                       final PostgreType sqlType,
                                                       final RangeFunction<?, ?> rangeFunc)
            throws IllegalArgumentException {
        Objects.requireNonNull(rangeFunc);
        if (isNotSingleRangeArrayType(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreSingleRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            throw errorJavaType(PostgreSingleRangeArrayType.class, javaType);
        }
        return new PostgreSingleRangeArrayType(sqlType, javaType, rangeFunc);
    }


    /**
     * @param javaType   one dimension or higher dimension array class
     *                   see {@link ArmyPostgreRange}
     * @param param      from {@link Mapping#params()} ,it's the name of <ul>
     *                   <li>{@link PostgreType#INT4RANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#INT8RANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#NUMRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#DATERANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#TSRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#TSTZRANGE_ARRAY}</li>
     *                   </ul>
     * @param methodName from {@link Mapping#func()}
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param or methodName error.
     */
    public static PostgreSingleRangeArrayType fromMethod(final Class<?> javaType, final String param,
                                                         final String methodName) throws MetaException {

        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }

        final Class<?> componentType;
        if (isNotSingleRangeArrayType(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreSingleRangeArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == String.class) {
            throw errorJavaType(PostgreSingleRangeArrayType.class, javaType);
        }
        final RangeFunction<?, ?> rangeFunc;
        rangeFunc = PostgreRangeType.createRangeFunction(componentType, boundJavaType(sqlType), methodName);
        return new PostgreSingleRangeArrayType(sqlType, javaType, rangeFunc);
    }


    public static final PostgreSingleRangeArrayType INT4_RANGE_LINEAR = new PostgreSingleRangeArrayType(PostgreType.INT4RANGE_ARRAY, String[].class, null);

    public static final PostgreSingleRangeArrayType INT8_RANGE_LINEAR = new PostgreSingleRangeArrayType(PostgreType.INT8RANGE_ARRAY, String[].class, null);

    public static final PostgreSingleRangeArrayType NUM_RANGE_LINEAR = new PostgreSingleRangeArrayType(PostgreType.NUMRANGE_ARRAY, String[].class, null);

    public static final PostgreSingleRangeArrayType DATE_RANGE_LINEAR = new PostgreSingleRangeArrayType(PostgreType.DATERANGE_ARRAY, String[].class, null);

    public static final PostgreSingleRangeArrayType TS_RANGE_LINEAR = new PostgreSingleRangeArrayType(PostgreType.TSRANGE_ARRAY, String[].class, null);

    public static final PostgreSingleRangeArrayType TS_TZ_RANGE_LINEAR = new PostgreSingleRangeArrayType(PostgreType.TSTZRANGE_ARRAY, String[].class, null);


    /**
     * private constructor
     */
    private PostgreSingleRangeArrayType(PostgreType sqlType, Class<?> javaType, @Nullable RangeFunction<?, ?> rangeFunc) {
        super(sqlType, javaType, rangeFunc);
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final Class<?> targetComponentType;
        final RangeFunction<?, ?> rangeFunc;

        final MappingType instance;
        if (targetType == String.class) {
            instance = TextType.INSTANCE;
        } else if (targetType == String[].class) {
            instance = linearInstance(this.dataType);
        } else if (!targetType.isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((targetComponentType = ArrayUtils.underlyingComponent(targetType)) == String.class) {
            instance = new PostgreSingleRangeArrayType(this.dataType, targetType, null);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(targetComponentType, boundJavaType(this.dataType))) == null) {
            throw noMatchCompatibleMapping(this, targetType);
        } else {
            instance = new PostgreSingleRangeArrayType(this.dataType, targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    public Object convert(final MappingEnv env, final Object source) throws CriteriaException {
        return arrayConvert(source, this.rangeFunc, this::deserialize, map(env.serverMeta()), this, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        return arrayBeforeBind(source, this::serialize, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return arrayAfterGet(source, this.rangeFunc, this::deserialize, dataType, this, ACCESS_ERROR_HANDLER);
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return new PostgreSingleRangeArrayType(this.dataType, ArrayUtils.arrayClassOf(this.javaType), this.rangeFunc);
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        if (this.javaType == String[][].class) {
            assert this.rangeFunc == null;
            instance = linearInstance(this.dataType);
        } else if (ArrayUtils.dimensionOf(this.javaType) > 1) {
            instance = new PostgreSingleRangeArrayType(this.dataType, this.javaType.getComponentType(), this.rangeFunc);
        } else {
            final PostgreSingleRangeType rangeType;
            rangeType = PostgreSingleRangeType.INT4_RANGE_TEXT._fromSingleArray(this);
            assert rangeType.dataType == this.dataType;
            instance = rangeType;
        }
        return instance;
    }

    public static <T, R> Object arrayConvert(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                             final Function<String, T> parseFunc, final DataType dataType,
                                             final MappingType type, final MappingSupport.ErrorHandler handler) {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, dataType, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 2) {
            throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        } else {
            value = parseRangeArray(text, rangeFunc, parseFunc, dataType, type, handler);
        }
        return value;
    }

    /**
     * @param boundSerializer serializer of bound
     * @param <T>             java type of subtype of range
     */
    public static <T> String arrayBeforeBind(final Object nonNull, final BiConsumer<T, StringBuilder> boundSerializer,
                                             final DataType dataType, final MappingType type,
                                             final ErrorHandler handler) {
        final BiConsumer<Object, StringBuilder> rangeSerializer;
        rangeSerializer = (range, appender) -> PostgreRangeType.rangeToText(range, boundSerializer, type, appender);
        return PostgreArrays.arrayBeforeBind(nonNull, rangeSerializer, dataType, type, handler);
    }


    public static <T> Object arrayAfterGet(final Object nonNull, final @Nullable RangeFunction<T, ?> rangeFunc,
                                           final Function<String, T> parseFunc, final DataType dataType,
                                           final MappingType type, final MappingSupport.ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw handler.apply(type, dataType, nonNull, null);
        }
        return parseRangeArray((String) nonNull, rangeFunc, parseFunc, dataType, type, handler);
    }

    private static <T> Object parseRangeArray(final String text, final @Nullable RangeFunction<T, ?> rangeFunc,
                                              final Function<String, T> parseFunc, final DataType dataType,
                                              final MappingType type, final MappingSupport.ErrorHandler handler) {

        final TextFunction<?> elementFunc;
        if (!(type instanceof MappingType.SqlArrayType)) {
            throw handler.apply(type, dataType, text, _Exceptions.notArrayMappingType(type));
        } else if (rangeFunc == null) {
            if (ArrayUtils.underlyingComponent(type.javaType()) != String.class) {
                String m = String.format("%s java type isn't %s array", type, String.class.getName());
                throw handler.apply(type, dataType, text, new IllegalArgumentException(m));
            }
            elementFunc = String::substring;
        } else {
            final Class<?> rangeClass;
            rangeClass = ArrayUtils.underlyingComponentClass(type);
            elementFunc = (str, offset, end) -> {
                final Object value;
                char ch;
                if (offset + 5 == end && ((ch = str.charAt(offset)) == 'e' || ch == 'E')
                        && str.regionMatches(true, offset, PostgreRangeType.EMPTY, 0, 5)) {
                    value = PostgreRangeType.emptyRange(rangeClass);
                } else {
                    value = PostgreRangeType.parseNonEmptyRange(str, offset, end, rangeFunc, parseFunc);
                }
                return value;
            };
        }
        return PostgreArrays.parseArray(text, false, elementFunc, _Constant.COMMA, dataType, type, handler);
    }


    private static PostgreSingleRangeArrayType linearInstance(final PostgreType sqlType) {
        final PostgreSingleRangeArrayType instance;
        switch (sqlType) {
            case INT4RANGE_ARRAY:
                instance = PostgreSingleRangeArrayType.INT4_RANGE_LINEAR;
                break;
            case INT8RANGE_ARRAY:
                instance = PostgreSingleRangeArrayType.INT8_RANGE_LINEAR;
                break;
            case NUMRANGE_ARRAY:
                instance = PostgreSingleRangeArrayType.NUM_RANGE_LINEAR;
                break;
            case DATERANGE_ARRAY:
                instance = PostgreSingleRangeArrayType.DATE_RANGE_LINEAR;
                break;
            case TSRANGE_ARRAY:
                instance = PostgreSingleRangeArrayType.TS_RANGE_LINEAR;
                break;
            case TSTZRANGE_ARRAY:
                instance = PostgreSingleRangeArrayType.TS_TZ_RANGE_LINEAR;
                break;
            default:
                throw _Exceptions.unexpectedEnum(sqlType);
        }
        return instance;
    }

    private static boolean isNotSingleRangeArrayType(final PostgreType sqlType) {
        final boolean match;
        switch (sqlType) {
            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case NUMRANGE_ARRAY:
            case DATERANGE_ARRAY:
            case TSRANGE_ARRAY:
            case TSTZRANGE_ARRAY:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }

    private static String sqlTypeErrorMessage(PostgreType sqlType) {
        return String.format("%s isn't postgre single-range array type", sqlType);
    }


}
