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
import io.army.mapping.ByteType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

public class ByteArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static ByteArrayType from(final Class<?> javaType) {
        final ByteArrayType instance;
        final Class<?> componentType;
        if (javaType == Byte[].class) {
            instance = LINEAR;
        } else if (javaType == byte[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(ByteArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == byte.class
                || componentType == Byte.class) {
            instance = new ByteArrayType(javaType, componentType);
        } else {
            throw errorJavaType(ByteArrayType.class, javaType);
        }
        return instance;
    }

    public static final ByteArrayType UNLIMITED = new ByteArrayType(Object.class, Byte.class);

    public static final ByteArrayType LINEAR = new ByteArrayType(Byte[].class, Byte.class);

    public static final ByteArrayType PRIMITIVE_UNLIMITED = new ByteArrayType(Object.class, byte.class);

    public static final ByteArrayType PRIMITIVE_LINEAR = new ByteArrayType(byte[].class, byte.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;


    /**
     * private constructor
     */
    private ByteArrayType(final Class<?> javaType, Class<?> underlyingJavaType) {
        this.javaType = javaType;
        this.underlyingJavaType = underlyingJavaType;
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == Byte[].class || javaType == byte[].class) {
            instance = ByteType.INSTANCE;
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
        // currently,same
        return ShortArrayType.mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, ByteArrayType::parseText,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, ByteArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, ByteArrayType::parseText,
                ACCESS_ERROR_HANDLER);
    }


    private static byte parseText(final String text, final int offset, final int end) {
        return Byte.parseByte(text.substring(offset, end));
    }

    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof Byte)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(element);
    }


}
