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

import io.army.dialect.LiteralParser;
import io.army.dialect._Constant;
import io.army.env.EscapeMode;
import io.army.mapping.*;
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
public abstract class _SqlRecordSupport extends _ArmyBuildInMapping {


    protected final List<MappingType> columnTypeList;

    protected _SqlRecordSupport(List<MappingType> columnTypeList) {
        this.columnTypeList = columnTypeList;
    }


    protected final SqlRecord parseSqlRecord(final MappingEnv env, final String source, final int offset, final int end) {
        final ServerMeta meta = env.serverMeta();
        final List<MappingType> columnTypeList = this.columnTypeList;
        final int columnTypeSize = columnTypeList.size();

        final SqlRecord record = ArraySqlRecord.create(columnTypeSize);
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
            } else if (ch == _Constant.LEFT_PAREN) {
                if (recordEnd) {
                    throw _Exceptions.parenNotMatch();
                }
                if (leftParenCount == 1 && startIndex < 0) {
                    startIndex = i;
                }
                leftParenCount++;
            } else if (leftParenCount == 0) {
                throw _Exceptions.parenNotMatch();
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
                if (!unlimited && columnIndex >= columnTypeSize) {
                    throw columnSizeNotMatch(columnIndex + 1, columnTypeSize);
                }

                elementText = source.substring(startIndex, endIndex);

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
            throw _Exceptions.parenNotMatch();
        } else if (inDoubleQuote) {
            throw _Exceptions.doubleQuoteNotMatch();
        }

        return record;
    }

    protected final StringBuilder postgreRecordText(final DataType dataType, final MappingEnv env, final SqlRecord record,
                                                    final StringBuilder builder) {
        final List<MappingType> columnTypeList = this.columnTypeList;
        final int columnSize = record.size(), typeSize = columnTypeList.size();
        if (typeSize > 0 && typeSize != columnSize) {
            final IllegalArgumentException error;
            error = _Exceptions.recordColumnCountNotMatch(record, columnSize, this);
            if (this instanceof SqlRecordType) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, record, error);
            } else {
                throw error;
            }
        }

        final EscapeMode mode;
        final int startIndex;
        if (this instanceof SqlRecordType) {
            mode = EscapeMode.DEFAULT;
            startIndex = 0;
        } else {
            mode = EscapeMode.ARRAY_ELEMENT_PART;
            startIndex = builder.length();
        }

        final ServerMeta meta = env.serverMeta();
        final LiteralParser literalParser = env.literalParser();


        final boolean unlimited = typeSize == 0;

        MappingType columnType;
        DataType columnDataType;

        builder.append(_Constant.LEFT_PAREN);
        boolean escapse = false;
        int index = 0;
        for (Object column : record) {
            if (index > 0) {
                builder.append(_Constant.COMMA);
            }
            if (column == null) {
                builder.append(_Constant.NULL);
                index++;
                continue;
            }

            if (unlimited) {
                columnType = TextType.INSTANCE;
            } else {
                columnType = columnTypeList.get(index);
            }
            columnDataType = columnType.map(meta);
            columnType.beforeBind(columnDataType, env, column);

            if (column == DOCUMENT_NULL_VALUE) {
                builder.append(_Constant.NULL);
            } else {
                escapse |= literalParser.parse(columnType, column, mode, builder);
            }

            index++;

        } // loop for

        builder.append(_Constant.RIGHT_PAREN);

        if (escapse && mode == EscapeMode.ARRAY_ELEMENT_PART) {  // array
            builder.insert(startIndex, _Constant.DOUBLE_QUOTE);
            builder.append(_Constant.DOUBLE_QUOTE);
        }

        return builder;
    }


    private IllegalArgumentException columnSizeNotMatch(int recordColumnSize, int columnSize) {
        String m = String.format("record column size[%s] and column size[%s] of %s not match.", recordColumnSize,
                columnSize, getClass().getName());
        return new IllegalArgumentException(m);
    }


}
