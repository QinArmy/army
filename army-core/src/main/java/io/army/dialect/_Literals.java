package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.mapping.BooleanType;
import io.army.meta.TypeMeta;
import io.army.session.executor.ExecutorSupport;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
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


    public static void bindBoolean(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                   final StringBuilder sqlBuilder) {
        if (!(value instanceof Boolean)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        }
        sqlBuilder.append(((Boolean) value) ? BooleanType.TRUE : BooleanType.FALSE);
    }

    public static void bindBigDecimal(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                      final StringBuilder sqlBuilder) {
        if (!(value instanceof BigDecimal)) {
            throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
        }
        sqlBuilder.append(((BigDecimal) value).toPlainString());
    }


    public static void bindLocalTime(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                     final StringBuilder sqlBuilder) {
        if (!(value instanceof LocalTime)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        }
        sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format((LocalTime) value, typeMeta))
                .append(_Constant.QUOTE);
    }

    public static void bindLocalDate(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                     final StringBuilder sqlBuilder) {
        if (!(value instanceof LocalDate)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        }
        sqlBuilder.append(_Constant.QUOTE)
                .append(value)
                .append(_Constant.QUOTE);
    }


    public static StringBuilder bindLocalDateTime(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                                  final StringBuilder sqlBuilder) {
        if (!(value instanceof LocalDateTime)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        }
        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format((LocalDateTime) value, typeMeta))
                .append(_Constant.QUOTE);

    }

    public static StringBuilder bindOffsetTime(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                               final StringBuilder sqlBuilder) {
        if (!(value instanceof OffsetTime)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        }
        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format((OffsetTime) value, typeMeta))
                .append(_Constant.QUOTE);
    }

    public static StringBuilder bindOffsetDateTime(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                                   final StringBuilder sqlBuilder) {
        final OffsetDateTime dateTime;
        if (value instanceof OffsetDateTime) {
            dateTime = (OffsetDateTime) value;
        } else if (value instanceof ZonedDateTime) {
            dateTime = ((ZonedDateTime) value).toOffsetDateTime();
        } else {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        }

        return sqlBuilder.append(_Constant.QUOTE)
                .append(_TimeUtils.format(dateTime, typeMeta))
                .append(_Constant.QUOTE);

    }

    public static void booleanArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                           final StringBuilder sqlBuilder) {
        if (!(element instanceof Boolean)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }

    public static void byteArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                        final StringBuilder sqlBuilder) {
        if (!(element instanceof Byte)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }

    public static void shortArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                         final StringBuilder sqlBuilder) {
        if (!(element instanceof Short)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }

    public static void integerArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                           final StringBuilder sqlBuilder) {
        if (!(element instanceof Integer)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }

    public static void longArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                        final StringBuilder sqlBuilder) {
        if (!(element instanceof Long)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }

    public static void bigDecimalArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                              final StringBuilder sqlBuilder) {
        if (!(element instanceof BigDecimal)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(((BigDecimal) element).toPlainString());
    }

    public static void doubleArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                          final StringBuilder sqlBuilder) {
        if (!(element instanceof Double)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }

    public static void floatArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                         final StringBuilder sqlBuilder) {
        if (!(element instanceof Float)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }


    public static void stringArrayElement(final TypeMeta typeMeta, final DataType dataType, final Object element,
                                          final StringBuilder sqlBuilder) {
        if (!(element instanceof String)) {
            throw arrayElementError(typeMeta, dataType, element);
        }
        sqlBuilder.append(element);
    }


    private static CriteriaException arrayElementError(TypeMeta typeMeta, DataType type, Object element) {
        String m = String.format("%s %s don't support %s array.", typeMeta.mappingType(), type
                , element.getClass().getName());
        return new CriteriaException(m);
    }

    public interface ArrayElementHandler {

        void appendElement(final TypeMeta typeMeta, final SqlType type, final Object element,
                           final StringBuilder sqlBuilder);

    }


}
