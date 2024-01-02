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
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.util._TimeUtils;

import java.time.Duration;
import java.time.LocalTime;

public final class DurationType extends _ArmyNoInjectionMapping implements MappingType.SqlDurationType {

    public static DurationType from(final Class<?> javaType) {
        if (javaType == Duration.class) {
            throw errorJavaType(DurationType.class, javaType);
        }
        return INSTANCE;
    }

    public static final DurationType INSTANCE = new DurationType();

    private DurationType() {
    }

    @Override
    public Class<?> javaType() {
        return Duration.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TIME;
                break;
            case PostgreSQL:
                dataType = PostgreType.INTERVAL;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Duration convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


    private Duration toDuration(DataType dataType, final Object source, ErrorHandler errorHandler) {
        final Duration value;
        if (source instanceof Duration) {
            value = (Duration) source;
        } else if (source instanceof LocalTime) {
            if (dataType != MySQLType.TIME) {
                throw errorHandler.apply(this, dataType, source, null);
            }
            value = _TimeUtils.convertToDuration((LocalTime) source);
        } else if (source instanceof String) {

        }
        return null;
    }


}
