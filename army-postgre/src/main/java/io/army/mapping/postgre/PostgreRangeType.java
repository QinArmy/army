package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.MetaException;
import io.army.sqltype.SqlType;
import io.army.util._ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

/**
 * <p>
 * This class representing Postgre Built-in Range and Multirange Types type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 */
public abstract class PostgreRangeType extends _ArmyNoInjectionMapping {

    static final String EMPTY = "empty";


    final Class<?> javaType;

    /**
     * package constructor
     */
    PostgreRangeType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    final <T> String rangeBeforeBind(final SqlType type, final Class<T> elementType, final Function<T, String> function,
                                     final Object nonNull)
            throws CriteriaException {

        final String value, text;
        char boundChar;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
            }
            value = rangeToText(nonNull, elementType, function, new StringBuilder())
                    .toString();
        } else if (EMPTY.equals(nonNull)) {
            value = EMPTY;
        } else if ((text = (String) nonNull).length() < 5) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
        } else if ((boundChar = text.charAt(0)) != '[' && boundChar != ')') {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
        } else if ((boundChar = text.charAt(text.length() - 1)) != '[' && boundChar != ')') {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
        } else {
            value = text;
        }
        return value;
    }

    final <T> Object textToRange(final String text, final _RangeFunction<T, ?> rangeFunc,
                                 final Function<String, T> function, final SqlType type, final ErrorHandler handler) {
        final int length = text.length();
        Boolean includeLowerBound = null, includeUpperBound = null;
        T lowerBound = null, upperBound = null;
        char ch;
        for (int i = 0, offset; i < length; i++) {
            ch = text.charAt(i);

        }

        throw new UnsupportedOperationException();
    }


    static <T> _RangeFunction<T, ?> createRangeFunction(final Class<?> javaType, final Class<T> elementType,
                                                        final Method method) {
        final int modifier;
        modifier = method.getModifiers();
        final Class<?>[] paramTypes;

        if (!(Modifier.isStatic(modifier) && Modifier.isPublic(modifier))) {
            String m = String.format("%s isn't public static method", method);
            throw new MetaException(m);
        } else if (!javaType.isAssignableFrom(method.getReturnType())) {
            String m = String.format("%s return type isn't %s", method, elementType.getName());
            throw new MetaException(m);
        } else if ((paramTypes = method.getParameterTypes()).length != 4) {
            String m = String.format("%s parameter count isn't 4.", method);
            throw new MetaException(m);
        } else if (!(elementType == paramTypes[0]
                && boolean.class.isAssignableFrom(paramTypes[1])
                && elementType == paramTypes[2]
                && boolean.class.isAssignableFrom(paramTypes[3]))) {
            String m = String.format("%s parameter list error", method);
            throw new MetaException(m);
        }
        return (lower, includeLower, upper, includeUpper) -> {
            try {
                final Object result;
                result = method.invoke(null, lower, includeLower, upper, includeUpper);
                if (result == null) {
                    throw new NullPointerException();
                }
                return result;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }


    static <T> StringBuilder rangeToText(final Object nonNull, final Class<T> rangeType, final Function<T, String> function,
                                         final StringBuilder builder) {
        final Class<?> javaType = nonNull.getClass();
        if (rangeBeanFunc(javaType, "isEmptyRange", Boolean.TYPE).apply(nonNull)) {
            return builder.append(EMPTY);
        }

        final T lowerBound, upperBound;

        lowerBound = rangeBeanFunc(javaType, "getLowerBound", rangeType).apply(nonNull);
        upperBound = rangeBeanFunc(javaType, "getUpperBound", rangeType).apply(nonNull);

        if (rangeBeanFunc(javaType, "isIncludeLowerBound", Boolean.TYPE).apply(nonNull)) {
            builder.append(_Constant.LEFT_SQUARE_BRACKET);
        } else {
            builder.append(_Constant.LEFT_PAREN);
        }

        builder.append(function.apply(lowerBound))
                .append(_Constant.COMMA)
                .append(function.apply(upperBound));

        if (rangeBeanFunc(javaType, "isIncludeUpperBound", Boolean.TYPE).apply(nonNull)) {
            builder.append(_Constant.LEFT_SQUARE_BRACKET);
        } else {
            builder.append(_Constant.LEFT_PAREN);
        }
        return builder;
    }


    static Object emptyRange(final Class<?> javaType) {
        final Object empty;
        if (javaType == String.class) {
            empty = "empty";
        } else {
            try {
                final Method method;
                method = javaType.getMethod("emptyRange");
                empty = method.invoke(null);
                if (!javaType.isInstance(empty)) {
                    String m = String.format("%s don't return %s instance.", method, javaType.getName());
                    throw new MetaException(m);
                }
            } catch (NoSuchMethodException e) {
                throw new MetaException(e.getMessage(), e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return empty;
    }


    @SuppressWarnings("unchecked")
    private static <R> Function<Object, R> rangeBeanFunc(final Class<?> javaType, final String methodName,
                                                         final Class<R> resultType) {
        try {
            final Method method;
            method = javaType.getMethod(methodName);
            return instance -> {
                try {
                    final Object result;
                    result = method.invoke(instance);
                    if (!resultType.isInstance(result)) {
                        String m = String.format("%s isn't instance of %s.", _ClassUtils.safeClassName(result),
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


}
