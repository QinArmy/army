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
import io.army.mapping.DayOfWeekType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.time.DayOfWeek;

public class DayOfWeekArrayType extends _ArmyNoInjectionType implements MappingType.SqlArrayType {


    public static DayOfWeekArrayType from(final Class<?> arrayClass) {
        final DayOfWeekArrayType instance;

        if (arrayClass == DayOfWeek[].class) {
            instance = LINEAR;
        } else if (arrayClass == Object.class) {
            instance = UNLIMITED;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(DayOfWeekArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == DayOfWeek.class) {
            instance = new DayOfWeekArrayType(arrayClass);
        } else {
            throw errorJavaType(DayOfWeekArrayType.class, arrayClass);
        }
        return instance;
    }

    public static final DayOfWeekArrayType UNLIMITED = new DayOfWeekArrayType(Object.class);

    public static final DayOfWeekArrayType LINEAR = new DayOfWeekArrayType(DayOfWeek[].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private DayOfWeekArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return DayOfWeek.class;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == DayOfWeek[].class) {
            instance = DayOfWeekType.DEFAULT;
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
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return null;
    }


}
