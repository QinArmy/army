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

package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.StringArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;

/**
 * <p>This class map {@link String} to sql varchar .
 * <p>If you need to map char ,you can use {@link SqlCharType} instead of this class.
 *
 * <p>This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Number}</li>
 *     <li>{@link Boolean} </li>
 *     <li>{@link CodeEnum} </li>
 *     <li>{@link TextEnum} </li>
 *     <li>{@link Enum} </li>
 *     <li>{@link LocalDate} </li>
 *     <li>{@link LocalDateTime} </li>
 *     <li>{@link LocalTime} </li>
 *     <li>{@link OffsetDateTime} </li>
 *     <li>{@link ZonedDateTime} </li>
 *     <li>{@link OffsetTime} </li>
 *     <li>{@link Year}  to {@link Year} string or {@link LocalDate} string</li>
 *     <li>{@link YearMonth}  to {@link LocalDate} string </li>
 *     <li>{@link MonthDay} to {@link LocalDate} string</li>
 *     <li>{@link Instant} to {@link Instant#getEpochSecond()} string</li>
 *     <li>{@link java.time.Duration} </li>
 *     <li>{@link java.time.Period} </li>
 * </ul>
 *  to {@link String},if error,throw {@link io.army.ArmyException}
 *
 * @see TextType
 * @see MediumTextType
 * @since 0.6.0
 */
public final class StringType extends _ArmyBuildInType implements MappingType.SqlStringType {


    public static StringType from(final Class<?> fieldType) {
        if (fieldType != String.class) {
            throw errorJavaType(StringType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final StringType INSTANCE = new StringType();

    /**
     * private constructor
     */
    private StringType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }


    @Override
    public MappingType arrayTypeOfThis() {
        return StringArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToDataType(this, meta);
    }


    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(final DataType dataType, final MappingEnv env, final Object source) {
        return toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.VARCHAR;
                break;
            case PostgreSQL:
                dataType = PostgreType.VARCHAR;
                break;
            case SQLite:
                dataType = SQLiteType.VARCHAR;
                break;
            case Oracle:
                dataType = OracleDataType.VARCHAR2;
                break;
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return dataType;
    }

    public static String toString(final MappingType type, final DataType dataType, final Object source,
                                  final ErrorHandler errorHandler) {
        final String value;
        if (source instanceof String) {
            value = (String) source;
        } else if (source instanceof BigDecimal) {
            value = ((BigDecimal) source).toPlainString();
        } else if (source instanceof Number) {
            value = source.toString();
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? BooleanType.TRUE : BooleanType.FALSE;
        } else if (source instanceof Enum) {
            if (source instanceof CodeEnum) {
                value = Integer.toString(((CodeEnum) source).code());
            } else if (source instanceof TextEnum) {
                value = ((TextEnum) source).text();
            } else {
                value = ((Enum<?>) source).name();
            }
        } else if (source instanceof Character) {
            value = source.toString();
        } else if (source instanceof TemporalAccessor) {
            if (source instanceof LocalDate) {
                // TODO postgre format ?
                value = source.toString();
            } else if (source instanceof LocalDateTime) {
                value = ((LocalDateTime) source).format(_TimeUtils.DATETIME_FORMATTER_6);
            } else if (source instanceof LocalTime) {
                value = ((LocalTime) source).format(_TimeUtils.TIME_FORMATTER_6);
            } else if (source instanceof OffsetDateTime || source instanceof ZonedDateTime) {
                value = _TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) source);
            } else if (source instanceof OffsetTime) {
                value = ((OffsetTime) source).format(_TimeUtils.OFFSET_TIME_FORMATTER_6);
            } else if (source instanceof Instant) {
                value = Long.toString(((Instant) source).getEpochSecond());
            } else {
                value = source.toString();
            }
        } else if (source instanceof TemporalAmount) {
//            if (nonNull instanceof Period) {
//
//            } else if (!(nonNull instanceof Duration)) {
//                throw errorHandler.apply(type, nonNull);
//            } //TODO handle
            throw errorHandler.apply(type, dataType, source, null);
        } else if (source instanceof byte[]) {
            value = new String((byte[]) source, StandardCharsets.UTF_8);
        } else {
            value = source.toString();
        }
        return value;
    }


}
