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
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.MediumIntType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;

public class MediumIntArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static MediumIntArrayType from(final Class<?> arrayClass) {
        final MediumIntArrayType instance;
        final Class<?> componentType;
        if (arrayClass == Integer[].class) {
            instance = LINEAR;
        } else if (arrayClass == int[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(MediumIntArrayType.class, arrayClass);
        } else if ((componentType = ArrayUtils.underlyingComponent(arrayClass)) == int.class
                || componentType == Integer.class) {
            instance = new MediumIntArrayType(arrayClass, componentType);
        } else {
            throw errorJavaType(MediumIntArrayType.class, arrayClass);
        }
        return instance;
    }

    public static MediumIntArrayType fromUnlimited(final Class<?> intClass) {
        final MediumIntArrayType instance;
        if (intClass == Integer.class) {
            instance = UNLIMITED;
        } else if (intClass == int.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(MediumIntArrayType.class, intClass);
        }
        return instance;
    }

    public static final MediumIntArrayType UNLIMITED = new MediumIntArrayType(Object.class, Integer.class);

    public static final MediumIntArrayType LINEAR = new MediumIntArrayType(Integer[].class, Integer.class);

    public static final MediumIntArrayType PRIMITIVE_UNLIMITED = new MediumIntArrayType(Object.class, int.class);

    public static final MediumIntArrayType PRIMITIVE_LINEAR = new MediumIntArrayType(int[].class, int.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;


    /**
     * private constructor
     */
    private MediumIntArrayType(final Class<?> javaType, Class<?> underlyingJavaType) {
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
        } else if (javaType == Integer[].class || javaType == int[].class) {
            instance = MediumIntType.INSTANCE;
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
        return IntegerArrayType.mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        final boolean nonNull = this.underlyingJavaType == int.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, MediumIntArrayType::parseText,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, MediumIntArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == int.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, MediumIntArrayType::parseText, ACCESS_ERROR_HANDLER);
    }

    private static int parseText(final String text, final int offset, final int end) {
        final int value;
        value = Integer.parseInt(text.substring(offset, end));
        if (value < MediumIntType.MIN_VALUE || value > MediumIntType.MAX_VALUE) {
            throw outRange(value);
        }
        return value;
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof Integer)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final int value = (Integer) element;
        if (value < MediumIntType.MIN_VALUE || value > MediumIntType.MAX_VALUE) {
            throw outRange(value);
        }
        appender.accept(element.toString());
    }

    private static IllegalArgumentException outRange(int value) {
        String m = String.format("%s not in medium int range.", value);
        return new IllegalArgumentException(m);
    }


}
