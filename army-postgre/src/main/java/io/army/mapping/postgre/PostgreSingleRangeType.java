package io.army.mapping.postgre;

import io.army.annotation.Mapping;
import io.army.criteria.CriteriaException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingSupport;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.postgre.array.PostgreSingleRangeArrayType;
import io.army.meta.MetaException;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class representing Postgre Built-in Range  Types type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 */
public final class PostgreSingleRangeType extends PostgreRangeType implements PostgreRangeType.SingleRangeType {


    /**
     * @param javaType non-array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param param    from {@link Mapping#params()} ,it's the name of <ul>
     *                 <li>{@link PostgreType#INT4RANGE}</li>
     *                 <li>{@link PostgreType#INT8RANGE}</li>
     *                 <li>{@link PostgreType#NUMRANGE}</li>
     *                 <li>{@link PostgreType#DATERANGE}</li>
     *                 <li>{@link PostgreType#TSRANGE}</li>
     *                 <li>{@link PostgreType#TSTZRANGE}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param error.
     */
    public static PostgreSingleRangeType from(final Class<?> javaType, final String param) throws MetaException {
        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }
        if (isNotSingleRangeType(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        }
        return from(javaType, sqlType);
    }


    /**
     * @param javaType non-array class. If javaType isn't String array,then must declare static 'create' factory method.
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  valid instance:<ul>
     *                 <li>{@link PostgreType#INT4RANGE}</li>
     *                 <li>{@link PostgreType#INT8RANGE}</li>
     *                 <li>{@link PostgreType#NUMRANGE}</li>
     *                 <li>{@link PostgreType#DATERANGE}</li>
     *                 <li>{@link PostgreType#TSRANGE}</li>
     *                 <li>{@link PostgreType#TSTZRANGE}</li>
     *                 </ul>
     */
    public static PostgreSingleRangeType from(final Class<?> javaType, final PostgreType sqlType)
            throws IllegalArgumentException {

        final RangeFunction<?, ?> rangeFunc;

        final PostgreSingleRangeType instance;
        if (isNotSingleRangeType(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (javaType == String.class) {
            instance = textInstance(sqlType);
        } else if (javaType.isArray()) {
            throw errorJavaType(PostgreSingleRangeType.class, javaType);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(javaType, boundJavaType(sqlType))) == null) {
            throw errorJavaType(PostgreSingleRangeType.class, javaType);
        } else {
            instance = new PostgreSingleRangeType(sqlType, javaType, rangeFunc);
        }
        return instance;
    }


    /**
     * @param javaType non-array and non-string class.
     *                 see {@link ArmyPostgreRange}
     * @param sqlType  valid instance: <ul>
     *                 <li>{@link PostgreType#INT4RANGE}</li>
     *                 <li>{@link PostgreType#INT8RANGE}</li>
     *                 <li>{@link PostgreType#NUMRANGE}</li>
     *                 <li>{@link PostgreType#DATERANGE}</li>
     *                 <li>{@link PostgreType#TSRANGE}</li>
     *                 <li>{@link PostgreType#TSTZRANGE}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType or sqlType error
     */
    public static <T, R> PostgreSingleRangeType fromFunc(final Class<? extends R> javaType, final PostgreType sqlType,
                                                         final RangeFunction<T, R> rangeFunc)
            throws IllegalArgumentException {
        Objects.requireNonNull(rangeFunc);
        if (isNotSingleRangeType(sqlType)) {
            throw new IllegalArgumentException(sqlTypeErrorMessage(sqlType));
        } else if (javaType == String.class || javaType.isArray()) {
            throw errorJavaType(PostgreSingleRangeType.class, javaType);
        }
        return new PostgreSingleRangeType(sqlType, javaType, rangeFunc);
    }


    /**
     * @param javaType non-array and non-string class.
     *                 see {@link ArmyPostgreRange}
     * @param param    from {@link Mapping#params()} ,it's the name of <ul>
     *                 <li>{@link PostgreType#INT4RANGE}</li>
     *                 <li>{@link PostgreType#INT8RANGE}</li>
     *                 <li>{@link PostgreType#NUMRANGE}</li>
     *                 <li>{@link PostgreType#DATERANGE}</li>
     *                 <li>{@link PostgreType#TSRANGE}</li>
     *                 <li>{@link PostgreType#TSTZRANGE}</li>
     *                 </ul>
     * @throws IllegalArgumentException throw when javaType error
     * @throws MetaException            throw when param or methodName error.
     */
    public static PostgreSingleRangeType fromMethod(final Class<?> javaType, final String param,
                                                    final String methodName) throws MetaException {

        final PostgreType sqlType;
        try {
            sqlType = PostgreType.valueOf(param);
        } catch (IllegalArgumentException e) {
            throw new MetaException(e.getMessage(), e);
        }

        if (isNotSingleRangeType(sqlType)) {
            throw new MetaException(sqlTypeErrorMessage(sqlType));
        } else if (javaType == String.class || javaType.isArray()) {
            throw errorJavaType(PostgreSingleRangeType.class, javaType);
        }
        final RangeFunction<?, ?> rangeFunc;
        rangeFunc = PostgreRangeType.createRangeFunction(javaType, boundJavaType(sqlType), methodName);
        return new PostgreSingleRangeType(sqlType, javaType, rangeFunc);
    }


    public static final PostgreSingleRangeType INT4_RANGE_TEXT = new PostgreSingleRangeType(PostgreType.INT4RANGE, String.class, null);

    public static final PostgreSingleRangeType INT8_RANGE_TEXT = new PostgreSingleRangeType(PostgreType.INT8RANGE, String.class, null);

    public static final PostgreSingleRangeType NUM_RANGE_TEXT = new PostgreSingleRangeType(PostgreType.NUMRANGE, String.class, null);

    public static final PostgreSingleRangeType DATE_RANGE_TEXT = new PostgreSingleRangeType(PostgreType.DATERANGE, String.class, null);

    public static final PostgreSingleRangeType TS_RANGE_TEXT = new PostgreSingleRangeType(PostgreType.TSRANGE, String.class, null);

    public static final PostgreSingleRangeType TS_TZ_RANGE_TEXT = new PostgreSingleRangeType(PostgreType.TSTZRANGE, String.class, null);


    /**
     * package method
     */
    static PostgreSingleRangeType fromMultiType(final PostgreMultiRangeType type) {
        final PostgreType sqlType;
        switch (type.dataType) {
            case INT4MULTIRANGE:
                sqlType = PostgreType.INT4RANGE;
                break;
            case INT8MULTIRANGE:
                sqlType = PostgreType.INT8RANGE;
                break;
            case NUMMULTIRANGE:
                sqlType = PostgreType.NUMRANGE;
                break;
            case DATEMULTIRANGE:
                sqlType = PostgreType.DATERANGE;
                break;
            case TSMULTIRANGE:
                sqlType = PostgreType.TSRANGE;
                break;
            case TSTZMULTIRANGE:
                sqlType = PostgreType.TSTZRANGE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(type.dataType);
        }
        final Class<?> javaType;
        javaType = type.javaType.getComponentType();
        assert !javaType.isArray();

        final PostgreSingleRangeType instance;
        if (javaType == String.class) {
            instance = textInstance(sqlType);
        } else {
            instance = new PostgreSingleRangeType(sqlType, javaType, type.rangeFunc);
        }
        return instance;
    }


    /**
     * <p>
     * package constructor
     * </p>
     */
    private PostgreSingleRangeType(PostgreType sqlType, Class<?> javaType, @Nullable RangeFunction<?, ?> rangeFunc) {
        super(sqlType, javaType, rangeFunc);
    }


    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final RangeFunction<?, ?> rangeFunc;
        final PostgreSingleRangeType instance;
        if (targetType == String.class) {
            instance = textInstance(this.dataType);
        } else if (targetType.isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((rangeFunc = tryCreateDefaultRangeFunc(targetType, boundJavaType(this.dataType))) == null) {
            throw noMatchCompatibleMapping(this, targetType);
        } else {
            instance = new PostgreSingleRangeType(this.dataType, targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    public Object convert(final MappingEnv env, final Object source) throws CriteriaException {
        return rangeConvert(source, this.rangeFunc, this::deserialize, this.map(env.serverMeta()), this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        return rangeBeforeBind(this::serialize, source, dataType, this, PARAM_ERROR_HANDLER);
    }


    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return rangeAfterGet(source, this.rangeFunc, this::deserialize, dataType, this, ACCESS_ERROR_HANDLER);
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final PostgreSingleRangeArrayType instance;
        final RangeFunction<?, ?> rangeFunc = this.rangeFunc;
        if (rangeFunc != null) {
            instance = PostgreSingleRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(this.javaType), this.dataType, rangeFunc);
        } else switch (this.dataType) {
            case INT4RANGE:
                instance = PostgreSingleRangeArrayType.INT4_RANGE_LINEAR;
                break;
            case INT8RANGE:
                instance = PostgreSingleRangeArrayType.INT8_RANGE_LINEAR;
                break;
            case NUMRANGE:
                instance = PostgreSingleRangeArrayType.NUM_RANGE_LINEAR;
                break;
            case DATERANGE:
                instance = PostgreSingleRangeArrayType.DATE_RANGE_LINEAR;
                break;
            case TSRANGE:
                instance = PostgreSingleRangeArrayType.TS_RANGE_LINEAR;
                break;
            case TSTZRANGE:
                instance = PostgreSingleRangeArrayType.TS_TZ_RANGE_LINEAR;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.dataType);
        }
        return instance;
    }

    @Override
    public MappingType multiRangeType() {
        return PostgreMultiRangeType.fromSingleType(this);
    }


    @Override
    public PostgreSingleRangeType _fromSingleArray(final PostgreSingleRangeArrayType type) {
        final PostgreType sqlType;
        switch (type.dataType) {
            case INT4RANGE_ARRAY:
                sqlType = PostgreType.INT4RANGE;
                break;
            case INT8RANGE_ARRAY:
                sqlType = PostgreType.INT8RANGE;
                break;
            case NUMRANGE_ARRAY:
                sqlType = PostgreType.NUMRANGE;
                break;
            case DATERANGE_ARRAY:
                sqlType = PostgreType.DATERANGE;
                break;
            case TSRANGE_ARRAY:
                sqlType = PostgreType.TSRANGE;
                break;
            case TSTZRANGE_ARRAY:
                sqlType = PostgreType.TSTZRANGE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(type.dataType);
        }
        final Class<?> javaType;
        javaType = type.javaType.getComponentType();
        assert !javaType.isArray();

        final PostgreSingleRangeType instance;
        if (javaType == String.class) {
            instance = textInstance(sqlType);
        } else {
            instance = new PostgreSingleRangeType(sqlType, javaType, type.rangeFunc);
        }
        return instance;
    }


    /**
     * @param parseFunc <ul>
     *                  <li>argument of function possibly is notion 'infinity',see {@link PostgreRangeType#INFINITY}</li>
     *                  <li>function must return null when argument is notion 'infinity' and support it,see {@link PostgreRangeType#INFINITY}</li>
     *                  <li>function must throw {@link IllegalArgumentException} when argument is notion 'infinity' and don't support it,see {@link PostgreRangeType#INFINITY}</li>
     *                  </ul>
     * @throws IllegalArgumentException when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws CriteriaException        when text error and handler throw this type.
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R rangeConvert(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                        final Function<String, T> parseFunc, final DataType dataType,
                                        final MappingType type, final ErrorHandler handler) {

        final R value;
        if (nonNull instanceof String) {
            value = parseRange((String) nonNull, rangeFunc, parseFunc, dataType, type, handler);
        } else if (type.javaType().isInstance(nonNull)) {
            value = (R) nonNull;
        } else {
            throw handler.apply(type, dataType, nonNull, null);
        }
        return value;
    }

    public static <T> String rangeBeforeBind(final BiConsumer<T, Consumer<String>> boundSerializer, final Object nonNull,
                                             final DataType dataType, final MappingType type, final ErrorHandler handler)
            throws CriteriaException {

        final String value, text;
        char boundChar;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, dataType, nonNull, null);
            }
            final StringBuilder builder = new StringBuilder();
            rangeToText(nonNull, boundSerializer, type, builder::append);
            value = builder.toString();
        } else if (EMPTY.equalsIgnoreCase((text = (String) nonNull).trim())) {
            value = EMPTY;
        } else if (text.length() < 3) {
            throw handler.apply(type, dataType, nonNull, null);
        } else if ((boundChar = text.charAt(0)) != '[' && boundChar != '(') {
            throw handler.apply(type, dataType, nonNull, null);
        } else if ((boundChar = text.charAt(text.length() - 1)) != ']' && boundChar != ')') {
            throw handler.apply(type, dataType, nonNull, null);
        } else {
            value = text;
        }
        return value;
    }

    /**
     * @param parseFunc <ul>
     *                  <li>argument of function possibly is notion 'infinity',see {@link PostgreRangeType#INFINITY}</li>
     *                  <li>function must return null when argument is notion 'infinity' and support it,see {@link PostgreRangeType#INFINITY}</li>
     *                  <li>function must throw {@link IllegalArgumentException} when argument is notion 'infinity' and don't support it,see {@link PostgreRangeType#INFINITY}</li>
     *                  </ul>
     * @throws IllegalArgumentException            when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws io.army.session.DataAccessException when text error and handler throw this type.
     */
    public static <T, R> R rangeAfterGet(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                         final Function<String, T> parseFunc, final DataType dataType,
                                         final MappingType type, final MappingSupport.ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(type, dataType, nonNull, null);
        }
        return parseRange((String) nonNull, rangeFunc, parseFunc, dataType, type, handler);
    }


    /*-------------------below private method -------------------*/


    private static boolean isNotSingleRangeType(final PostgreType sqlType) {
        final boolean match;
        switch (sqlType) {
            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case DATERANGE:
            case TSRANGE:
            case TSTZRANGE:
                match = false;
                break;
            default:
                match = true;

        }
        return match;
    }

    private static PostgreSingleRangeType textInstance(final PostgreType sqlType) {
        final PostgreSingleRangeType instance;
        switch (sqlType) {
            case INT4RANGE:
                instance = INT4_RANGE_TEXT;
                break;
            case INT8RANGE:
                instance = INT8_RANGE_TEXT;
                break;
            case NUMRANGE:
                instance = NUM_RANGE_TEXT;
                break;
            case DATERANGE:
                instance = DATE_RANGE_TEXT;
                break;
            case TSRANGE:
                instance = TS_RANGE_TEXT;
                break;
            case TSTZRANGE:
                instance = TS_TZ_RANGE_TEXT;
                break;
            default:
                throw _Exceptions.unexpectedEnum(sqlType);

        }
        return instance;
    }

    private static String sqlTypeErrorMessage(PostgreType sqlType) {
        return String.format("%s isn't postgre single-range type", sqlType);
    }


}
