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
import io.army.executor.DataAccessException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLiteType;
import io.army.type.TextPath;
import io.army.util.ClassUtils;

import java.io.Reader;

/**
 * @see TinyTextType
 * @see TextType
 * @see MediumTextType
 */
public final class LongText extends _ArmyBuildInType implements MappingType.SqlTextType {

    public static LongText from(final Class<?> javaType) {
        final LongText instance;
        final String clobClasName = "io.army.reactive.type.Clob";
        if (javaType == String.class) {
            instance = STRING;
        } else if (Reader.class.isAssignableFrom(javaType) || TextPath.class.isAssignableFrom(javaType)) {
            instance = new LongText(javaType);
        } else if (!ClassUtils.isPresent(clobClasName, null)) {
            throw errorJavaType(LongText.class, javaType);
        } else if (ClassUtils.isAssignableFrom(clobClasName, null, javaType)) {
            instance = new LongText(javaType);
        } else {
            throw errorJavaType(LongText.class, javaType);
        }
        return instance;
    }

    public static final LongText STRING = new LongText(String.class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private LongText(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.LONGTEXT;
                break;
            case PostgreSQL:
                dataType = PostgreType.TEXT;
                break;
            case SQLite:
                dataType = SQLiteType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Object convert(MappingEnv env, final Object source) throws CriteriaException {
        return convertToObject(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        if (source instanceof String && this.javaType == String.class) {
            return source;
        }
        // TODO
        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return convertToObject(dataType, source, ACCESS_ERROR_HANDLER);
    }


    private Object convertToObject(DataType dataType, final Object source, ErrorHandler errorHandler) {
        if (source instanceof String && this.javaType == String.class) {
            return source;
        }
        // TODO
        throw errorHandler.apply(this, dataType, source, null);
    }


}
