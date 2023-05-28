package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingSupport;
import io.army.mapping.MappingType;
import io.army.mapping.optional.PostgreArrays;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * This class representing army build-in postgre multi-range array type.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 * @since 1.0
 */
public abstract class PostgreMultiRangeType<T> extends PostgreRangeType<T> {


    PostgreMultiRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                          Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }


    @Override
    public final boolean isSameType(MappingType type) {
        return super.isSameType(type);
    }


    @Override
    public Object convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        final SqlType type;
        type = map(env.serverMeta());
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = ((String) nonNull).trim()).length()) < 5) { // non-empty,non-null
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else {
            //PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, )
            value = null;
        }
        return value;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
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
            elementFunc = (str, offset, end) -> {
                if (str.regionMatches(true, offset, PostgreRangeType.EMPTY, 0, 5)) {
                    String m = "multi-range must be non-empty and non-null";
                    throw handler.apply(type, sqlType, text, new IllegalArgumentException(m));
                }
                return PostgreRangeType.parseNonEmptyRange(str, offset, end, rangeFunc, parseFunc);
            };
        }
        final Object array;
        array = PostgreArrays.parseArray(text, true, elementFunc, _Constant.COMMA, sqlType, type, handler);

        if (!(array instanceof List || _ArrayUtils.dimensionOf(array.getClass()) == 1)) {
            throw handler.apply(type, sqlType, text, null);
        }
        return array;
    }


}
