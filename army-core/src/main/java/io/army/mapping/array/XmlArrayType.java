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

package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class XmlArrayType extends _ArmyBuildInMapping {


    public static XmlArrayType from(final Class<?> javaType) {
        if (!javaType.isArray()) {
            throw errorJavaType(XmlArrayType.class, javaType);
        }

        return INSTANCE_MAP.computeIfAbsent(javaType, XmlArrayType::new);
    }

    public static final XmlArrayType UNLIMITED = new XmlArrayType();

    public static final XmlArrayType TEXT_LINEAR = new XmlArrayType(String[].class);

    private static final ConcurrentMap<Class<?>, XmlArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;

    private final Class<?> underlyingType;

    /**
     * @see #UNLIMITED
     */
    private XmlArrayType() {
        this.javaType = Object.class;
        this.underlyingType = Object.class;
    }

    private XmlArrayType(Class<?> javaType) {
        this.javaType = javaType;
        this.underlyingType = ArrayUtils.underlyingComponent(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.VARCHAR_ARRAY;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }


}
