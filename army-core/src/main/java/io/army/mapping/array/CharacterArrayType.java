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
import io.army.mapping.CharacterType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;
import io.army.util.ArrayUtils;

/**
 * @see CharacterType
 */
public class CharacterArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static CharacterArrayType from(final Class<?> javaType) {
        final CharacterArrayType instance;

        final Class<?> underlyingJavaType;
        if (javaType == Character[].class) {
            instance = LINEAR;
        } else if (javaType == char[].class) {
            instance = PRIMITIVE_UNLIMITED;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(CharacterArrayType.class, javaType);
        } else if ((underlyingJavaType = ArrayUtils.underlyingComponent(javaType)) == Character.class
                || underlyingJavaType == char.class) {
            instance = new CharacterArrayType(javaType, underlyingJavaType);
        } else {
            throw errorJavaType(CharacterArrayType.class, javaType);
        }
        return instance;
    }

    /**
     * unlimited dimension array of {@code char}
     */
    public static final CharacterArrayType PRIMITIVE_UNLIMITED = new CharacterArrayType(Object.class, char.class);

    /**
     * one dimension array of {@code  char}
     */
    public static final CharacterArrayType PRIMITIVE_LINEAR = new CharacterArrayType(char[].class, char.class);

    /**
     * one dimension array of {@link Character}
     */
    public static final CharacterArrayType LINEAR = new CharacterArrayType(Character[].class, Character.class);

    /**
     * unlimited dimension array of {@link Character}
     */
    public static final CharacterArrayType UNLIMITED = new CharacterArrayType(Object.class, Character.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private CharacterArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
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
        } else if (javaType == Character[].class || javaType == char[].class) {
            instance = CharacterType.INSTANCE;
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
        final SQLType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.CHAR_ARRAY;
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
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, CharacterArrayType::parseText, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, CharacterArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, CharacterArrayType::parseText, ACCESS_ERROR_HANDLER);
    }


    /*-------------------below static methods -------------------*/

    private static char parseText(final String text, final int offset, final int end) {
        if (end - offset != 1) {
            throw new IllegalArgumentException("not char");
        }
        return text.charAt(offset);
    }

    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof Character)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(element);
    }


}
