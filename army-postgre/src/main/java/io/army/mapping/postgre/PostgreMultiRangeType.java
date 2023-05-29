package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.UnaryGenericsMapping;
import io.army.mapping.optional.PostgreArrays;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class representing army build-in postgre multi-range array type.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 * @since 1.0
 */
public abstract class PostgreMultiRangeType<T> extends PostgreRangeType<T>
        implements PostgreRangeType.MultiRangeType {

    /**
     * <p>
     * package constructor
     * </p>
     */
    PostgreMultiRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                          Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }


    @Override
    public final boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (this instanceof UnaryGenericsMapping.ListMapping) {
            final Class<?> thisClass, typeClass;
            thisClass = this.getClass();
            typeClass = type.getClass();
            match = thisClass == typeClass || thisClass.getSuperclass() == typeClass;
        } else if (type instanceof UnaryGenericsMapping.ListMapping) {
            final Class<?> thisClass, typeClass;
            thisClass = this.getClass();
            typeClass = type.getClass();
            match = thisClass == typeClass || typeClass.getSuperclass() == thisClass;
        } else {
            match = this.getClass().isInstance(type);
        }
        return match;
    }

    @Override
    public final <Z> MappingType compatibleFor(final Class<Z> targetType) throws NoMatchMappingException {
        final Class<?> thisJavaType = this.javaType, targetComponentType;
        final RangeFunction<T, ?> rangeFunc;
        if (targetType == List.class) {
            if (!thisJavaType.isArray()) {
                throw noMatchCompatibleMapping(this, targetType);
            }
            // array -> List
            targetComponentType = thisJavaType.getComponentType();
            rangeFunc = this.rangeFunc;
        } else if (!targetType.isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if ((targetComponentType = targetType.getComponentType()).isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if (this instanceof UnaryGenericsMapping.ListMapping) {  // List -> array
            if (targetComponentType.isAssignableFrom(((UnaryGenericsMapping.ListMapping<?>) this).genericsType())) {
                rangeFunc = this.rangeFunc;
            } else {
                rangeFunc = PostgreRangeType.tryCreateDefaultRangeFunc(targetComponentType, boundJavaType());
            }
        } else if (targetComponentType.isAssignableFrom(thisJavaType.getComponentType())) { // array -> array
            rangeFunc = this.rangeFunc;
        } else {   // array -> array
            rangeFunc = PostgreRangeType.tryCreateDefaultRangeFunc(targetComponentType, boundJavaType());
        }
        if (rangeFunc == null) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return compatibleFor(targetType, targetComponentType, rangeFunc);
    }

    @Override
    public final Object convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        return rangeConvert(nonNull, this.rangeFunc, this.parseFunc, map(env.serverMeta()), this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return rangeBeforeBind(nonNull, this::boundToText, type, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return rangeAfterGet(nonNull, this.rangeFunc, this.parseFunc, type, this, ACCESS_ERROR_HANDLER);
    }


    abstract MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<T, ?> rangeFunc)
            throws NoMatchMappingException;


    public static <T> Object rangeConvert(Object nonNull, @Nullable RangeFunction<T, ?> rangeFunc,
                                          Function<String, T> parseFunc, SqlType sqlType,
                                          MappingType type, ErrorHandler handler) {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 5) { // non-empty,non-null
            throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        } else {
            value = parseMultiRange((String) nonNull, rangeFunc, parseFunc, sqlType, type, handler);
        }
        return value;
    }


    public static <T> String rangeBeforeBind(final Object nonNull, final BiConsumer<T, Consumer<String>> boundSerializer,
                                             final SqlType sqlType, final MappingType type, final ErrorHandler handler)
            throws CriteriaException {

        final String value, text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, sqlType, nonNull, null);
            }
            final BiConsumer<Object, Consumer<String>> rangeSerializer;
            rangeSerializer = (range, appender) -> rangeToText(range, boundSerializer, type, appender);

            final StringBuilder builder = new StringBuilder();
            PostgreArrays.toArrayText(nonNull, rangeSerializer, builder);
            value = builder.toString();
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 5) { //non-empty
            throw handler.apply(type, sqlType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw handler.apply(type, sqlType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw handler.apply(type, sqlType, nonNull, null);
        } else {
            value = text;
        }
        return value;
    }

    public static <T> Object rangeAfterGet(Object nonNull, @Nullable RangeFunction<T, ?> rangeFunc,
                                           Function<String, T> parseFunc, SqlType sqlType,
                                           MappingType type, ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw handler.apply(type, sqlType, nonNull, null);
        }
        return parseMultiRange((String) nonNull, rangeFunc, parseFunc, sqlType, type, handler);
    }


    static <T> TextFunction<?> multiRangeParseFunc(final Object nonNull, final RangeFunction<T, ?> rangeFunc,
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


    private static <T> Object parseMultiRange(final String text, final @Nullable RangeFunction<T, ?> rangeFunc,
                                              final Function<String, T> parseFunc, final SqlType sqlType,
                                              final MappingType type, final ErrorHandler handler) {

        final TextFunction<?> elementFunc;
        if (rangeFunc == null) {
            if (type.javaType() != String.class) {
                String m = String.format("%s java type isn't %s", type, String.class.getName());
                throw handler.apply(type, sqlType, text, new IllegalArgumentException(m));
            }
            elementFunc = String::substring;
        } else {
            elementFunc = multiRangeParseFunc(text, rangeFunc, parseFunc, sqlType, type, handler);
        }
        final Object array;
        array = PostgreArrays.parseArray(text, true, elementFunc, _Constant.COMMA, sqlType, type, handler);

        if (!(array instanceof List || ArrayUtils.dimensionOf(array.getClass()) == 1)) {
            throw handler.apply(type, sqlType, text, null);
        }
        return array;
    }


}
