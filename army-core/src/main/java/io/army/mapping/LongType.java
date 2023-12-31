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
import io.army.mapping.array.LongArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * This class is mapping class of {@link Long}.
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
 *  to {@link Long},if overflow,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class LongType extends _NumericType._IntegerType {

    public static LongType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(LongType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final LongType INSTANCE = new LongType();

    /**
     * private constructor
     */
    private LongType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return LongArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToDataType(this, meta);
    }


    @Override
    public Long convert(MappingEnv env, Object source) throws CriteriaException {
        return toLong(this, map(env.serverMeta()), source, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toLong(this, dataType, source, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(DataType dataType, MappingEnv env, Object source) {
        return toLong(this, dataType, source, Long.MIN_VALUE, Long.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }

    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.BIGINT;
                break;
            case PostgreSQL:
                dataType = PostgreType.BIGINT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }


    static long toLong(final MappingType type, final DataType dataType, final Object nonNull,
                       final long min, final long max, final ErrorHandler errorHandler) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue();
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseLong((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof BigDecimal) {
            try {
                value = ((BigDecimal) nonNull).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof BigInteger) {
            try {
                value = ((BigInteger) nonNull).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            try {
                value = new BigDecimal(nonNull.toString()).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1L : 0L;
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }

        if (value < min || value > max) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
