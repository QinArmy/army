package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.MetaException;
import io.army.sqltype.SqlType;
import io.army.util._ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link PostgreSingleRangeType}</li>
 *         <li>{@link PostgreMultiRangeType}</li>
 *     </ul>
 * </p>
 *
 * @param <T> java class of subtype of range
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 */
public abstract class PostgreRangeType<T> extends ArmyPostgreRangeType<T> {

    public static final String INFINITY = "infinity";
    public static final String EMPTY = "empty";
    private static final Object INFINITY_BOUND = new Object();

    /**
     * package constructor
     */
    PostgreRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                     Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }


    public abstract MappingType subtype();


    public static <T> MockRangeFunction<T> createMockFunction(Class<?> javaType, Class<T> elementType) {
        return new MockRangeFunction<>(javaType, elementType);
    }

    /**
     * @param function <ul>
     *                 <li>argument of function possibly is notion 'infinity',see {@link #INFINITY}</li>
     *                 <li>function must return null when argument is notion 'infinity' and support it,see {@link #INFINITY}</li>
     *                 <li>function must throw {@link RuntimeException} when argument is notion 'infinity' and don't support it,see {@link #INFINITY}</li>
     *                 </ul>
     * @see <a href="https://www.postgresql.org/docs/current/rangetypes.html">Range Types</a>
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R parseNonEmptyRange(final String text, final int offset, final int end,
                                              final RangeFunction<T, R> rangeFunc, final Function<String, T> function)
            throws IllegalArgumentException {

        if (!(offset < end && end <= text.length())) {
            throw new IllegalArgumentException("offset or end error");
        }
        if (text.regionMatches(true, offset, EMPTY, 0, EMPTY.length())) {
            throw new IllegalArgumentException("range must non-empty.");
        }

        final Function<String, Object> functionDecoration;
        functionDecoration = s -> {
            Object value;
            value = function.apply(s.trim());
            if (value == null) {
                value = INFINITY_BOUND;
            }
            return value;
        };
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
                    lowerBound = functionDecoration.apply(text.substring(lowerIndex, i));
                } else {
                    inQuote = false;
                    assert upperBound == null && upperIndex > 0;
                    upperBound = functionDecoration.apply(text.substring(upperIndex, i));
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
                    lowerBound = functionDecoration.apply(text.substring(lowerIndex, i));
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
                    upperBound = functionDecoration.apply(text.substring(upperIndex, i));
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
        return rangeFunc.apply(includeLowerBound, (T) lowerBound, (T) upperBound, includeUpperBound);
    }

    /**
     * @param methodName public static factory method name,for example : com.my.Factory#create
     * @throws io.army.meta.MetaException throw when factory method name error.
     */
    @SuppressWarnings("unchecked")
    public static <T, R> RangeFunction<T, R> createRangeFunction(final Class<R> javaType, final Class<T> elementType,
                                                                 final String methodName) throws MetaException {

        final int colonIndex, methodIndex;
        colonIndex = methodName.lastIndexOf(_Constant.DOUBLE_COLON);
        if (colonIndex < 0) {
            methodIndex = -2;
        } else if (colonIndex < 1 || colonIndex + 2 >= methodName.length()) {
            String m = String.format("method name[%s] error", methodName);
            throw new MetaException(m);
        } else {
            methodIndex = colonIndex;
        }

        final RangeFunction<T, R> function;

        if (methodName.endsWith("::new")) {
            final Constructor<?> constructor;
            constructor = loadConstructor(javaType, methodName, colonIndex, elementType);
            function = (lower, includeLower, upper, includeUpper) -> {
                try {
                    final Object result;
                    result = constructor.newInstance(includeLower, lower, upper, includeUpper);
                    return (R) result;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            final Method method;
            method = loadFactoryMethod(javaType, methodName, methodIndex, elementType);
            function = (lower, includeLower, upper, includeUpper) -> {
                try {
                    final Object result;
                    result = method.invoke(null, includeLower, lower, upper, includeUpper);
                    if (result == null) {
                        throw new NullPointerException();
                    }
                    return (R) result;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }

        return function;
    }

    @SuppressWarnings("unchecked")
    public static <T> T emptyRange(final Class<T> javaType) {
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
        return (T) empty;
    }


    @Nullable
    static <T, R> RangeFunction<T, R> tryCreateDefaultRangeFunc(final Class<R> targetType, final Class<T> elementType) {
        RangeFunction<T, R> rangeFunc;
        try {
            rangeFunc = createRangeFunction(targetType, elementType, CREATE);
        } catch (Throwable e) {
            rangeFunc = null;
        }
        return rangeFunc;
    }

    /**
     * @throws IllegalArgumentException            when rangeFunc is null and {@link MappingType#javaType()} isn't {@link String#getClass()}
     * @throws CriteriaException                   when text error and handler throw this type.
     * @throws io.army.session.DataAccessException when text error and handler throw this type.
     */
    @SuppressWarnings("unchecked")
    static <T, R> R parseRange(final String text, final @Nullable RangeFunction<T, R> rangeFunc,
                               final Function<String, T> parseFunc, final SqlType sqlType,
                               final MappingType type, final ErrorHandler handler) {
        final Class<?> javaType;
        javaType = type.javaType();
        final Object value;
        if (rangeFunc == null) {
            if (javaType != String.class) {
                String m = String.format("function is null,but %s.javaType() isn't %s",
                        MappingType.class.getName(), String.class.getName());
                throw new IllegalArgumentException(m);
            }
            value = text;
        } else if (EMPTY.equalsIgnoreCase(text)) {
            value = emptyRange(javaType);
        } else {
            try {
                value = parseNonEmptyRange(text, 0, text.length(), rangeFunc, parseFunc);
            } catch (Throwable e) {
                throw handler.apply(type, sqlType, text, e);
            }
        }
        return (R) value;
    }

    @SuppressWarnings("unchecked")
    static <T> void rangeToText(final Object nonNull, final BiConsumer<T, Consumer<String>> consumer,
                                final MappingType type, final Consumer<String> appendConsumer) {
        if (nonNull instanceof ArmyPostgreRange) {
            final ArmyPostgreRange<T> range = (ArmyPostgreRange<T>) nonNull;
            if (range.isEmpty()) {
                appendConsumer.accept(EMPTY);
            } else {
                if (range.isIncludeLowerBound()) {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_PAREN));
                }
                final T lowerBound, upperBound;
                lowerBound = range.getLowerBound();
                upperBound = range.getUpperBound();

                if (lowerBound != null) {
                    consumer.accept(lowerBound, appendConsumer);
                }
                appendConsumer.accept(String.valueOf(_Constant.COMMA));
                if (upperBound != null) {
                    consumer.accept(upperBound, appendConsumer);
                }

                if (range.isIncludeUpperBound()) {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_PAREN));
                }
            }
        } else {
            final MockRangeFunction<T> mockFunction;
            if (type instanceof PostgreSingleRangeType) {
                mockFunction = ((PostgreSingleRangeType<T>) type).mockFunction;
                assert mockFunction != null;
            } else if (type instanceof UserDefinedRangeType) {
                mockFunction = ((UserDefinedRangeType<T>) type).mockFunction();
            } else {
                String m = String.format("either %s is %s type or %s is %s type.",
                        type.javaType().getName(),
                        ArmyPostgreRange.class.getName(),
                        type,
                        UserDefinedRangeType.class.getName()
                );
                throw new IllegalArgumentException(m);
            }
            if (mockFunction.isEmpty.apply(nonNull)) {
                appendConsumer.accept(EMPTY);
            } else {
                if (mockFunction.isIncludeLowerBound.apply(nonNull)) {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.LEFT_PAREN));
                }
                final T lowerBound, upperBound;
                lowerBound = mockFunction.getLowerBound.apply(nonNull);
                upperBound = mockFunction.getUpperBound.apply(nonNull);

                if (lowerBound != null) {
                    consumer.accept(lowerBound, appendConsumer);
                }
                appendConsumer.accept(String.valueOf(_Constant.COMMA));
                if (upperBound != null) {
                    consumer.accept(upperBound, appendConsumer);
                }

                if (mockFunction.isIncludeUpperBound.apply(nonNull)) {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_SQUARE_BRACKET));
                } else {
                    appendConsumer.accept(String.valueOf(_Constant.RIGHT_PAREN));
                }
            }

        }

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

    private static Constructor<?> loadConstructor(final Class<?> javaType, final String methodName, final int colonIndex,
                                                  final Class<?> elementType) throws MetaException {

        if (!javaType.getName().equals(methodName.substring(0, colonIndex))) {
            String m = String.format("%s isn't the constructor of %s", methodName, javaType.getName());
            throw new MetaException(m);
        }
        try {
            return javaType.getConstructor(boolean.class, elementType, elementType, boolean.class);
        } catch (NoSuchMethodException e) {
            String m = String.format("constructor[%s] error", methodName);
            throw new MetaException(m, e);
        }
    }

    /**
     * @param colonIndex -2 or positive
     */
    private static Method loadFactoryMethod(final Class<?> javaType, final String methodName, final int colonIndex,
                                            final Class<?> elementType) {
        try {
            final Class<?> classOfMethod;
            final String name;
            if (colonIndex == -2) {
                classOfMethod = javaType;
                name = methodName;
            } else {
                classOfMethod = Class.forName(methodName.substring(0, colonIndex));
                name = methodName.substring(colonIndex + 2);
            }
            final Method method;
            method = classOfMethod.getMethod(name, boolean.class, elementType, elementType, boolean.class);
            final int modifier;
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

    private static IllegalArgumentException nearbyError(String text) {
        return new IllegalArgumentException(String.format("'%s' nearby error.", text));
    }


    public interface RangeType {

        /**
         * @return the {@link MappingType} of subtype of range.
         */
        MappingType subtype();
    }


    public interface SingleRangeType extends RangeType {

        MappingType multiRangeType();

    }

    public interface MultiRangeType extends RangeType {

        MappingType rangeType();
    }


    public interface UserDefinedRangeType<T> extends RangeType {

        /**
         * @throws UnsupportedOperationException when {@link MappingType#javaType()} is {@link ArmyPostgreRange} type.
         * @see #createMockFunction(Class, Class)
         */
        MockRangeFunction<T> mockFunction();


    }


    public static final class MockRangeFunction<T> {

        private final Function<Object, Boolean> isEmpty;

        private final Function<Object, Boolean> isIncludeLowerBound;

        private final Function<Object, T> getLowerBound;

        private final Function<Object, T> getUpperBound;

        private final Function<Object, Boolean> isIncludeUpperBound;

        /**
         * private constructor
         */
        private MockRangeFunction(Class<?> javaType, Class<T> elementType) {
            this.isEmpty = rangeBeanFunc(javaType, "isEmpty", Boolean.TYPE);

            this.isIncludeLowerBound = rangeBeanFunc(javaType, "isIncludeLowerBound", Boolean.TYPE);
            this.getLowerBound = rangeBeanFunc(javaType, "getLowerBound", elementType);
            this.getUpperBound = rangeBeanFunc(javaType, "getUpperBound", elementType);
            this.isIncludeUpperBound = rangeBeanFunc(javaType, "isIncludeUpperBound", Boolean.TYPE);

        }

    }//MockRangeFunction


}
