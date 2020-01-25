package io.army.dialect.mysql;

import io.army.dialect.DDLUtils;
import io.army.dialect.func.SQLFuncDescribe;
import io.army.meta.FieldMeta;
import io.army.sqltype.MySQLDataType;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.sql.JDBCType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

abstract class MySQL57DDLUtils extends DDLUtils {




    /*################################## blow jdbc method ##################################*/

    static String bitFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.BIT, 1);
    }

    static String tinyIntFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.TINYINT);
    }

    static String smallIntFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.SMALLINT);
    }

    static String integerFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.INT);
    }

    static String bitIntFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.BIGINT);
    }

    static String floatFunction(FieldMeta<?, ?> fieldMeta) {
        return MySQLDataType.FLOAT.name();
    }

    static String doubleFunction(FieldMeta<?, ?> fieldMeta) {
        return MySQLDataType.DOUBLE.name();
    }

    static String decimalFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        int scale = fieldMeta.scale();
        final int maxPrecision = MySQLDataType.DECIMAL.maxPrecision();
        final int maxScale = MySQLDataType.DECIMAL.maxScale();
        if (precision < 0) {
            precision = 10;
        } else if (precision == 0 || precision > maxPrecision) {
            throwPrecisionException(fieldMeta);
        }

        if (scale < 0) {
            scale = 0;
        } else if (scale > maxScale) {
            throwScaleException(fieldMeta);
        }
        return MySQLDataType.DECIMAL.name() + "(" + precision + "," + scale + ")";
    }

    /*################################## blow string function method ##################################*/

    static String charFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.CHAR);
    }

    static String varcharFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.VARCHAR);
    }

    static String longVarcharFunction(FieldMeta<?, ?> fieldMeta) {
        final int precision = fieldMeta.precision();
        MySQLDataType dataType = null;
        if (precision < 0) {
            dataType = MySQLDataType.TEXT;
        } else if (precision <= MySQLDataType.TINYTEXT.maxPrecision()) {
            dataType = MySQLDataType.TINYTEXT;
        } else if (precision <= MySQLDataType.TEXT.maxPrecision()) {
            dataType = MySQLDataType.TEXT;
        } else if (precision <= MySQLDataType.MEDIUMTEXT.maxPrecision()) {
            dataType = MySQLDataType.MEDIUMTEXT;
        } else {
            throwPrecisionException(fieldMeta);
        }
        return dataType.name();
    }

    static String ncharFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.NCHAR);
    }

    static String nvarcharFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.NVARCHAR);
    }

    static String binaryFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.BINARY, 1);
    }

    static String varbinaryFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.VARBINARY, 1);
    }

    static String longVarbinaryFunction(FieldMeta<?, ?> fieldMeta) {
        return blobFunction(fieldMeta);
    }


    static String blobFunction(FieldMeta<?, ?> fieldMeta) {
        final int precision = fieldMeta.precision();
        MySQLDataType dataType = null;
        if (precision < 0) {
            dataType = MySQLDataType.BLOB;
        } else if (precision <= MySQLDataType.TINYBLOB.maxPrecision()) {
            dataType = MySQLDataType.TINYBLOB;
        } else if (precision <= MySQLDataType.BLOB.maxPrecision()) {
            dataType = MySQLDataType.BLOB;
        } else if (precision <= MySQLDataType.MEDIUMBLOB.maxPrecision()) {
            dataType = MySQLDataType.MEDIUMBLOB;
        } else {
            throwPrecisionException(fieldMeta);
        }
        return dataType.name();
    }

    /*################################## blow date time function method ##################################*/

    static String dateFunction(FieldMeta<?, ?> fieldMeta) {
        MySQLDataType dataType;
        if (fieldMeta.precision() == 4) {
            dataType = MySQLDataType.YEAR;
        } else {
            dataType = MySQLDataType.DATE;
        }
        return dataType.name();
    }

    static String timeFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.TIME, 0);
    }

    static String timestampFunction(FieldMeta<?, ?> fieldMeta) {
        return onlyPrecisionType(fieldMeta, MySQLDataType.DATETIME, 0);
    }

    /**
     * @return a unmodifiable map
     */
    static Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap() {
        Map<JDBCType, Function<FieldMeta<?, ?>, String>> map;
        map = new EnumMap<>(JDBCType.class);

        map.put(JDBCType.BIT, MySQL57DDLUtils::bitFunction);
        map.put(JDBCType.TINYINT, MySQL57DDLUtils::tinyIntFunction);
        map.put(JDBCType.SMALLINT, MySQL57DDLUtils::smallIntFunction);
        map.put(JDBCType.INTEGER, MySQL57DDLUtils::integerFunction);

        map.put(JDBCType.BIGINT, MySQL57DDLUtils::bitIntFunction);
        map.put(JDBCType.FLOAT, MySQL57DDLUtils::floatFunction);
        map.put(JDBCType.DOUBLE, MySQL57DDLUtils::doubleFunction);
        map.put(JDBCType.DECIMAL, MySQL57DDLUtils::decimalFunction);

        map.put(JDBCType.CHAR, MySQL57DDLUtils::charFunction);
        map.put(JDBCType.VARCHAR, MySQL57DDLUtils::varcharFunction);
        map.put(JDBCType.LONGVARCHAR, MySQL57DDLUtils::longVarcharFunction);
        map.put(JDBCType.NCHAR, MySQL57DDLUtils::ncharFunction);

        map.put(JDBCType.NVARCHAR, MySQL57DDLUtils::nvarcharFunction);
        map.put(JDBCType.BINARY, MySQL57DDLUtils::binaryFunction);
        map.put(JDBCType.VARBINARY, MySQL57DDLUtils::varbinaryFunction);
        map.put(JDBCType.LONGVARBINARY, MySQL57DDLUtils::longVarbinaryFunction);

        map.put(JDBCType.BLOB, MySQL57DDLUtils::blobFunction);
        map.put(JDBCType.DATE, MySQL57DDLUtils::dateFunction);
        map.put(JDBCType.TIME, MySQL57DDLUtils::timeFunction);
        map.put(JDBCType.TIMESTAMP, MySQL57DDLUtils::timestampFunction);

        return Collections.unmodifiableMap(map);
    }

    static String zeroDateTime(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        int precision = fieldMeta.precision();
        DateTimeFormatter formatter = null;
        if (precision < 0) {
            formatter = TimeUtils.DATE_TIME_FORMATTER;
        } else if (precision <= MySQLDataType.DATETIME.maxPrecision()) {
            formatter = TimeUtils.SIX_FRACTION_DATE_TIME_FORMATTER;
        } else {
            throwPrecisionException(fieldMeta);
        }
        return StringUtils.quote(
                ZonedDateTime.ofInstant(Instant.EPOCH, zoneId)
                        .format(formatter)
        );
    }

    static String zeroDate(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return StringUtils.quote(
                ZonedDateTime.ofInstant(Instant.EPOCH, zoneId)
                        .format(TimeUtils.DATE_FORMATTER)
        );
    }

    static String zeroYear(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return StringUtils.quote(
                ZonedDateTime.ofInstant(Instant.EPOCH, zoneId)
                        .format(TimeUtils.YEAR_FORMATTER)
        );
    }

    static String quoteDefaultIfNeed(FieldMeta<?, ?> fieldMeta) {
        String defaultValue = fieldMeta.defaultValue().trim();
        if(QUOTE_JDBC_TYPE.contains(fieldMeta.jdbcType())){
            defaultValue = StringUtils.quote(defaultValue);
        }
        return defaultValue;
    }






    /*################################## blow private method ##################################*/

}
