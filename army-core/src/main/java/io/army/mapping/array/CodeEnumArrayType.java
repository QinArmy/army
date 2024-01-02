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
import io.army.mapping.CodeEnumType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util.ArrayUtils;
import io.army.util.ClassUtils;
import io.army.util._Collections;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * <p>This class is mapping class of {@link CodeEnum}.
 *
 * @see CodeEnumType
 */
public class CodeEnumArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static CodeEnumArrayType from(final Class<?> javaType) {
        if (!javaType.isArray()) {
            throw errorJavaType(CodeEnumArrayType.class, javaType);
        }
        final Class<?> enumClass;
        enumClass = ArrayUtils.underlyingComponent(javaType);

        if (!(Enum.class.isAssignableFrom(enumClass) && CodeEnum.class.isAssignableFrom(enumClass))) {
            throw errorJavaType(CodeEnumArrayType.class, javaType);
        } else if (TextEnum.class.isAssignableFrom(enumClass)) {
            throw errorJavaType(CodeEnumArrayType.class, enumClass);
        }
        return INSTANCE_MAP.computeIfAbsent(javaType, key -> new CodeEnumArrayType(javaType, enumClass));
    }

    public static CodeEnumArrayType fromUnlimited(final Class<?> enumClass) {
        if (!(Enum.class.isAssignableFrom(enumClass) && CodeEnum.class.isAssignableFrom(enumClass))) {
            throw errorJavaType(CodeEnumArrayType.class, enumClass);
        } else if (TextEnum.class.isAssignableFrom(enumClass)) {
            throw errorJavaType(CodeEnumArrayType.class, enumClass);
        }
        final Class<?> actualClass;
        actualClass = ClassUtils.enumClass(enumClass);
        return INSTANCE_MAP.computeIfAbsent(actualClass, key -> new CodeEnumArrayType(Object.class, actualClass));
    }


    private static final ConcurrentMap<Class<?>, CodeEnumArrayType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> javaType;

    private final Class<?> enumClass;

    private final Map<Integer, ? extends CodeEnum> codeMap;

    /**
     * private constructor
     */
    private CodeEnumArrayType(Class<?> javaType, Class<?> enumClass) {
        this.javaType = javaType;
        this.enumClass = enumClass;
        this.codeMap = CodeEnum.getCodeToEnumMap(enumClass);
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
            instance = CodeEnumType.from(componentType);
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


    private CodeEnum parseText(final String text, final int offset, final int end) {
        final int code;
        code = Integer.parseInt(text.substring(offset, end));
        final CodeEnum value;
        value = this.codeMap.get(code);
        if (value == null) {
            String m = String.format("code[%s] no appropriate instance of %s", code, this.enumClass.getName());
            throw new IllegalArgumentException(m);
        }
        return value;
    }

    private void appendToText(final Object element, final Consumer<String> appender) {
        if (!this.enumClass.isInstance(element)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(Integer.toString(((CodeEnum) element).code()));
    }


}
