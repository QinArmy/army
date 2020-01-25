package io.army.dialect;

import java.sql.JDBCType;
import java.util.EnumSet;

public abstract class DialectUtils {




   protected static final EnumSet<JDBCType> TEXT_JDBC_TYPE = EnumSet.of(
            JDBCType.VARCHAR,
            JDBCType.CHAR,
            JDBCType.BLOB,
            JDBCType.NCHAR,

            JDBCType.NVARCHAR,
            JDBCType.LONGVARCHAR
    );







}
