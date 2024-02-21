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
import io.army.mapping.ShortType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

public class ShortArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static ShortArrayType from(final Class<?> arrayClass) {
        final ShortArrayType instance;
        final Class<?> componentType;
        if (arrayClass == Short[].class) {
            instance = LINEAR;
        } else if (arrayClass == short[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(ShortArrayType.class, arrayClass);
        } else if ((componentType = ArrayUtils.underlyingComponent(arrayClass)) == short.class
                || componentType == Short.class) {
            instance = new ShortArrayType(arrayClass, componentType);
        } else {
            throw errorJavaType(ShortArrayType.class, arrayClass);
        }
        return instance;
    }

    public static ShortArrayType fromUnlimited(final Class<?> shortClass) {
        final ShortArrayType instance;
        if (shortClass == Short.class) {
            instance = UNLIMITED;
        } else if (shortClass == short.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(ShortArrayType.class, shortClass);
        }
        return instance;
    }

    public static final ShortArrayType UNLIMITED = new ShortArrayType(Object.class, Short.class);

    public static final ShortArrayType LINEAR = new ShortArrayType(Short[].class, Short.class);

    public static final ShortArrayType PRIMITIVE_UNLIMITED = new ShortArrayType(Object.class, short.class);

    public static final ShortArrayType PRIMITIVE_LINEAR = new ShortArrayType(short[].class, short.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;


    /**
     * private constructor
     */
    private ShortArrayType(final Class<?> javaType, Class<?> underlyingJavaType) {
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
        } else if (javaType == Short[].class || javaType == short[].class) {
            instance = ShortType.INSTANCE;
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
        return mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        final boolean nonNull = this.underlyingJavaType == short.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, ShortArrayType::parseText,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, ShortArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == short.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, ShortArrayType::parseText, ACCESS_ERROR_HANDLER);
    }

    /*-------------------below static methods -------------------*/

    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.SMALLINT_ARRAY;
                break;
            case Oracle:
            case H2:
            case MySQL:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }

    private static short parseText(final String text, final int offset, final int end) {
        return Short.parseShort(text.substring(offset, end));
    }

    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof Short)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(element);
    }


}
