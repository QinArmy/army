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
                    //army couldn't safely escapes
                    // ,because army don't known the current value of @@SESSION.sql_mode for (NO_BACKSLASH_ESCAPES).
                    existBackSlash = true;
                    break outerFor;
                default:
                    //no-op
            }
        }

        if (existBackSlash) {
            sqlBuilder.delete(startIndex, sqlBuilder.length())
                    .append("_utf8mb4 0x")
                    .append(_Literals.hexEscapes(nonNull.getBytes(StandardCharsets.UTF_8)));
        } else {
            if (lastWritten < charArray.length) {
                sqlBuilder.append(charArray, lastWritten, charArray.length - lastWritten);
            }
            sqlBuilder.append(_Constant.QUOTE);
        }

    }


}
