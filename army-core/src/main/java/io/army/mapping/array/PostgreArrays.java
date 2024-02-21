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

package io.army.mapping.array;

import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.mapping.MappingType;
import io.army.mapping.UnaryGenericsMapping;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.ImmutableSpec;
import io.army.util.*;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class PostgreArrays extends ArrayMappings {

    private PostgreArrays() {
    }


    public static byte[] parseBytea(final String text, int offset, final int end) {
        if (!text.startsWith("0x", offset)) {
            throw new IllegalArgumentException("not start with 0x");
        }
        offset += 2;

        final byte[] bytea;
        bytea = text.substring(offset, end).getBytes(StandardCharsets.UTF_8);
        return HexUtils.decodeHex(bytea, 0, bytea.length);
    }

    /**
     * decode array element
     *
     * @see #encodeElement(String, StringBuilder)
     * @see <a href="https://www.postgresql.org/docs/current/arrays.html#ARRAYS-IO">Array Input and Output Syntax</a>
     */
    public static String decodeElement(final String text, int offset, int end) {
        final boolean enclose;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            if (text.charAt(end - 1) != _Constant.DOUBLE_QUOTE) {
                throw new IllegalArgumentException("postgre array format error");
            }
            offset++;
            end--;
            enclose = true;
        } else {
            enclose = false;
        }

        char ch;
        StringBuilder builder = null;
        int lastWritten = offset;
        for (int i = offset; i < end; i++) {
            ch = text.charAt(i);

            if (ch != _Constant.BACK_SLASH) {
                continue;
            }

            if (builder == null) {
                builder = new StringBuilder((end - offset) + 10);
            }

            if (i > lastWritten) {
                builder.append(text, lastWritten, i);
            }

            i++;  // skip current char
            lastWritten = i;

        }

        if (builder != null && lastWritten < end) {
            builder.append(text, lastWritten, end);
        }

        final String elementText;
        if (builder != null) {
            elementText = builder.toString();
        } else if (enclose) {
            elementText = text.substring(offset, end);
        } else {
            elementText = text;
        }
        return elementText;
    }

    /**
     * escape array element
     *
     * @see #decodeElement(String, int, int)
     * @see <a href="https://www.postgresql.org/docs/current/arrays.html#ARRAYS-IO">Array Input and Output Syntax</a>
     */
    public static void encodeElement(final String element, final StringBuilder builder) {

        builder.append(_Constant.DOUBLE_QUOTE); // left doubleQuote

        final int length = element.length();
        int lastWritten = 0;
        char ch;
        for (int i = 0; i < length; i++) {
            ch = element.charAt(i);

            if (ch == _Constant.BACK_SLASH || ch == _Constant.DOUBLE_QUOTE) {
                if (i > lastWritten) {
                    builder.append(element, lastWritten, i);
                }
                builder.append(_Constant.BACK_SLASH);
                lastWritten = i; // not i + 1 as current char wasn't written
            }
        }

        if (lastWritten < length) {
            builder.append(element, lastWritten, length);
        }

        builder.append(_Constant.DOUBLE_QUOTE);// right doubleQuote
    }


    public static String arrayBeforeBind(final Object source, final BiConsumer<Object, StringBuilder> consumer,
                                         final DataType dataType, final MappingType type,
                                         final ErrorHandler handler) {

        if (source instanceof String) {
            final String text;
            final int length;
            if ((length = (text = ((String) source).trim()).length()) < 2) {
                throw handler.apply(type, dataType, source, null);
            } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
                throw handler.apply(type, dataType, source, null);
            } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
                throw handler.apply(type, dataType, source, null);
            }
            return (String) source;
        }

        if (source instanceof List) {
            final StringBuilder builder = new StringBuilder();
            try {
                listToArrayText((List<?>) source, consumer, builder);
            } catch (Exception e) {
                throw handler.apply(type, dataType, source, e);
            }
            return builder.toString();
        }


        final Class<?> javaType = type.javaType(), sourceType = source.getClass();
        if (!sourceType.isArray()) {
            throw handler.apply(type, dataType, source, null);
        }

        final Class<?> sourceComponentType;
        sourceComponentType = ArrayUtils.underlyingComponent(sourceType);
        if (javaType == Object.class) { // unlimited dimension array
            if (sourceComponentType != String.class
                    && sourceComponentType != ((MappingType.SqlArrayType) type).underlyingJavaType()) {
                throw handler.apply(type, dataType, source, null);
            }
        } else if (sourceComponentType == String.class) {
            if (ArrayUtils.dimensionOf(sourceType) != ArrayUtils.dimensionOf(javaType)) {
                throw handler.apply(type, dataType, source, null);
            }
        } else if (!javaType.isInstance(source)
                && !ClassUtils.isWrapperClass(((MappingType.SqlArrayType) type).underlyingJavaType(), sourceComponentType)) {
            throw handler.apply(type, dataType, source, null);
        }

        try {
            final BiConsumer<Object, StringBuilder> actualConsumer;
            if (sourceComponentType == String.class) {
                actualConsumer = TextArrayType::appendToText;
            } else {
                actualConsumer = consumer;
            }
            return PostgreArrays.toArrayText(source, actualConsumer, new StringBuilder())
                    .toString();
        } catch (Exception e) {
            throw handler.apply(type, dataType, source, e);
        }
    }

    public static Object arrayAfterGet(MappingType type, DataType dataType, final Object source,
                                       final boolean nonNull, final TextFunction<?> elementFunc, ErrorHandler errorHandler) {
        final Object value;
        final Class<?> javaType = type.javaType(), sourceType = source.getClass();
        if (source instanceof String) {
            final char delimiter;
            if (dataType == PostgreType.BOX_ARRAY) {
                delimiter = ';';
            } else {
                delimiter = _Constant.COMMA;
            }
            try {
                value = PostgreArrays.parseArray((String) source, nonNull, elementFunc, delimiter,
                        dataType, type, errorHandler);
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (!sourceType.isArray()) {
            throw errorHandler.apply(type, dataType, source, null);
        } else if (javaType == Object.class) { // unlimited dimension array
            final Class<?> underlyingJavaType, temp;
            underlyingJavaType = ((MappingType.SqlArrayType) type).underlyingJavaType();

            if (underlyingJavaType.isArray()) {
                temp = ArrayUtils.underlyingComponent(underlyingJavaType);
            } else {
                temp = underlyingJavaType;
            }
            if (ArrayUtils.underlyingComponent(sourceType) != temp) {
                throw errorHandler.apply(type, dataType, source, null);
            } else if (ArrayUtils.dimensionOf(sourceType) <= ArrayUtils.dimensionOf(underlyingJavaType)) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = source;
        } else if (javaType.isInstance(source)) {
            value = source;
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }

    public static StringBuilder toArrayText(final Object array, final BiConsumer<Object, StringBuilder> consumer,
                                            final StringBuilder builder)
            throws IllegalArgumentException {
        if (array instanceof List) {
            listToArrayText((List<?>) array, consumer, builder);
        } else if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("non-array");
        } else {
            arrayToArrayText(array, consumer, builder);
        }
        return builder;
    }

    public static StringBuilder byteaArrayToText(final MappingType type, final DataType dataType, final Object source,
                                                 final StringBuilder builder, final ErrorHandler errorHandler) {
        final Class<?> sourceClass = source.getClass();
        final int dimension;
        if (!sourceClass.isArray()
                || ArrayUtils.underlyingComponent(sourceClass) != byte.class
                || (dimension = ArrayUtils.dimensionOf(sourceClass) - 1) < 1) {
            throw errorHandler.apply(type, dataType, source, null);
        }

        _byteaArrayToText(type, dataType, source, dimension, builder, errorHandler);
        return builder;
    }


    /**
     * TODO handle UNLIMITED array
     *
     * @param nonNull     true : element of one dimension array non-null
     * @param elementFunc <ul>
     *                    <li>offset is non-whitespace,non-whitespace before end</li>
     *                    <li>no notation <strong>null</strong>,because have handled</li>
     *                    </ul>
     */
    public static Object parseArray(final String text, final boolean nonNull, final TextFunction<?> elementFunc,
                                    final char delimiter, final DataType dataType, final MappingType type,
                                    final ErrorHandler handler) throws IllegalArgumentException {

        if (!(type instanceof MappingType.SqlArrayType)) {
            throw _Exceptions.notArrayMappingType(type);
        }


        final Object array, value;
        try {
            final Class<?> arrayJavaType;
            arrayJavaType = javaTypeOfArray(type, text, 0, text.length());

            array = PostgreArrays.parseArrayText(arrayJavaType, text, nonNull, delimiter, elementFunc);
        } catch (Throwable e) {
            throw handler.apply(type, dataType, text, e);
        }
        if (type instanceof UnaryGenericsMapping.ListMapping) {
            value = PostgreArrays.linearToList(array,
                    ((UnaryGenericsMapping.ListMapping<?>) type).listConstructor()
            );
        } else if (type.javaType().isInstance(array)) {
            value = array;
        } else {
            String m = String.format("%s return value and %s not match.", TextFunction.class.getName(), type);
            throw handler.apply(type, dataType, text, new IllegalArgumentException(m));
        }
        return value;
    }

    public static Object parseMultiRange(final String text, final TextFunction<?> elementFunc, final DataType dataType,
                                         final MappingType type, final ErrorHandler handler)
            throws IllegalArgumentException {
        final Class<?> arrayJavaType;
        if (type instanceof UnaryGenericsMapping.ListMapping) {
            final Class<?> elementType;
            elementType = ((UnaryGenericsMapping.ListMapping<?>) type).genericsType();
            arrayJavaType = ArrayUtils.arrayClassOf(elementType);
        } else {
            arrayJavaType = type.javaType();
            if (!arrayJavaType.isArray()) {
                throw notArrayJavaType(type);
            }
        }
        final Object array, value;
        try {
            final int dimension;
            dimension = dimensionOfArray(text, 0, text.length());

            array = PostgreArrays._parseArray(arrayJavaType, text, true, 0, text.length(),
                    _Constant.COMMA, dimension, 1, true, elementFunc);
        } catch (Throwable e) {
            throw handler.apply(type, dataType, text, e);
        }
        if (type instanceof UnaryGenericsMapping.ListMapping) {
            value = PostgreArrays.linearToList(array,
                    ((UnaryGenericsMapping.ListMapping<?>) type).listConstructor()
            );
        } else if (type.javaType().isInstance(array)) {
            value = array;
        } else {
            String m = String.format("%s return value and %s not match.", TextFunction.class.getName(), type);
            throw handler.apply(type, dataType, text, new IllegalArgumentException(m));
        }
        return value;
    }


    public static int parseArrayLength(final String text, final int offset, final int end)
            throws IllegalArgumentException {
        char ch;
        boolean leftBrace = true, inBrace = false, inQuote = false, arrayEnd = false;

        int length = 0;
        for (int i = offset, itemCount = 0; i < end; i++) {
            ch = text.charAt(i);
            if (leftBrace) {
                if (ch == _Constant.LEFT_BRACE) {
                    leftBrace = false;
                } else if (!Character.isWhitespace(ch)) {
                    throw isNotWhitespaceError(i);
                }
            } else if (inQuote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                } else if (ch == _Constant.DOUBLE_QUOTE) {
                    inQuote = false;
                }
            } else if (inBrace) {
                if (ch == _Constant.RIGHT_BRACE) {
                    inBrace = false;
                }
            } else if (ch == _Constant.LEFT_BRACE) {
                itemCount++;
                inBrace = true;
            } else if (ch == _Constant.DOUBLE_QUOTE) {
                itemCount++;
                inQuote = true;
            } else if (ch == _Constant.COMMA) {
                length++;
            } else if (ch == _Constant.RIGHT_BRACE) {
                if (itemCount > 0) {
                    length++;
                }
                arrayEnd = true;
                break;
            } else if (itemCount == 0 && !Character.isWhitespace(ch)) {
                itemCount++;
            }

        }
        if (leftBrace) {
            throw noLeftBrace(offset);
        } else if (!arrayEnd) {
            throw noRightBrace(end);
        }
        return length;
    }


    /**
     * <p>
     * parse postgre array text.
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/15/arrays.html#ARRAYS-IO">Array Input and Output Syntax</a>
     */
    static Object parseArrayText(final Class<?> javaType, final String text, final boolean nonNull, final char delimiter,
                                 final TextFunction<?> function) throws IllegalArgumentException {
        final int length;
        length = text.length();
        int offset = 0;
        for (; offset < length; offset++) {
            if (!Character.isWhitespace(text.charAt(offset))) {
                break;
            }
        }
        if (offset == length) {
            throw new IllegalArgumentException("no text");
        }

        if (text.charAt(offset) == _Constant.LEFT_SQUARE_BRACKET) {
            offset = text.indexOf('=', offset);
            if (offset < 0) {
                throw new IllegalArgumentException("postgre array meta error");
            }
            offset++;
        }

        final int classDimension, dimension;
        dimension = dimensionOfArray(text, offset, length);

        if ((classDimension = ArrayUtils.dimensionOf(javaType)) != dimension) {
            String m = String.format("%s dimension[%s] and postgre array dimension[%s] not match",
                    javaType.getName(), classDimension, dimension);
            throw new IllegalArgumentException(m);
        }
        return _parseArray(javaType, text, nonNull, offset, length, delimiter, dimension, 1, false, function);
    }


    @SuppressWarnings("unchecked")
    static <E> List<E> linearToList(final Object array, final Supplier<List<E>> supplier) {

        List<E> list;
        list = supplier.get();
        final int arrayLength;
        arrayLength = Array.getLength(array);
        for (int i = 0; i < arrayLength; i++) {
            list.add((E) Array.get(array, i));
        }
        if (list instanceof ImmutableSpec) {
            switch (arrayLength) {
                case 0:
                    list = _Collections.emptyList();
                    break;
                case 1:
                    list = _Collections.singletonList((E) Array.get(array, 0));
                    break;
                default:
                    list = _Collections.unmodifiableList(list);
            }
        }
        return list;
    }


    /**
     * <p>
     * parse postgre array text.
     * *
     *
     * @param function       before end index possibly with trailing whitespace.
     * @param dimensionIndex based one
     * @see <a href="https://www.postgresql.org/docs/15/arrays.html#ARRAYS-IO">Array Input and Output Syntax</a>
     */
    private static Object _parseArray(final Class<?> javaType, final String text, final boolean nonNull,
                                      final int offset, final int end, final char delimiter, final int dimension,
                                      final int dimensionIndex, final boolean doubleQuoteEscapes, final TextFunction<?> function) {
        assert offset >= 0 && offset < end;

        final int arrayLength;
        arrayLength = parseArrayLength(text, offset, end);

        final Class<?> componentType;
        componentType = javaType.getComponentType();
        final boolean oneDimension;
        oneDimension = dimensionIndex >= dimension;

        final Object array;
        array = Array.newInstance(componentType, arrayLength);
        if (arrayLength == 0) {
            return array;
        }


        Object elementValue;
        boolean leftBrace = true, inBrace = false, inQuote = false, inElementBrace = false, arrayEnd = false;
        char ch, startChar = _Constant.NUL_CHAR, preChar = _Constant.NUL_CHAR;
        for (int i = offset, startIndex = -1, arrayIndex = 0, tailIndex, nextIndex; i < end; i++) {
            ch = text.charAt(i);
            if (leftBrace) {
                if (ch == _Constant.LEFT_BRACE) {
                    leftBrace = false;
                } else if (!Character.isWhitespace(ch)) {
                    throw isNotWhitespaceError(i);
                }
            } else if (inQuote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                } else if (ch == _Constant.DOUBLE_QUOTE) {
                    if (doubleQuoteEscapes
                            && (nextIndex = i + 1) < end
                            && text.charAt(nextIndex) == _Constant.DOUBLE_QUOTE) {
                        i++;
                    } else {
                        inQuote = false;
                        if (oneDimension) {
                            assert startIndex > 0;
                            Array.set(array, arrayIndex++, function.apply(text, startIndex, i));
                            startIndex = -1;
                        }
                    }

                }
            } else if (inBrace) {
                if (ch == _Constant.LEFT_BRACE) {
                    inElementBrace = true;
                } else if (ch == _Constant.RIGHT_BRACE) {
                    if (inElementBrace) {
                        inElementBrace = false;
                    } else {
                        inBrace = false;
                        elementValue = _parseArray(componentType, text, nonNull, startIndex, i + 1, delimiter,
                                dimension, dimensionIndex + 1, doubleQuoteEscapes, function);
                        Array.set(array, arrayIndex++, elementValue);
                        startIndex = -1;
                    }
                }
            } else if (ch == _Constant.LEFT_BRACE) {
                if (oneDimension) {
                    String m = String.format("postgre array isn't one dimension array after offset[%s]", i);
                    throw new IllegalArgumentException(m);
                }
                assert startIndex < 0;
                startIndex = i;
                inBrace = true;
                inElementBrace = false;
            } else if (ch == _Constant.DOUBLE_QUOTE) {
                inQuote = true;
                if (oneDimension) {
                    assert startIndex < 0;
                    startIndex = i + 1;
                }
            } else if (ch == delimiter || ch == _Constant.RIGHT_BRACE) {
                if (startIndex > 0) {
                    assert oneDimension;
                    if ((startChar == 'n' || startChar == 'N')
                            && (tailIndex = startIndex + 4) <= i
                            && text.regionMatches(true, startIndex, _Constant.NULL, 0, 4)
                            && (tailIndex == i || _StringUtils.isWhitespace(text, tailIndex, i))) {
                        if (nonNull) {
                            throw new IllegalArgumentException("element must be non-null");
                        }
                        Array.set(array, arrayIndex++, null);
                    } else if (Character.isWhitespace(preChar)) {
                        for (tailIndex = i - 2; tailIndex > startIndex; tailIndex--) {
                            if (!Character.isWhitespace(text.charAt(tailIndex))) {
                                break;
                            }
                        }
                        Array.set(array, arrayIndex++, function.apply(text, startIndex, tailIndex + 1));
                    } else {
                        Array.set(array, arrayIndex++, function.apply(text, startIndex, i));
                    }
                    startIndex = -1;
                }
                if (ch == _Constant.RIGHT_BRACE) {
                    arrayEnd = true;
                    break;
                }
            } else if (startIndex < 0) {
                if (!Character.isWhitespace(ch)) {
                    if (!oneDimension) {
                        String m = String.format("postgre array isn't multi-dimension array after offset[%s]", i);
                        throw new IllegalArgumentException(m);
                    }
                    startChar = ch;
                    startIndex = i;
                }
            }

            preChar = ch;

        }//for

        if (leftBrace) {
            throw noLeftBrace(offset);
        } else if (!arrayEnd) {
            throw noRightBrace(end);
        }
        return array;
    }


    /**
     * @see #toArrayText(Object, BiConsumer, StringBuilder)
     */
    private static void arrayToArrayText(final Object array, final BiConsumer<Object, StringBuilder> consumer,
                                         final StringBuilder builder) {

        final int arrayLength;
        arrayLength = Array.getLength(array);
        final boolean multiDimension;
        multiDimension = array.getClass().getComponentType().isArray();
        Object element;
        builder.append(_Constant.LEFT_BRACE);
        for (int i = 0; i < arrayLength; i++) {
            if (i > 0) {
                builder.append(_Constant.COMMA);
            }
            element = Array.get(array, i);
            if (multiDimension) {
                if (element == null) {
                    throw new IllegalArgumentException("element array couldn't be null.");
                }
                arrayToArrayText(element, consumer, builder);
            } else if (element == null) {
                builder.append("null");
            } else {
                consumer.accept(element, builder);
            }

        }
        builder.append(_Constant.RIGHT_BRACE);
    }

    /**
     * @see #toArrayText(Object, BiConsumer, StringBuilder)
     */
    private static void listToArrayText(final List<?> list, final BiConsumer<Object, StringBuilder> consumer,
                                        final StringBuilder builder) {

        final int size;
        size = list.size();
        builder.append(_Constant.LEFT_BRACE);
        Object element;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.COMMA);
            }
            element = list.get(i);
            if (element == null) {
                builder.append(_Constant.NULL);
            } else {
                consumer.accept(element, builder);
            }
        }
        builder.append(_Constant.RIGHT_BRACE);
    }

    private static int dimensionOfArray(final String text, int offset, final int end) {

        char ch;
        for (; offset < end; offset++) {
            ch = text.charAt(offset);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch != _Constant.LEFT_SQUARE_BRACKET) {
                break;
            }
            offset = text.indexOf('=');
            if (offset < 0) {
                throw new IllegalArgumentException("postgre array meta error");
            }

            offset++;

            break;
        }
        if (offset == end) {
            throw new IllegalArgumentException("no text");
        }


        int dimension = 0;
        for (int i = offset; i < end; i++) {
            ch = text.charAt(i);
            if (ch == _Constant.LEFT_BRACE) {
                dimension++;
            } else if (!Character.isWhitespace(ch)) {
                break;
            }
        }

        if (dimension == 0) {
            throw new IllegalArgumentException("postgre array dimension is zero");
        }
        return dimension;
    }


    /**
     * @see #byteaArrayToText(MappingType, DataType, Object, StringBuilder, ErrorHandler)
     */
    private static void _byteaArrayToText(final MappingType type, final DataType dataType, final Object source,
                                          final int dimension, final StringBuilder builder,
                                          final ErrorHandler errorHandler) {


        final int length;
        length = Array.getLength(source);

        builder.append(_Constant.LEFT_BRACE);

        Object element;
        for (int i = 0; i < length; i++) {
            element = Array.get(source, i);

            if (i > 0) {
                builder.append(_Constant.COMMA);
            }

            if (element == null) {
                if (dimension > 1) {
                    final IllegalArgumentException e;
                    e = new IllegalArgumentException("multi-dimension must not null");
                    throw errorHandler.apply(type, dataType, source, e);
                }
                builder.append("null");
            } else if (dimension > 1) {
                _byteaArrayToText(type, dataType, element, dimension - 1, builder, errorHandler);
            } else {
                builder.append("0x")
                        .append(HexUtils.hexEscapesText(false, (byte[]) element));
            }

        } // for loop

        builder.append(_Constant.RIGHT_BRACE);

    }


    static int[] parseArrayLengthMap(final Class<?> javaType, final String text, final int offset,
                                     final int equalIndex) {
        final char colon = ':';
        final int dimension;
        dimension = dimensionOfArray(text, equalIndex + 1, text.length());

        if (javaType != Object.class && dimension != ArrayUtils.dimensionOf(javaType)) {
            throw boundDecorationNotMatch(javaType, text.substring(offset, equalIndex));
        }

        final int[] meta = new int[dimension];


        boolean inBracket = false, afterColon = false;
        char ch;
        for (int i = offset, lowerIndex = -1, upperIndex = -1, dimensionIndex = 0, lower = 0, length; i < equalIndex; i++) {
            ch = text.charAt(i);
            if (!inBracket) {
                if (ch == _Constant.LEFT_SQUARE_BRACKET) {
                    inBracket = true;
                } else if (!Character.isWhitespace(ch)) {
                    throw isNotWhitespaceError(i);
                }
            } else if (lowerIndex < 0) {
                if (!Character.isWhitespace(ch)) {
                    lowerIndex = i;
                }
            } else if (lowerIndex > 0) {
                if (ch == colon || Character.isWhitespace(ch)) {
                    lower = Integer.parseInt(text.substring(lowerIndex, i));
                    lowerIndex = 0;
                }
                if (ch == colon) {
                    afterColon = true;
                }
            } else if (!afterColon) {
                if (ch == colon) {
                    afterColon = true;
                } else if (!Character.isWhitespace(ch)) {
                    throw lengthOfDimensionError(text.substring(offset, equalIndex));
                }
            } else if (upperIndex < 0) {
                if (!Character.isWhitespace(ch)) {
                    upperIndex = i;
                }
            } else if (upperIndex == 0) {
                if (ch == _Constant.RIGHT_SQUARE_BRACKET) {
                    inBracket = false;
                    lowerIndex = upperIndex = -1;
                } else if (!Character.isWhitespace(ch)) {
                    throw lengthOfDimensionError(text.substring(offset, equalIndex));
                }
            } else if (ch == _Constant.RIGHT_SQUARE_BRACKET || Character.isWhitespace(ch)) {
                length = Integer.parseInt(text.substring(upperIndex, i)) - lower + 1;
                if (length < 0) {
                    throw lengthOfDimensionError(text.substring(offset, equalIndex));
                }
                upperIndex = 0;
                if (dimensionIndex == dimension) {
                    throw boundDecorationNotMatch(javaType, text.substring(offset, equalIndex));
                }
                meta[dimensionIndex++] = length;
                if (ch == _Constant.RIGHT_SQUARE_BRACKET) {
                    inBracket = false;
                    lowerIndex = upperIndex = -1;
                }
            }


        }//for

        if (inBracket) {
            throw lengthOfDimensionError(text.substring(offset, equalIndex));
        }

        return meta;
    }


    private static Class<?> javaTypeOfArray(final MappingType type, final String text, final int offset, final int end) {
        if (!(type instanceof MappingType.SqlArrayType)) {
            throw new IllegalArgumentException("not array");
        }

        if (type instanceof UnaryGenericsMapping.ListMapping) {
            final Class<?> elementType;
            elementType = ((UnaryGenericsMapping.ListMapping<?>) type).genericsType();
            return ArrayUtils.arrayClassOf(elementType);
        }

        final Class<?> arrayJavaType, underlyingJavaType, javaType;
        javaType = type.javaType();
        if (javaType == Object.class) {
            underlyingJavaType = ((MappingType.SqlArrayType) type).underlyingJavaType();
            final int dimension;
            dimension = dimensionOfArray(text, offset, end);
            arrayJavaType = ArrayUtils.arrayClassOf(underlyingJavaType, dimension);
        } else if (javaType.isArray()) {
            arrayJavaType = javaType;
        } else {
            throw notArrayJavaType(type);
        }
        return arrayJavaType;
    }


    private static IllegalArgumentException notArrayJavaType(MappingType type) {
        String m = String.format("%s java type isn't array.", type);
        return new IllegalArgumentException(m);
    }

    private static IllegalArgumentException boundDecorationNotMatch(Class<?> javaType, String decoration) {
        String m = String.format("postgre bound decoration %s and %s not match.", decoration, javaType.getName());
        return new IllegalArgumentException(m);
    }


    private static IllegalArgumentException lengthOfDimensionError(String lengths) {
        String m = String.format("postgre bound decoration %s error.", lengths);
        return new IllegalArgumentException(m);
    }


    private static IllegalArgumentException noRightBrace(int end) {
        return new IllegalArgumentException(String.format("postgre array no right brace before offset[%s] nearby", end));
    }

    private static IllegalArgumentException noLeftBrace(int offset) {
        return new IllegalArgumentException(String.format("postgre array no left brace at offset[%s] nearby", offset));
    }

    private static IllegalArgumentException isNotWhitespaceError(int offset) {
        return new IllegalArgumentException(String.format("postgre array error at offset[%s]", offset));
    }


}
