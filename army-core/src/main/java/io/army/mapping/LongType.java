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
import io.army.sqltype.SQLiteType;

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

    public static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.BIGINT;
                break;
            case PostgreSQL:
                dataType = PostgreType.BIGINT;
                break;
            case SQLite:
                dataType = SQLiteType.BIGINT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }


    public static long toLong(final MappingType type, final DataType dataType, final Object source,
                       final long min, final long max, final ErrorHandler errorHandler) {
        final long value;
        if (source instanceof Long) {
            value = (Long) source;
        } else if (source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = ((Number) source).longValue();
        } else if (source instanceof String) {
            try {
                value = Long.parseLong((String) source);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof BigDecimal) {
            try {
                value = ((BigDecimal) source).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof BigInteger) {
            try {
                value = ((BigInteger) source).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof Double || source instanceof Float) {
            try {
                value = new BigDecimal(source.toString()).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1L : 0L;
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }

        if (value < min || value > max) {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


}
