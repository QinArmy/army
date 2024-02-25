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
import io.army.mapping.array.VarBinaryArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

/**
 * <p>This class map {@code byte[]} to sql varbinary type.
 * <p>If you need to map binary ,you can use {@link BinaryType} instead of this class.
 *
 * @see BinaryType
 */
public final class VarBinaryType extends _ArmyBuildInMapping implements MappingType.SqlBinaryType {

    public static VarBinaryType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(VarBinaryType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final VarBinaryType INSTANCE = new VarBinaryType();


    /**
     * private constructor
     */
    private VarBinaryType() {
    }

    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.VARBINARY;
                break;
            case PostgreSQL:
                dataType = PostgreType.BYTEA;
                break;
            case SQLite:
                dataType = SQLiteType.BLOB;
                break;
            case Oracle:
                dataType = OracleDataType.BLOB;
                break;
            case H2:
                dataType = H2DataType.VARBINARY;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return dataType;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return VarBinaryArrayType.LINEAR;
    }


    @Override
    public byte[] convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        return (byte[]) source;
    }

    @Override
    public byte[] beforeBind(DataType dataType, MappingEnv env, final Object source) {
        if (!(source instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return (byte[]) source;
    }

    @Override
    public byte[] afterGet(DataType dataType, MappingEnv env, final Object source) {
        if (!(source instanceof byte[])) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return (byte[]) source;
    }


}
