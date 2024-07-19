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

package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;

import java.util.UUID;

public final class PostgreUuidType extends _ArmyNoInjectionType {

    public static final PostgreUuidType INSTANCE = new PostgreUuidType();

    public static PostgreUuidType from(Class<?> javaType) {
        if (javaType != UUID.class) {
            throw errorJavaType(PostgreUuidType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgreUuidType() {
    }

    @Override
    public Class<?> javaType() {
        return UUID.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.UUID;
    }


    @Override
    public UUID convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        final UUID value;
        if (source instanceof UUID) {
            value = (UUID) source;
        } else if (source instanceof String) {

        } else {
            throw PARAM_ERROR_HANDLER_0.apply(this, source);
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
