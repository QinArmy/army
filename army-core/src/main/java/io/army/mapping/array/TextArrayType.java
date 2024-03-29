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
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;
import io.army.util.ArrayUtils;

public class TextArrayType extends ArmyTextArrayType implements MappingType.SqlArrayType {


    public static TextArrayType from(final Class<?> arrayType) {
        final TextArrayType instance;
        if (arrayType == String[].class) {
            instance = LINEAR;
        } else if (arrayType.isArray() && ArrayUtils.underlyingComponent(arrayType) == String.class) {
            instance = new TextArrayType(arrayType);
        } else {
            throw errorJavaType(TextArrayType.class, arrayType);
        }
        return instance;
    }

    public static TextArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final TextArrayType UNLIMITED = new TextArrayType(Object.class);

    public static final TextArrayType LINEAR = new TextArrayType(String[].class);

    /**
     * private constructor
     */
    private TextArrayType(Class<?> javaType) {
        super(javaType);
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == String[].class) {
            instance = TextType.INSTANCE;
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


    /*-------------------below static methods -------------------*/

    static SQLType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SQLType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.TEXT_ARRAY;
                break;
            case Oracle:
            case H2:
            case MySQL:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }

    static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof String)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        PostgreArrays.encodeElement((String) element, appender);

    }


}
