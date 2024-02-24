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
import io.army.mapping.array.JsonbArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public final class JsonbType extends ArmyJsonType implements MappingType.SqlJsonbType {

    public static JsonbType from(final Class<?> javaType) {
        final JsonbType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, CONSTRUCTOR);
        }
        return instance;
    }

    /**
     * This instance map {@link String} to jsonb
     */
    public static final JsonbType TEXT = new JsonbType(String.class);

    private static final ConcurrentMap<Class<?>, JsonbType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private static final Function<Class<?>, JsonbType> CONSTRUCTOR = JsonbType::new;


    /**
     * private constructor
     */
    private JsonbType(Class<?> javaType) {
        super(javaType);
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return JsonbArrayType.from(this.javaType);
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SQLType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.JSONB;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }



}
