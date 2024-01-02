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

package io.army.dialect.mysql;

import io.army.dialect._Constant;
import io.army.dialect._Literals;

import java.nio.charset.StandardCharsets;

abstract class MySQLLiterals extends _Literals {


    static void mysqlEscapes(final String nonNull, final StringBuilder sqlBuilder) {
        //firstly,store start index
        final int startIndex = sqlBuilder.length();

        final char[] charArray = nonNull.toCharArray();
        sqlBuilder.append(_Constant.QUOTE);
        boolean existBackSlash = false;
        int lastWritten = 0;
        outerFor:
        for (int i = 0; i < charArray.length; i++) {
            switch (charArray[i]) {
                case _Constant.QUOTE: {
                    if (i > lastWritten) {
                        sqlBuilder.append(charArray, lastWritten, i - lastWritten);
                    }
                    sqlBuilder.append(_Constant.QUOTE);
                    lastWritten = i;//not i + 1 as current char wasn't written

                }
                break;
                case _Constant.BACK_SLASH:
                case _Constant.NUL_CHAR:
                case '\b':
                case '\n':
                case '\r':
                case '\t':
                case '\032':
                    //army couldn't safely escapes
                    // ,because army don't known the current value of @@SESSION.sql_mode for (NO_BACKSLASH_ESCAPES).
                    existBackSlash = true;
                    break outerFor;
                default:
                    //no-op
            }
        }

        if (existBackSlash) {
            sqlBuilder.setLength(startIndex);
            sqlBuilder.append("_utf8mb4 0x")
                    .append(_Literals.hexEscapes(nonNull.getBytes(StandardCharsets.UTF_8)));
        } else {
            if (lastWritten < charArray.length) {
                sqlBuilder.append(charArray, lastWritten, charArray.length - lastWritten);
            }
            sqlBuilder.append(_Constant.QUOTE);
        }

    }


}
