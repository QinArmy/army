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
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @see UnsignedBigintType
 * @see UnsignedIntegerType
 */
public final class UnsignedLongType extends _ArmyNoInjectionType
        implements MappingType.SqlIntegerType, MappingType.SqlUnsignedNumberType {


    public static final UnsignedLongType INSTANCE = new UnsignedLongType();


    private UnsignedLongType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }


    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }


    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return UnsignedBigintType.mapToDataType(this, meta);
    }

    @Override
    public Long convert(MappingEnv env, Object source) throws CriteriaException {
        return toUnsignedLong(this, map(env.serverMeta()), source, -1L, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return toUnsignedLong(this, dataType, source, -1L, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return toUnsignedLong(this, dataType, source, -1L, ACCESS_ERROR_HANDLER);
    }


    public static long toUnsignedLong(MappingType type, DataType dataType, final Object source, final long max,
                                      ErrorHandler errorHandler) {
        final long value;
        if (source instanceof Long) {
            value = (Long) source;
        } else if (source instanceof Integer) {
            value = (Integer) source & 0xFFFF_FFFFL;
        } else if (source instanceof Short) {
            value = (Short) source & 0xFFFFL;
        } else if (source instanceof Byte) {
            value = (Byte) source & 0xFFL;
        } else if (source instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) source);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof BigDecimal) {
            try {
                value = Long.parseUnsignedLong(((BigDecimal) source).stripTrailingZeros().toPlainString());
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof BigInteger) {
            try {
                value = Long.parseUnsignedLong(source.toString());
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof Double || source instanceof Float) {
            try {
                value = Long.parseUnsignedLong(new BigDecimal(source.toString()).stripTrailingZeros().toPlainString());
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1L : 0L;
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }

        if (max != -1L) {
            if ((max > -1L && value > max) || (max < 0 && value < max)) {
                throw errorHandler.apply(type, dataType, source, null);
            }
        }
        return value;
    }

}
