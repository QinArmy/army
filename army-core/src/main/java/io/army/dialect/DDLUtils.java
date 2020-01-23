package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.meta.FieldMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;

public abstract class DDLUtils {


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
        throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].prop[%s]'s precision[%s] error."
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

    protected boolean isQuoted(String expresion, String pattern, int index) {
        boolean match = false;
        if (index == 0 || index == expresion.length() - 1) {
            match = false;
        } else if (expresion.charAt(index - 1) == '\''
                && expresion.charAt(index + pattern.length()) == '\'') {
            match = true;
        }
        return match;
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

    protected static String nullable(boolean nullable){
        String not ;
        if(nullable){
            not = "";
        }else {
            not = "NOT";
        }
        return not;
    }
}
