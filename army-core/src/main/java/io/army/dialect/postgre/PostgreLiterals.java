package io.army.dialect.postgre;

import io.army.dialect._Constant;
import io.army.dialect._Literals;
import io.army.meta.TypeMeta;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

abstract class PostgreLiterals extends _Literals {

    private PostgreLiterals() {
    }


    static StringBuilder postgreBackslashEscapes(final TypeMeta typeMeta, final SqlType type, final Object value,
                                                 final StringBuilder sqlBuilder) {
        if (!(value instanceof String)) {//TODO think long string
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
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
        return sqlBuilder.append(_Constant.QUOTE);

    }


    static StringBuilder postgreBitString(final TypeMeta typeMeta, final SqlType type, final Object value,
                                          final StringBuilder sqlBuilder) {
        if (!(value instanceof String)) {
            throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
        } else if (!_StringUtils.isBinary((String) value)) {
            throw _Exceptions.valueOutRange(type, value);
        }

        return sqlBuilder.append('B')
                .append(_Constant.QUOTE)
                .append(value)
                .append(_Constant.QUOTE);

    }


}
