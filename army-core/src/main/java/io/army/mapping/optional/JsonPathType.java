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

package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;

public final class JsonPathType extends _ArmyBuildInMapping implements MappingType.SqlJsonPathType {

    public static JsonPathType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(JsonPathType.class, javaType);
        }
        return INSTANCE;
    }

    public static final JsonPathType INSTANCE = new JsonPathType();

    /**
     * private constructor
     */
    private JsonPathType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SQLType type;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                type = PostgreType.JSONPATH;
                break;
            case MySQL:
                type = MySQLType.VARCHAR;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof String)) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        return (String) source;
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof String)) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return (String) source;
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return (String) source;
    }


}
