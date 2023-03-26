package io.army.dialect;

import io.army.meta.TypeMeta;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import java.time.*;

public abstract class _Literals {

    protected _Literals() {
        throw new UnsupportedOperationException();
    }


    private static final char[] LOWER_CASE_HEX_DIGITS = new char[]{
            '0', '1', '2', '3'
            , '4', '5', '6', '7'
            , '8', '9', 'a', 'b'
            , 'c', 'd', 'e', 'f'};


    public static char[] hexEscapes(final byte[] dataBytes) {
        final int bytesLength = dataBytes.length;
        final char[] hexDigitArray = new char[bytesLength << 1];
        byte b;
        for (int i = 0, j = 0; i < bytesLength; i++, j += 2) {
            b = dataBytes[i];
            hexDigitArray[j] = LOWER_CASE_HEX_DIGITS[(b >> 4) & 0xF]; // write highBits
            hexDigitArray[j + 1] = LOWER_CASE_HEX_DIGITS[b & 0xF]; // write lowBits
        }
        return hexDigitArray;
    }


    public static StringBuilder bindLocalTime(final TypeMeta typeMeta, final SqlType type, final Object value,
                                              final StringBuilder sqlBuilder) {
        if (!(value instanceof LocalTime)) {
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
        }
        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format((LocalTime) value, typeMeta))
                .append(_Constant.QUOTE);
    }

    public static StringBuilder bindLocalDate(final TypeMeta typeMeta, final SqlType type, final Object value,
                                              final StringBuilder sqlBuilder) {
        if (!(value instanceof LocalDate)) {
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
        }
        return sqlBuilder.append(_Constant.QUOTE)
                .append(value)
                .append(_Constant.QUOTE);
    }


    public static StringBuilder bindLocalDateTime(final TypeMeta typeMeta, final SqlType type, final Object value,
                                                  final StringBuilder sqlBuilder) {
        if (!(value instanceof LocalDateTime)) {
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
        }
        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format((LocalDateTime) value, typeMeta))
                .append(_Constant.QUOTE);

    }

    public static StringBuilder bindOffsetTime(final TypeMeta typeMeta, final SqlType type, final Object value,
                                               final StringBuilder sqlBuilder) {
        if (!(value instanceof OffsetTime)) {
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
        }
        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format((OffsetTime) value, typeMeta))
                .append(_Constant.QUOTE);
    }

    public static StringBuilder bindOffsetDateTime(final TypeMeta typeMeta, final SqlType type, final Object value,
                                                   final StringBuilder sqlBuilder) {
        final OffsetDateTime dateTime;
        if (value instanceof OffsetDateTime) {
            dateTime = (OffsetDateTime) value;
        } else if (value instanceof ZonedDateTime) {
            dateTime = ((ZonedDateTime) value).toOffsetDateTime();
        } else {
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
        }

        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format(dateTime, typeMeta))
                .append(_Constant.QUOTE);

    }


}
