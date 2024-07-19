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
import io.army.mapping.array.LocalTimeArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLiteType;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * This class is mapping class of {@link LocalTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalTime}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link LocalTime},if overflow,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class LocalTimeType extends _ArmyNoInjectionType implements MappingType.SqlLocalTimeType {


    public static LocalTimeType from(final Class<?> javaType) {
        if (javaType != LocalTime.class) {
            throw errorJavaType(LocalTimeType.class, javaType);
        }
        return INSTANCE;
    }

    public static final LocalTimeType INSTANCE = new LocalTimeType();

    /**
     * private constructor
     */
    private LocalTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return LocalTimeArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TIME;
                break;
            case PostgreSQL:
                dataType = PostgreType.TIME;
                break;
            case SQLite:
                dataType = SQLiteType.TIME;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return dataType;
    }


    @Override
    public LocalTime convert(MappingEnv env, Object source) throws CriteriaException {
        return toLocalTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalTime beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toLocalTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalTime afterGet(final DataType dataType, MappingEnv env, final Object source) {
        return toLocalTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    static LocalTime toLocalTime(final MappingType type, final DataType dataType, final Object nonNull,
                                 final ErrorHandler errorHandler) {
        final LocalTime value;
        if (nonNull instanceof LocalTime) {
            value = (LocalTime) nonNull;
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalTime();
        } else if (nonNull instanceof String) {
            try {
                value = LocalTime.parse((String) nonNull, _TimeUtils.TIME_FORMATTER_6);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
