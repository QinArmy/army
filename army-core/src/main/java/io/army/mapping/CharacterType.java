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
import io.army.executor.DataAccessException;
import io.army.mapping.array.CharacterArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.math.BigDecimal;
import java.util.BitSet;

public final class CharacterType extends _ArmyBuildInType implements MappingType.SqlStringType {


    public static final CharacterType INSTANCE = new CharacterType();

    public static CharacterType from(final Class<?> javaType) {
        if (javaType != Character.class) {
            throw errorJavaType(CharacterType.class, javaType);
        }
        return INSTANCE;
    }

    /**
     * private constructor
     */
    private CharacterType() {
    }

    @Override
    public Class<?> javaType() {
        return Character.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return CharacterArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SQLType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.CHAR;
                break;
            case PostgreSQL:
                type = PostgreType.CHAR;
                break;
            case SQLite:
                type = SQLiteType.VARCHAR;
                break;
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return toCharacter(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(final DataType dataType, final MappingEnv env, final Object source)
            throws CriteriaException {
        final String value;
        if (source instanceof Character) {
            value = source.toString();
        } else if (source instanceof String) {
            if (((String) source).length() != 1) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            value = ((String) source).substring(0, 1);
        } else if (source instanceof Number) {
            final String v;
            if (source instanceof BigDecimal) {
                v = ((BigDecimal) source).toPlainString();
            } else {
                v = source.toString();
            }
            if (v.length() != 1) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            value = v.substring(0, 1);
        } else if (source instanceof BitSet) {
            final BitSet v = (BitSet) source;
            if (v.length() != 1) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            if (v.get(0)) {
                value = "1";
            } else {
                value = "0";
            }
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public Character afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return toCharacter(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    public static Character toCharacter(MappingType type, DataType dataType, Object source, ErrorHandler errorHandler) {
        final Character value;
        if (source instanceof Character) {
            value = (Character) source;
        } else if (!(source instanceof String)) {
            throw errorHandler.apply(type, dataType, source, null);
        } else if (((String) source).length() == 1) {
            value = ((String) source).charAt(0);
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }

        return value;

    }


}
