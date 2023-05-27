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

    public static final String INFINITY = "infinity";

    static final String EMPTY = "empty";

    private static final Object INFINITY_BOUND = new Object();


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
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = rangeToText(nonNull, elementType, function, new StringBuilder())
                    .toString();
        } else if (EMPTY.equalsIgnoreCase((String) nonNull)) {
            value = EMPTY;
        } else if ((text = (String) nonNull).length() < 5) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if ((boundChar = text.charAt(0)) != '[' && boundChar != ')') {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if ((boundChar = text.charAt(text.length() - 1)) != '[' && boundChar != ')') {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else {
            value = text;
        }
        return value;
    }

    /**
     * @param function <ul>
     *                 <li>argument of function possibly with any leading and trailing whitespace.</li>
     *                 <li>argument of function possibly is notion 'infinity',see {@link #INFINITY}</li>
     *                 <li>function must return null when argument is notion 'infinity' and support it,see {@link #INFINITY}</li>
     *                 <li>function must throw {@link IllegalArgumentException} when argument is notion 'infinity' and don't support it,see {@link #INFINITY}</li>
     *                 </ul>
     * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html">Range Types</a>
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R textToNonEmptyRange(final String text, final int offset, final int end,
                                               final RangeFunction<T, R> rangeFunc, final Function<String, T> function) {
        if (!(offset < end && end <= text.length())) {
            throw new IllegalArgumentException("offset or end error");
        }
        if (text.regionMatches(true, offset, EMPTY, 0, EMPTY.length())) {
            throw new IllegalArgumentException("range must non-empty.");
        }
        Boolean includeLowerBound = null, includeUpperBound = null;
        Object lowerBound = null, upperBound = null;
        boolean inQuote = false, findComma = true;
        char ch;
        for (int i = offset, lowerIndex = -1, upperIndex = -1, nextIndex; i < end; i++) {
            ch = text.charAt(i);
            if (includeLowerBound == null) {
                if (ch == _Constant.LEFT_SQUARE_BRACKET) {
                    includeLowerBound = Boolean.TRUE;
                } else if (ch == _Constant.LEFT_PAREN) {
                    includeLowerBound = Boolean.FALSE;
                } else if (!Character.isWhitespace(ch)) {
                    throw nearbyError(text.substring(offset, i));
                }
                continue;
            } else if (inQuote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                } else if (ch != _Constant.DOUBLE_QUOTE) {
                    continue;
                } else if ((nextIndex = i + 1) < end && text.charAt(nextIndex) == _Constant.DOUBLE_QUOTE) {
                    continue;
                } else if (lowerBound == null) {
                    inQuote = false;
                    assert lowerIndex > 0;
                    lowerBound = function.apply(text.substring(lowerIndex, i));
                    if (lowerBound == null) {
                        lowerBound = INFINITY_BOUND;
                    }
                } else {
                    inQuote = false;
                    assert upperBound == null && upperIndex > 0;
                    upperBound = function.apply(text.substring(upperIndex, i));
                    if (upperBound == null) {
                        upperBound = INFINITY_BOUND;
                    }
                }
                continue;
            }

            if (lowerIndex < 0) {
                if (ch == _Constant.DOUBLE_QUOTE) {
                    inQuote = true;
                    lowerIndex = i + 1;
                } else if (ch == _Constant.COMMA) {
                    lowerBound = INFINITY_BOUND;
                    lowerIndex = 0;
                    findComma = false;
                } else if (!Character.isWhitespace(ch)) {
                    lowerIndex = i;
                }
            } else if (lowerBound == null) {
                if (ch == _Constant.COMMA) {
                    findComma = false;
                    lowerBound = function.apply(text.substring(lowerIndex, i));
                    if (lowerBound == null) {
                        lowerBound = INFINITY_BOUND;
                    }
                }
            } else if (findComma) {
                if (ch == _Constant.COMMA) {
                    findComma = false;
                } else if (!Character.isWhitespace(ch)) {
                    throw nearbyError(text.substring(lowerIndex, i + 1));
                }
            } else if (upperIndex < 0) {
                if (ch == _Constant.DOUBLE_QUOTE) {
                    inQuote = true;
                    upperIndex = i + 1;
                } else if (ch == _Constant.RIGHT_SQUARE_BRACKET) {
                    includeUpperBound = Boolean.TRUE;
                    upperBound = INFINITY_BOUND;
                    upperIndex = 0;
                } else if (ch == _Constant.RIGHT_PAREN) {
                    includeUpperBound = Boolean.FALSE;
                    upperBound = INFINITY_BOUND;
                    upperIndex = 0;
                } else if (!Character.isWhitespace(ch)) {
                    upperIndex = i;
                }
            } else if (upperBound == null) {
                if (ch == _Constant.RIGHT_SQUARE_BRACKET) {
                    includeUpperBound = Boolean.TRUE;
                } else if (ch == _Constant.RIGHT_PAREN) {
                    includeUpperBound = Boolean.FALSE;
                }
                if (includeUpperBound != null) {
                    upperBound = function.apply(text.substring(upperIndex, i));
                    if (upperBound == null) {
                        upperBound = INFINITY_BOUND;
                    }
                }
            } else if (includeUpperBound != null) {
                break;
            } else if (ch == _Constant.RIGHT_SQUARE_BRACKET) {
                includeUpperBound = Boolean.TRUE;
            } else if (ch == _Constant.RIGHT_PAREN) {
                includeUpperBound = Boolean.FALSE;
            }


        }// for

        if (lowerBound == null || includeLowerBound == null || upperBound == null || includeUpperBound == null) {
            throw new IllegalArgumentException("postgre range format error.");
        }
        if (lowerBound == INFINITY_BOUND) {
            lowerBound = null;
        }
        if (upperBound == INFINITY_BOUND) {
            upperBound = null;
        }
        return rangeFunc.apply((T) lowerBound, includeLowerBound, (T) upperBound, includeUpperBound);
    }

    /**
     * @param methodName public static factory method name,for example : com.my.Factory#create
     * @throws io.army.meta.MetaException throw when factory method name error.
     */
    static <T> RangeFunction<T, ?> createRangeFunction(final Class<?> javaType, final Class<T> elementType,
                                                       final String methodName) {

        final Method method;
        method = loadFactoryMethod(javaType, methodName, elementType);
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


    @SuppressWarnings("unchecked")
    static <T> StringBuilder rangeToText(final Object nonNull, final Class<T> rangeType, final Function<T, String> function,
                                         final StringBuilder builder) {
        final ArmyPostgreRange<T> range;
        if (!(nonNull instanceof ArmyPostgreRange)) {
            rangeToTextByReflection(nonNull, rangeType, function, builder);
        } else if ((range = (ArmyPostgreRange<T>) nonNull).isEmpty()) {
            builder.append(EMPTY);
        } else {
            if (range.isIncludeLowerBound()) {
                builder.append(_Constant.LEFT_SQUARE_BRACKET);
            } else {
                builder.append(_Constant.LEFT_PAREN);
            }
            final T lowerBound, upperBound;
            lowerBound = range.getLowerBound();
            upperBound = range.getUpperBound();

            if (lowerBound != null) {
                builder.append(function.apply(lowerBound));
            }
            builder.append(_Constant.COMMA);
            if (upperBound != null) {
                builder.append(function.apply(upperBound));
            }

            if (range.isIncludeUpperBound()) {
                builder.append(_Constant.RIGHT_SQUARE_BRACKET);
            } else {
                builder.append(_Constant.RIGHT_PAREN);
            }
        }
        return builder;
    }


    static Object emptyRange(final Class<?> javaType) {
        final Object empty;
        if (javaType == String.class) {
            empty = EMPTY;
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

    private static Method loadFactoryMethod(final Class<?> javaType, final String methodName,
                                            final Class<?> elementType) {
        try {
            final int poundIndex, modifier;
            poundIndex = methodName.indexOf('#');
            if (poundIndex < 1 || poundIndex + 1 >= methodName.length()) {
                String m = String.format("%s isn't method name", methodName);
                throw new MetaException(m);
            }
            final Class<?> methodClass;
            methodClass = Class.forName(methodName.substring(0, poundIndex));
            final Method method;
            method = methodClass.getMethod(methodName.substring(poundIndex + 1),
                    elementType, boolean.class, elementType, boolean.class);
            modifier = method.getModifiers();
            if (!(Modifier.isPublic(modifier)
                    && Modifier.isStatic(modifier)
                    && javaType.isAssignableFrom(method.getReturnType()))) {
                String m = String.format("%s isn't public static factory method.", methodName);
                throw new MetaException(m);
            }
            return method;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new MetaException(e.getMessage(), e);
        }
    }


    /**
     * @see #rangeToText(Object, Class, Function, StringBuilder)
     */
    private static <T> void rangeToTextByReflection(final Object nonNull, final Class<T> rangeType,
                                                    final Function<T, String> function, final StringBuilder builder) {
        final Class<?> javaType = nonNull.getClass();
        if (rangeBeanFunc(javaType, "isEmptyRange", Boolean.TYPE).apply(nonNull)) {
            builder.append(EMPTY);
            return;
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
    }


    private static IllegalArgumentException nearbyError(String text) {
        return new IllegalArgumentException(String.format("'%s' nearby error.", text));
    }


}
