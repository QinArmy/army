package io.army.dialect;

import io.army.meta.mapping.MappingType;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.util.EnumSet;

public abstract class DialectUtils {


    protected static final EnumSet<JDBCType> TEXT_JDBC_TYPE = EnumSet.of(
            JDBCType.VARCHAR,
            JDBCType.CHAR,
            JDBCType.BLOB,
            JDBCType.NCHAR,

            JDBCType.NVARCHAR,
            JDBCType.LONGVARCHAR,
            JDBCType.DATE,
            JDBCType.TIME,

            JDBCType.TIMESTAMP,
            JDBCType.TIME_WITH_TIMEZONE,
            JDBCType.TIMESTAMP_WITH_TIMEZONE
    );


    public static String quoteIfNeed(MappingType mappingType, String textValue) {
        if (TEXT_JDBC_TYPE.contains(mappingType.jdbcType())) {
            return StringUtils.quote(textValue);
        }
        return textValue;
    }


}
