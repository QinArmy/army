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
import io.army.dialect._Constant;
import io.army.executor.DataAccessException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.MonthType;
import io.army.mapping._ArmyNoInjectionType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;
import io.army.util._StringUtils;

import io.army.lang.Nullable;
import java.time.LocalDate;
import java.time.Month;

public class MonthArrayType extends _ArmyNoInjectionType implements MappingType.SqlArrayType {

    public static MonthArrayType from(final Class<?> arrayClass) {
        final MonthArrayType instance;
        if (arrayClass == Month[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(MonthArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == Month.class) {
            instance = new MonthArrayType(arrayClass, null);
        } else {
            throw errorJavaType(MonthArrayType.class, arrayClass);
        }
        return instance;
    }

    public static MonthArrayType fromParam(final Class<?> arrayClass, final String enumName) {
        if (!_StringUtils.hasText(enumName)) {
            throw new IllegalArgumentException("enumName no text");
        }
        final MonthArrayType instance;
        if (arrayClass == Month[].class) {
            instance = new MonthArrayType(arrayClass, enumName);
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(MonthArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == Month.class) {
            instance = new MonthArrayType(arrayClass, enumName);
        } else {
            throw errorJavaType(MonthArrayType.class, arrayClass);
        }
        return instance;
    }

    public static MonthArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final MonthArrayType LINEAR = new MonthArrayType(Month[].class, null);

    public static final MonthArrayType UNLIMITED = new MonthArrayType(Object.class, null);

    private final Class<?> javaType;

    private final String enumName;

    /**
     * private constructor
     */
    private MonthArrayType(Class<?> javaType, @Nullable String enumName) {
        this.javaType = javaType;
        this.enumName = enumName;
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return Month.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == Month[].class) {
            instance = MonthType.DEFAULT;
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
        return NameEnumArrayType.mapToDataType(this, meta, this.enumName);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                MonthArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, MonthArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                MonthArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }


    /*-------------------below static methods -------------------*/


    private static Month parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }

        final Month value;
        if (timeStr.indexOf('-') < 0) {
            value = Month.valueOf(timeStr);
        } else {
            value = Month.from(LocalDate.parse(timeStr));
        }
        return value;
    }

    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof Month)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(((Month) element).name());

    }


}
