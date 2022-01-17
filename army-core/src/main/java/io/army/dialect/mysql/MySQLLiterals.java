package io.army.dialect.mysql;

import io.army.dialect._Literals;
import io.army.sqltype.SqlType;
import io.army.util.StringUtils;
import io.army.util._Exceptions;
import io.qinarmy.util.BufferUtils;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Year;
import java.util.BitSet;

abstract class MySQLLiterals extends _Literals {


    static String text(final SqlType sqlType, final boolean hexEscapes, final Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        final String literal;
        if (hexEscapes) {
            final byte[] jsonBytes = ((String) nonNull).getBytes(StandardCharsets.UTF_8);
            final StringBuilder builder = new StringBuilder((jsonBytes.length << 1) + 10)
                    .append('X')
                    .append(QUOTE_CHAR)
                    .append(BufferUtils.hexEscapesText(true, jsonBytes, jsonBytes.length));
            literal = builder.append(QUOTE_CHAR)
                    .toString();
        } else {
            final char[] jsonChars = ((String) nonNull).toCharArray();
            final StringBuilder builder = new StringBuilder(jsonChars.length + 10)
                    .append(QUOTE_CHAR);
            textEscapes(builder, jsonChars, jsonChars.length);
            literal = builder
                    .append(QUOTE_CHAR)
                    .toString();
        }
        return literal;
    }

    static String binary(final SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        final byte[] array = (byte[]) nonNull;
        StringBuilder builder = new StringBuilder()
                .append('X')
                .append(QUOTE_CHAR)
                .append(BufferUtils.hexEscapesText(true, array, array.length));
        return builder.append(QUOTE_CHAR)
                .toString();
    }

    static String bit(final SqlType sqlType, final Object nonNull) {
        final String value;
        if (nonNull instanceof Long) {
            value = Long.toBinaryString((Long) nonNull);
        } else if (nonNull instanceof Integer) {
            value = Integer.toBinaryString((Integer) nonNull);
        } else if (nonNull instanceof Short) {
            value = Integer.toBinaryString(((Short) nonNull) & 0xFFFF);
        } else if (nonNull instanceof Byte) {
            value = Integer.toBinaryString(((Byte) nonNull) & 0xFF);
        } else if (nonNull instanceof BitSet) {
            final BitSet v = (BitSet) nonNull;
            if (v.length() > 64) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            value = bitSet(sqlType, nonNull, true);
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            if (!StringUtils.isBinary(v)) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            value = v;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return "B'" + value + QUOTE_CHAR;
    }


    static String year(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Year) {
            literal = Integer.toString(((Year) nonNull).getValue());
        } else if (nonNull instanceof Integer || nonNull instanceof Short) {
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            try {
                Year.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return literal;

    }


    static void textEscapes(StringBuilder builder, final char[] array, final int length) {
        if (length < 0 || length > array.length) {
            throw new IllegalArgumentException(String.format(
                    "length[%s] and array.length[%s] not match.", length, array.length));
        }

        int lastWritten = 0;
        char ch;
        for (int i = 0; i < length; i++) {
            ch = array[i];
            if (ch == EMPTY_CHAR) {
                if (i > lastWritten) {
                    builder.append(array, lastWritten, i - lastWritten);
                }
                builder.append(BACK_SLASH);
                builder.append('0');
                lastWritten = i + 1;
            } else if (ch == '\032') {
                if (i > lastWritten) {
                    builder.append(array, lastWritten, i - lastWritten);
                }
                builder.append(BACK_SLASH);
                builder.append('Z');
                lastWritten = i + 1;
            } else if (ch == BACK_SLASH || ch == QUOTE_CHAR || ch == DOUBLE_QUOTE) {
                if (i > lastWritten) {
                    builder.append(array, lastWritten, i - lastWritten);
                }
                builder.append(BACK_SLASH);
                lastWritten = i; // not i+1 as ch wasn't written.
            }

        }
        if (lastWritten < length) {
            builder.append(array, lastWritten, length - lastWritten);
        }
    }


}
