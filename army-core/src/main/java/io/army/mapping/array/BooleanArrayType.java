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
import io.army.mapping.BooleanType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;

public class BooleanArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static BooleanArrayType from(final Class<?> javaType) {
        final BooleanArrayType instance;

        final Class<?> underlyingJavaType;
        if (javaType == Boolean[].class) {
            instance = LINEAR;
        } else if (javaType == boolean[].class) {
            instance = PRIMITIVE_UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(BooleanArrayType.class, javaType);
        } else if ((underlyingJavaType = ArrayUtils.underlyingComponent(javaType)) == Boolean.class
                || underlyingJavaType == boolean.class) {
            instance = new BooleanArrayType(javaType, underlyingJavaType);
        } else {
            throw errorJavaType(BooleanArrayType.class, javaType);
        }
        return instance;
    }

    public static BooleanArrayType fromUnlimited(final Class<?> booleanClass) {
        final BooleanArrayType instance;
        if (booleanClass == Boolean.class) {
            instance = UNLIMITED;
        } else if (booleanClass == boolean.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(BooleanArrayType.class, booleanClass);
        }
        return instance;
    }

    /**
     * unlimited dimension array of {@code boolean}
     */
    public static final BooleanArrayType PRIMITIVE_UNLIMITED = new BooleanArrayType(Object.class, boolean.class);

    /**
     * one dimension array of {@code  boolean}
     */
    public static final BooleanArrayType PRIMITIVE_LINEAR = new BooleanArrayType(boolean[].class, boolean.class);

    /**
     * one dimension array of {@link Boolean}
     */
    public static final BooleanArrayType LINEAR = new BooleanArrayType(Boolean[].class, Boolean.class);

    /**
     * unlimited dimension array of {@link Boolean}
     */
    public static final BooleanArrayType UNLIMITED = new BooleanArrayType(Object.class, Boolean.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private BooleanArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
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
        } else if (javaType == Boolean[].class || javaType == boolean[].class) {
            instance = BooleanType.INSTANCE;
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
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.BOOLEAN_ARRAY;
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
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, BooleanArrayType::parseText, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, BooleanArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, BooleanArrayType::parseText, ACCESS_ERROR_HANDLER);
    }


    /*-------------------below static methods -------------------*/

    private static Boolean parseText(final String text, final int offset, final int end) {

        final Boolean value;
        if (text.regionMatches(true, offset, "true", 0, 4)) {
            if (offset + 4 != end) {
                throw new IllegalArgumentException("not boolean");
            }
            value = Boolean.TRUE;
        } else if (text.regionMatches(true, offset, "false", 0, 5)) {
            if (offset + 5 != end) {
                throw new IllegalArgumentException("not boolean");
            }
            value = Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("not boolean");
        }
        return value;
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof Boolean)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(element.toString());
    }


}
