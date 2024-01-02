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
import io.army.sqltype.SqlType;

import java.util.Map;

@Deprecated
public final class JsonMapType extends MappingType {


    public static JsonMapType from(MappingType keyType, MappingType valueType) {
        throw new UnsupportedOperationException();
    }

    /**
     * private constructor
     */
    private JsonMapType() {
    }

    @Override
    public Class<?> javaType() {
        return Map.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType type, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }


}
