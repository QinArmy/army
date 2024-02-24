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
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;

import java.math.BigInteger;

/**
 * <p>
 * This class representing the mapping from {@link BigInteger} to (unsigned) bigint.
 * * @see BigInteger
 */
public final class UnsignedBigintType extends _NumericType._UnsignedIntegerType {

    public static UnsignedBigintType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedBigintType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BigInteger MAX_VALUE = new BigInteger(Long.toUnsignedString(-1L));

    public static final UnsignedBigintType INSTANCE = new UnsignedBigintType();

    /**
     * private constructor
     */
    private UnsignedBigintType() {
    }

    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.BIG_LONG;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SQLType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.BIGINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreType.DECIMAL;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public BigInteger convert(MappingEnv env, Object source) throws CriteriaException {
        return UnsignedBigIntegerType.toUnsignedBigInteger(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Number beforeBind(final DataType dataType, MappingEnv env, final Object source) {
        final Number value;
        switch (((SQLType) dataType).database()) {
            case MySQL:
                value = UnsignedBigIntegerType.toUnsignedBigInteger(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            case PostgreSQL:
                value = UnsignedBigIntegerType.toUnsignedBigDecimal(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(DataType dataType, MappingEnv env, final Object source) {
        return UnsignedBigIntegerType.toUnsignedBigInteger(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
