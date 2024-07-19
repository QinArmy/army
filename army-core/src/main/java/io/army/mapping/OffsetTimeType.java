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
import io.army.mapping.array.OffsetTimeArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

/**
 * <p>
 * This class is mapping class of {@link OffsetTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link java.time.OffsetTime}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link OffsetTime},if error,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class OffsetTimeType extends _ArmyNoInjectionType implements MappingType.SqlOffsetTimeType {

    public static OffsetTimeType from(Class<?> javaType) {
        if (javaType != OffsetTime.class) {
            throw errorJavaType(OffsetTimeType.class, javaType);
        }
        return INSTANCE;
    }

    public static final OffsetTimeType INSTANCE = new OffsetTimeType();


    /**
     * private constructor
     */
    private OffsetTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetTime.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return OffsetTimeArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TIME;
                break;
            case PostgreSQL:
                dataType = PostgreType.TIMETZ;
                break;
            case SQLite:
                dataType = SQLiteType.TIME_WITH_TIMEZONE;
                break;
            case Oracle:
                dataType = OracleDataType.TIMESTAMPTZ;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return dataType;
    }

    @Override
    public OffsetTime convert(MappingEnv env, Object source) throws CriteriaException {
        return toOffsetTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetTime beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        return toOffsetTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetTime afterGet(DataType dataType, final MappingEnv env, final Object source) {
        return toOffsetTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static OffsetTime toOffsetTime(MappingType type, DataType dataType, final Object source,
                                   ErrorHandler errorHandler) {
        final OffsetTime value;
        if (source instanceof OffsetTime) {
            value = (OffsetTime) source;
        } else if (source instanceof OffsetDateTime) {
            value = ((OffsetDateTime) source).toOffsetTime();
        } else if (source instanceof ZonedDateTime) {
            value = ((ZonedDateTime) source).toOffsetDateTime().toOffsetTime();
        } else if (source instanceof String) {
            try {
                value = OffsetTime.parse((String) source, _TimeUtils.OFFSET_TIME_FORMATTER_6);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }

}
