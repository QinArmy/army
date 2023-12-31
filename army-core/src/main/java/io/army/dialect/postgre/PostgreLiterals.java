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

package io.army.dialect.postgre;

import io.army.dialect._Constant;
import io.army.dialect._Literals;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;
import io.army.session.executor.ExecutorSupport;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.lang.reflect.Array;

abstract class PostgreLiterals extends _Literals {

    private PostgreLiterals() {
    }


    static void postgreBackslashEscapes(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                        final StringBuilder sqlBuilder) {
        if (!(value instanceof String)) {//TODO think long string
            throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
        }

        final char[] charArray = ((String) value).toCharArray();
        final int startIndex;
        startIndex = sqlBuilder.length();

        sqlBuilder.append(_Constant.QUOTE);
        int lastWritten = 0;
        char followChar = _Constant.NUL_CHAR;
        for (int i = 0; i < charArray.length; i++) {
            switch (charArray[i]) {
                case _Constant.QUOTE: {
                    if (i > lastWritten) {
                        sqlBuilder.append(charArray, lastWritten, i - lastWritten);
                    }
                    sqlBuilder.append(_Constant.QUOTE);
                    lastWritten = i;//not i + 1 as current char wasn't written
                }
                continue;
                case _Constant.BACK_SLASH:
                    followChar = _Constant.BACK_SLASH;
                    break;
                case _Constant.NUL_CHAR:
                    followChar = '0';
                    break;
                case '\b':
                    followChar = 'b';
                    break;
                case '\f':
                    followChar = 'f';
                    break;
                case '\n':
                    followChar = 'n';
                    break;
                case '\r':
                    followChar = 'r';
                    break;
                case '\t':
                    followChar = 't';
                    break;
                default:
                    continue;
            }

            if (i > lastWritten) {
                sqlBuilder.append(charArray, lastWritten, i - lastWritten);
            }
            sqlBuilder.append(_Constant.BACK_SLASH)
                    .append(followChar);
            lastWritten = i + 1;


        }// for

        if (lastWritten < charArray.length) {
            sqlBuilder.append(charArray, lastWritten, charArray.length - lastWritten);
        }
        if (followChar != _Constant.NUL_CHAR) {
            sqlBuilder.insert(startIndex, 'E');
        }
        sqlBuilder.append(_Constant.QUOTE);

    }


    static void postgreBitString(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                 final StringBuilder sqlBuilder) {
        if (!(value instanceof String)) {
            throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
        } else if (!_StringUtils.isBinary((String) value)) {
            throw _Exceptions.valueOutRange(dataType, value);
        }

        sqlBuilder.append('B')
                .append(_Constant.QUOTE)
                .append(value)
                .append(_Constant.QUOTE);

    }


    static void appendSimpleTypeArray(final MappingType mappingType, final DataType dataType, final Object array,
                                      final StringBuilder sqlBuilder,
                                      final ArrayElementHandler handler) {

        final int length, dimension;
        dimension = ArrayUtils.dimensionOf(array.getClass());
        length = Array.getLength(array);


        sqlBuilder.append(_Constant.LEFT_BRACE);
        Object component;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.COMMA);
            }
            component = Array.get(array, i);
            if (component == null) {
                sqlBuilder.append(_Constant.NULL);
            } else if (dimension > 1) {
                appendSimpleTypeArray(mappingType, dataType, component, sqlBuilder, handler);
            } else {
                handler.appendElement(mappingType, dataType, component, sqlBuilder);
            }
        }
        sqlBuilder.append(_Constant.RIGHT_BRACE);

    }


}
