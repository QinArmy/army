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

package io.army.dialect.postgre;

import io.army.dialect._Constant;
import io.army.dialect._Literals;
import io.army.env.EscapeMode;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;
import io.army.session.executor.ExecutorSupport;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public abstract class _PostgreLiterals extends _Literals {

    private _PostgreLiterals() {
    }


    public static boolean buildInLiteral(final TypeMeta typeMeta, final PostgreType dataType, final Object value,
                                         final EscapeMode mode, final boolean typeName, final boolean inArray,
                                         final StringBuilder sqlBuilder) {
        final char delimiter;
        if (inArray) {
            delimiter = _Constant.DOUBLE_QUOTE;
        } else {
            delimiter = _Constant.QUOTE;
        }
        boolean enclose = false;
        switch (dataType) {
            case BOOLEAN:
                _Literals.bindBoolean(typeMeta, dataType, value, sqlBuilder);
                break;
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::SMALLINT");
                }
            }
            break;
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::INTEGER");
                }
            }
            break;
            case BIGINT: {
                if (!(value instanceof Long)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::BIGINT");
                }
            }
            break;
            case DECIMAL: {
                if (!(value instanceof BigDecimal)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(((BigDecimal) value).toPlainString());
                if (typeName) {
                    sqlBuilder.append("::DECIMAL");
                }
            }
            break;
            case FLOAT8: {
                if (!(value instanceof Double)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::FLOAT8");
                }
            }
            break;
            case REAL: {
                if (!(value instanceof Float)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::REAL");
                }
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIME ");
                }
                sqlBuilder.append(delimiter)
                        .append(_TimeUtils.TIME_FORMATTER_6.format((LocalTime) value))
                        .append(delimiter);
                enclose = true;
            }
            break;
            case DATE: {
                if (!(value instanceof LocalDate)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("DATE ");
                }
                sqlBuilder.append(delimiter)
                        .append(DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate) value))
                        .append(delimiter);
                enclose = true;
            }
            break;
            case TIMETZ: {
                if (!(value instanceof OffsetTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIMETZ ");
                }
                sqlBuilder.append(delimiter)
                        .append(_TimeUtils.OFFSET_TIME_FORMATTER_6.format((OffsetTime) value))
                        .append(delimiter);
                enclose = true;
            }
            break;
            case TIMESTAMP: {
                if (!(value instanceof LocalDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIMESTAMP ");
                }
                sqlBuilder.append(delimiter)
                        .append(_TimeUtils.DATETIME_FORMATTER_6.format((LocalDateTime) value))
                        .append(delimiter);
                enclose = true;
            }
            break;
            case TIMESTAMPTZ: {
                if (!(value instanceof OffsetDateTime || value instanceof ZonedDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIMESTAMPTZ ");
                }
                sqlBuilder.append(delimiter)
                        .append(_TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(delimiter);
                enclose = true;
            }
            break;
            case CHAR:
            case BPCHAR:
            case VARCHAR:
            case TEXT:
            case JSON:
            case JSONB:
            case JSONPATH:
            case XML:
                // Geometric Types
            case POINT:
            case LINE:
            case LSEG:
            case BOX:
            case PATH:
            case POLYGON:
            case CIRCLE:
                // Network Address Types
            case CIDR:
            case INET:
            case MACADDR:
            case MACADDR8:
                //
            case UUID:
            case MONEY:
            case TSQUERY:
            case TSVECTOR:
                // Range Types
            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case TSRANGE:
            case TSTZRANGE:
            case DATERANGE:
                // multi range Types
            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case DATEMULTIRANGE:
            case TSMULTIRANGE:
            case TSTZMULTIRANGE:

            case INTERVAL:

            case ACLITEM:
            case PG_LSN:
            case PG_SNAPSHOT: {
                if (!(value instanceof String)) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                if (typeName) {
                    sqlBuilder.append(dataType.typeName())
                            .append(_Constant.SPACE); //use dataType 'string' syntax not 'string'::dataType syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                }
                postgreTextLiteral(mode, inArray, (String) value, 0, ((String) value).length(), sqlBuilder);
                enclose = true;
            }
            break;
            case RECORD: {
                if (!(value instanceof String)) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                final String v = (String) value;
                final int length = v.length();
                if (v.length() < 2
                        || v.charAt(0) != _Constant.LEFT_PAREN
                        || v.charAt(length - 1) != _Constant.RIGHT_PAREN) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                _PostgreLiterals.postgreTextLiteral(mode, inArray, v, 0, length, sqlBuilder);
                enclose = true;
            }
            break;
            case BYTEA: {
                if (!(value instanceof byte[])) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }

                if (typeName) {
                    sqlBuilder.append(dataType.typeName())
                            .append(_Constant.SPACE); //use dataType 'string' syntax not 'string'::dataType syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                }

                sqlBuilder.append(delimiter)
                        .append(_Constant.BACK_SLASH)
                        .append('x')
                        .append(HexUtils.hexEscapesText(true, (byte[]) value))
                        .append(delimiter);
            }
            break;
            case VARBIT:
            case BIT: {
                if (typeName) {
                    sqlBuilder.append(dataType.typeName())
                            .append(_Constant.SPACE); //use dataType 'string' syntax not 'string'::dataType syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                }
                postgreBitString(typeMeta, dataType, value, sqlBuilder);
            }
            break;
            case UNKNOWN:
            case REF_CURSOR:
                throw ExecutorSupport.mapMethodError(typeMeta.mappingType(), dataType);
            default:
                throw _Exceptions.unexpectedEnum(dataType);

        } // switch

        return enclose;
    }


    public static void postgreTextLiteral(final EscapeMode mode, final boolean inArray, final CharSequence literal, final int offset, final int end,
                                          final StringBuilder sqlBuilder) {

        final int startIndex;
        startIndex = sqlBuilder.length();

        sqlBuilder.append(_Constant.QUOTE);

        int lastWritten = 0;
        char followChar = _Constant.NUL_CHAR;
        for (int i = offset; i < end; i++) {
            switch (literal.charAt(i)) {
                case _Constant.QUOTE: {
                    if (i > lastWritten) {
                        sqlBuilder.append(literal, lastWritten, i);
                    }
                    sqlBuilder.append(_Constant.QUOTE);
                    lastWritten = i;//not i + 1 as current char wasn't written
                }
                continue;
                case _Constant.BACK_SLASH:
                    followChar = _Constant.BACK_SLASH;
                    break;
                case _Constant.NUL_CHAR:
                    followChar = '0';
                    break;
                case '\b':
                    followChar = 'b';
                    break;
                case '\f':
                    followChar = 'f';
                    break;
                case '\n':
                    followChar = 'n';
                    break;
                case '\r':
                    followChar = 'r';
                    break;
                case '\t':
                    followChar = 't';
                    break;
                default:
                    continue;
            }

            if (i > lastWritten) {
                sqlBuilder.append(literal, lastWritten, i);
            }
            sqlBuilder.append(_Constant.BACK_SLASH)
                    .append(followChar);
            lastWritten = i + 1;


        } // for loop

        if (lastWritten < end) {
            sqlBuilder.append(literal, lastWritten, end);
        }
        if (followChar != _Constant.NUL_CHAR) {
            sqlBuilder.insert(startIndex, 'E');
        }
        sqlBuilder.append(_Constant.QUOTE);

    }


    static void postgreBitString(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                 final StringBuilder sqlBuilder) {
        if (!(value instanceof String)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        } else if (!_StringUtils.isBinary((String) value)) {
            throw _Exceptions.valueOutRange(dataType, value);
        }

        sqlBuilder.append('B')
                .append(_Constant.QUOTE)
                .append(value)
                .append(_Constant.QUOTE);

    }


    static void appendSimpleTypeArray(final MappingType mappingType, final DataType dataType, final Object array,
                                      final StringBuilder sqlBuilder,
                                      final ArrayElementHandler handler) {

        final int length, dimension;
        dimension = ArrayUtils.dimensionOf(array.getClass());
        length = Array.getLength(array);


        sqlBuilder.append(_Constant.LEFT_BRACE);
        Object component;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.COMMA);
            }
            component = Array.get(array, i);
            if (component == null) {
                sqlBuilder.append(_Constant.NULL);
            } else if (dimension > 1) {
                appendSimpleTypeArray(mappingType, dataType, component, sqlBuilder, handler);
            } else {
                handler.appendElement(mappingType, dataType, component, sqlBuilder);
            }
        }
        sqlBuilder.append(_Constant.RIGHT_BRACE);

    }


}
