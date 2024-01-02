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

/**
 * @see UnsignedIntegerType
 */
public final class UnsignedLongType extends _ArmyNoInjectionMapping
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
        return UnsignedSqlIntType.mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

}
