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
import io.army.dialect._Constant;
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
    public SqlRecord convert(MappingEnv env, final Object source) throws CriteriaException {
        return toSqlRecord(map(env.serverMeta()), env, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final String value;
        if (source instanceof SqlRecord) {
            value = postgreRecordText(dataType, env, (SqlRecord) source, new StringBuilder())
                    .toString();
        } else if (source instanceof String && isRecordText((String) source)) {
            if (this == UNLIMITED) {
                value = (String) source;
            } else if (readColumnSize(dataType, (String) source) == this.columnTypeList.size()) {
                value = (String) source;
            } else {
                final IllegalArgumentException e = new IllegalArgumentException("column size not match");
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else {

            final MappingType type;
            if (this == UNLIMITED) {
                type = ObjectType.INSTANCE;
            } else if (this.columnTypeList.size() == 1) {
                type = this.columnTypeList.get(0);
            } else {
                final IllegalArgumentException e = new IllegalArgumentException("column size not match");
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
            }
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.LEFT_PAREN);
            env.literalParser().parse(type, source, false, builder);
            builder.append(_Constant.RIGHT_PAREN);

            value = builder.toString();
        }
        return value;
    }

    @Override
    public SqlRecord afterGet(DataType dataType, MappingEnv env, final Object source) throws DataAccessException {
        return toSqlRecord(dataType, env, source, ACCESS_ERROR_HANDLER);
    }


    private SqlRecord toSqlRecord(final DataType dataType, final MappingEnv env, final Object source,
                                  final ErrorHandler errorHandler) {
        final SqlRecord value;
        if (source instanceof SqlRecord) {
            if (this != UNLIMITED && ((SqlRecord) source).size() != this.columnTypeList.size()) {
                final IllegalArgumentException error;
                error = _Exceptions.recordColumnCountNotMatch((SqlRecord) source, this.columnTypeList.size(), this);
                throw errorHandler.apply(this, dataType, source, error);
            }
            value = (SqlRecord) source;
        } else if (source instanceof String && isRecordText((String) source)) {
            try {
                value = parseSqlRecord(env, (String) source, 0, ((String) source).length());
            } catch (Exception e) {
                throw errorHandler.apply(this, dataType, source, e);
            }
        } else if (this == UNLIMITED) {
            value = ArraySqlRecord.forSize(1);
            value.add(ObjectType.INSTANCE.afterGet(dataType, env, source));
        } else if (this.columnTypeList.size() == 1) {
            value = ArraySqlRecord.forSize(1);
            value.add(this.columnTypeList.get(0).afterGet(dataType, env, source));
        } else {
            throw errorHandler.apply(this, dataType, source, null);
        }
        return value;
    }


    private int readColumnSize(final DataType dataType, final String source) {
        final int length = source.length();
        boolean inDoubleQuote = false, recordEnd = false;
        int leftParenCount = 0;
        char ch;
        int commaCount = 0;
        for (int i = 0; i < length; i++) {
            ch = source.charAt(i);
            if (inDoubleQuote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                } else if (ch == _Constant.DOUBLE_QUOTE) {
                    inDoubleQuote = false;
                }
            } else if (ch == _Constant.DOUBLE_QUOTE) {
                inDoubleQuote = true;
            } else if (ch == _Constant.LEFT_PAREN) {
                if (recordEnd) {
                    throw _Exceptions.parenNotMatch(source.substring(i, 3));
                }
                leftParenCount++;
            } else if (leftParenCount == 0) {
                throw _Exceptions.parenNotMatch(source.substring(i, 3));
            } else if (ch == _Constant.COMMA) {
                commaCount++;
            } else if (ch == _Constant.RIGHT_PAREN) {
                if (leftParenCount > 1) {
                    leftParenCount--;
                } else {
                    recordEnd = true;
                }
            }

        } // loop for

        if (!recordEnd) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, _Exceptions.parenNotMatch(source.substring(0, 3)));
        } else if (inDoubleQuote) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, _Exceptions.doubleQuoteNotMatch());
        }
        return commaCount + 1;
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
