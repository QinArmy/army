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
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.JsonType;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

public final class JsonArrayType extends ArmyJsonArrayType {

    public static JsonArrayType from(final Class<?> arrayClass) {
        final JsonArrayType instance;
        if (!arrayClass.isArray()) {
            throw errorJavaType(JsonArrayType.class, arrayClass);
        } else if (arrayClass == String[].class) {
            instance = TEXT_LINEAR;
        } else {
            instance = new JsonArrayType(arrayClass, ArrayUtils.underlyingComponent(arrayClass));
        }
        return instance;
    }

    public static JsonArrayType fromUnlimited(final Class<?> underlyingJavaType) {
        final JsonArrayType instance;
        if (underlyingJavaType == String.class) {
            instance = TEXT_UNLIMITED;
        } else if (underlyingJavaType.isArray()) {
            throw errorJavaType(JsonArrayType.class, underlyingJavaType);
        } else {
            instance = new JsonArrayType(Object.class, underlyingJavaType);
        }
        return instance;
    }

    public static final JsonArrayType TEXT_LINEAR = new JsonArrayType(String[].class, String.class);

    public static final JsonArrayType TEXT_UNLIMITED = new JsonArrayType(Object.class, String.class);

    /**
     * private constructor
     */
    private JsonArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        super(javaType, underlyingJavaType);
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (ArrayUtils.dimensionOf(javaType) == 1) {
            instance = JsonType.from(this.underlyingJavaType);
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.JSON_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }


}
