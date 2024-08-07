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
import io.army.mapping.array.MonthArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.util._Collections;
import io.army.util._StringUtils;
import io.army.util._TimeUtils;

import io.army.lang.Nullable;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>
 * This class is mapping class of {@link Month}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link MonthDay}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link String} , {@link Month#name()} or {@link LocalDate} string</li>
 * </ul>
 *  to {@link Month},if error,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class MonthType extends _ArmyNoInjectionType implements MappingType.SqlTemporalFieldType {


    public static MonthType form(final Class<?> javaType) {
        if (Month.class.isAssignableFrom(javaType)) {
            throw errorJavaType(MonthType.class, javaType);
        }
        return DEFAULT;
    }

    public static MonthType fromParam(final Class<?> enumType, final String enumName) {
        if (Month.class.isAssignableFrom(enumType)) {
            throw errorJavaType(MonthType.class, enumType);
        } else if (!_StringUtils.hasText(enumName)) {
            throw new IllegalArgumentException("no text");
        }
        return MonthEnumHolder.INSTANCE.computeIfAbsent(enumName, MonthType::new);
    }

    public static final MonthType DEFAULT = new MonthType(null);

    private final String enumName;

    /**
     * private constructor
     */
    private MonthType(@Nullable String enumName) {
        this.enumName = enumName;
    }

    @Override
    public Class<?> javaType() {
        return Month.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return MonthArrayType.LINEAR;
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (type instanceof MonthType) {
            final MonthType o = (MonthType) type;
            match = Objects.equals(o.enumName, this.enumName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToDataType(this, meta, this.enumName);
    }


    @Override
    public Month convert(MappingEnv env, Object source) throws CriteriaException {
        return toMoth(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toMoth(this, dataType, source, PARAM_ERROR_HANDLER)
                .name();
    }

    @Override
    public Month afterGet(DataType dataType, MappingEnv env, Object source) {
        return toMoth(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static Month toMoth(final MappingType type, final DataType dataType, final Object source,
                        final ErrorHandler errorHandler) {
        final Month value;

        final String sourceStr;
        final int length;

        if (source instanceof Month) {
            value = (Month) source;
        } else if (source instanceof LocalDate
                || source instanceof YearMonth
                || source instanceof MonthDay
                || source instanceof LocalDateTime
                || source instanceof OffsetDateTime
                || source instanceof ZonedDateTime) {
            value = Month.from((TemporalAccessor) source);
        } else if (source instanceof Integer) { // https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_month
            final int v = (Integer) source;
            if (v < 1 || v > 12) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = Month.of(v);
        } else if (source instanceof Long) {  // https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_month
            if (errorHandler != ACCESS_ERROR_HANDLER) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            final long v = (Long) source;
            if (v < 1 || v > 12) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = Month.of((int) v);
        } else if (!(source instanceof String) || (length = (sourceStr = (String) source).length()) == 0) {
            throw errorHandler.apply(type, dataType, source, null);
        } else if (length < 10) {
            try {
                // https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_monthname
                value = Month.valueOf(sourceStr.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            try {
                final char ch;
                if (length > 24 && ((ch = sourceStr.charAt(length - 6)) == '-' || ch == '+')) {
                    value = Month.from(OffsetDateTime.parse(sourceStr, _TimeUtils.OFFSET_DATETIME_FORMATTER_6));
                } else if (sourceStr.lastIndexOf(':') < 0) {
                    value = Month.from(LocalDate.parse(sourceStr));
                } else {
                    value = Month.from(LocalDateTime.parse(sourceStr, _TimeUtils.DATETIME_FORMATTER_6));
                }
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        }
        return value;
    }


    private static abstract class MonthEnumHolder {

        private static final ConcurrentMap<String, MonthType> INSTANCE = _Collections.concurrentHashMap();


    } // MonthEnumHolder


}
