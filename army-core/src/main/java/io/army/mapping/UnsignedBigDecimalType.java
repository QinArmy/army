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

import java.math.BigDecimal;

/**
 * <p>
 * This class representing the mapping from {@link BigDecimal} to unsigned decimal.
 * * @see BigDecimal
 */
public final class UnsignedBigDecimalType extends _NumericType._UnsignedNumericType
        implements MappingType.SqlDecimalType {


    public static UnsignedBigDecimalType from(final Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
            throw errorJavaType(UnsignedBigDecimalType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedBigDecimalType INSTANCE = new UnsignedBigDecimalType();


    /**
     * private constructor
     */
    private UnsignedBigDecimalType() {
    }

    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }

    @Override
    public BigDecimal convert(MappingEnv env, Object source) throws CriteriaException {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        return value;
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, Object source) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, dataType, source, PARAM_ERROR_HANDLER);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public BigDecimal afterGet(DataType dataType, MappingEnv env, Object source) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, dataType, source, ACCESS_ERROR_HANDLER);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }


    static SQLType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SQLType sqlType;
         switch (meta.serverDatabase()) {
             case MySQL:
                 sqlType = MySQLType.DECIMAL_UNSIGNED;
                 break;
             case PostgreSQL:
                 sqlType = PostgreType.DECIMAL;
                 break;
             case Oracle:
             case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return sqlType;
    }


}
