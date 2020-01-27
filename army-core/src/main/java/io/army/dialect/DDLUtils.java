package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.meta.FieldMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.util.EnumSet;

public abstract class DDLUtils {


    protected static  EnumSet<JDBCType> QUOTE_JDBC_TYPE = EnumSet.of(
            JDBCType.VARCHAR,
            JDBCType.CHAR,
            JDBCType.BLOB,
            JDBCType.NCHAR,

            JDBCType.NVARCHAR,
            JDBCType.BINARY,
            JDBCType.VARBINARY,
            JDBCType.LONGVARBINARY,

            JDBCType.LONGVARCHAR,
            JDBCType.DATE,
            JDBCType.TIME,
            JDBCType.TIMESTAMP,

            JDBCType.TIME_WITH_TIMEZONE,
            JDBCType.TIMESTAMP_WITH_TIMEZONE
    );


    protected static String onlyPrecisionType(FieldMeta<?, ?> fieldMeta, SQLDataType dataType) {
        return onlyPrecisionType(fieldMeta, dataType, dataType.maxPrecision());
    }

    protected static String onlyPrecisionType(FieldMeta<?, ?> fieldMeta, SQLDataType dataType, int defaultValue) {
        int precision = fieldMeta.precision();
        final int maxPrecision = dataType.maxPrecision();
        if (precision < 0) {
            precision = defaultValue;
        } else if (precision == 0 || precision > maxPrecision) {
            throwPrecisionException(fieldMeta);
        }
        return dataType.name() + "(" + precision + ")";
    }

    public static void throwPrecisionException(FieldMeta<?, ?> fieldMeta) {
        throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].prop[%s]'s columnSize[%s] error."
                , fieldMeta.table().javaType()
                , fieldMeta.propertyName()
                , fieldMeta.precision()
        );
    }

    protected static void throwScaleException(FieldMeta<?, ?> fieldMeta) {
        throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].prop[%s]'s scale[%s] error."
                , fieldMeta.table().javaType()
                , fieldMeta.propertyName()
                , fieldMeta.scale()
        );
    }


    protected static String ascOrDesc(Boolean asc) {
        String text;
        if (asc == null) {
            text = "";
        } else if (asc) {
            text = "ASC";
        } else {
            text = "DESC";
        }
        return text;
    }

}
