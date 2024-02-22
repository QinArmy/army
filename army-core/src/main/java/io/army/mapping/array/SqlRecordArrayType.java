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
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.UnaryGenericsMapping;
import io.army.mapping.optional.SqlRecordType;
import io.army.mapping.optional._SqlRecordSupport;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.SqlRecord;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


/**
 * @see SqlRecord
 * @see io.army.mapping.optional.SqlRecordType
 */
public class SqlRecordArrayType extends _SqlRecordSupport implements MappingType.SqlArrayType {

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


    public static SqlRecordArrayType fromList(final @Nullable Supplier<List<SqlRecord>> constructor,
                                              final List<MappingType> columnTypeList) {
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        }
        return new ListType(columnTypeList, constructor);
    }


    public static MappingType elementTypeOf(final SqlRecordArrayType arrayType) {
        final Class<?> javaType = arrayType.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = arrayType;
        } else if (javaType == SqlRecord[].class || arrayType instanceof ListType) {
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
        } else if (arrayType instanceof ListType) {
            instance = new SqlRecordArrayType(SqlRecord[][].class, arrayType.columnTypeList);
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
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final Class<?> underlyingJavaType() {
        return SqlRecord.class;
    }

    @Override
    public final MappingType elementType() {
        return elementTypeOf(this);
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        return arrayTypeOf(this);
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.RECORD_ARRAY;
    }

    @Override
    public final Object convert(MappingEnv env, Object source) throws CriteriaException {
        if (this == UNLIMITED) {
            throw errorUseCase();
        }
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PostgreArrays::decodeElement,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(final DataType dataType, final MappingEnv env, Object source) throws CriteriaException {
        if (this == UNLIMITED) {
            throw errorUseCase();
        }
        final BiConsumer<Object, StringBuilder> consumer;
        consumer = (o, c) -> appendToText(dataType, env, o, c);

        return PostgreArrays.arrayBeforeBind(source, consumer, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(DataType dataType, final MappingEnv env, Object source) throws DataAccessException {
        final TextFunction<?> function;
        function = (text, offset, end) -> decodeElement(env, text, offset, end);
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, function, ACCESS_ERROR_HANDLER);
    }


    private void appendToText(final DataType dataType, final MappingEnv env, final Object element, final StringBuilder builder) {
        if (!(element instanceof SqlRecord)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        postgreRecordText(dataType, env, (SqlRecord) element, builder);

    }

    private SqlRecord decodeElement(final MappingEnv env, String text, final int offset, final int end) {
        final SqlRecord record;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            text = PostgreArrays.decodeElement(text, offset, end);
            record = parseSqlRecord(env, text, 0, text.length());
        } else {
            record = parseSqlRecord(env, text, offset, end);
        }
        return record;
    }


    private static CriteriaException errorUseCase() {
        String m = String.format("%s.UNLIMITED only can use read column from database", SqlRecordArrayType.class.getName());
        return new CriteriaException(m);
    }


    private static final class ListType extends SqlRecordArrayType
            implements UnaryGenericsMapping.ListMapping<SqlRecord> {

        private final Supplier<List<SqlRecord>> constructor;

        private ListType(List<MappingType> columnTypeList, @Nullable Supplier<List<SqlRecord>> constructor) {
            super(List.class, columnTypeList);
            this.constructor = constructor;
        }

        @Override
        public Class<SqlRecord> genericsType() {
            return SqlRecord.class;
        }

        @Override
        public Supplier<List<SqlRecord>> listConstructor() {
            return this.constructor;
        }


    } // ListType


}
