package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingSupport;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;

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
public abstract class PostgreSingleRangeType<T> extends PostgreRangeType<T> {


    /**
     * <p>
     * package constructor
     * </p>
     *
     * @param elementType null when only javaType is {@link String#getClass()}
     */
    PostgreSingleRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                           Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }


    @Override
    public final MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
    }

    @Override
    public final Object convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        return rangeConvert(nonNull, this.rangeFunc, this.parseFunc, this.map(env.serverMeta()), this,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        return rangeBeforeBind(this::boundToText, nonNull, type, this, PARAM_ERROR_HANDLER);
    }


    @Override
    public final Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return rangeAfterGet(nonNull, this.rangeFunc, this.parseFunc, type, this, ACCESS_ERROR_HANDLER);
    }

    @Override
    public final boolean isSameType(MappingType type) {
        return super.isSameType(type);
    }

    public abstract MappingType subtype();


    /**
     * @param parseFunc <ul>
     *                  <li>argument of function possibly is notion 'infinity',see {@link ArmyPostgreRangeType#INFINITY}</li>
     *                  <li>function must return null when argument is notion 'infinity' and support it,see {@link ArmyPostgreRangeType#INFINITY}</li>
     *                  <li>function must throw {@link IllegalArgumentException} when argument is notion 'infinity' and don't support it,see {@link ArmyPostgreRangeType#INFINITY}</li>
     *                  </ul>
     * @throws IllegalArgumentException when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws CriteriaException        when text error and handler throw this type.
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R rangeConvert(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                        final Function<String, T> parseFunc, final SqlType sqlType,
                                        final MappingType type, final MappingSupport.ErrorHandler handler) {

        final R value;
        if (nonNull instanceof String) {
            value = parseRange((String) nonNull, rangeFunc, parseFunc, sqlType, type, handler);
        } else if (type.javaType().isInstance(nonNull)) {
            value = (R) nonNull;
        } else {
            throw handler.apply(type, sqlType, nonNull, null);
        }
        return value;
    }

    public static <T> String rangeBeforeBind(final BiConsumer<T, Consumer<String>> serializer, final Object nonNull,
                                             final SqlType sqlType, final MappingType type, final ErrorHandler handler)
            throws CriteriaException {

        final String value, text;
        char boundChar;
        if (!(nonNull instanceof String)) {
            if (!type.javaType().isInstance(nonNull)) {
                throw handler.apply(type, sqlType, nonNull, null);
            }
            final StringBuilder builder = new StringBuilder();
            rangeToText(nonNull, serializer, type, builder::append);
            value = builder.toString();
        } else if (EMPTY.equalsIgnoreCase((text = (String) nonNull).trim())) {
            value = EMPTY;
        } else if (text.length() < 3) {
            throw handler.apply(type, sqlType, nonNull, null);
        } else if ((boundChar = text.charAt(0)) != '[' && boundChar != '(') {
            throw handler.apply(type, sqlType, nonNull, null);
        } else if ((boundChar = text.charAt(text.length() - 1)) != ']' && boundChar != ')') {
            throw handler.apply(type, sqlType, nonNull, null);
        } else {
            value = text;
        }
        return value;
    }

    /**
     * @param function <ul>
     *                 <li>argument of function possibly is notion 'infinity',see {@link ArmyPostgreRangeType#INFINITY}</li>
     *                 <li>function must return null when argument is notion 'infinity' and support it,see {@link ArmyPostgreRangeType#INFINITY}</li>
     *                 <li>function must throw {@link IllegalArgumentException} when argument is notion 'infinity' and don't support it,see {@link ArmyPostgreRangeType#INFINITY}</li>
     *                 </ul>
     * @throws IllegalArgumentException            when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws io.army.session.DataAccessException when text error and handler throw this type.
     */
    public static <T, R> R rangeAfterGet(final Object nonNull, final @Nullable RangeFunction<T, R> rangeFunc,
                                         final Function<String, T> function, final SqlType sqlType,
                                         final MappingType type, final MappingSupport.ErrorHandler handler) {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(type, sqlType, nonNull, null);
        }
        return parseRange((String) nonNull, rangeFunc, function, sqlType, type, handler);
    }


    /*-------------------below private method -------------------*/


}
