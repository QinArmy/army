package io.army.mapping.postgre;

import io.army.annotation.Mapping;
import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.TextType;
import io.army.mapping.array.PostgreArrays;
import io.army.mapping.postgre.array.PostgreMultiRangeArrayType;
import io.army.meta.MetaException;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class representing army build-in postgre multi-range array type.
 * * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 *
 * @since 0.6.0
 */
public final class PostgreMultiRangeType extends PostgreRangeType implements PostgreRangeType.MultiRangeType {


    /**
     * @param javaType one dimension array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param param    from {@link Mapping#params()} ,it's the name of <ul>
     *                 <li>{@link PostgreType#INT4MULTIRANGE}</li>
     *                 <li>{@link PostgreType#INT8MULTIRANGE}</li>
     *                 <li>{@link PostgreType#NUMMULTIRANGE}</li>
     *                 <li>{@link PostgreType#DATEMULTIRANGE}</li>
     *                 <li>{@link PostgreType#TSMULTIRANGE}</li>
     *                 <li>{@link PostgreType#TSTZMULTIRANGE}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param error.
     */
    public static PostgreMultiRangeType from(final Class<?> javaType, final String param) throws MetaException {
        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }
        if (isNotMultiRange(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        }
        return from(javaType, sqlType);
    }


    /**
     * @param javaType one dimension array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  from {@link Mapping#params()} ,valid instance: <ul>
     *                 <li>{@link PostgreType#INT4MULTIRANGE}</li>
     *                 <li>{@link PostgreType#INT8MULTIRANGE}</li>
     *                 <li>{@link PostgreType#NUMMULTIRANGE}</li>
     *                 <li>{@link PostgreType#DATEMULTIRANGE}</li>
     *                 <li>{@link PostgreType#TSMULTIRANGE}</li>
     *                 <li>{@link PostgreType#TSTZMULTIRANGE}</li>
     *                 </ul>
     */
    public static PostgreMultiRangeType from(final Class<?> javaType, final PostgreType sqlType)
            throws IllegalArgumentException {

        final RangeFunction<?, ?> rangeFunc;
        final Class<?> componentType;

        final PostgreMultiRangeType instance;
        if (isNotMultiRange(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        } else if (javaType == String[].class) {
            instance = textInstance(sqlType);
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(componentType, boundJavaType(sqlType))) == null) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        } else {
            instance = new PostgreMultiRangeType(sqlType, javaType, rangeFunc);
        }
        return instance;
    }


    /**
     * @param javaType one dimension non-string array class
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  from {@link Mapping#params()} ,valid instance: <ul>
     *                 <li>{@link PostgreType#INT4MULTIRANGE}</li>
     *                 <li>{@link PostgreType#INT8MULTIRANGE}</li>
     *                 <li>{@link PostgreType#NUMMULTIRANGE}</li>
     *                 <li>{@link PostgreType#DATEMULTIRANGE}</li>
     *                 <li>{@link PostgreType#TSMULTIRANGE}</li>
     *                 <li>{@link PostgreType#TSTZMULTIRANGE}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType or sqlType error
     */
    public static PostgreMultiRangeType fromFunc(final Class<?> javaType, final PostgreType sqlType,
                                                 final RangeFunction<?, ?> rangeFunc)
            throws IllegalArgumentException {
        Objects.requireNonNull(rangeFunc);
        if (isNotMultiRange(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (javaType == String[].class || !javaType.isArray()) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        } else if (javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        }
        return new PostgreMultiRangeType(sqlType, javaType, rangeFunc);
    }


    /**
     * @param javaType   one dimension non-string array class
     *                   see {@link ArmyPostgreRange}
     * @param param      from {@link Mapping#params()} ,it's the name of <ul>
     *                   <li>{@link PostgreType#INT4MULTIRANGE}</li>
     *                   <li>{@link PostgreType#INT8MULTIRANGE}</li>
     *                   <li>{@link PostgreType#NUMMULTIRANGE}</li>
     *                   <li>{@link PostgreType#DATEMULTIRANGE}</li>
     *                   <li>{@link PostgreType#TSMULTIRANGE}</li>
     *                   <li>{@link PostgreType#TSTZMULTIRANGE}</li>
     *                   </ul>
     * @param methodName from {@link Mapping#func()}
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param or methodName error.
     */
    public static PostgreMultiRangeType fromMethod(final Class<?> javaType, final String param,
                                                   final String methodName) throws MetaException {

        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }

        final Class<?> componentType;

        if (isNotMultiRange(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        } else if (javaType == String[].class || !javaType.isArray()) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)).isArray()) {
            throw errorJavaType(PostgreMultiRangeType.class, javaType);
        }
        final RangeFunction<?, ?> rangeFunc;
        rangeFunc = PostgreRangeType.createRangeFunction(componentType, boundJavaType(sqlType), methodName);
        return new PostgreMultiRangeType(sqlType, javaType, rangeFunc);
    }


    public static final PostgreMultiRangeType INT4_MULTI_RANGE_TEXT = new PostgreMultiRangeType(PostgreType.INT4MULTIRANGE, String[].class, null);

    public static final PostgreMultiRangeType INT8_MULTI_RANGE_TEXT = new PostgreMultiRangeType(PostgreType.INT8MULTIRANGE, String[].class, null);

    public static final PostgreMultiRangeType NUM_MULTI_RANGE_TEXT = new PostgreMultiRangeType(PostgreType.NUMMULTIRANGE, String[].class, null);

    public static final PostgreMultiRangeType DATE_MULTI_RANGE_TEXT = new PostgreMultiRangeType(PostgreType.DATEMULTIRANGE, String[].class, null);

    public static final PostgreMultiRangeType TS_MULTI_RANGE_TEXT = new PostgreMultiRangeType(PostgreType.TSMULTIRANGE, String[].class, null);

    public static final PostgreMultiRangeType TS_TZ_MULTI_RANGE_TEXT = new PostgreMultiRangeType(PostgreType.TSTZMULTIRANGE, String[].class, null);

    /**
     * package method
     */
    static PostgreMultiRangeType fromSingleType(final PostgreSingleRangeType type) {
        final PostgreType sqlType;
        switch (type.dataType) {
            case INT4RANGE:
                sqlType = PostgreType.INT4MULTIRANGE;
                break;
            case INT8RANGE:
                sqlType = PostgreType.INT8MULTIRANGE;
                break;
            case NUMRANGE:
                sqlType = PostgreType.NUMMULTIRANGE;
                break;
            case DATERANGE:
                sqlType = PostgreType.DATEMULTIRANGE;
                break;
            case TSRANGE:
                sqlType = PostgreType.TSMULTIRANGE;
                break;
            case TSTZRANGE:
                sqlType = PostgreType.TSTZMULTIRANGE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(type.dataType);
        }

        final PostgreMultiRangeType instance;
        if (type.javaType == String.class) {
            instance = textInstance(sqlType);
        } else {
            assert !type.javaType.isArray();
            instance = new PostgreMultiRangeType(sqlType, ArrayUtils.arrayClassOf(type.javaType), type.rangeFunc);
        }
        return instance;
    }


    /**
     * <p>
     * package constructor
     *     */
    private PostgreMultiRangeType(PostgreType sqlType, Class<?> javaType, @Nullable RangeFunction<?, ?> rangeFunc) {
        super(sqlType, javaType, rangeFunc);
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final Class<?> targetComponentType;

        final RangeFunction<?, ?> rangeFunc;
        final MappingType instance;
        if (targetType == String.class) {
            instance = TextType.INSTANCE;
        } else if (targetType == String[].class) {
            instance = textInstance(this.dataType);
        } else if (!targetType.isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((targetComponentType = targetType.getComponentType()).isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(targetComponentType, boundJavaType(this.dataType))) == null) {
            throw noMatchCompatibleMapping(this, targetType);
        } else {
            instance = new PostgreMultiRangeType(this.dataType, targetType, rangeFunc);
        }
        return instance;
    }


    @Override
    public Object convert(MappingEnv env, final Object source) throws CriteriaException {
        return rangeConvert(source, this.rangeFunc, this::deserialize, map(env.serverMeta()), this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return rangeBeforeBind(source, this::serialize, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return rangeAfterGet(source, this.rangeFunc, this::deserialize, dataType, this, ACCESS_ERROR_HANDLER);
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<?, ?> rangeFunc = this.rangeFunc;
        assert rangeFunc != null || this.javaType == String[].class;

        final PostgreMultiRangeArrayType instance;
        if (rangeFunc != null) {
            instance = PostgreMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(this.javaType), this.dataType, rangeFunc);
        } else switch (this.dataType) {
            case INT4MULTIRANGE:
                instance = PostgreMultiRangeArrayType.INT4_MULTI_RANGE_LINEAR;
                break;
            case INT8MULTIRANGE:
                instance = PostgreMultiRangeArrayType.INT8_MULTI_RANGE_LINEAR;
                break;
            case NUMMULTIRANGE:
                instance = PostgreMultiRangeArrayType.NUM_MULTI_RANGE_LINEAR;
                break;
            case DATEMULTIRANGE:
                instance = PostgreMultiRangeArrayType.DATE_MULTI_RANGE_LINEAR;
                break;
            case TSMULTIRANGE:
                instance = PostgreMultiRangeArrayType.TS_MULTI_RANGE_LINEAR;
                break;
            case TSTZMULTIRANGE:
                instance = PostgreMultiRangeArrayType.TS_TZ_MULTI_RANGE_LINEAR;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.dataType);

        }
        return instance;
    }

    @Override
    public MappingType rangeType() {
        return PostgreSingleRangeType.fromMultiType(this);
    }

    @Override
    public PostgreMultiRangeType _fromMultiArray(final PostgreMultiRangeArrayType type) {
        final PostgreType sqlType;
        switch (type.dataType) {
            case INT4MULTIRANGE_ARRAY:
                sqlType = PostgreType.INT4MULTIRANGE;
                break;
            case INT8MULTIRANGE_ARRAY:
                sqlType = PostgreType.INT8MULTIRANGE;
                break;
            case NUMMULTIRANGE_ARRAY:
                sqlType = PostgreType.NUMMULTIRANGE;
                break;
            case DATEMULTIRANGE_ARRAY:
                sqlType = PostgreType.DATEMULTIRANGE;
                break;
            case TSMULTIRANGE_ARRAY:
                sqlType = PostgreType.TSMULTIRANGE;
                break;
            case TSTZMULTIRANGE_ARRAY:
                sqlType = PostgreType.TSTZMULTIRANGE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(type.dataType);
        }
        final Class<?> javaType;
        javaType = type.javaType.getComponentType();

        final PostgreMultiRangeType instance;
        final RangeFunction<?, ?> rangeFunc = type.rangeFunc;
        if (rangeFunc == null) {
            assert javaType == String[].class;
            instance = textInstance(sqlType);
        } else {
            assert !javaType.getComponentType().isArray();
            instance = new PostgreMultiRangeType(sqlType, javaType, rangeFunc);
        }
        return instance;
    }

    public static <T> Object rangeConvert(Object nonNull, @Nullable RangeFunction<T, ?> rangeFunc,
                                          Function<String, T> parseFunc, DataType dataType,
                                          MappingType type, ErrorHandler handler) {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 5) { // non-empty,non-null
            throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        } else {
            value = parseMultiRange((String) nonNull, rangeFunc, parseFunc, dataType, type, handler);
        }
        return value;
    }


    public static <T> String rangeBeforeBind(final Object nonNull, final BiConsumer<T, Consumer<String>> boundSerializer,
                                             final DataType dataType, final MappingType type, final ErrorHandler handler)
            throws CriteriaException {

        final String value, text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, dataType, nonNull, null);
            }
            final BiConsumer<Object, Consumer<String>> rangeSerializer;
            rangeSerializer = (range, appender) -> rangeToText(range, boundSerializer, type, appender);

            final StringBuilder builder = new StringBuilder();
            PostgreArrays.toArrayText(nonNull, rangeSerializer, builder);
            value = builder.toString();
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 5) { //non-empty
            throw handler.apply(type, dataType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw handler.apply(type, dataType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw handler.apply(type, dataType, nonNull, null);
        } else {
            value = text;
        }
        return value;
    }

    public static <T> Object rangeAfterGet(Object nonNull, @Nullable RangeFunction<T, ?> rangeFunc,
                                           Function<String, T> parseFunc, DataType dataType,
                                           MappingType type, ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw handler.apply(type, dataType, nonNull, null);
        }
        return parseMultiRange((String) nonNull, rangeFunc, parseFunc, dataType, type, handler);
    }


    private static <T> Object parseMultiRange(final String text, final @Nullable RangeFunction<T, ?> rangeFunc,
                                              final Function<String, T> parseFunc, final DataType dataType,
                                              final MappingType type, final ErrorHandler handler) {

        final TextFunction<?> elementFunc;
        if (rangeFunc == null) {
            if (type.javaType() != String.class) {
                String m = String.format("%s java type isn't %s", type, String.class.getName());
                throw handler.apply(type, dataType, text, new IllegalArgumentException(m));
            }
            elementFunc = String::substring;
        } else {
            elementFunc = multiRangeParseFunc(text, rangeFunc, parseFunc, dataType, type, handler);
        }
        final Object array;
        array = PostgreArrays.parseMultiRange(text, elementFunc, dataType, type, handler);

        if (!(array instanceof List || ArrayUtils.dimensionOf(array.getClass()) == 1)) {
            throw handler.apply(type, dataType, text, null);
        }
        return array;
    }


    private static boolean isNotMultiRange(final PostgreType sqlType) {
        final boolean match;
        switch (sqlType) {
            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case DATEMULTIRANGE:
            case TSMULTIRANGE:
            case TSTZMULTIRANGE:
                match = false;
                break;
            default:
                match = true;

        }
        return match;
    }

    private static PostgreMultiRangeType textInstance(final PostgreType sqlType) {
        final PostgreMultiRangeType instance;
        switch (sqlType) {
            case INT4MULTIRANGE:
                instance = INT4_MULTI_RANGE_TEXT;
                break;
            case INT8MULTIRANGE:
                instance = INT8_MULTI_RANGE_TEXT;
                break;
            case NUMMULTIRANGE:
                instance = NUM_MULTI_RANGE_TEXT;
                break;
            case DATEMULTIRANGE:
                instance = DATE_MULTI_RANGE_TEXT;
                break;
            case TSMULTIRANGE:
                instance = TS_MULTI_RANGE_TEXT;
                break;
            case TSTZMULTIRANGE:
                instance = TS_TZ_MULTI_RANGE_TEXT;
                break;
            default:
                throw _Exceptions.unexpectedEnum(sqlType);

        }
        return instance;
    }

    private static String sqlTypeErrorMessage(PostgreType sqlType) {
        return String.format("%s isn't postgre multi-range type", sqlType);
    }


}
