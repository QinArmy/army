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
import io.army.executor.DataAccessException;
import io.army.sqltype.*;

import java.util.UUID;

public final class UUIDType extends _ArmyNoInjectionType {


    public static UUIDType from(final Class<?> javaType) {
        if (javaType != UUID.class) {
            throw errorJavaType(UUIDType.class, javaType);
        }
        return INSTANCE;
    }

    public static final UUIDType INSTANCE = new UUIDType();

    /**
     * private constructor
     */
    private UUIDType() {

    }

    @Override
    public Class<?> javaType() {
        return UUID.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SQLType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.UUID;
                break;
            case MySQL:
                dataType = MySQLType.CHAR;
                break;
            case SQLite:
                dataType = SQLiteType.VARCHAR;
                break;
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return toUUID(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final Object value;
        switch (((SQLType) dataType).database()) {
            case PostgreSQL:
                value = toUUID(dataType, source, PARAM_ERROR_HANDLER);
                break;
            case MySQL:
            case SQLite: {
                if (source instanceof UUID) {
                    value = source.toString();
                } else if (source instanceof String) {
                    try {
                        UUID.fromString((String) source);
                    } catch (Exception e) {
                        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
                    }
                    value = source;
                } else {
                    throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
                }
            }
            break;
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return toUUID(dataType, source, ACCESS_ERROR_HANDLER);
    }


    private UUID toUUID(final DataType dataType, final Object source, final ErrorHandler errorHandler) {
        final UUID value;
        if (source instanceof UUID) {
            value = (UUID) source;
        } else if (source instanceof String) {
            try {
                value = UUID.fromString((String) source);
            } catch (Exception e) {
                throw errorHandler.apply(this, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(this, dataType, source, null);
        }
        return value;
    }


}
