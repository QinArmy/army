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

package io.army.mapping.sqlite;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLiteType;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.BitSet;
import java.util.Objects;

public final class SQLiteDynamicType extends _ArmyBuildInMapping {

    public static SQLiteDynamicType from(Class<?> javaType) {
        Objects.requireNonNull(javaType);
        return INSTANCE;
    }

    public static final SQLiteDynamicType INSTANCE = new SQLiteDynamicType();


    /**
     * private constructor
     */
    private SQLiteDynamicType() {
    }

    @Override
    public Class<?> javaType() {
        return Object.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.SQLite) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return SQLiteType.DYNAMIC;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return source;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return dynamicTypeBeforeBind(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return source;
    }


    public static Object dynamicTypeBeforeBind(MappingType type, final DataType dataType, final Object source,
                                               ErrorHandler errorHandler) {
        final Object value;
        if (source instanceof BigDecimal) {
            value = ((BigDecimal) source).toPlainString();
        } else if (source instanceof Number) {
            if (source instanceof Integer
                    || source instanceof Long
                    || source instanceof Double
                    || source instanceof Float
                    || source instanceof Short
                    || source instanceof Byte
                    || source instanceof BigInteger) {
                value = source;
            } else {
                value = source.toString();
            }
        } else if (source instanceof String) {
            value = source;
        } else if (source instanceof byte[]) {
            value = source;
        } else if (source instanceof BitSet) {
            final BitSet v = (BitSet) source;
            if (v.length() > 64) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = v.toLongArray()[0];
        } else if (source instanceof Temporal) {
            if (source instanceof LocalDateTime) {
                value = _TimeUtils.DATETIME_FORMATTER_6.format((LocalDateTime) source);
            } else if (source instanceof OffsetDateTime || source instanceof ZonedDateTime) {
                value = _TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) source);
            } else if (source instanceof LocalDate) {
                value = DateTimeFormatter.ISO_LOCAL_DATE.format((TemporalAccessor) source);
            } else if (source instanceof LocalTime) {
                value = _TimeUtils.TIME_FORMATTER_6.format((TemporalAccessor) source);
            } else if (source instanceof OffsetTime) {
                value = _TimeUtils.OFFSET_TIME_FORMATTER_6.format((TemporalAccessor) source);
            } else if (source instanceof Year) {
                value = ((Year) source).getValue();
            } else {
                value = source.toString();
            }
        } else {
            value = source.toString();
        }
        return value;
    }


}
