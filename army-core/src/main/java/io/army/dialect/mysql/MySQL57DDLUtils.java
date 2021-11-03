package io.army.dialect.mysql;

import io.army.dialect.DDLUtils;
import io.army.sqltype.MySQLDataType;
import io.army.util.StringUtils;

import java.util.EnumSet;
import java.util.Set;

abstract class MySQL57DDLUtils extends DDLUtils {

    private static final Set<MySQLDataType> NEED_QUOTE_TYPE_SET = EnumSet.of(
            MySQLDataType.CHAR,
           // MySQLDataType.NCHAR,
            MySQLDataType.VARCHAR,
            // MySQLDataType.NVARCHAR,

            MySQLDataType.BINARY,
            MySQLDataType.VARBINARY,
            MySQLDataType.TINYBLOB,
            MySQLDataType.BLOB,

            MySQLDataType.MEDIUMBLOB,
            MySQLDataType.TINYTEXT,
            MySQLDataType.TEXT,
            MySQLDataType.MEDIUMTEXT,

            MySQLDataType.ENUM

    );

    static boolean needQuoteForDefault(MySQLDataType dataType) {
        return NEED_QUOTE_TYPE_SET.contains(dataType);
    }


    static String tableCharset(String charset) {
        String actualCharset = charset;
        if (!StringUtils.hasText(actualCharset)) {
            actualCharset = "UTF8MB4";
        }
        return actualCharset;
    }






    /*################################## blow private method ##################################*/

}
