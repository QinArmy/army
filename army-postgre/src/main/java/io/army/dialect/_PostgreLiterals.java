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

package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;
import io.army.util._StringUtils;

import java.lang.reflect.Array;

public abstract class _PostgreLiterals extends _Literals {

    private _PostgreLiterals() {
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-STRINGS-ESCAPE">String Constants With C-Style Escapes</a>
     */
    public static void backslashEscape(final CharSequence literal, final int offset, final int end,
                                       final StringBuilder sqlBuilder) {


        final int startIndex;
        startIndex = sqlBuilder.length();

        sqlBuilder.append(_Constant.QUOTE);

        int lastWritten = 0;
        char ch, followChar = _Constant.NUL_CHAR;
        for (int i = offset; i < end; i++) {
            ch = literal.charAt(i);
            switch (ch) {
                case _Constant.QUOTE: {
                    if (i > lastWritten) {
                        sqlBuilder.append(literal, lastWritten, i);
                    }
                    sqlBuilder.append(_Constant.QUOTE);
                    lastWritten = i; // not i + 1 as current char wasn't written
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
                sqlBuilder.append(literal, lastWritten, i);
            }
            sqlBuilder.append(_Constant.BACK_SLASH)
                    .append(followChar);
            lastWritten = i + 1;


        } // for loop

        if (lastWritten < end) {
            sqlBuilder.append(literal, lastWritten, end);
        }

        if (followChar != _Constant.NUL_CHAR) {
            sqlBuilder.insert(startIndex, 'E');
        }

        sqlBuilder.append(_Constant.QUOTE);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-STRINGS-UESCAPE">String Constants With Unicode Escapes</a>
     */
    public static void unicodeEscape(final CharSequence literal, final int offset, final int end, final char escapeChar,
                                     final StringBuilder sqlBuilder) {

        if (escapeChar == '+'
                || escapeChar == _Constant.QUOTE
                || escapeChar == _Constant.DOUBLE_QUOTE
                || (escapeChar >= '0' && escapeChar <= '9')
                || (escapeChar >= 'a' && escapeChar <= 'f')
                || (escapeChar >= 'A' && escapeChar <= 'F')
                || Character.isWhitespace(escapeChar)) {
            throw new CriteriaException(String.format("Illegal unicode escape char[%s]", escapeChar));
        }

        sqlBuilder.append("U&'");

        int lastWritten = 0;
        String hexStr;
        char ch;
        for (int i = offset, codePoint; i < end; i++) {
            ch = literal.charAt(i);
            switch (ch) {
                case _Constant.QUOTE:
                case _Constant.NUL_CHAR:
                case '\b':
                case '\f':
                case '\n':
                case '\r':
                case '\t':
                    break;
                default: {
                    if (ch != escapeChar) {
                        continue;
                    }
                } // default

            } // switch

            if (i > lastWritten) {
                sqlBuilder.append(literal, lastWritten, i);
            }

            codePoint = Character.codePointAt(literal, i);
            hexStr = Integer.toHexString(codePoint);

            sqlBuilder.append(escapeChar);
            switch (hexStr.length()) {
                case 1:
                    sqlBuilder.append("000");
                    break;
                case 2:
                    sqlBuilder.append("00");
                    break;
                case 3:
                    sqlBuilder.append('0');
                    break;
                case 4:
                    break;
                case 5:
                    sqlBuilder.append("+0");
                    break;
                case 6:
                    sqlBuilder.append('+');
                    break;
                default:
                    throw new IllegalStateException(String.format("unknown codePoint[%s]", codePoint));
            }

            sqlBuilder.append(hexStr);

            lastWritten = i + 1;

        } // for loop

        if (lastWritten < end) {
            sqlBuilder.append(literal, lastWritten, end);
        }

        sqlBuilder.append(_Constant.QUOTE);

        if (escapeChar != _Constant.BACK_SLASH) {
            sqlBuilder.append(_Constant.SPACE)
                    .append("UESCAPE")
                    .append(_Constant.SPACE)
                    .append(_Constant.QUOTE)
                    .append(escapeChar)
                    .append(_Constant.QUOTE);
        }


    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-DOLLAR-QUOTING">Dollar-Quoted String Constants</a>
     */
    public static void dollarQuotedEscape(final CharSequence literal, int offset, int end, final String tag,
                                          final StringBuilder sqlBuilder) {

        final String dollarQuote;
        dollarQuote = parseDollarQuotedTag(tag);

        final String value;
        if (literal instanceof String) {
            value = (String) literal;
        } else {
            value = literal.subSequence(offset, end).toString();
            offset = 0;
            end = value.length();
        }

        if (value.regionMatches(false, offset, dollarQuote, 0, dollarQuote.length())) {
            throw new CriteriaException(String.format("lDollar-Quoted String literal couldn't contain %s ", dollarQuote));
        }

        sqlBuilder.append(dollarQuote)
                .append(value, offset, end)
                .append(dollarQuote);

    }


    static void postgreBitString(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                 final StringBuilder sqlBuilder) {


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


    /**
     * @see #dollarQuotedEscape(CharSequence, int, int, String, StringBuilder)
     */
    private static String parseDollarQuotedTag(final String tag) {
        final int tagLength = tag.length();
        char ch;
        for (int i = 0; i < tagLength; i++) {
            ch = tag.charAt(i);
            if (ch == '$' || ch == _Constant.BACK_SLASH || Character.isWhitespace(ch)) {
                throw new CriteriaException(String.format("tag[%s] contain '$' or back slash or  white space", tag));
            }
        }

        final boolean useTag = tagLength > 0;

        final String quoteTag;
        if (useTag) {
            quoteTag = _StringUtils.builder(2 + tagLength)
                    .append('$')
                    .append(tag)
                    .append('$')
                    .toString();
        } else {
            quoteTag = "$$";
        }
        return quoteTag;
    }


}
