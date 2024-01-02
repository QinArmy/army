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
import io.army.mapping.JsonbType;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

public final class JsonbArrayType extends ArmyJsonArrayType implements MappingType.SqlArrayType {

    public static JsonbArrayType from(final Class<?> arrayClass) {
        final JsonbArrayType instance;
        if (!arrayClass.isArray()) {
            throw errorJavaType(JsonbArrayType.class, arrayClass);
        } else if (arrayClass == String[].class) {
            instance = TEXT_LINEAR;
        } else {
            instance = new JsonbArrayType(arrayClass, ArrayUtils.underlyingComponent(arrayClass));
        }
        return instance;
    }

    public static JsonbArrayType fromUnlimited(final Class<?> underlyingJavaType) {
        final JsonbArrayType instance;
        if (underlyingJavaType == String.class) {
            instance = TEXT_UNLIMITED;
        } else if (underlyingJavaType.isArray()) {
            throw errorJavaType(JsonbArrayType.class, underlyingJavaType);
        } else {
            instance = new JsonbArrayType(Object.class, underlyingJavaType);
        }
        return instance;
    }

    public static final JsonbArrayType TEXT_LINEAR = new JsonbArrayType(String[].class, String.class);

    public static final JsonbArrayType TEXT_UNLIMITED = new JsonbArrayType(Object.class, String.class);

    /**
     * private constructor
     */
    private JsonbArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        super(javaType, underlyingJavaType);
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (ArrayUtils.dimensionOf(javaType) == 1) {
            instance = JsonbType.from(this.underlyingJavaType);
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
                dataType = PostgreType.JSONB_ARRAY;
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
