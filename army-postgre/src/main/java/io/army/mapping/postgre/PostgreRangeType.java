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
        } else if (EMPTY.equalsIgnoreCase((String) nonNull)) {
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

    final <T> Object textToRange(final String text, final int offset, final _RangeFunction<T, ?> rangeFunc,
                                 final Function<String, T> function, final SqlType type, final ErrorHandler handler) {
        final int length = text.length();
        assert offset < length;
        Boolean includeLowerBound = null, includeUpperBound = null;
        T lowerBound = null, upperBound = null;
        boolean inQuote = false;
        char ch;
        for (int i = offset, statIndex = -1, nextIndex; i < length; i++) {
            ch = text.charAt(i);
            if (includeLowerBound == null) {
                if (ch == _Constant.LEFT_SQUARE_BRACKET) {
                    includeLowerBound = Boolean.TRUE;
                } else if (ch == _Constant.LEFT_PAREN) {
                    includeLowerBound = Boolean.FALSE;
                } else if (!Character.isWhitespace(ch)) {
                    throw handler.apply(this, type, text);
                }
            } else if (statIndex < 0) {
                if (ch == _Constant.DOUBLE_QUOTE) {
                    inQuote = true;
                    statIndex = i;
                } else if (!Character.isWhitespace(ch)) {
                    statIndex = i;
                }
            } else if (inQuote) {
                if (ch == _Constant.DOUBLE_QUOTE) {
                    nextIndex = i + 1;
                    inQuote = nextIndex < length && text.charAt(nextIndex) == _Constant.DOUBLE_QUOTE;
                }
            } else if (lowerBound == null) {
                if (ch == _Constant.COMMA || Character.isWhitespace(ch)) {
                    try {
                        lowerBound = function.apply(text.substring(statIndex, i));
                    } catch (Exception e) {
                        throw handler.apply(this, type, text);
                    }
                    statIndex = -1;
                }
            } else if (includeUpperBound == null) {
                if (ch == _Constant.LEFT_SQUARE_BRACKET) {
                    includeUpperBound = Boolean.TRUE;
                } else if (ch == _Constant.LEFT_PAREN) {
                    includeUpperBound = Boolean.FALSE;
                } else if (!Character.isWhitespace(ch)) {
                    throw handler.apply(this, type, text);
                }
                if (upperBound != null) {
                    continue;
                }
                try {
                    upperBound = function.apply(text.substring(statIndex, i));
                } catch (Exception e) {
                    throw handler.apply(this, type, text);
                }
            } else {
                break;
            }


        }// for

        if (lowerBound == null || includeLowerBound == null || upperBound == null || includeUpperBound) {
            throw handler.apply(this, type, text);
        }
        return rangeFunc.apply(lowerBound, includeLowerBound, upperBound, includeUpperBound);
    }

    /**
     * @param methodName public static factory method name,for example : com.my.Factory#create
     * @throws io.army.meta.MetaException throw when factory method name error.
     */
    static <T> _RangeFunction<T, ?> createRangeFunction(final Class<?> javaType, final Class<T> elementType,
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

        if (!(nonNull instanceof ArmyPostgreRange)) {
            rangeToTextByReflection(nonNull, rangeType, function, builder);
        } else if (((ArmyPostgreRange) nonNull).isEmpty()) {
            builder.append(EMPTY);
        } else {
            if (((ArmyPostgreRange) nonNull).isIncludeLowerBound()) {
                builder.append(_Constant.LEFT_SQUARE_BRACKET);
            } else {
                builder.append(_Constant.LEFT_PAREN);
            }

            final Object lowerBound, upperBound;
            if (nonNull instanceof ArmyPostgreObjectRange) {
                final ArmyPostgreObjectRange<?> range = (ArmyPostgreObjectRange<?>) nonNull;
                lowerBound = range.getLowerBound();
                upperBound = range.getUpperBound();
            } else if (nonNull instanceof ArmyPostgreInt4Range) {
                final ArmyPostgreInt4Range range = (ArmyPostgreInt4Range) nonNull;
                lowerBound = range.getLowerBound();
                upperBound = range.getUpperBound();
            } else if (nonNull instanceof ArmyPostgreInt8Range) {
                final ArmyPostgreInt8Range range = (ArmyPostgreInt8Range) nonNull;
                lowerBound = range.getLowerBound();
                upperBound = range.getUpperBound();
            } else {
                String m = String.format("unknown %s type.", ArmyPostgreRange.class.getName());
                throw new MetaException(m);
            }

            builder.append(function.apply((T) lowerBound))
                    .append(_Constant.COMMA)
                    .append(function.apply((T) upperBound));

            if (((ArmyPostgreRange) nonNull).isIncludeUpperBound()) {
                builder.append(_Constant.LEFT_SQUARE_BRACKET);
            } else {
                builder.append(_Constant.LEFT_PAREN);
            }
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


}
