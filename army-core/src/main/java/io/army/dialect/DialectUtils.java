package io.army.dialect;

import io.army.dialect.mysql.MySQLDialectFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EnumSet;

public abstract class DialectUtils {


   public static final EnumSet<JDBCType> QUOTE_JDBC_TYPE = EnumSet.of(
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


   public static final EnumSet<JDBCType> TEXT_JDBC_TYPE = EnumSet.of(
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


    public static boolean hasPrecision(JDBCType jdbcType) {
        return PRECISION_JDBC_TYPE.contains(jdbcType);
    }

    public static boolean hasScale(JDBCType jdbcType) {
        return jdbcType == JDBCType.DECIMAL;
    }

    public static Dialect decideDialect(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        DataBase dataBase = DataBase.valueOf(metaData.getDatabaseProductName().toUpperCase());
        Dialect dialect;
        int major, minor;
        major = metaData.getDatabaseMajorVersion();
        minor = metaData.getDatabaseMinorVersion();

        switch (dataBase) {
            case MySQL:
                dialect = MySQLDialectFactory.decideDialect(major, minor);
                break;
            case Postgre:
            case OceanBase:
            case Oracle:
            case SQL_Server:
            case Db2:
            default:
                throw new IllegalArgumentException(String.format("unknown database %s", dataBase));
        }
        return dialect;

    }


}
