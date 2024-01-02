/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.mapping.postgre;

import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.meta.MetaException;
import io.army.sqltype.PostgreType;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link PostgreSingleRangeType}</li>
 *         <li>{@link PostgreMultiRangeType}</li>
 *     </ul>
*
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 */
public abstract class PostgreRangeType extends _ArmyPostgreRangeType {

    public static final String INFINITY = "infinity";
    public static final String EMPTY = "empty";
    private static final Object INFINITY_BOUND = new Object();


    PostgreRangeType(final PostgreType sqlType, final Class<?> javaType, final @Nullable RangeFunction<?, ?> rangeFunc) {
        super(sqlType, javaType, rangeFunc);
    }


    public final MappingType subtype() {
        final MappingType type;
        switch (this.dataType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
                type = IntegerType.INSTANCE;
                break;
            case INT8RANGE:
            case INT8MULTIRANGE:
                type = LongType.INSTANCE;
                break;
            case NUMRANGE:
            case NUMMULTIRANGE:
                type = BigDecimalType.INSTANCE;
                break;
            case DATERANGE:
            case DATEMULTIRANGE:
                type = LocalDateType.INSTANCE;
                break;
            case TSRANGE:
            case TSMULTIRANGE:
                type = LocalDateTimeType.INSTANCE;
                break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
                type = OffsetDateTimeType.INSTANCE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.dataType);
        }
        return type;
    }


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

         final Function<Object, Boolean> isEmpty;

        final Function<Object, Boolean> isIncludeLowerBound;

        final Function<Object, T> getLowerBound;

        final Function<Object, T> getUpperBound;

        final Function<Object, Boolean> isIncludeUpperBound;

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
