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
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * <p>
 * This class is mapping class of {@link ZonedDateTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link ZonedDateTime},if error,throw {@link io.army.ArmyException}
 ** @since 0.6.0
 */
public final class ZonedDateTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlOffsetDateTimeType {


    public static ZonedDateTimeType from(Class<?> javaType) {
        if (javaType != ZonedDateTime.class) {
            throw errorJavaType(ZonedDateTimeType.class, javaType);
        }
        return INSTANCE;
    }

    public static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    /**
     * private constructor
     */
    private ZonedDateTimeType() {
    }


    @Override
    public Class<?> javaType() {
        return ZonedDateTime.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return super.arrayTypeOfThis();
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return OffsetDateTimeType.mapToDataType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final MappingType instance;
        if (targetType == String.class) {
            instance = StringType.INSTANCE;
        } else if (targetType == OffsetDateTime.class) {
            instance = OffsetDateTimeType.INSTANCE;
        } else {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return instance;
    }

    @Override
    public ZonedDateTime convert(MappingEnv env, Object source) throws CriteriaException {
        return toZonedDateTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetDateTime beforeBind(final DataType dataType, final MappingEnv env, final Object source)
            throws CriteriaException {
        return OffsetDateTimeType.toOffsetDateTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public ZonedDateTime afterGet(final DataType dataType, final MappingEnv env, final Object source)
            throws DataAccessException {
        return toZonedDateTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

     static ZonedDateTime toZonedDateTime(MappingType type, DataType dataType, final Object nonNull,
                                          ErrorHandler errorHandler) {
         final ZonedDateTime value;
         if (nonNull instanceof ZonedDateTime) {
             value = (ZonedDateTime) nonNull;
         } else if (nonNull instanceof OffsetDateTime) {
             value = ((OffsetDateTime) nonNull).toZonedDateTime();
         } else if (nonNull instanceof String) {
             try {
                 value = ZonedDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
             } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
