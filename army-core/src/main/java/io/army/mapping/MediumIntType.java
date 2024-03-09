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
import io.army.mapping.array.MediumIntArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLiteType;

/**
 * <p>
 * This class is mapping class of medium {@link Integer}.
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
 *  to medium {@link Integer},if overflow,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class MediumIntType extends _NumericType._IntegerType {

    public static MediumIntType from(final Class<?> javaType) {
        if (javaType != Integer.class) {
            throw errorJavaType(MediumIntType.class, javaType);
        }
        return INSTANCE;
    }

    public static final MediumIntType INSTANCE = new MediumIntType();

    public static final int MAX_VALUE = 0x7FFF_FF;

    public static final int MIN_VALUE = -MAX_VALUE - 1;

    /**
     * private constructor
     */
    private MediumIntType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.MEDIUM;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return MediumIntArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.MEDIUMINT;
                break;
            case PostgreSQL:
                dataType = PostgreType.INTEGER;
                break;
            case SQLite:
                dataType = SQLiteType.MEDIUMINT;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Integer convert(MappingEnv env, Object source) throws CriteriaException {
        return IntegerType.toInt(this, map(env.serverMeta()), source, MIN_VALUE, MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(DataType dataType, MappingEnv env, Object source) {
        return IntegerType.toInt(this, dataType, source, MIN_VALUE, MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(DataType dataType, MappingEnv env, Object source) {
        return IntegerType.toInt(this, dataType, source, MIN_VALUE, MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


}
