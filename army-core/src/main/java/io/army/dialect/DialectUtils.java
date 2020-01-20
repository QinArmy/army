package io.army.dialect;

import java.sql.JDBCType;
import java.util.EnumSet;

public abstract class DialectUtils {


    static final EnumSet<JDBCType> QUOTE_JDBC_TYPE = EnumSet.of(
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


    static final EnumSet<JDBCType> TEXT_JDBC_TYPE = EnumSet.of(
            JDBCType.VARCHAR,
            JDBCType.CHAR,
            JDBCType.BLOB,
            JDBCType.NCHAR,

            JDBCType.NVARCHAR,
            JDBCType.LONGVARCHAR
    );

    static final EnumSet<JDBCType> PRECISION_JDBC_TYPE = EnumSet.of(
            // number
            JDBCType.TINYINT,
            JDBCType.SMALLINT,
            JDBCType.INTEGER,
            JDBCType.BIGINT,

            JDBCType.FLOAT,
            JDBCType.REAL,
            JDBCType.DOUBLE,
            JDBCType.NUMERIC,

            JDBCType.DECIMAL,
            // char
            JDBCType.VARCHAR,
            JDBCType.CHAR,
            JDBCType.BLOB,
            JDBCType.NCHAR,

            JDBCType.BINARY,
            JDBCType.VARBINARY,
            JDBCType.LONGVARBINARY,

            JDBCType.NVARCHAR,
            JDBCType.LONGVARCHAR,
            // time
            JDBCType.TIME,
            JDBCType.TIMESTAMP,
            JDBCType.TIME_WITH_TIMEZONE,
            JDBCType.TIMESTAMP_WITH_TIMEZONE

    );


   public static boolean hasPrecision(JDBCType jdbcType){
      return PRECISION_JDBC_TYPE.contains(jdbcType);
   }

   public static boolean hasScale(JDBCType jdbcType){
       return jdbcType == JDBCType.DECIMAL;
   }



}
