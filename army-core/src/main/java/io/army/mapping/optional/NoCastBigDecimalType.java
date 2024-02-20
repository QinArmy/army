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

package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.BigDecimalType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._NumericType;
import io.army.mapping.array.BigDecimalArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.math.BigDecimal;

public final class NoCastBigDecimalType extends _NumericType implements MappingType.SqlDecimalType, NoCastType {


    public static NoCastBigDecimalType from(Class<?> javaType) {
        if (javaType != BigDecimal.class) {
            throw errorJavaType(NoCastBigDecimalType.class, javaType);
        }
        return INSTANCE;
    }


    public static final NoCastBigDecimalType INSTANCE = new NoCastBigDecimalType();

    /**
     * private constructor
     */
    private NoCastBigDecimalType() {
    }


    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        return BigDecimalType.mapToSqlType(this, meta);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BigDecimalArrayType.LINEAR;
    }

    @Override
    public BigDecimal convert(MappingEnv env, Object source) throws CriteriaException {
        return BigDecimalType.toBigDecimal(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return BigDecimalType.toBigDecimal(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal afterGet(DataType dataType, MappingEnv env, final Object source) {
        return BigDecimalType.toBigDecimal(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
