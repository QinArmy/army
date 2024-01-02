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
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;

public final class XmlType extends _ArmyBuildInMapping {


    public static XmlType from(final Class<?> javaType) {
        final XmlType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, XmlType::new);
        }
        return instance;
    }

    public static final XmlType TEXT = new XmlType(String.class);

    private static final ConcurrentMap<Class<?>, XmlType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private XmlType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlDataType = MySQLType.TEXT;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.XML;
                break;
            case Oracle:

            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return sqlDataType;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        //TODO
        if (source instanceof String) {
            return (String) source;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) {
        return (String) source;
    }


}
