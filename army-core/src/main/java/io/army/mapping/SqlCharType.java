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
import io.army.mapping.array.SqlCharArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

/**
 * <p>This class map {@link String} to sql char .
 * <p>If you need to map varchar ,you can use {@link StringType} instead of this class.
 *
 * @since 0.6.0
 */
public final class SqlCharType extends _ArmyBuildInType implements MappingType.SqlStringType {

    public static SqlCharType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(SqlCharType.class, javaType);
        }
        return INSTANCE;
    }

    public static final SqlCharType INSTANCE = new SqlCharType();

    /**
     * private constructor
     */
    private SqlCharType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return SqlCharArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.CHAR;
                break;
            case PostgreSQL:
                dataType = PostgreType.CHAR;
                break;
            case SQLite:
                dataType = SQLiteType.CHAR;
                break;
            case Oracle:
                dataType = OracleDataType.CHAR;
                break;
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return dataType;
    }


    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
