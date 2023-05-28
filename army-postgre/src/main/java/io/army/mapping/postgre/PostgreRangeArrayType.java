package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingSupport;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.optional.PostgreArrays;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

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
public abstract class PostgreRangeArrayType<T> extends ArmyPostgreRangeType<T> implements MappingType.SqlArrayType {


    /**
     * package constructor
     */
    PostgreRangeArrayType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                          Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }

    @Override
    public final MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
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
        if (rangeFunc == null) {
            if (type.javaType() != String.class) {
                String m = String.format("%s java type isn't %s", type, String.class.getName());
                throw handler.apply(type, sqlType, text, new IllegalArgumentException(m));
            }
            elementFunc = String::substring;
        } else {
            final Class<?> rangeClass;
            rangeClass = _ArrayUtils.underlyingComponentClass(type);
            elementFunc = (str, offset, end) -> {
                final Object value;
                if (str.regionMatches(true, offset, EMPTY, 0, 5)) {
                    value = PostgreSingleRangeType.emptyRange(rangeClass);
                } else {
                    value = PostgreSingleRangeType.parseNonEmptyRange(str, offset, end, rangeFunc, parseFunc);
                }
                return value;
            };
        }
        return PostgreArrays.parseArray(text, elementFunc, _Constant.COMMA, sqlType, type, handler);
    }


}
