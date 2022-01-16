package io.army.dialect;

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

public abstract class _Literals {

    protected _Literals() {
        throw new UnsupportedOperationException();
    }


    public static String booleanLiteral(final SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return (Boolean) nonNull ? Constant.TRUE : Constant.FALSE;
    }


    public static String tinyInt(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Byte) {
            literal = Byte.toString((Byte) nonNull);
        } else if (nonNull instanceof Integer || nonNull instanceof Short) {
            final int v = ((Number) nonNull).intValue();
            if (v > Byte.MAX_VALUE || v < Byte.MIN_VALUE) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = Integer.toString(v);
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v > Byte.MAX_VALUE || v < Byte.MIN_VALUE) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = Long.toString(v);
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(BigDecimal.valueOf(Byte.MAX_VALUE)) > 0
                    || v.compareTo(BigDecimal.valueOf(Byte.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Byte.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                Byte.parseByte(v);
            } catch (NumberFormatException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return literal;
    }

    public static String smallInt(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Short
                || nonNull instanceof Byte) {
            literal = nonNull.toString();
        } else if (nonNull instanceof Integer) {
            final int v = (Integer) nonNull;
            if (v > Short.MAX_VALUE || v < Short.MIN_VALUE) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = Integer.toString(v);
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v > Short.MAX_VALUE || v < Short.MIN_VALUE) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = Long.toString(v);
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(BigDecimal.valueOf(Short.MAX_VALUE)) > 0
                    || v.compareTo(BigDecimal.valueOf(Short.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Short.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                Short.parseShort(v);
            } catch (NumberFormatException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return literal;
    }

    public static String integer(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof CodeEnum) {
            CodeEnumType.checkCodeEnum(nonNull.getClass());
            literal = Integer.toString(((CodeEnum) nonNull).code());
        } else if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            literal = nonNull.toString();
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0
                    || v.compareTo(BigDecimal.valueOf(Integer.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                Integer.parseInt(v);
            } catch (NumberFormatException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
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
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            try {
                Long.parseLong(v);
            } catch (NumberFormatException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
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
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
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
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
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
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return literal;
    }

    public static String unsignedLong(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof BigInteger
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            literal = nonNull.toString();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0 || v.compareTo(new BigDecimal(Numbers.MAX_UNSIGNED_LONG)) > 0) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = v.toPlainString();
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return literal;
    }

    public static String time(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        if (nonNull instanceof LocalTime) {
            final int scale;
            if (paramMeta instanceof FieldMeta) {
                scale = ((FieldMeta<?, ?>) paramMeta).scale();
            } else {
                scale = 6;
            }
            text = ((LocalTime) nonNull).format(TimeUtils.getTimeFormatter(scale));
        } else if (nonNull instanceof String) {
            try {
                LocalTime.parse((String) nonNull, TimeUtils.getTimeFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return "'" + text + "'";
    }

    public static String timeWithZone(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        if (nonNull instanceof OffsetTime) {
            final int scale;
            if (paramMeta instanceof FieldMeta) {
                scale = ((FieldMeta<?, ?>) paramMeta).scale();
            } else {
                scale = 6;
            }
            text = ((OffsetTime) nonNull).format(TimeUtils.getOffsetTimeFormatter(scale));
        } else if (nonNull instanceof String) {
            try {
                OffsetTime.parse((String) nonNull, TimeUtils.getOffsetTimeFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return "'" + text + "'";
    }

    public static String date(final SqlType sqlType, final Object nonNull) {
        final LocalDate v;
        if (nonNull instanceof LocalDate) {
            v = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            try {
                v = LocalDate.parse((String) nonNull);
            } catch (Exception e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return "'" + v.toString() + "'";
    }

    public static String datetime(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final String text;
        if (nonNull instanceof LocalDateTime) {
            final int scale;
            if (paramMeta instanceof FieldMeta) {
                scale = ((FieldMeta<?, ?>) paramMeta).scale();
            } else {
                scale = 6;
            }
            text = ((LocalDateTime) nonNull).format(TimeUtils.getDatetimeFormatter(scale));
        } else if (nonNull instanceof String) {
            try {
                LocalDateTime.parse((String) nonNull, TimeUtils.getDatetimeFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }

        return "'" + text + "'";
    }

    public static String dateTimeWithZone(final SqlType sqlType, final ParamMeta paramMeta, final Object nonNull) {
        final int scale;
        if (paramMeta instanceof FieldMeta) {
            scale = ((FieldMeta<?, ?>) paramMeta).scale();
        } else {
            scale = 6;
        }
        final String text;
        final DateTimeFormatter formatter = TimeUtils.getDatetimeOffsetFormatter(scale);
        if (nonNull instanceof OffsetDateTime) {
            text = ((OffsetDateTime) nonNull).format(formatter);
        } else if (nonNull instanceof ZonedDateTime) {
            text = ((ZonedDateTime) nonNull).format(formatter);
        } else if (nonNull instanceof String) {
            try {
                OffsetDateTime.parse((String) nonNull, TimeUtils.getDatetimeOffsetFormatter(6));
            } catch (DateTimeException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            text = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return "'" + text + "'";
    }


}
