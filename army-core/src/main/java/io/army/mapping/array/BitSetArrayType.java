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
import io.army.mapping.BitSetType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;
import io.army.util.ArrayUtils;
import io.army.util._StringUtils;

import java.util.BitSet;

/**
 * @see BitSetType
 */
public class BitSetArrayType extends _ArmyNoInjectionType implements MappingType.SqlArrayType {

    public static BitSetArrayType from(final Class<?> javaType) {
        final BitSetArrayType instance;

        if (javaType == BitSet[].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(BitSetArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == BitSet.class) {
            instance = new BitSetArrayType(javaType);
        } else {
            throw errorJavaType(BitSetArrayType.class, javaType);
        }
        return instance;
    }


    /**
     * one dimension array of {@link BitSet}
     */
    public static final BitSetArrayType LINEAR = new BitSetArrayType(BitSet[].class);

    /**
     * unlimited dimension array of {@link BitSet}
     */
    public static final BitSetArrayType UNLIMITED = new BitSetArrayType(Object.class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private BitSetArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return BitSet.class;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == BitSet[].class) {
            instance = BitSetType.INSTANCE;
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
        final SQLType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.VARBIT_ARRAY;
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
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, _StringUtils::bitStringToBitSet,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, BitSetArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, _StringUtils::bitStringToBitSet,
                ACCESS_ERROR_HANDLER);
    }


    private static void appendToText(final Object element, final StringBuilder appender) {
        if (!(element instanceof BitSet)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.append(_StringUtils.bitSetToBitString((BitSet) element, true));
    }


}
