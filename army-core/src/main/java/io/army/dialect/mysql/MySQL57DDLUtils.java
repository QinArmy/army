package io.army.dialect.mysql;

import io.army.dialect.DDLUtils;
import io.army.sqltype.MySqlType;
import io.army.util.StringUtils;

import java.util.EnumSet;
import java.util.Set;

abstract class MySQL57DDLUtils extends DDLUtils {

    private static final Set<MySqlType> NEED_QUOTE_TYPE_SET = EnumSet.of(
            MySqlType.CHAR,
            // MySQLDataType.NCHAR,
            MySqlType.VARCHAR,
            // MySQLDataType.NVARCHAR,

            MySqlType.BINARY,
            MySqlType.VARBINARY,
            MySqlType.TINYBLOB,
            MySqlType.BLOB,

            MySqlType.MEDIUMBLOB,
            MySqlType.TINYTEXT,
            MySqlType.TEXT,
            MySqlType.MEDIUMTEXT,

            MySqlType.ENUM

    );

    static boolean needQuoteForDefault(MySqlType dataType) {
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
