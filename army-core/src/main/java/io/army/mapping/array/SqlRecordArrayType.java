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

package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.function.TextFunction;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.optional.SqlRecordType;
import io.army.mapping.optional._SqlRecordSupport;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.SqlRecord;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * @see SqlRecord
 * @see io.army.mapping.optional.SqlRecordType
 */
public final class SqlRecordArrayType extends _SqlRecordSupport implements MappingType.SqlArrayType {

    public static SqlRecordArrayType fromColumn(final Class<?> arrayType, final MappingType columnType) {
        Objects.requireNonNull(columnType);
        final SqlRecordArrayType instance;
        if (arrayType.isArray() && ArrayUtils.underlyingComponent(arrayType) == SqlRecord.class) {
            instance = new SqlRecordArrayType(arrayType, Collections.singletonList(columnType));
        } else {
            throw errorJavaType(SqlRecordArrayType.class, arrayType);
        }
        return instance;
    }


    public static SqlRecordArrayType fromRow(final Class<?> arrayType, final List<MappingType> columnTypeList) {
        final SqlRecordArrayType instance;
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        } else if (arrayType.isArray() && ArrayUtils.underlyingComponent(arrayType) == SqlRecord.class) {
            instance = new SqlRecordArrayType(arrayType, _Collections.asUnmodifiableList(columnTypeList));
        } else {
            throw errorJavaType(SqlRecordArrayType.class, arrayType);
        }
        return instance;
    }




    public static MappingType elementTypeOf(final SqlRecordArrayType arrayType) {
        final Class<?> javaType = arrayType.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = arrayType;
        } else if (javaType == SqlRecord[].class) {
            instance = SqlRecordType.fromRow(arrayType.columnTypeList);
        } else {
            instance = new SqlRecordArrayType(javaType.getComponentType(), arrayType.columnTypeList);
        }
        return instance;
    }

    public static SqlRecordArrayType arrayTypeOf(final SqlRecordArrayType arrayType) {
        final Class<?> javaType = arrayType.javaType;
        final SqlRecordArrayType instance;
        if (javaType == Object.class) { // unlimited dimension array
            instance = arrayType;
        } else {
            instance = new SqlRecordArrayType(ArrayUtils.arrayClassOf(javaType), arrayType.columnTypeList);
        }
        return instance;
    }

    public static SqlRecordArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final SqlRecordArrayType UNLIMITED = new SqlRecordArrayType(Object.class, Collections.emptyList());

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private SqlRecordArrayType(Class<?> javaType, List<MappingType> columnTypeList) {
        super(columnTypeList);
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return SqlRecord.class;
    }

    @Override
    public MappingType elementType() {
        return elementTypeOf(this);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return arrayTypeOf(this);
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.RECORD_ARRAY;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, dontSupportBind());
    }

    @Override
    public String beforeBind(final DataType dataType, final MappingEnv env, Object source) throws CriteriaException {
        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, dontSupportBind());
    }

    @Override
    public Object afterGet(DataType dataType, final MappingEnv env, Object source) throws DataAccessException {
        final TextFunction<?> function;
        function = (text, offset, end) -> parseSqlRecord(env, text, offset, end);
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, function, ACCESS_ERROR_HANDLER);
    }




}
