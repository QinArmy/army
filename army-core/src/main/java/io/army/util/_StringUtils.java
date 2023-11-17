package io.army.util;


import io.army.dialect._Constant;
import io.army.modelgen._MetaBridge;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Locale;
import java.util.Map;

public abstract class _StringUtils extends io.qinarmy.util.StringUtils {

    protected _StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static String toLowerCase(String text) {
        return text == null ? null : text.toLowerCase(Locale.ROOT);
    }

    public static String toUpperCase(String text) {
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



    /*################################## private method #############################################*/

}
