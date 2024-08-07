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
import io.army.executor.DataAccessException;
import io.army.function.TextFunction;
import io.army.mapping.*;
import io.army.mapping.array.PostgreArrays;
import io.army.mapping.postgre.*;
import io.army.meta.MetaException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class representing army build-in postgre multi-range array type.
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 * @since 0.6.0
 */
public class PostgreMultiRangeArrayType extends _ArmyPostgreRangeType implements MappingType.SqlArrayType {


    /**
     * @param javaType 2 dimension or higher dimension array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param param    from {@link Mapping#params()} ,it's the name of <ul>
     *                 <li>{@link PostgreType#INT4MULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#INT8MULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#NUMMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#DATEMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSTZMULTIRANGE_ARRAY}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param error.
     */
    public static PostgreMultiRangeArrayType from(final Class<?> javaType, final String param) throws MetaException {
        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }
        if (isNotMultiRangeType(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        }
        return from(javaType, sqlType);
    }


    /**
     * @param javaType 2 dimension or higher dimension array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  <ul>
     *                 <li>{@link PostgreType#INT4MULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#INT8MULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#NUMMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#DATEMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSTZMULTIRANGE_ARRAY}</li>
     *                 </ul>
     */
    public static PostgreMultiRangeArrayType from(final Class<?> javaType, final PostgreType sqlType)
            throws IllegalArgumentException {

        final RangeFunction<?, ?> rangeFunc;
        final Class<?> componentType;

        final PostgreMultiRangeArrayType instance;
        if (isNotMultiRangeType(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else if (javaType == String[][].class) {
            instance = linearInstance(sqlType);
        } else if (ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == String.class) {
            instance = new PostgreMultiRangeArrayType(sqlType, javaType, null);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(componentType, boundJavaType(sqlType))) == null) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else {
            instance = new PostgreMultiRangeArrayType(sqlType, javaType, rangeFunc);
        }
        return instance;
    }


    /**
     * @param javaType 2 dimension or higher dimension non-string array class
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  <ul>
     *                 <li>{@link PostgreType#INT4MULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#INT8MULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#NUMMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#DATEMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSMULTIRANGE_ARRAY}</li>
     *                 <li>{@link PostgreType#TSTZMULTIRANGE_ARRAY}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType or sqlType error
     */
    public static PostgreMultiRangeArrayType fromFunc(final Class<?> javaType, final PostgreType sqlType,
                                                      final RangeFunction<?, ?> rangeFunc)
            throws IllegalArgumentException {
        Objects.requireNonNull(rangeFunc);
        if (isNotMultiRangeType(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        }
        return new PostgreMultiRangeArrayType(sqlType, javaType, rangeFunc);
    }


    /**
     * @param javaType   2 dimension or higher dimension non-string array class
     *                   see {@link ArmyPostgreRange}
     * @param param      from {@link Mapping#params()} ,it's the name of <ul>
     *                   <li>{@link PostgreType#INT4MULTIRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#INT8MULTIRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#NUMMULTIRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#DATEMULTIRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#TSMULTIRANGE_ARRAY}</li>
     *                   <li>{@link PostgreType#TSTZMULTIRANGE_ARRAY}</li>
     *                   </ul>
     * @param methodName from {@link Mapping#func()}
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param or methodName error.
     */
    public static PostgreMultiRangeArrayType fromMethod(final Class<?> javaType, final String param,
                                                        final String methodName) throws MetaException {

        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }

        final Class<?> componentType;
        if (isNotMultiRangeType(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == String.class) {
            throw errorJavaType(PostgreMultiRangeArrayType.class, javaType);
        }
        final RangeFunction<?, ?> rangeFunc;
        rangeFunc = PostgreRangeType.createRangeFunction(componentType, boundJavaType(sqlType), methodName);
        return new PostgreMultiRangeArrayType(sqlType, javaType, rangeFunc);
    }


    public static final PostgreMultiRangeArrayType INT4_MULTI_RANGE_LINEAR = new PostgreMultiRangeArrayType(PostgreType.INT4MULTIRANGE_ARRAY, String[][].class, null);

    public static final PostgreMultiRangeArrayType INT8_MULTI_RANGE_LINEAR = new PostgreMultiRangeArrayType(PostgreType.INT8MULTIRANGE_ARRAY, String[][].class, null);

    public static final PostgreMultiRangeArrayType NUM_MULTI_RANGE_LINEAR = new PostgreMultiRangeArrayType(PostgreType.NUMMULTIRANGE_ARRAY, String[][].class, null);

    public static final PostgreMultiRangeArrayType DATE_MULTI_RANGE_LINEAR = new PostgreMultiRangeArrayType(PostgreType.DATEMULTIRANGE_ARRAY, String[][].class, null);

    public static final PostgreMultiRangeArrayType TS_MULTI_RANGE_LINEAR = new PostgreMultiRangeArrayType(PostgreType.TSMULTIRANGE_ARRAY, String[][].class, null);

    public static final PostgreMultiRangeArrayType TS_TZ_MULTI_RANGE_LINEAR = new PostgreMultiRangeArrayType(PostgreType.TSTZMULTIRANGE_ARRAY, String[][].class, null);


    /**
     * <p>
     * package constructor
     *
     */
    private PostgreMultiRangeArrayType(final PostgreType sqlType, final Class<?> javaType,
                                       final @Nullable RangeFunction<?, ?> rangeFunc) {
        super(sqlType, javaType, rangeFunc);
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {

        final int targetDimension;
        final Class<?> componentType;

        final MappingType instance;
        if (targetType == String.class) {
            instance = TextType.INSTANCE;
        } else if (!targetType.isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((targetDimension = ArrayUtils.dimensionOf(targetType)) < 2) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((componentType = ArrayUtils.underlyingComponent(targetType)) != String.class) {
            final RangeFunction<?, ?> rangeFunc;
            if ((rangeFunc = tryCreateDefaultRangeFunc(componentType, boundJavaType(this.dataType))) == null) {
                throw noMatchCompatibleMapping(this, targetType);
            }
            instance = new PostgreMultiRangeArrayType(this.dataType, targetType, rangeFunc);
        } else if (targetDimension > 2) {
            instance = new PostgreMultiRangeArrayType(this.dataType, targetType, null);
        } else {
            instance = linearInstance(this.dataType);
        }
        return instance;
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return new PostgreMultiRangeArrayType(this.dataType, ArrayUtils.arrayClassOf(this.javaType), this.rangeFunc);
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        if (this.javaType == String[][][].class) {
            instance = linearInstance(this.dataType);
        } else if (ArrayUtils.dimensionOf(this.javaType) > 2) {
            instance = new PostgreMultiRangeArrayType(this.dataType, this.javaType.getComponentType(), this.rangeFunc);
        } else {
            final PostgreMultiRangeType rangeType;
            rangeType = PostgreMultiRangeType.INT4_MULTI_RANGE_TEXT._fromMultiArray(this);
            assert rangeType.dataType == this.dataType;
            instance = rangeType;
        }
        return instance;
    }


    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return arrayConvert(source, this.rangeFunc, this::deserialize, map(env.serverMeta()), this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return arrayBeforeBind(source, this::serialize, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return arrayAfterGet(source, this.rangeFunc, this::deserialize, dataType, this, ACCESS_ERROR_HANDLER);
    }


    /**
     * @param rangeFunc null when {@link MappingType#javaType()} of type is {@link String#getClass()}
     */
    public static <T, R> Object arrayConvert(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                             final Function<String, T> parseFunc, final DataType dataType,
                                             final MappingType type, final ErrorHandler handler) {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, dataType, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 5) { // non-empty
            throw handler.apply(type, dataType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw handler.apply(type, dataType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw handler.apply(type, dataType, nonNull, null);
        } else {
            value = parseMultiRangeArray(text, rangeFunc, parseFunc, dataType, type, handler);
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
                                           final MappingType type, final ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw handler.apply(type, dataType, nonNull, null);
        }
        return parseMultiRangeArray((String) nonNull, rangeFunc, parseFunc, dataType, type, handler);
    }

    private static <T> Object parseMultiRangeArray(final String text, final @Nullable RangeFunction<T, ?> rangeFunc,
                                                   final Function<String, T> parseFunc, final DataType dataType,
                                                   final MappingType type, final ErrorHandler handler) {

        final TextFunction<?> elementFunc;
        if (rangeFunc == null) {
            if (type.javaType() != String.class) {
                String m = String.format("%s java type isn't %s", type, String.class.getName());
                throw handler.apply(type, dataType, text, new IllegalArgumentException(m));
            }
            elementFunc = String::substring;
        } else if (!(type instanceof MappingType.SqlArrayType)) {
            throw handler.apply(type, dataType, text, _Exceptions.notArrayMappingType(type));
        } else if (type instanceof UnaryGenericsMapping.ListMapping) {
            String m = String.format("multi range array type don't support %s",
                    UnaryGenericsMapping.ListMapping.ListMapping.class);
            throw handler.apply(type, dataType, text, new IllegalArgumentException(m));
        } else {
            elementFunc = PostgreMultiRangeType.multiRangeParseFunc(text, rangeFunc, parseFunc, dataType, type, handler);
        }
        final Object array;
        array = PostgreArrays.parseArray(text, true, elementFunc, _Constant.COMMA, dataType, type, handler);
        if (ArrayUtils.dimensionOf(array.getClass()) < 2) {
            throw handler.apply(type, dataType, text, new IllegalArgumentException("Not multi-ranage array"));
        }
        return array;
    }


    private static PostgreMultiRangeArrayType linearInstance(final PostgreType sqlType) {
        final PostgreMultiRangeArrayType instance;
        switch (sqlType) {
            case INT4MULTIRANGE_ARRAY:
                instance = PostgreMultiRangeArrayType.INT4_MULTI_RANGE_LINEAR;
                break;
            case INT8MULTIRANGE_ARRAY:
                instance = PostgreMultiRangeArrayType.INT8_MULTI_RANGE_LINEAR;
                break;
            case NUMMULTIRANGE_ARRAY:
                instance = PostgreMultiRangeArrayType.NUM_MULTI_RANGE_LINEAR;
                break;
            case DATEMULTIRANGE_ARRAY:
                instance = PostgreMultiRangeArrayType.DATE_MULTI_RANGE_LINEAR;
                break;
            case TSMULTIRANGE_ARRAY:
                instance = PostgreMultiRangeArrayType.TS_MULTI_RANGE_LINEAR;
                break;
            case TSTZMULTIRANGE_ARRAY:
                instance = PostgreMultiRangeArrayType.TS_TZ_MULTI_RANGE_LINEAR;
                break;
            default:
                throw _Exceptions.unexpectedEnum(sqlType);
        }
        return instance;
    }

    private static boolean isNotMultiRangeType(final PostgreType sqlType) {
        final boolean match;
        switch (sqlType) {
            case INT4MULTIRANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }

    private static String sqlTypeErrorMessage(PostgreType sqlType) {
        return String.format("%s isn't postgre multi-range array type", sqlType);
    }


}
