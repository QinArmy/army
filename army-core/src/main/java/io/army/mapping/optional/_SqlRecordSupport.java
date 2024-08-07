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

import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.ObjectType;
import io.army.mapping._ArmyBuildInType;
import io.army.mapping.array.PostgreArrays;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.type.ArraySqlRecord;
import io.army.type.SqlRecord;
import io.army.util._Exceptions;

import java.util.List;


/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link SqlRecordType}</li>
 *     <li>{@link io.army.mapping.array.SqlRecordArrayType}</li>
 * </ul>
 */
public abstract class _SqlRecordSupport extends _ArmyBuildInType {


    protected final List<MappingType> columnTypeList;

    protected _SqlRecordSupport(List<MappingType> columnTypeList) {
        this.columnTypeList = columnTypeList;
    }


    protected final SqlRecord parseSqlRecord(final MappingEnv env, final String source, final int offset, final int end) {
        final ServerMeta meta = env.serverMeta();
        final List<MappingType> columnTypeList = this.columnTypeList;
        final int columnTypeSize = columnTypeList.size();

        final SqlRecord record = ArraySqlRecord.forSize(columnTypeSize);
        final boolean unlimited = columnTypeSize == 0;

        DataType columnDataType;
        MappingType columnType;
        Object columnValue;
        String elementText;
        boolean inDoubleQuote = false, recordEnd = false;
        int leftParenCount = 0;
        char ch;
        for (int i = offset, startIndex = -1, columnIndex = 0, endIndex; i < end; i++) {
            ch = source.charAt(i);
            if (inDoubleQuote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                } else if (ch == _Constant.DOUBLE_QUOTE) {
                    inDoubleQuote = false;
                }
            } else if (ch == _Constant.DOUBLE_QUOTE) {
                inDoubleQuote = true;
                if (startIndex < 0) {
                    startIndex = i;
                }
            } else if (ch == _Constant.LEFT_PAREN) {
                if (recordEnd) {
                    throw _Exceptions.parenNotMatch(source.substring(offset, offset + 5));
                }
                if (leftParenCount == 1 && startIndex < 0) {
                    startIndex = i;
                }
                leftParenCount++;
            } else if (leftParenCount == 0) {
                throw _Exceptions.parenNotMatch(source.substring(offset, offset + 5));
            } else if (startIndex < 0) {
                if (!Character.isWhitespace(ch)) {
                    startIndex = i;
                }
            } else if (leftParenCount > 1) {
                if (ch == _Constant.RIGHT_PAREN) {
                    leftParenCount--;
                }
            } else if (ch == _Constant.COMMA || ch == _Constant.RIGHT_PAREN) {
                for (endIndex = i - 1; endIndex > startIndex; endIndex--) {
                    if (!Character.isWhitespace(ch)) {
                        endIndex++;
                        break;
                    }

                } // inner loop for
                if (endIndex == startIndex) {
                    endIndex = startIndex + 1;
                }
                if (!unlimited && columnIndex >= columnTypeSize) {
                    throw columnSizeNotMatch(columnIndex + 1, columnTypeSize);
                }

                if (source.charAt(startIndex) == _Constant.DOUBLE_QUOTE) {
                    elementText = PostgreArrays.decodeElement(source, startIndex, endIndex);
                } else {
                    elementText = source.substring(startIndex, endIndex);
                }

                if (_Constant.NULL.equalsIgnoreCase(elementText)) {
                    columnValue = null;
                } else {
                    if (unlimited) {
                        columnType = ObjectType.INSTANCE;
                    } else {
                        columnType = columnTypeList.get(columnIndex++);
                    }

                    columnDataType = columnType.map(meta);
                    columnValue = columnType.afterGet(columnDataType, env, elementText);
                    if (columnValue == DOCUMENT_NULL_VALUE) {
                        columnValue = null;
                    }
                }

                record.add(columnValue);
                if (ch == _Constant.RIGHT_PAREN) {
                    recordEnd = true;
                }
                startIndex = -1; // reset

            } // else if

        } // outer loop for

        if (!recordEnd) {
            throw _Exceptions.parenNotMatch(source.substring(offset, offset + 5));
        } else if (inDoubleQuote) {
            throw _Exceptions.doubleQuoteNotMatch();
        }

        return record;
    }


    private IllegalArgumentException columnSizeNotMatch(int recordColumnSize, int columnSize) {
        String m = String.format("record column size[%s] and column size[%s] of %s not match.", recordColumnSize,
                columnSize, getClass().getName());
        return new IllegalArgumentException(m);
    }


    protected static UnsupportedDialectException dontSupportBind() {
        return new UnsupportedDialectException("record type don't support bind");
    }



}
