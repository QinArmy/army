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
 * @see UnsignedSqlIntType
 * @see UnsignedLongType
 */
public final class UnsignedIntegerType extends _ArmyNoInjectionMapping
        implements MappingType.SqlIntegerType, MappingType.SqlUnsignedNumberType {


    public static final UnsignedIntegerType INSTANCE = new UnsignedIntegerType();


    private UnsignedIntegerType() {
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
        return UnsignedSqlIntType.mapToDataType(this, meta);
    }

    @Override
    public Integer convert(MappingEnv env, Object source) throws CriteriaException {
        return toUnsignedInt(this, map(env.serverMeta()), source, -1, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return toUnsignedInt(this, map(env.serverMeta()), source, -1, PARAM_ERROR_HANDLER) & 0xFFFF_FFFFL;
    }

    @Override
    public Integer afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return toUnsignedInt(this, map(env.serverMeta()), source, -1, ACCESS_ERROR_HANDLER);
    }


    /**
     * @param max unsigned int
     */
    public static int toUnsignedInt(final MappingType type, DataType dataType, final Object nonNull, final int max,
                                    final ErrorHandler errorHandler) {
        final int value;
        if (nonNull instanceof Integer) {
            value = (Integer) nonNull;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue();
            if (value < 0) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v > (max & 0xFFFF_FFFFL)) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
            value = (int) v;
        } else if (nonNull instanceof String) {
            try {
                value = Integer.parseUnsignedInt((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof BigDecimal) {
            try {
                value = Integer.parseUnsignedInt(((BigDecimal) nonNull).stripTrailingZeros().toPlainString());
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof BigInteger) {
            try {
                value = Integer.parseUnsignedInt(nonNull.toString());
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            try {
                value = Integer.parseUnsignedInt(new BigDecimal(nonNull.toString()).stripTrailingZeros().toPlainString());
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1 : 0;
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        if (value > (max & 0xFFFF_FFFFL)) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;

    }


}
