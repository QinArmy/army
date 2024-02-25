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
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.util._TimeUtils;

import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

/**
 * <p>
 * This class is mapping class of {@link YearMonth}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} ,{@link YearMonth} string or {@link LocalDate} string</li>
 * </ul>
 *  to {@link YearMonth},if error,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class YearMonthType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateType {

    public static YearMonthType from(final Class<?> fieldType) {
        if (fieldType != YearMonth.class) {
            throw errorJavaType(YearMonthType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final YearMonthType INSTANCE = new YearMonthType();

    /**
     * private constructor
     */
    private YearMonthType() {
    }

    @Override
    public Class<?> javaType() {
        return YearMonth.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.DATE;
                break;
            case PostgreSQL:
                dataType = PostgreType.DATE;
                break;
            case SQLite:
                dataType = SQLiteType.YEAR_MONTH;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public YearMonth convert(MappingEnv env, Object source) throws CriteriaException {
        return toYearMonth(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Temporal beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        final Temporal value;
        switch (((SQLType) dataType).database()) {
            case MySQL:
            case PostgreSQL: {
                if (source instanceof LocalDate) {
                    value = (LocalDate) source;
                } else if (source instanceof LocalDateTime
                        || source instanceof OffsetDateTime
                        || source instanceof ZonedDateTime) {
                    value = LocalDate.from((TemporalAccessor) source);
                } else {
                    final YearMonth v;
                    v = toYearMonth(this, dataType, source, PARAM_ERROR_HANDLER);
                    value = LocalDate.of(v.getYear(), v.getMonth(), 1);
                }
            }
            break;
            case SQLite:
                value = toYearMonth(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public YearMonth afterGet(DataType dataType, MappingEnv env, Object source) {
        return toYearMonth(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static YearMonth toYearMonth(final MappingType type, final DataType dataType, final Object source,
                                 final ErrorHandler errorHandler) {
        final YearMonth value;
        if (source instanceof YearMonth) {
            value = (YearMonth) source;
        } else if (source instanceof LocalDate
                || source instanceof LocalDateTime
                || source instanceof OffsetDateTime
                || source instanceof ZonedDateTime) {
            value = YearMonth.from((TemporalAccessor) source);
        } else if (source instanceof Integer) {
            value = yearMonthFromInt(type, dataType, (Integer) source, errorHandler);
        } else if (source instanceof Long) {
            final long v = (Long) source;
            if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = yearMonthFromInt(type, dataType, (int) v, errorHandler);
        } else if (source instanceof String) {
            try {
                final String sourceStr = (String) source;
                final int length = sourceStr.length();
                final char ch;
                if (length > 24 && ((ch = sourceStr.charAt(length - 6)) == '-' || ch == '+')) {
                    value = YearMonth.from(OffsetDateTime.parse(sourceStr, _TimeUtils.OFFSET_DATETIME_FORMATTER_6));
                } else if (sourceStr.lastIndexOf(':') < 0) {
                    value = YearMonth.from(LocalDate.parse(sourceStr));
                } else {
                    value = YearMonth.from(LocalDateTime.parse(sourceStr, _TimeUtils.DATETIME_FORMATTER_6));
                }
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }

        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


    private static YearMonth yearMonthFromInt(final MappingType type, final DataType dataType, final int source,
                                              final ErrorHandler errorHandler) {
        // https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_period-add
        if (errorHandler != ACCESS_ERROR_HANDLER || (dataType != MySQLType.INT && dataType != MySQLType.BIGINT)) {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return YearMonth.of(source / 100, source % 100);

    }


}
