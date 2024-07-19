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
import io.army.mapping.MonthDayType;
import io.army.mapping._ArmyNoInjectionType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.time.LocalDate;
import java.time.MonthDay;

public class MonthDayArrayType extends _ArmyNoInjectionType implements MappingType.SqlArrayType {


    public static MonthDayArrayType from(final Class<?> arrayClass) {
        final MonthDayArrayType instance;
        if (arrayClass == MonthDay[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(MonthDayArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == MonthDay.class) {
            instance = new MonthDayArrayType(arrayClass);
        } else {
            throw errorJavaType(MonthDayArrayType.class, arrayClass);
        }
        return instance;
    }

    public static MonthDayArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final MonthDayArrayType LINEAR = new MonthDayArrayType(MonthDay[].class);

    public static final MonthDayArrayType UNLIMITED = new MonthDayArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private MonthDayArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return MonthDay.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == MonthDay[].class) {
            instance = MonthDayType.INSTANCE;
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
        return LocalDateArrayType.mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                MonthDayArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, MonthDayArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                MonthDayArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }


    /*-------------------below static methods -------------------*/


    private static MonthDay parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }

        final MonthDay value;
        if (timeStr.length() == 5) {
            value = MonthDay.parse(timeStr);
        } else {
            value = MonthDay.from(LocalDate.parse(timeStr));
        }
        return value;
    }

    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof MonthDay)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        appender.append(_Constant.DOUBLE_QUOTE);
        appender.append("1970-");
        appender.append(element);
        appender.append(_Constant.DOUBLE_QUOTE);

    }


}
