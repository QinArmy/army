package io.army.sqltype;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.Database;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;

public abstract class SQLDataTypeUtils {

    protected SQLDataTypeUtils() {
        throw new UnsupportedOperationException();
    }

    public static int obtainTimePrecision(SQLDataType dataType, FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > 6) {
            throw SQLDataTypeUtils.createPrecisionException(dataType, 0, 6, fieldMeta);
        }
        return precision;
    }

    public static void appendDataTypeWithMaxPrecision(SQLDataType dataType, FieldMeta<?, ?> fieldMeta, int maxPrecision
            , SQLBuilder builder) {
        int precision = fieldMeta.precision();
        if (precision > maxPrecision) {
            throw SQLDataTypeUtils.createPrecisionException(dataType, 1, maxPrecision, fieldMeta);
        }
        SQLDataTypeUtils.appendDataTypeWithPrecision(dataType, precision, builder);
    }


    public static void appendDataTypeWithPrecision(SQLDataType dataType, int precision, SQLBuilder builder) {
        builder.append(dataType.typeName());
        if (precision > 0) {
            builder.append("(")
                    .append(precision)
                    .append(")");
        }
    }

    public static void decimalDataTypeClause(SQLDataType dataType, int maxPrecision, int maxScale
            , FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {

        int precision = fieldMeta.precision(), scale = fieldMeta.scale();
        if (precision < 0) {
            precision = 14;
        } else if (precision > maxPrecision) {
            throw SQLDataTypeUtils.createPrecisionException(dataType, 1, maxPrecision, fieldMeta);
        }
        builder.append(dataType.typeName())
                .append("(")
                .append(precision)
        ;
        if (scale < 0) {
            scale = 2;
        } else if (scale > maxScale) {
            throw SQLDataTypeUtils.createScaleException(dataType, 0, maxScale, fieldMeta);
        }
        builder.append(",")
                .append(scale)
                .append(")");
    }

    public static String decimalDefaultValue(FieldMeta<?, ?> fieldMeta) {
        String text;
        int scale = fieldMeta.scale();
        if (scale < 0) {
            text = "0.00";
        } else if (scale == 0) {
            text = "0";
        } else {
            char[] chars = new char[fieldMeta.scale() + 2];
            chars[0] = '0';
            chars[1] = '.';
            for (int i = 2; i < chars.length; i++) {
                chars[i] = '0';
            }
            text = new String(chars);
        }
        return text;
    }

    public static MetaException createNotSupportDefaultClause(SQLDataType dataType, FieldMeta<?, ?> fieldMeta
            , Database database) {
        return new MetaException("%s,%s not support default clause for %s.", fieldMeta, dataType, database);
    }


    public static MetaException createPrecisionException(SQLDataType dataType, int includeMin, int includeMax
            , FieldMeta<?, ?> fieldMeta) {
        throw new MetaException(ErrorCode.META_ERROR, "%s,%s precision must in[%s,%s]."
                , fieldMeta, dataType.name(), includeMin, includeMax);
    }

    public static MetaException createNotJavaTypeException(SQLDataType dataType, FieldMeta<?, ?> fieldMeta) {
        return new MetaException("%s,%s not support java type[%s]"
                , fieldMeta, dataType, fieldMeta.javaType().getName());
    }

    public static MetaException createScaleException(SQLDataType dataType, int includeMin, int includeMax
            , FieldMeta<?, ?> fieldMeta) {
        throw new MetaException(ErrorCode.META_ERROR, "%s,%s scale must in[%s,%s]."
                , fieldMeta, dataType.name(), includeMin, includeMax);
    }

    public static MetaException createNotSupportZeroValueException(SQLDataType dataType, FieldMeta<?, ?> fieldMeta
            , Database database) {
        return new MetaException("%s,%s not support \"zero value\" for %s.", fieldMeta, dataType, database);
    }

    public static MetaException createNotSupportNowExpressionException(SQLDataType dataType, FieldMeta<?, ?> fieldMeta
            , Database database) {
        return new MetaException("%s,%s not support io.army.domain.IDomain.NOW for %s.", fieldMeta, dataType, database);
    }

    /*################################## blow package method ##################################*/


    /**
     * @param database reserved param
     */
    static void postgreDateNowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {
        // builder.append("TRIM(TO_CHAR(CURRENT_DATE, 'MONTH'))");
        //  builder.append("TRIM(TO_CHAR(CURRENT_DATE, 'DAY'))");
        Class<?> javaClass = fieldMeta.javaType();
        if (javaClass == LocalDate.class) {
            builder.append("CURRENT_DATE");
        } else if (javaClass == Year.class) {
            builder.append("TO_DATE((TO_CHAR(CURRENT_DATE, 'YYYY') || '-01-01'),'YYYY-MM-DD')");
        } else if (javaClass == YearMonth.class) {
            builder.append("TO_DATE((TO_CHAR(CURRENT_DATE, 'YYYY-MM') || '-01'),'YYYY-MM-DD')");
        } else if (javaClass == MonthDay.class) {
            builder.append("TO_DATE(('1970-' || TO_CHAR(CURRENT_DATE, 'MM-DD')),'YYYY-MM-DD')");
        } else {
            throw createNotJavaTypeException(PostgreDataType.DATE, fieldMeta);
        }
    }

    static void mySQLDateNowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {
        //  builder.append("(UPPER(MONTHNAME(CURRENT_DATE)))");
        //  builder.append("(UPPER(DATE_FORMAT(CURRENT_DATE,'%W')))");
        Class<?> javaClass = fieldMeta.javaType();
        if (javaClass == LocalDate.class) {
            builder.append("(CURRENT_DATE)");
        } else if (javaClass == Year.class) {
            builder.append("(YEAR(CURRENT_DATE))");
        } else if (javaClass == YearMonth.class) {
            builder.append("(DATE_FORMAT(CURRENT_DATE,'%Y-%m'))");
        } else if (javaClass == MonthDay.class) {
            builder.append("(DATE_FORMAT(CURRENT_DATE,'%m-%d'))");
        } else {
            throw createNotJavaTypeException(MySQLDataType.DATE, fieldMeta);
        }
    }
}
