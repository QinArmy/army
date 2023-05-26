package io.army.mapping.postgre;

import io.army.dialect._Constant;
import io.army.util._Collections;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Map;

public abstract class PostgreArrayParsers {

    private PostgreArrayParsers() {
        throw new UnsupportedOperationException();
    }

    public interface ElementFunction {
        Object apply(String text, int offset, int end);
    }

    private static final Map<Class<?>, Integer> EMPTY_LENGTHS = Collections.emptyMap();

    /**
     * <p>
     * parse postgre array text.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/15/arrays.html#ARRAYS-IO">Array Input and Output Syntax</a>
     */
    static Object parseArrayText(final Class<?> javaType, final String text, final char delimiter,
                                 final ElementFunction function) throws IllegalArgumentException {
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

        final Map<Class<?>, Integer> lengthMap;
        if (text.charAt(offset) == _Constant.LEFT_SQUARE_BRACKET) {
            final int index;
            index = text.indexOf('=', offset);
            if (index < offset || index >= length) {
                throw new IllegalArgumentException("postgre array format error.");
            }
            lengthMap = parseArrayLengths(text, offset, index);
            offset = index + 1;
        } else {
            lengthMap = EMPTY_LENGTHS;
        }
        return parseArray(javaType, text, offset, length, delimiter, lengthMap, function);
    }


    static int parseArrayLength(final String text, final int offset, final int end)
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

    static Map<Class<?>, Integer> parseArrayLengths(final String text, final int offset, final int end) {
        boolean inBracket = false;

        final Map<Class<?>, Integer> map = _Collections.hashMap();
        char ch;
        for (int i = offset; i < end; i++) {
            ch = text.charAt(i);
            if (!inBracket) {
                if (ch == _Constant.LEFT_SQUARE_BRACKET) {
                    inBracket = true;
                } else if (!Character.isWhitespace(ch)) {
                    throw isNotWhitespaceError(i);
                }
                continue;
            } else if (Character.isWhitespace(ch)) {
                continue;
            }


        }//for
        return _Collections.unmodifiableMap(map);
    }


    /**
     * <p>
     * parse postgre array text.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/15/arrays.html#ARRAYS-IO">Array Input and Output Syntax</a>
     */
    private static Object parseArray(final Class<?> javaType, final String text, final int offset, final int end,
                                     final char delimiter, Map<Class<?>, Integer> lengthMap,
                                     final ElementFunction function) {
        assert offset >= 0 && offset < end;

        final int arrayLength;
        if (lengthMap == EMPTY_LENGTHS) {
            arrayLength = parseArrayLength(text, offset, end);
        } else {
            arrayLength = lengthMap.get(javaType);
        }
        final Class<?> componentType;
        componentType = javaType.getComponentType();
        final Object array;
        array = Array.newInstance(componentType, arrayLength);
        if (arrayLength == 0) {
            return array;
        }
        final boolean oneDimension = !componentType.isArray();

        Object elementValue;
        boolean leftBrace = true, inBrace = false, inQuote = false, inElementBrace = false, arrayEnd = false;
        char ch;
        for (int i = offset, startIndex = -1, arrayIndex = 0; i < end; i++) {
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
                    if (oneDimension) {
                        assert startIndex > 0;
                        Array.set(array, arrayIndex++, function.apply(text, startIndex, i));
                        startIndex = -1;
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
                        elementValue = parseArray(componentType, text, startIndex, i + 1, delimiter, lengthMap, function);
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
            } else if (ch == delimiter || ch == _Constant.RIGHT_BRACE || Character.isWhitespace(ch)) {
                if (startIndex > 0) {
                    assert oneDimension;
                    Array.set(array, arrayIndex++, function.apply(text, startIndex, i));
                    startIndex = -1;
                }
                if (ch == _Constant.RIGHT_BRACE) {
                    arrayEnd = true;
                    break;
                }
            } else if (!oneDimension) {
                String m = String.format("postgre array isn't multi-dimension array after offset[%s]", i);
                throw new IllegalArgumentException(m);
            } else if (startIndex < 0) {
                startIndex = i;
            }

        }//for

        if (leftBrace) {
            throw noLeftBrace(offset);
        } else if (!arrayEnd) {
            throw noRightBrace(end);
        }
        return array;
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
