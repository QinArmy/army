package io.army.dialect.mysql;

import io.army.dialect.DDLUtils;
import io.army.meta.FieldMeta;

import java.sql.JDBCType;
import java.time.Year;

abstract class MySQLDDLUtils extends DDLUtils {


    /*################################## blow number type function method ##################################*/

    /**
     * @see JDBCType#BIT
     */
    static String bitFunction(FieldMeta<?, ?> fieldMeta) {
        return "BIT(" + getNumberPrecision(fieldMeta, 1, 64) + ")";
    }

    /**
     * @see JDBCType#TINYINT
     */
    static String tinyIntFunction(FieldMeta<?, ?> fieldMeta) {
        return "TINYINT(" + getNumberPrecision(fieldMeta, 1, 3) + ")";
    }

    /**
     * @see JDBCType#BOOLEAN
     */
    static String booleanFunction(FieldMeta<?, ?> fieldMeta) {
        DDLUtils.getNumberPrecision(fieldMeta, 1, 1);
        return "BOOLEAN";
    }

    /**
     * @see JDBCType#SMALLINT
     */
    static String smallIntFunction(FieldMeta<?, ?> fieldMeta) {
        return "SMALLINT(" + getNumberPrecision(fieldMeta, 1, 6) + ")";
    }

    /**
     * @see JDBCType#INTEGER
     */
    static String intFunction(FieldMeta<?, ?> fieldMeta) {
        return "INT(" + getNumberPrecision(fieldMeta, 1, 11) + ")";
    }

    /**
     * @see JDBCType#BIGINT
     */
    static String bigIntFunction(FieldMeta<?, ?> fieldMeta) {
        return "BIGINT(" + getNumberPrecision(fieldMeta, 1, 20) + ")";
    }

    /**
     * @see JDBCType#DECIMAL
     */
    static String decimalFunction(FieldMeta<?, ?> fieldMeta) {
        return "DECIMAL(" + getNumberPrecision(fieldMeta, 0, 65) + "," + getNumberScale(fieldMeta, 0, 30) + ")";
    }

    /**
     * @see JDBCType#FLOAT
     */
    static String floatFunction(FieldMeta<?, ?> fieldMeta) {
        return "FLOAT(" + getNumberPrecision(fieldMeta, 0, 23) + ")";
    }

    /**
     * @see JDBCType#DOUBLE
     */
    static String doubleFunction(FieldMeta<?, ?> fieldMeta) {
        return "DOUBLE(" + getNumberPrecision(fieldMeta, 24, 53) + ")";
    }

    /*################################## blow data time type function method ##################################*/

    /**
     * @see JDBCType#TIME
     */
    static String timeFunction(FieldMeta<?, ?> fieldMeta) {
        return "TIME(" + getNumberPrecision(fieldMeta, 0, 6) + ")";
    }

    /**
     * @see JDBCType#DATE
     */
    static String dateFunction(FieldMeta<?, ?> fieldMeta) {
        String sqlType;
        if (fieldMeta.javaType() == Year.class) {
            sqlType = "YEAR";
        } else {
            sqlType = "DATE";
        }
        return sqlType;
    }

    /**
     * @see JDBCType#TIMESTAMP
     */
    static String timestampFunction(FieldMeta<?, ?> fieldMeta) {
        return "DATETIME(" + getNumberPrecision(fieldMeta, 0, 6) + ")";
    }

    /*################################## blow string type function method ##################################*/

    /**
     * @see JDBCType#CHAR
     */
    static String charFunction(FieldMeta<?, ?> fieldMeta) {
        return "CHAR(" + getNumberPrecision(fieldMeta, 0, 255) + ")";
    }

    /**
     * @see JDBCType#VARCHAR
     */
    static String varcharFunction(FieldMeta<?, ?> fieldMeta) {
        return "VARCHAR(" + getNumberPrecision(fieldMeta, 0, 65535) + ")";
    }

    /**
     * @see JDBCType#BINARY
     */
    static String binaryFunction(FieldMeta<?, ?> fieldMeta) {
        return "BINARY(" + getNumberPrecision(fieldMeta, 0, 255) + ")";
    }

    /**
     * @see JDBCType#VARBINARY
     */
    static String varbinaryFunction(FieldMeta<?, ?> fieldMeta) {
        return "VARBINARY(" + getNumberPrecision(fieldMeta, 0, 65535) + ")";
    }

    /**
     * @see JDBCType#BLOB
     */
    static String blobFunction(FieldMeta<?, ?> fieldMeta) {
        return "BLOB(" + getNumberPrecision(fieldMeta, 0, 65535) + ")";
    }


    /*################################## blow private method ##################################*/


}
