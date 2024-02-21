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
import io.army.mapping.NameEnumType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util.ArrayUtils;
import io.army.util.ClassUtils;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentMap;


/**
 * @see Enum
 * @see NameEnumType
 */
public class NameEnumArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static NameEnumArrayType from(final Class<?> arrayClass) {
        if (!arrayClass.isArray()) {
            throw errorJavaType(NameEnumArrayType.class, arrayClass);
        }
        final Class<?> enumClass;
        enumClass = ArrayUtils.underlyingComponent(arrayClass);

        if (!Enum.class.isAssignableFrom(enumClass)
                || CodeEnum.class.isAssignableFrom(enumClass)
                || TextEnum.class.isAssignableFrom(enumClass)) {
            throw errorJavaType(NameEnumArrayType.class, arrayClass);
        }
        return INSTANCE_MAP.computeIfAbsent(arrayClass, key -> new NameEnumArrayType(arrayClass, enumClass));
    }

    public static NameEnumArrayType fromUnlimited(final Class<?> enumClass) {
        if (!Enum.class.isAssignableFrom(enumClass)
                || CodeEnum.class.isAssignableFrom(enumClass)
                || TextEnum.class.isAssignableFrom(enumClass)) {
            throw errorJavaType(NameEnumArrayType.class, enumClass);
        }
        final Class<?> actualClass;
        actualClass = ClassUtils.enumClass(enumClass);
        return INSTANCE_MAP.computeIfAbsent(actualClass, key -> new NameEnumArrayType(Object.class, actualClass));
    }


    private static final ConcurrentMap<Class<?>, NameEnumArrayType> INSTANCE_MAP = _Collections.concurrentHashMap();


    private final Class<?> javaType;

    private final Class<?> enumClass;

    /**
     * private constructor
     */
    private NameEnumArrayType(Class<?> javaType, Class<?> enumClass) {
        this.javaType = javaType;
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.enumClass;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType, componentType;
        final MappingType instance;

        if (javaType == Object.class) {
            instance = this;
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            instance = from(componentType);
        } else {
            instance = NameEnumType.from(componentType);
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
        return StringArrayType.mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, this::parseText, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, this::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, this::parseText, ACCESS_ERROR_HANDLER);
    }

    private Enum<?> parseText(final String text, final int offset, final int end) {
        return NameEnumType.valueOf(this.enumClass, text.substring(offset, end));
    }

    private void appendToText(final Object element, final StringBuilder appender) {
        if (!this.enumClass.isInstance(element)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(((Enum<?>) element).name());
    }

    static DataType mapToDataType(final MappingType type, final ServerMeta meta, final @Nullable String enumName) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.ENUM;
                break;
            case PostgreSQL: {
                if (enumName == null) {
                    dataType = PostgreType.VARCHAR;
                } else {
                    dataType = DataType.from(enumName);
                }
            }
            break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }


}
