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

package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * This class representing the mapping from {@link TextEnum} to {@link SQLType}.
 *
 * @see TextEnum
 * @see NameEnumType
 * @see CodeEnumType
 */
public final class TextEnumType extends MappingType {

    public static TextEnumType from(final Class<?> enumType) {
        final Class<?> actualType;
        actualType = checkEnumClass(enumType);
        return INSTANCE_MAP.computeIfAbsent(actualType, k -> new TextEnumType(actualType, NameEnumType.obtainEnumName(actualType)));
    }

    public static TextEnumType fromParam(final Class<?> enumType, final String enumName) {
        final Class<?> actualEnumType;
        actualEnumType = checkEnumClass(enumType);
        if (!_StringUtils.hasText(enumName)) {
            throw new IllegalArgumentException("no text");
        }

        final String key;
        key = actualEnumType.getName() + '#' + enumName;
        return INSTANCE_MAP.computeIfAbsent(key, k -> new TextEnumType(actualEnumType, enumName));
    }

    private static Class<?> checkEnumClass(final Class<?> javaType) {
        if (!Enum.class.isAssignableFrom(javaType)
                || !TextEnum.class.isAssignableFrom(javaType)
                || CodeEnum.class.isAssignableFrom(javaType)) {
            throw errorJavaType(TextEnumType.class, javaType);
        }
        return ClassUtils.enumClass(javaType);
    }

    private static final ConcurrentMap<Object, TextEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();


    private final Class<?> enumClass;

    private final String enumName;

    private final Map<String, ? extends TextEnum> textMap;

    /**
     * private constructor
     */
    private TextEnumType(final Class<?> enumClass, @Nullable String enumName) {
        this.enumClass = enumClass;
        this.enumName = enumName;
        this.textMap = TextEnum.getTextToEnumMap(enumClass);
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (type instanceof TextEnumType) {
            final TextEnumType o = (TextEnumType) type;
            match = o.enumClass == this.enumClass && Objects.equals(o.enumName, this.enumName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToDataType(this, meta, this.enumName);
    }

    @Override
    public TextEnum convert(MappingEnv env, Object source) throws CriteriaException {
        return toTextEnum(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toTextEnum(dataType, source, PARAM_ERROR_HANDLER).text();
    }

    @Override
    public TextEnum afterGet(DataType dataType, MappingEnv env, Object source) {
        return toTextEnum(dataType, source, ACCESS_ERROR_HANDLER);
    }

    private TextEnum toTextEnum(final DataType dataType, final Object nonNull, final ErrorHandler errorHandler) {
        final TextEnum value;
        if (this.enumClass.isInstance(nonNull)) {
            value = ((TextEnum) nonNull);
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(this, dataType, nonNull, null);
        } else if ((value = this.textMap.get(nonNull)) == null) {
            throw errorHandler.apply(this, dataType, nonNull, null);
        }
        return value;
    }

}
