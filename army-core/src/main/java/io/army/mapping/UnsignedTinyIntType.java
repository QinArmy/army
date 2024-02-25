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
import io.army.sqltype.*;

/**
 * <p>
 * This class representing the mapping from {@link Short} to (unsigned TINY)  INT.
 *
 * @see Short
 */
public final class UnsignedTinyIntType extends _NumericType._UnsignedIntegerType {

    public static UnsignedTinyIntType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(UnsignedTinyIntType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedTinyIntType INSTANCE = new UnsignedTinyIntType();

    /**
     * private constructor
     */
    private UnsignedTinyIntType() {
    }


    @Override
    public Class<?> javaType() {
        return Short.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.SMALL;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TINYINT_UNSIGNED;
                break;
            case PostgreSQL:
                dataType = PostgreType.SMALLINT;
                break;
            case Oracle:
            case SQLite:
                dataType = SQLiteType.INTEGER;
                break;
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }


    @Override
    public Short convert(MappingEnv env, Object source) throws CriteriaException {
        return (short) UnsignedIntegerType.toUnsignedInt(this, map(env.serverMeta()), source, 0xFF, PARAM_ERROR_HANDLER);
    }

    @Override
    public Number beforeBind(final DataType dataType, MappingEnv env, final Object source) {
        final int v;
        v = UnsignedIntegerType.toUnsignedInt(this, dataType, source, 0xFF, PARAM_ERROR_HANDLER);
        final Number value;
        switch (((SQLType) dataType).database()) {
            case MySQL:
            case PostgreSQL:
                value = (short) v;
                break;
            default:
                value = v;
        }
        return value;
    }

    @Override
    public Short afterGet(DataType dataType, MappingEnv env, Object source) {
        return (short) UnsignedIntegerType.toUnsignedInt(this, dataType, source, 0xFF, ACCESS_ERROR_HANDLER);
    }


}
