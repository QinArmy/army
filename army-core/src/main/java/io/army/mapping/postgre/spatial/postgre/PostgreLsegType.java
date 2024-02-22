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

package io.army.mapping.postgre.spatial.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

/**
 * <p>
 * This class representing Postgre lseg type {@link MappingType}
*
 * @see <a href="https://www.postgresql.org/docs/current/datatype-geometric.html#DATATYPE-GEO-TABLE">lseg</a>
 */
public final class PostgreLsegType extends PostgreGeometricType implements MappingType.SqlLineType {


    public static final PostgreLsegType INSTANCE = new PostgreLsegType();

    public static PostgreLsegType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreLsegType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgreLsegType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        //TODO
        throw new UnsupportedOperationException();
    }


    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
