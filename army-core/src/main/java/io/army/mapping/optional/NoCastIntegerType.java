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
import io.army.mapping.IntegerType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._NumericType;
import io.army.mapping.array.IntegerArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

public final class NoCastIntegerType extends _NumericType._IntegerType implements NoCastType {

    public static NoCastIntegerType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(NoCastIntegerType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final NoCastIntegerType INSTANCE = new NoCastIntegerType();

    /**
     * private constructor
     */
    private NoCastIntegerType() {
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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return IntegerArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return IntegerType.mapToDataType(this, meta);
    }

    @Override
    public Integer convert(MappingEnv env, Object source) throws CriteriaException {
        return IntegerType.toInt(this, map(env.serverMeta()), source, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        return IntegerType.toInt(this, dataType, source, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(DataType dataType, final MappingEnv env, Object source) {
        return IntegerType.toInt(this, dataType, source, Integer.MIN_VALUE, Integer.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


}
