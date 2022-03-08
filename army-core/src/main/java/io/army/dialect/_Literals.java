package io.army.dialect;

import io.army.mapping.BooleanType;
import io.army.mapping.CodeEnumType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.util.Numbers;
import io.army.util.TimeUtils;
import io.army.util._Exceptions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.BitSet;

public abstract class _Literals {

    protected _Literals() {
        throw new UnsupportedOperationException();
    }


    public static String booleanLiteral(final SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return (Boolean) nonNull ? BooleanType.TRUE : BooleanType.FALSE;
    }

    public static String tinyInt(final SqlType sqlType, final Object nonNull) {
        return doInteger(sqlType, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public static String smallInt(final SqlType sqlType, final Object nonNull) {
        return doInteger(sqlType, nonNull, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public static String mediumInt(final SqlType sqlType, final Object nonNull) {
        return doInteger(sqlType, nonNull, -0x7FFF_FF - 1, 0x7FFF_FF);
    }

    public static String integer(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof CodeEnum) {
            CodeEnumType.getCodeMap(nonNull.getClass());
            literal = Integer.toString(((CodeEnum) nonNull).code());
        } else {
            literal = doInteger(sqlType, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return literal;
    }

    private static String doInteger(final SqlType sqlType, final Object nonNull
            , final int minValue, final int maxValue) {
        final String literal;
        if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            final int v = ((Number) nonNull).intValue();
            if (v < minValue || v > maxValue) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < minValue || v > maxValue) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(BigDecimal.valueOf(maxValue)) > 0
                    || v.compareTo(BigDecimal.valueOf(minValue)) < 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(maxValue)) > 0 || v.compareTo(BigInteger.valueOf(minValue)) < 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                final int value = Integer.parseInt(v);
                if (value < minValue || value > maxValue) {
                    throw _Exceptions.valueOutRange(sqlType, nonNull);
                }
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }


    public static String bigInt(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            literal = nonNull.toString();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0
                    || v.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) < 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                Long.parseLong(v);
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    public static String decimal(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof BigDecimal) {
            literal = ((BigDecimal) nonNull).toPlainString();
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof BigInteger
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                new BigDecimal(v);
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    public static String floatLiteral(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Float) {
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            try {
                Float.parseFloat((String) nonNull);
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    public static String doubleLiteral(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Double) {
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            try {
                Double.parseDouble((String) nonNull);
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    public static String unsignedTinyInt(final SqlType sqlType, final Object nonNull) {
        return doUnsignedInteger(sqlType, 0xFFL, nonNull);
    }

    public static String unsignedSmallInt(final SqlType sqlType, final Object nonNull) {
        return doUnsignedInteger(sqlType, 0xFFFFL, nonNull);
    }

    public static String unsignedMediumInt(final SqlType sqlType, final Object nonNull) {
        return doUnsignedInteger(sqlType, 0xFFFF_FFL, nonNull);
    }

    public static String unsignedInt(final SqlType sqlType, final Object nonNull) {
        return doUnsignedInteger(sqlType, 0xFFFF_FFFFL, nonNull);
    }

    public static String unsignedLBigInt(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            literal = nonNull.toString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.ZERO) < 0 || v.compareTo(Numbers.MAX_UNSIGNED_LONG) > 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toString();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(new BigDecimal(Numbers.MAX_UNSIGNED_LONG)) > 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof String) {
            try {
                final BigInteger v = new BigInteger((String) nonNull);
                if (v.compareTo(BigInteger.ZERO) < 0 || v.compareTo(Numbers.MAX_UNSIGNED_LONG) > 0) {
                    throw _Exceptions.valueOutRange(sqlType, nonNull);
                }
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    public static String unsignedDecimal(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof BigDecimal) {
            final BigDecimal v = (BigDecimal) nonNull;
            if (v.compareTo(BigDecimal.ZERO) < 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof BigInteger
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            final long v = ((Number) nonNull).longValue();
            if (v < 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                final BigDecimal decimal = new BigDecimal(v);
                if (decimal.compareTo(BigDecimal.ZERO) < 0) {
                    throw _Exceptions.valueOutRange(sqlType, nonNull);
                }
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    public static String time(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        if (nonNull instanceof LocalTime) {
            text = ((LocalTime) nonNull).format(TimeUtils.getTimeFormatter(getTimeTypeScale(paramMeta)));
        } else if (nonNull instanceof String) {
            try {
                LocalTime.parse((String) nonNull, TimeUtils.getTimeFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return Constant.QUOTE + text + Constant.QUOTE;
    }

    public static String timeWithZone(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        if (nonNull instanceof OffsetTime) {
            text = ((OffsetTime) nonNull).format(TimeUtils.getOffsetTimeFormatter(getTimeTypeScale(paramMeta)));
        } else if (nonNull instanceof String) {
            try {
                OffsetTime.parse((String) nonNull, TimeUtils.getOffsetTimeFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return Constant.QUOTE + text + Constant.QUOTE;
    }

    public static String date(final SqlType sqlType, final Object nonNull) {
        final LocalDate v;
        if (nonNull instanceof LocalDate) {
            v = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            try {
                v = LocalDate.parse((String) nonNull);
            } catch (Exception e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return Constant.QUOTE + v.toString() + Constant.QUOTE;
    }

    public static String datetime(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        if (nonNull instanceof LocalDateTime) {
            text = ((LocalDateTime) nonNull).format(TimeUtils.getDatetimeFormatter(getTimeTypeScale(paramMeta)));
        } else if (nonNull instanceof String) {
            try {
                LocalDateTime.parse((String) nonNull, TimeUtils.getDatetimeFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }

        return Constant.QUOTE + text + Constant.QUOTE;
    }

    public static String dateTimeWithZone(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        final DateTimeFormatter formatter = TimeUtils.getDatetimeOffsetFormatter(getTimeTypeScale(paramMeta));
        if (nonNull instanceof OffsetDateTime) {
            text = ((OffsetDateTime) nonNull).format(formatter);
        } else if (nonNull instanceof ZonedDateTime) {
            text = ((ZonedDateTime) nonNull).format(formatter);
        } else if (nonNull instanceof String) {
            try {
                OffsetDateTime.parse((String) nonNull, TimeUtils.getDatetimeOffsetFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return Constant.QUOTE + text + Constant.QUOTE;
    }

    public static String enumLiteral(final SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof Enum)) {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return Constant.QUOTE + ((Enum<?>) nonNull).name() + Constant.QUOTE;
    }

    private static String doUnsignedInteger(final SqlType sqlType, final long maxUnsigned, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Integer
                || nonNull instanceof Long
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            final long v = ((Number) nonNull).longValue();
            if (v < 0 || v > maxUnsigned) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = Long.toString(v);
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(BigDecimal.ZERO) < 0
                    || v.compareTo(BigDecimal.valueOf(maxUnsigned)) > 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.ZERO) < 0
                    || v.compareTo(BigInteger.valueOf(maxUnsigned)) > 0) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = v.toString();
        } else if (nonNull instanceof String) {
            try {
                final long v = Long.parseLong((String) nonNull);
                if (v < 0 || v > maxUnsigned) {
                    throw _Exceptions.valueOutRange(sqlType, nonNull);
                }
            } catch (NumberFormatException e) {
                throw _Exceptions.valueOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        return literal;
    }

    protected static String bitSet(final SqlType sqlType, final Object nonNull, final boolean bitEndian) {
        if (!(nonNull instanceof BitSet)) {
            throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
        }
        final BitSet bitSet = (BitSet) nonNull;
        final byte[] bitBytes = bitSet.toByteArray();
        final int length = bitSet.length();
        final char[] bitChars = new char[length];
        if (bitEndian) {
            for (int i = 0, bitIndex = length - 1; i < length; i++, bitIndex--) {
                bitChars[i] = (bitBytes[bitIndex >> 3] & (1 << (bitIndex & 7))) == 0 ? '0' : '1';
            }
        } else {
            for (int i = 0; i < length; i++) {
                bitChars[i] = (bitBytes[i >> 3] & (1 << (i & 7))) == 0 ? '0' : '1';
            }
        }
        return new String(bitChars);
    }


    private static int getTimeTypeScale(ParamMeta paramMeta) {
        int scale;
        if (paramMeta instanceof FieldMeta) {
            scale = ((FieldMeta<?, ?>) paramMeta).scale();
            if (scale < 0) {
                scale = 6;
            }
        } else {
            scale = 6;
        }
        return scale;
    }


}
