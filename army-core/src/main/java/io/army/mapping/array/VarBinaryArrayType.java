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
import io.army.mapping.VarBinaryType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;


/**
 * <p>This class is array type of {@link VarBinaryType}.
 *
 * @see VarBinaryType
 * @see BinaryArrayType
 * @since 0.6.0
 */
public final class VarBinaryArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static VarBinaryArrayType from(final Class<?> javaType) {
        final VarBinaryArrayType instance;

        if (javaType == byte[][].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray() || ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(VarBinaryArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == byte.class) {
            instance = new VarBinaryArrayType(javaType);
        } else {
            throw errorJavaType(VarBinaryArrayType.class, javaType);
        }
        return instance;
    }


    public static final VarBinaryArrayType UNLIMITED = new VarBinaryArrayType(Object.class);

    public static final VarBinaryArrayType LINEAR = new VarBinaryArrayType(byte[][].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private VarBinaryArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return byte[].class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            instance = this;
        } else if (javaType == byte[][].class) {
            instance = VarBinaryType.INSTANCE;
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
                dataType = PostgreType.BYTEA_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }


    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PostgreArrays::parseBytea,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        return PostgreArrays.byteaArrayToText(this, dataType, source, new StringBuilder(), PARAM_ERROR_HANDLER)
                .toString();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, PostgreArrays::parseBytea,
                ACCESS_ERROR_HANDLER);
    }




    /*-------------------below static methods -------------------*/


}
