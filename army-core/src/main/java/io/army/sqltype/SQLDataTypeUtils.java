package io.army.sqltype;

import io.army.ErrorCode;
import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class SQLDataTypeUtils {

    protected SQLDataTypeUtils() {
        throw new UnsupportedOperationException();
    }

    public static int obtainTimePrecision(SqlType dataType, FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > 6) {
            throw SQLDataTypeUtils.createPrecisionException(dataType, 0, 6, fieldMeta);
        }
        return precision;
    }

    public static void appendDataTypeWithMaxPrecision(SqlType dataType, FieldMeta<?, ?> fieldMeta
            , int maxPrecision, int defaultPrecision, StringBuilder builder) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = defaultPrecision;
        } else if (precision > maxPrecision) {
            throw SQLDataTypeUtils.createPrecisionException(dataType, 1, maxPrecision, fieldMeta);
        }
        SQLDataTypeUtils.appendDataTypeWithPrecision(dataType, precision, builder);
    }


    public static void appendDataTypeWithPrecision(SqlType dataType, int precision, StringBuilder builder) {
        builder.append(dataType.typeName());
        if (precision > 0) {
            builder.append("(")
                    .append(precision)
                    .append(")");
        }
    }

    public static void decimalDataTypeClause(SqlType dataType, int maxPrecision, int maxScale
            , FieldMeta<?, ?> fieldMeta, StringBuilder builder) {

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

    public static MetaException createNotSupportDefaultClause(SqlType dataType, FieldMeta<?, ?> fieldMeta
            , Database database) {
        return new MetaException("%s,%s not support default clause for %s.", fieldMeta, dataType, database);
    }


    public static MetaException createPrecisionException(SqlType dataType, int includeMin, int includeMax
            , FieldMeta<?, ?> fieldMeta) {
        throw new MetaException(ErrorCode.META_ERROR, "%s,%s precision must in[%s,%s]."
                , fieldMeta, dataType.name(), includeMin, includeMax);
    }

    public static MetaException createNotJavaTypeException(SqlType dataType, FieldMeta<?, ?> fieldMeta) {
        return new MetaException("%s,%s not support java type[%s]"
                , fieldMeta, dataType, fieldMeta.javaType().getName());
    }

    public static MetaException createScaleException(SqlType dataType, int includeMin, int includeMax
            , FieldMeta<?, ?> fieldMeta) {
        throw new MetaException(ErrorCode.META_ERROR, "%s,%s scale must in[%s,%s]."
                , fieldMeta, dataType.name(), includeMin, includeMax);
    }

    public static MetaException createNotSupportZeroValueException(SqlType dataType, FieldMeta<?, ?> fieldMeta
            , Database database) {
        return new MetaException("%s,%s not support \"zero value\" for %s.", fieldMeta, dataType, database);
    }

    public static MetaException createNotSupportNowExpressionException(SqlType dataType, FieldMeta<?, ?> fieldMeta
            , Database database) {
        return new MetaException("%s,%s not support IDomain.NOW for %s.", fieldMeta, dataType, database);
    }

    /*################################## blow package method ##################################*/


    /**
     * @param database reserved param
     */
    static void postgreDateNowValue(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
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
            throw createNotJavaTypeException(PostgreType.DATE, fieldMeta);
        }
    }

    static void mySQLDateNowValue(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
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
            throw createNotJavaTypeException(MySqlType.DATE, fieldMeta);
        }
    }

    static <E extends Enum<E> & SqlType> Map<String, E> createTypeNameMap(Class<E> dataTypeEnumClass) {
        E[] array = dataTypeEnumClass.getEnumConstants();
        Map<String, E> map = new HashMap<>((int) (array.length % 0.75F));
        for (E dataType : array) {
            if (map.putIfAbsent(dataType.typeName(), dataType) != null) {
                throw new IllegalStateException(String.format("%s typeName[%s] duplication."
                        , dataTypeEnumClass.getName(), dataType.typeName()));
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
