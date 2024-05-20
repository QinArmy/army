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

package io.army.util;


import io.army.dialect.impl._Constant;
import io.army.modelgen._MetaBridge;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Locale;
import java.util.Map;

public abstract class _StringUtils {

    protected _StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isEmpty(@Nullable Object str) {
        return str == null || str.equals("");
    }

    public static boolean hasLength(@Nullable CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasText(@Nullable CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    @Nullable
    public static String toLowerCaseIfNonNull(final @Nullable String text) {
        return text == null ? null : text.toLowerCase(Locale.ROOT);
    }

    @Nullable
    public static String toUpperCase(final @Nullable String text) {
        return text == null ? null : text.toUpperCase(Locale.ROOT);
    }

    public static String camelToUpperCase(String camel) {
        return _MetaBridge.camelToUpperCase(camel);
    }


    public static String camelToLowerCase(String camel) {
        return _MetaBridge.camelToLowerCase(camel);
    }

    public static boolean isWhitespace(final String text, final int offset, final int end) {
        boolean match = offset < end;
        for (int i = offset; i < end; i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                match = false;
                break;
            }
        }
        return match;
    }

    public static boolean isBinary(String text) {
        final char[] array = text.toCharArray();
        boolean match = array.length > 0;
        for (char c : array) {
            if (c != '0' && c != '1') {
                match = false;
                break;
            }
        }
        return match;
    }


    public static StringBuilder builder() {
        return new StringBuilder();
    }

    public static StringBuilder builder(int capacity) {
        return new StringBuilder(capacity);
    }

    public static String enumToString(final Enum<?> words) {
        Class<?> clazz;
        clazz = words.getClass();
        if (clazz.isAnonymousClass()) {
            clazz = clazz.getSuperclass();
        }
        return builder()
                .append(clazz.getName())
                .append(_Constant.PERIOD)
                .append(words.name())
                .toString();
    }

    /**
     * @return a unmodified map
     */
    @SuppressWarnings("all")
    public static Map<String, Boolean> whiteMap(final @Nullable String whitelist) {
        if (whitelist == null) {
            return _Collections.emptyMap();
        }
        final String[] array;
        array = whitelist.split(",");
        final Map<String, Boolean> map;
        switch (array.length) {
            case 0:
                map = _Collections.emptyMap();
                break;
            case 1:
                map = _Collections.singletonMap(array[0].trim(), Boolean.TRUE);
                break;
            default: {
                final Map<String, Boolean> temp = _Collections.hashMap((int) (array.length / 0.75f));
                for (int i = 0; i < array.length; i++) {
                    temp.put(array[i].trim(), Boolean.TRUE);
                }
                map = _Collections.unmodifiableMap(temp);
            }
        }
        return map;
    }


    /**
     * @see #bitStringToBitSet(String, boolean)
     */
    public static String bitSetToBitString(final BitSet bitSet, final boolean bitEndian) {
        final int length = bitSet.length();
        final char[] bitChars = new char[length];
        if (bitEndian) {
            for (int i = 0, bitIndex = length - 1; i < length; i++, bitIndex--) {
                if (bitSet.get(bitIndex)) {
                    bitChars[i] = '1';
                } else {
                    bitChars[i] = '0';
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (bitSet.get(i)) {
                    bitChars[i] = '1';
                } else {
                    bitChars[i] = '0';
                }
            }
        }
        return new String(bitChars);
    }

    /**
     * @throws IllegalArgumentException when bitString isn't bit string.
     * @see #bitSetToBitString(BitSet, boolean)
     */
    public static BitSet bitStringToBitSet(final String bitString, final boolean bitEndian)
            throws IllegalArgumentException {
        final int length;
        length = bitString.length();
        final BitSet bitSet = new BitSet(length);
        char ch;
        if (bitEndian) {
            for (int i = 0, bitIndex = length - 1; i < length; i++, bitIndex--) {
                ch = bitString.charAt(i);
                if (ch == '1') {
                    bitSet.set(bitIndex, true);
                } else if (ch != '0') {
                    throw new IllegalArgumentException(String.format("[%s] isn't bit string.", bitString));
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                ch = bitString.charAt(i);
                if (ch == '1') {
                    bitSet.set(i, true);
                } else if (ch != '0') {
                    throw new IllegalArgumentException(String.format("[%s] isn't bit string.", bitString));
                }
            }
        }
        return bitSet;
    }

    public static BitSet bitStringToBitSet(final String bitStr, final int offset, final int end) throws IllegalArgumentException {
        final int bitLength;
        bitLength = end - offset;
        if (bitLength < 1 || bitStr.length() < end) {
            throw new IllegalArgumentException("bit string must non-empty.");
        }
        final BitSet bitSet = new BitSet(bitLength);
        final int maxIndex = end - 1;
        for (int i = offset; i < end; i++) {
            switch (bitStr.charAt(maxIndex - i)) {
                case '0':
                    bitSet.set(i, false);
                    break;
                case '1':
                    bitSet.set(i, true);
                    break;
                default:
                    throw new IllegalArgumentException("non-bit string");
            }
        }
        return bitSet;
    }



    /*################################## private method #############################################*/

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


}
