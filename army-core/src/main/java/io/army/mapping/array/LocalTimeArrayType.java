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
import io.army.mapping.LocalTimeType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._TimeUtils;

import java.time.LocalTime;
import java.util.function.Consumer;

public final class LocalTimeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static LocalTimeArrayType from(final Class<?> arrayClass) {
        final LocalTimeArrayType instance;
        if (arrayClass == LocalTime[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(LocalTimeArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == LocalTime.class) {
            instance = new LocalTimeArrayType(arrayClass);
        } else {
            throw errorJavaType(LocalTimeArrayType.class, arrayClass);
        }
        return instance;
    }

    public static LocalTimeArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final LocalTimeArrayType LINEAR = new LocalTimeArrayType(LocalTime[].class);

    public static final LocalTimeArrayType UNLIMITED = new LocalTimeArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private LocalTimeArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return LocalTime.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == LocalTime[].class) {
            instance = LocalTimeType.INSTANCE;
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
                dataType = PostgreType.TIME_ARRAY;
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
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                LocalTimeArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, LocalTimeArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                LocalTimeArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }

    /*-------------------below static methods -------------------*/

    private static LocalTime parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }
        return LocalTime.parse(timeStr, _TimeUtils.TIME_FORMATTER_6);
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof LocalTime)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final String doubleQuote;
        doubleQuote = String.valueOf(_Constant.DOUBLE_QUOTE);

        appender.accept(doubleQuote);
        appender.accept(((LocalTime) element).format(_TimeUtils.TIME_FORMATTER_6));
        appender.accept(doubleQuote);

    }


}
