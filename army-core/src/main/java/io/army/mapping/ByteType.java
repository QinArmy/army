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
import io.army.mapping.array.ByteArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class is mapping class of {@link Byte}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link java.math.BigDecimal},it has a zero fractional part</li>
 *     <li>{@link Boolean} true : 1 , false: 0</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Byte},if overflow,throw {@link io.army.ArmyException}
 ** @since 0.6.0
 */
public final class ByteType extends _NumericType._IntegerType {


    public static ByteType from(final Class<?> javaType) {
        if (javaType != Byte.class) {
            throw errorJavaType(ByteType.class, javaType);
        }
        return INSTANCE;
    }

    public static final ByteType INSTANCE = new ByteType();

    /**
     * private constructor
     */
    private ByteType() {
    }

    @Override
    public Class<?> javaType() {
        return Byte.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return ByteArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.TINYINT;
                break;
            case PostgreSQL:
                type = PostgreType.SMALLINT;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Byte convert(MappingEnv env, Object source) throws CriteriaException {
        return (byte) IntegerType.toInt(this, map(env.serverMeta()), source, Byte.MIN_VALUE, Byte.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Number beforeBind(final DataType dataType, MappingEnv env, final Object source) {
        final int intValue;
        intValue = IntegerType.toInt(this, dataType, source, Byte.MIN_VALUE, Byte.MAX_VALUE, PARAM_ERROR_HANDLER);
        final Number value;
        switch (((SqlType) dataType).database()) {
            case MySQL:
                value = (byte) intValue;
                break;
            case PostgreSQL:
                value = (short) intValue;
                break;
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }


    @Override
    public Byte afterGet(DataType dataType, MappingEnv env, Object source) {
        return (byte) IntegerType.toInt(this, dataType, source, Byte.MIN_VALUE, Byte.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


}
