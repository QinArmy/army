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

package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;

import java.util.BitSet;

/**
 * @see Long
 */
public final class MySqlBitType extends _ArmyNoInjectionMapping {

    public static MySqlBitType from(Class<?> javaType) {
        if (javaType != Long.class) {
            throw errorJavaType(MySqlBitType.class, javaType);
        }
        return INSTANCE;
    }

    public static final MySqlBitType INSTANCE = new MySqlBitType();

    /**
     * private constructor
     */
    private MySqlBitType() {
    }


    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        if (meta.serverDatabase() != Database.MySQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return MySQLType.BIT;
    }

    @Override
    public Long convert(MappingEnv env, Object source) throws CriteriaException {
        return toBitLong(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toBitLong(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(DataType dataType, MappingEnv env, Object source) {
        return toBitLong(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    public static long toBitLong(MappingType type, DataType dataType, final Object source, ErrorHandler errorHandler) {
        final long value;
        if (source instanceof Long) {
            value = (Long) source;
        } else if (source instanceof Integer) {
            value = (Integer) source & 0xFFFF_FFFFL;
        } else if (source instanceof Short) {
            value = (Short) source & 0xFFFFL;
        } else if (source instanceof Byte) {
            value = (Byte) source & 0xFFL;
        } else if (source instanceof BitSet) {
            final BitSet v = (BitSet) source;
            if (v.length() > 64) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = v.toLongArray()[0];
        } else if (source instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) source, 2);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


}
