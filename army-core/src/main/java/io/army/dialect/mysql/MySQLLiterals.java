package io.army.dialect.mysql;

import io.army.dialect._Literals;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;

import java.time.DateTimeException;
import java.time.Year;

abstract class MySQLLiterals extends _Literals {


    static String year(final SqlType sqlType, final Object nonNull) {
        final String literal;
        if (nonNull instanceof Year) {
            literal = Integer.toString(((Year) nonNull).getValue());
        } else if (nonNull instanceof Integer || nonNull instanceof Short) {
            literal = nonNull.toString();
        } else if (nonNull instanceof String) {
            try {
                Year.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw _Exceptions.literalOutRange(sqlType, nonNull);
            }
            literal = (String) nonNull;
        } else {
            throw _Exceptions.errorLiteralType(sqlType, nonNull);
        }
        return literal;

    }


}
