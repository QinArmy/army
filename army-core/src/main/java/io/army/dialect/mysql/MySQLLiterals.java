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
import io.army.env.EscapeMode;
import io.army.util.HexUtils;

import java.nio.charset.StandardCharsets;

abstract class MySQLLiterals extends _Literals {


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-literals.html#character-escape-sequences">String Literals</a>
     */
    static void mysqlEscapes(final EscapeMode mode, final String literal, final StringBuilder sqlBuilder) {
        //firstly,store start index
        final int startIndex, valueLength;
        startIndex = sqlBuilder.length();
        valueLength = literal.length();

        final boolean backSlashEscape;
        switch (mode) {
            case DEFAULT:
                backSlashEscape = false;
                break;
            case BACK_SLASH:
                backSlashEscape = true;
                break;
            default:
                // no bug ,never here
                throw new IllegalArgumentException();
        }

        // left quote
        sqlBuilder.append(_Constant.QUOTE);

        int lastWritten = 0;

        char charAfterBachSlash = _Constant.NUL_CHAR;

        for (int i = 0; i < valueLength; i++) {
            switch (literal.charAt(i)) {
                case _Constant.QUOTE: {
                    if (i > lastWritten) {
                        sqlBuilder.append(literal, lastWritten, i);
                    }
                    sqlBuilder.append(_Constant.QUOTE);
                    lastWritten = i; // not i + 1 as current char wasn't written
                }
                continue;
                case _Constant.BACK_SLASH:
                    charAfterBachSlash = _Constant.BACK_SLASH;
                    break;
                case _Constant.NUL_CHAR:
                    charAfterBachSlash = '0';
                    break;
                case '\b':
                    charAfterBachSlash = 'b';
                    break;
                case '\n':
                    charAfterBachSlash = 'n';
                    break;
                case '\r':
                    charAfterBachSlash = 'r';
                    break;
                case '\t':
                    charAfterBachSlash = 't';
                    break;
                case '\032':
                    charAfterBachSlash = 'Z';
                    break;
                default:
                    continue;

            } // switch

            if (!backSlashEscape) {
                //army couldn't safely escapes
                // ,because army don't known the current value of @@SESSION.sql_mode for (NO_BACKSLASH_ESCAPES).
                break;
            }

            if (i > lastWritten) {
                sqlBuilder.append(literal, lastWritten, i);
            }
            sqlBuilder.append(_Constant.BACK_SLASH)
                    .append(charAfterBachSlash);

            lastWritten = i + 1;

        }

        if (!backSlashEscape && charAfterBachSlash != _Constant.NUL_CHAR) {
            sqlBuilder.setLength(startIndex);
            sqlBuilder.append("_utf8mb4 0x");
            sqlBuilder.append(HexUtils.hexEscapesText(true, literal.getBytes(StandardCharsets.UTF_8)));
        } else {
            if (lastWritten < valueLength) {
                sqlBuilder.append(literal, lastWritten, valueLength);
            }
            sqlBuilder.append(_Constant.QUOTE);
        }


    }


}
