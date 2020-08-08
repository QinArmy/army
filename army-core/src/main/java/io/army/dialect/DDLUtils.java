package io.army.dialect;

import io.army.criteria.MetaException;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.modelgen.MetaConstant;
import io.army.sqltype.SQLDataType;
import io.army.struct.CodeEnum;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

public abstract class DDLUtils {

    protected DDLUtils() {
        throw new UnsupportedOperationException();
    }

    protected static EnumSet<JDBCType> QUOTE_JDBC_TYPE = EnumSet.of(
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


    /**
     * @see io.army.domain.IDomain#ZERO_TIME
     * @see io.army.domain.IDomain#ZERO_DATE
     * @see io.army.domain.IDomain#ZERO_YEAR
     * @see io.army.domain.IDomain#ZERO_DATE_TIME
     */
    public static String zeroForTimeType(FieldMeta<?, ?> fieldMeta) {
        Class<?> javaType = fieldMeta.javaType();
        String zeroValue;
        if (javaType == Month.class) {
            zeroValue = Month.JANUARY.name();
        } else if (javaType == DayOfWeek.class) {
            zeroValue = DayOfWeek.MONDAY.name();
        } else {
            zeroValue = StringUtils.quote(
                    TimeUtils.ZERO_DATE_TIME.format(formatterForTimeTypeDefaultValue(fieldMeta))
            );
        }
        return zeroValue;
    }

    public static boolean timeTypeSupportedByArmy(Class<?> javaType) {
        return javaType == LocalDateTime.class
                || javaType == LocalDate.class
                || javaType == LocalTime.class
                || javaType == ZonedDateTime.class
                || javaType == OffsetDateTime.class
                || javaType == OffsetTime.class
                || javaType == Year.class
                || javaType == MonthDay.class
                || javaType == YearMonth.class
                || javaType == Month.class
                || javaType == DayOfWeek.class
                ;
    }

    public static boolean timeTypeWithoutZoneSupportedByArmy(Class<?> javaType) {
        return javaType == LocalDateTime.class
                || javaType == LocalDate.class
                || javaType == LocalTime.class
                || javaType == Year.class
                || javaType == MonthDay.class
                || javaType == YearMonth.class
                || javaType == Month.class
                || javaType == DayOfWeek.class
                ;
    }

    public static boolean timeTypeWithZone(Class<?> javaType) {
        return javaType == ZonedDateTime.class
                || javaType == OffsetDateTime.class
                || javaType == OffsetTime.class;
    }

    /**
     * see {@code io.army.dialect.AbstractDDL#doDefaultForCreateOrUpdateTime(io.army.meta.FieldMeta, io.army.dialect.DDLContext) }
     */
    public static MetaException createPropertyNotSupportJavaTypeException(FieldMeta<?, ?> fieldMeta, Database database) {
        return new MetaException("Property[%s] not support %s for %s"
                , fieldMeta.propertyName(), fieldMeta.javaType().getName(), database);
    }

    public static MetaException createNowExpressionNotSupportJavaTypeException(FieldMeta<?, ?> fieldMeta, Database database) {
        return new MetaException("%s, %s's NOW() expression not support %s ."
                , fieldMeta, database, fieldMeta.javaType().getName());
    }

    public static void assertTimePrecision(FieldMeta<?, ?> fieldMeta, Database database) {
        if (fieldMeta.precision() > 6) {
            throw createTimePrecisionException(fieldMeta, database);
        }
    }

    public static MetaException createTimePrecisionException(FieldMeta<?, ?> fieldMeta, Database database) {
        return new MetaException("%s precision must in [0,6]", fieldMeta);
    }


    /**
     * @see io.army.modelgen.MetaConstant#SIMPLE_JAVA_TYPE_SET
     */
    static String defaultValueForSimpleJavaType(FieldMeta<?, ?> fieldMeta) {
        Class<?> javaType = fieldMeta.javaType();
        if (CodeEnum.class.isAssignableFrom(javaType)) {
            javaType = Integer.class;
        }
        String defaultValueExp;
        if (javaType == String.class) {
            defaultValueExp = "''";
        } else if (Number.class.isAssignableFrom(javaType)) {
            if (javaType == BigDecimal.class) {
                defaultValueExp = decimalDefault(fieldMeta);
            } else {
                defaultValueExp = "0";
            }
        } else if (Temporal.class.isAssignableFrom(javaType)) {
            // don't need assert java type ,because data type clause do this.
            defaultValueExp = zeroForTimeType(fieldMeta);
        } else {
            throw new IllegalArgumentException(fieldMeta + " java type isn't simple.");
        }
        return defaultValueExp;
    }

    static boolean simpleJavaType(FieldMeta<?, ?> fieldMeta) {
        return MetaConstant.MAYBE_NO_DEFAULT_TYPES.contains(fieldMeta.javaType())
                || (Enum.class.isAssignableFrom(fieldMeta.javaType())
                && CodeEnum.class.isAssignableFrom(fieldMeta.javaType()));
    }


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
        throw new MetaException("Entity[%s].prop[%s]'s columnSize[%s] error."
                , fieldMeta.tableMeta().javaType()
                , fieldMeta.propertyName()
                , fieldMeta.precision()
        );
    }

    protected static void throwScaleException(FieldMeta<?, ?> fieldMeta) {
        throw new MetaException("Entity[%s].prop[%s]'s scale[%s] error."
                , fieldMeta.tableMeta().javaType()
                , fieldMeta.propertyName()
                , fieldMeta.scale()
        );
    }

    /**
     * @return a unmodifiable list
     */
    static List<FieldMeta<?, ?>> sortFieldMetaCollection(TableMeta<?> tableMeta) {
        Set<FieldMeta<?, ?>> fieldMetas = new HashSet<>(tableMeta.fieldCollection());

        List<FieldMeta<?, ?>> fieldMetaList = new ArrayList<>(fieldMetas.size());

        fieldMetaList.add(tableMeta.id());
        if (tableMeta.mappingProp(TableMeta.CREATE_TIME)) {
            fieldMetaList.add(tableMeta.getField(TableMeta.CREATE_TIME));
        }
        if (tableMeta.mappingProp(TableMeta.UPDATE_TIME)) {
            fieldMetaList.add(tableMeta.getField(TableMeta.UPDATE_TIME));
        }
        if (tableMeta.mappingProp(TableMeta.VERSION)) {
            fieldMetaList.add(tableMeta.getField(TableMeta.VERSION));
        }
        if (tableMeta.mappingProp(TableMeta.VISIBLE)) {
            fieldMetaList.add(tableMeta.getField(TableMeta.VISIBLE));
        }
        FieldMeta<?, ?> fieldMeta = tableMeta.discriminator();
        if (fieldMeta != null && fieldMeta.tableMeta() == tableMeta) {
            fieldMetaList.add(fieldMeta);
        }
        // firstly
        fieldMetas.removeAll(fieldMetaList);
        // secondly
        fieldMetaList.addAll(fieldMetas);
        return Collections.unmodifiableList(fieldMetaList);
    }

    /**
     * @return a unmodifiable list
     */
    static List<IndexMeta<?>> sortIndexMetaCollection(TableMeta<?> tableMeta) {
        Set<IndexMeta<?>> indexMetas = new HashSet<>(tableMeta.indexCollection());

        List<IndexMeta<?>> indexMetaList = new ArrayList<>(indexMetas.size());
        IndexMeta<?> primaryKey = null;
        // place holder for primary key
        indexMetaList.add(null);
        for (IndexMeta<?> indexMeta : indexMetas) {
            if (indexMeta.isPrimaryKey()) {
                if (primaryKey != null) {
                    throw new MetaException("TableMeta[%s] error,primary key duplicate", tableMeta);
                }
                primaryKey = indexMeta;
            } else {
                indexMetaList.add(indexMeta);
            }
        }
        if (primaryKey == null) {
            throw new MetaException("TableMeta[%s] error,no primary key index.", tableMeta);
        } else {
            indexMetaList.set(0, primaryKey);
        }
        return Collections.unmodifiableList(indexMetaList);
    }


    private static String decimalDefault(FieldMeta<?, ?> fieldMeta) {
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


    private static DateTimeFormatter formatterForTimeTypeDefaultValue(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision > 6) {
            throw new MetaException("% ,precision must in [0,6].", fieldMeta);
        }
        final boolean sixFraction = precision > 0;

        Class<?> javaType = fieldMeta.javaType();
        String format;
        if (javaType == LocalDateTime.class) {
            format = sixFraction ? TimeUtils.SIX_FRACTION_DATE_TIME_FORMAT : TimeUtils.DATE_TIME_FORMAT;
        } else if (javaType == LocalDate.class) {
            format = TimeUtils.DATE_FORMAT;
        } else if (javaType == LocalTime.class) {
            format = sixFraction ? TimeUtils.SIX_FRACTION_TIME_FORMAT : TimeUtils.TIME_FORMAT;
        } else if (javaType == ZonedDateTime.class || javaType == OffsetDateTime.class) {
            format = sixFraction ? TimeUtils.SIX_FRACTION_ZONE_DATE_TIME_FORMAT : TimeUtils.ZONE_DATE_TIME_FORMAT;
        } else if (javaType == OffsetTime.class) {
            format = sixFraction ? TimeUtils.SIX_FRACTION_ZONED_TIME_FORMAT : TimeUtils.ZONE_TIME_FORMAT;
        } else if (javaType == Year.class) {
            format = TimeUtils.YEAR_FORMAT;
        } else if (javaType == YearMonth.class) {
            format = TimeUtils.YEAR_MONTH_FORMAT;
        } else if (javaType == MonthDay.class) {
            format = TimeUtils.MONTH_DAY_FORMAT;
        } else {
            throw new MetaException("%s java type isn't supported by io.army.domain.IDomain default constant."
                    , fieldMeta);
        }
        return TimeUtils.dateTimeFormatter(format);
    }


}
