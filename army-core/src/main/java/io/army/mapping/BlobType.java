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
import io.army.mapping.array.BlobArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

/**
 * <p>
 * This class is mapping class of {@code byte[]}.
 * * @see BinaryType
 *
 * @see MediumBlobType
 * @since 0.6.0
 */
public final class BlobType extends _ArmyBuildInType implements MappingType.SqlBlobType {


    public static BlobType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(BlobType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BlobType INSTANCE = new BlobType();

    /**
     * private constructor
     */
    private BlobType() {
    }


    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.MEDIUM;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BlobArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SQLType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.BLOB;
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
