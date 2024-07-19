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
import io.army.executor.DataAccessException;
import io.army.mapping.DoubleType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

public class DoubleArrayType extends _ArmyNoInjectionType implements MappingType.SqlArrayType {

    public static DoubleArrayType from(final Class<?> javaType) {
        final DoubleArrayType instance;
        final Class<?> componentType;
        if (javaType == Double[].class) {
            instance = LINEAR;
        } else if (javaType == double[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (!javaType.isArray()) {
            throw errorJavaType(DoubleArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == double.class
                || componentType == Double.class) {
            instance = new DoubleArrayType(javaType, componentType);
        } else {
            throw errorJavaType(DoubleArrayType.class, javaType);
        }
        return instance;
    }

    public static DoubleArrayType fromUnlimited(final Class<?> doubleClass) {
        final DoubleArrayType instance;
        if (doubleClass == Double.class) {
            instance = UNLIMITED;
        } else if (doubleClass == double.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(DoubleArrayType.class, doubleClass);
        }
        return instance;
    }


    public static final DoubleArrayType UNLIMITED = new DoubleArrayType(Object.class, Double.class);

    public static final DoubleArrayType LINEAR = new DoubleArrayType(Double[].class, Double.class);

    public static final DoubleArrayType PRIMITIVE_LINEAR = new DoubleArrayType(double[].class, double.class);

    public static final DoubleArrayType PRIMITIVE_UNLIMITED = new DoubleArrayType(Object.class, double.class);

    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private DoubleArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
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
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == double[].class || javaType == Double[].class) {
            instance = DoubleType.INSTANCE;
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
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.FLOAT8_ARRAY;
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

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, DoubleArrayType::parseText, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, DoubleArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, DoubleArrayType::parseText, ACCESS_ERROR_HANDLER);
    }

    /*-------------------below static methods -------------------*/

    private static double parseText(final String text, final int offset, final int end) {
        return Double.parseDouble(text.substring(offset, end));
    }

    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof Double)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(element);
    }


}
