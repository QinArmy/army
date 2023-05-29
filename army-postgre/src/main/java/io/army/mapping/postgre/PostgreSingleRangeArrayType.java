package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.PostgreArrays;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class representing army build-in postgre range array type.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 * @since 1.0
 */
public abstract class PostgreSingleRangeArrayType<T> extends ArmyPostgreRangeType<T>
        implements MappingType.SqlArrayType {


    /**
     * package constructor
     */
    PostgreSingleRangeArrayType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                                Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }

    @Override
    public final <Z> MappingType compatibleFor(final Class<Z> targetType) throws NoMatchMappingException {
        final Class<?> thisJavaType = this.javaType, targetComponentType;
        final RangeFunction<T, ?> rangeFunc;
        if (targetType == List.class) { // array -> List
            if (!thisJavaType.isArray() || (targetComponentType = thisJavaType.getComponentType()).isArray()) {
                throw noMatchCompatibleMapping(this, targetType);
            }
            rangeFunc = this.rangeFunc;
        } else if (!targetType.isArray()) {
            throw noMatchCompatibleMapping(this, targetType);
        } else if (!thisJavaType.isArray()) {
            if (!(this instanceof UnaryGenericsMapping.ListMapping)
                    || (targetComponentType = targetType.getComponentType()).isArray()) {
                throw noMatchCompatibleMapping(this, targetType);
            }
            rangeFunc = PostgreRangeType.tryCreateDefaultRangeFunc(targetComponentType, boundJavaType());
        } else if (ArrayUtils.dimensionOf(targetType) == ArrayUtils.dimensionOf(thisJavaType)) {
            targetComponentType = ArrayUtils.underlyingComponent(targetType);
            rangeFunc = PostgreRangeType.tryCreateDefaultRangeFunc(targetComponentType, boundJavaType());
        } else {
            throw noMatchCompatibleMapping(this, targetType);
        }
        if (rangeFunc == null) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return compatibleFor(targetType, targetComponentType, rangeFunc);
    }

    @Override
    public final boolean isSameType(MappingType type) {
        return super.isSameType(type);
    }

    @Override
    public final Object convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        return arrayConvert(nonNull, this.rangeFunc, this.parseFunc, map(env.serverMeta()), this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        return arrayBeforeBind(nonNull, this::boundToText, type, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return arrayAfterGet(nonNull, this.rangeFunc, this.parseFunc, type, this, ACCESS_ERROR_HANDLER);
    }

    abstract MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<T, ?> rangeFunc)
            throws NoMatchMappingException;

    public static <T, R> Object arrayConvert(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                             final Function<String, T> parseFunc, final SqlType sqlType,
                                             final MappingType type, final MappingSupport.ErrorHandler handler) {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, sqlType, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 2) {
            throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        } else {
            value = parseRangeArray(text, rangeFunc, parseFunc, sqlType, type, handler);
        }
        return value;
    }

    /**
     * @param boundSerializer serializer of bound
     * @param <T>             java type of subtype of range
     */
    public static <T> String arrayBeforeBind(final Object nonNull, final BiConsumer<T, Consumer<String>> boundSerializer,
                                             final SqlType sqlType, final MappingType type,
                                             final ErrorHandler handler) {
        final BiConsumer<Object, Consumer<String>> rangeSerializer;
        rangeSerializer = (range, appender) -> PostgreRangeType.rangeToText(range, boundSerializer, type, appender);
        return PostgreArrays.arrayBeforeBind(nonNull, rangeSerializer, sqlType, type, handler);
    }


    public static <T> Object arrayAfterGet(final Object nonNull, final @Nullable RangeFunction<T, ?> rangeFunc,
                                           final Function<String, T> parseFunc, final SqlType sqlType,
                                           final MappingType type, final MappingSupport.ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw handler.apply(type, sqlType, nonNull, null);
        }
        return parseRangeArray((String) nonNull, rangeFunc, parseFunc, sqlType, type, handler);
    }

    private static <T> Object parseRangeArray(final String text, final @Nullable RangeFunction<T, ?> rangeFunc,
                                              final Function<String, T> parseFunc, final SqlType sqlType,
                                              final MappingType type, final MappingSupport.ErrorHandler handler) {

        final TextFunction<?> elementFunc;
        if (!(type instanceof MappingType.SqlArrayType)) {
            throw handler.apply(type, sqlType, text, _Exceptions.notArrayMappingType(type));
        } else if (rangeFunc == null) {
            if (ArrayUtils.underlyingComponent(type.javaType()) != String.class) {
                String m = String.format("%s java type isn't %s array", type, String.class.getName());
                throw handler.apply(type, sqlType, text, new IllegalArgumentException(m));
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
        return PostgreArrays.parseArray(text, false, elementFunc, _Constant.COMMA, sqlType, type, handler);
    }


}
