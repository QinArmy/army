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
import io.army.mapping.array.PathArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util._StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>This class map {@link java.nio.file.Path} to sql varchar .
 * <p>If you need to map char ,you can use {@link SqlCharType} instead of this class.
 */
public final class PathType extends _ArmyBuildInMapping implements MappingType.SqlStringType {


    public static PathType from(final Class<?> javaType) {
        if (javaType != Path.class) {
            throw errorJavaType(PathType.class, javaType);
        }
        return INSTANCE;
    }

    public static final PathType INSTANCE = new PathType();


    /**
     * private constructor
     */
    private PathType() {
    }

    @Override
    public Class<?> javaType() {
        return Path.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return PathArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        return StringType.mapToDataType(this, meta);
    }


    @Override
    public Path convert(MappingEnv env, final Object source) throws CriteriaException {
        return toPath(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final String value;
        if (source instanceof Path) {
            value = ((Path) source).toString();
        } else if (source instanceof String) {
            value = (String) source;
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public Path afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return toPath(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    private static Path toPath(MappingType type, DataType dataType, Object source, ErrorHandler errorHandler) {
        final Path value;
        if (source instanceof Path) {
            value = (Path) source;
        } else if (!(source instanceof String)) {
            throw errorHandler.apply(type, dataType, source, null);
        } else if (_StringUtils.hasText((String) source)) {
            value = Paths.get((String) source);
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


}
