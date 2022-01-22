package io.army.dialect.mysql;

import io.army.dialect.Constant;
import io.army.dialect._Literals;
import io.army.sqltype.SqlType;
import io.army.util.StringUtils;
import io.army.util._Exceptions;
import io.qinarmy.util.BufferUtils;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Year;
import java.util.BitSet;
import java.util.Set;

abstract class MySQLLiterals extends _Literals {


    static String text(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Boolean) {
            literal = (Boolean) nonNull ? "'T'" : "'F'";
        } else if (nonNull instanceof String) {
            literal = textEscapes((String) nonNull);
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    static String binary(final SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        final byte[] array = (byte[]) nonNull;
        final StringBuilder builder = new StringBuilder()
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
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            value = bitSet(sqlType, nonNull, true);
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            if (!StringUtils.isBinary(v)) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            value = v;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
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
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;

    }

    static String setType(final SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof Set)) {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        final StringBuilder builder = new StringBuilder()
                .append(QUOTE_CHAR);
        Class<?> enumClass = null;
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!(e instanceof Enum)) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            if (enumClass == null) {
                enumClass = e.getClass();
                if (enumClass.isAnonymousClass()) {
                    enumClass = enumClass.getSuperclass();
                }
            } else if (!enumClass.isAssignableFrom(e.getClass())) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            if (index > 0) {
                builder.append(Constant.COMMA);
            }
            builder.append(((Enum<?>) e).name());
            index++;
        }
        return builder.append(QUOTE_CHAR)
                .toString();
    }


    private static String textEscapes(final String value) {
        final char[] array = value.toCharArray();
        final StringBuilder builder = new StringBuilder(array.length + 5)
                .append(QUOTE_CHAR);
        int lastWritten = 0;
        char ch;
        boolean hexEscapes = false;
        outFor:
        for (int i = 0; i < array.length; i++) {
            ch = array[i];
            switch (ch) {
                case QUOTE_CHAR: {
                    if (i > lastWritten) {
                        builder.append(array, lastWritten, i - lastWritten);
                    }
                    builder.append(QUOTE_CHAR);
                    lastWritten = i; // not i+1 as ch wasn't written.
                }
                break;
                case BACK_SLASH:
                case EMPTY_CHAR:
                case '\b':
                case '\n':
                case '\r':
                case '\t':
                case '\032':
                    hexEscapes = true;
                    break outFor;

            }
        }
        final String literal;
        if (hexEscapes) {
            final byte[] bytes;
            bytes = value.getBytes(StandardCharsets.UTF_8);
            literal = "X'" + BufferUtils.hexEscapesText(true, bytes, bytes.length) + QUOTE_CHAR;
        } else {
            if (lastWritten < array.length) {
                builder.append(array, lastWritten, array.length - lastWritten);
            }
            literal = builder
                    .append(QUOTE_CHAR)
                    .toString();
        }
        return literal;
    }


}
