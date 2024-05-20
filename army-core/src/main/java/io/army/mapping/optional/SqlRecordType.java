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
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect.impl._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.ObjectType;
import io.army.mapping.array.SqlRecordArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.ArraySqlRecord;
import io.army.type.SqlRecord;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * <p>This class mapping List to database record (for example : postgre record ,oid : 2249)
 *
 * @see io.army.type.SqlRecord
 * @see io.army.mapping.array.SqlRecordArrayType
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-type.html">Postgre pg_type table ,oid : 2249</a>
 */
public final class SqlRecordType extends _SqlRecordSupport implements MappingType.SqlRecordColumnType {

    public static SqlRecordType fromColumn(final MappingType columnType) {
        Objects.requireNonNull(columnType);
        return new SqlRecordType(Collections.singletonList(columnType));
    }


    public static SqlRecordType fromRow(final List<MappingType> columnTypeList) {
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        }
        return new SqlRecordType(_Collections.asUnmodifiableList(columnTypeList));
    }


    public static SqlRecordType fromUnlimited() {
        return UNLIMITED;
    }


    public static final SqlRecordType UNLIMITED = new SqlRecordType(Collections.emptyList());

    /**
     * private constructor
     */
    private SqlRecordType(List<MappingType> columnTypeList) {
        super(columnTypeList);
    }

    @Override
    public Class<?> javaType() {
        return SqlRecord.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.RECORD;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final MappingType instance;
        switch (this.columnTypeList.size()) {
            case 0:
                instance = SqlRecordArrayType.UNLIMITED;
                break;
            case 1:
                instance = SqlRecordArrayType.fromColumn(SqlRecord[].class, this.columnTypeList.get(0));
                break;
            default:
                instance = SqlRecordArrayType.fromRow(SqlRecord[].class, this.columnTypeList);
        }
        return instance;
    }

    @Override
    public Object convert(MappingEnv env, final Object source) throws CriteriaException {
        throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, dontSupportBind());
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, dontSupportBind());
    }

    @Override
    public SqlRecord afterGet(DataType dataType, MappingEnv env, final Object source) throws DataAccessException {
        final SqlRecord value;
        if (source instanceof SqlRecord) {
            if (this != UNLIMITED && ((SqlRecord) source).size() != this.columnTypeList.size()) {
                final IllegalArgumentException error;
                error = _Exceptions.recordColumnCountNotMatch((SqlRecord) source, this.columnTypeList.size(), this);
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, error);
            }
            value = (SqlRecord) source;
        } else if (source instanceof String && isRecordText((String) source)) {
            try {
                value = parseSqlRecord(env, (String) source, 0, ((String) source).length());
            } catch (Exception e) {
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else if (this == UNLIMITED) {
            value = ArraySqlRecord.forSize(1);
            value.add(ObjectType.INSTANCE.afterGet(dataType, env, source));
        } else if (this.columnTypeList.size() == 1) {
            value = ArraySqlRecord.forSize(1);
            value.add(this.columnTypeList.get(0).afterGet(dataType, env, source));
        } else {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }


    private static boolean isRecordText(final String source) {
        final int length = source.length();
        final boolean match;
        if (length < 3) {
            match = false;
        } else {
            match = source.charAt(0) == _Constant.LEFT_PAREN
                    && source.charAt(length - 1) == _Constant.RIGHT_PAREN;
        }
        return match;
    }


}
