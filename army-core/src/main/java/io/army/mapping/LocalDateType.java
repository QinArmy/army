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
import io.army.mapping.array.LocalDateArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLiteType;

import java.time.*;

/**
 * <p>
 * This class is mapping class of {@link LocalDate}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link MonthDay}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to sql date,if overflow,throw {@link io.army.ArmyException}
 * * @since 0.6.0
 */
public final class LocalDateType extends _ArmyNoInjectionType implements MappingType.SqlLocalDateType {


    public static LocalDateType from(final Class<?> javaType) {
        if (javaType != LocalDate.class) {
            throw errorJavaType(LocalDateType.class, javaType);
        }
        return INSTANCE;
    }

    public static final LocalDateType INSTANCE = new LocalDateType();

    /**
     * private constructor
     */
    private LocalDateType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return LocalDateArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToDataType(this, meta);
    }

    @Override
    public LocalDate convert(MappingEnv env, Object source) throws CriteriaException {
        return toLocalDateTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toLocalDateTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate afterGet(DataType dataType, MappingEnv env, Object source) {
        return toLocalDateTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.DATE;
                break;
            case PostgreSQL:
                dataType = PostgreType.DATE;
                break;
            case SQLite:
                dataType = SQLiteType.DATE;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return dataType;
    }

    static LocalDate toLocalDateTime(final MappingType type, final DataType dataType, final Object nonNull,
                                     final ErrorHandler errorHandler) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDate.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalDate();
        } else if (nonNull instanceof YearMonth) {
            final YearMonth v = (YearMonth) nonNull;
            value = LocalDate.of(v.getYear(), v.getMonth(), 1);
        } else if (nonNull instanceof MonthDay) {
            final MonthDay v = (MonthDay) nonNull;
            value = LocalDate.of(1970, v.getMonth(), v.getDayOfMonth());
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
