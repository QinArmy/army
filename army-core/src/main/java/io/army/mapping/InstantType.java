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

import java.time.Instant;

public final class InstantType extends _ArmyNoInjectionMapping implements MappingType.SqlIntegerType {

    public static InstantType from(final Class<?> javaType) {
        if (javaType != Instant.class) {
            throw errorJavaType(InstantType.class, javaType);
        }
        return INSTANCE;
    }

    public static final InstantType INSTANCE = new InstantType();

    /**
     * private constructor
     */
    private InstantType() {
    }

    @Override
    public Class<?> javaType() {
        return Instant.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return LongType.mapToDataType(this, meta);
    }


    @Override
    public Instant convert(MappingEnv env, final Object source) throws CriteriaException {
        if (source instanceof Instant) {
            return (Instant) source;
        }
        final long value;
        value = LongType.toLong(this, map(env.serverMeta()), source, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
        return Instant.ofEpochMilli(value);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, final Object source) {
        if (source instanceof Instant) {
            return ((Instant) source).toEpochMilli();
        }
        return LongType.toLong(this, dataType, source, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Instant afterGet(DataType dataType, MappingEnv env, Object source) {
        if (source instanceof Instant) {
            return (Instant) source;
        }
        final long value;
        value = LongType.toLong(this, dataType, source, Long.MIN_VALUE, Long.MAX_VALUE, ACCESS_ERROR_HANDLER);
        return Instant.ofEpochMilli(value);
    }


}
