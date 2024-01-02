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
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.ZonedDateTimeType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;
import io.army.util._TimeUtils;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

public class ZonedDateTimeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static ZonedDateTimeArrayType from(final Class<?> arrayClass) {
        final ZonedDateTimeArrayType instance;
        if (arrayClass == ZonedDateTime[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(ZonedDateTimeArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == ZonedDateTime.class) {
            instance = new ZonedDateTimeArrayType(arrayClass);
        } else {
            throw errorJavaType(ZonedDateTimeArrayType.class, arrayClass);
        }
        return instance;
    }

    public static ZonedDateTimeArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final ZonedDateTimeArrayType LINEAR = new ZonedDateTimeArrayType(ZonedDateTime[].class);

    public static final ZonedDateTimeArrayType UNLIMITED = new ZonedDateTimeArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private ZonedDateTimeArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return ZonedDateTime.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == ZonedDateTime[].class) {
            instance = ZonedDateTimeType.INSTANCE;
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
        return OffsetDateTimeArrayType.mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                ZonedDateTimeArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, ZonedDateTimeArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                ZonedDateTimeArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }


    /*-------------------below static methods -------------------*/

    private static ZonedDateTime parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }
        return ZonedDateTime.parse(timeStr, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof ZonedDateTime)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final String doubleQuote;
        doubleQuote = String.valueOf(_Constant.DOUBLE_QUOTE);

        appender.accept(doubleQuote);
        appender.accept(((ZonedDateTime) element).format(_TimeUtils.OFFSET_DATETIME_FORMATTER_6));
        appender.accept(doubleQuote);

    }


}
