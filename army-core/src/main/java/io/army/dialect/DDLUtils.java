package io.army.dialect;

import io.army.criteria.MetaException;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.SQLDataType;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;

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

    public static String zeroDateTime(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        int precision = fieldMeta.precision();
        DateTimeFormatter formatter;
        if (precision < 0) {
            formatter = TimeUtils.DATE_TIME_FORMATTER;
        } else if (precision <= 6) {
            formatter = TimeUtils.SIX_FRACTION_DATE_TIME_FORMATTER;
        } else {
            throw new MetaException("% ,precision must in [0,6] for dialect");
        }
        return StringUtils.quote(
                ZonedDateTime.ofInstant(Instant.EPOCH, zoneId)
                        .format(formatter)
        );
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


    static Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> createDefaultFunctionMap() {
        Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> map = new HashMap<>();

        map.put(String.class, DDLUtils::stringDefault);
        map.put(Long.class, DDLUtils::intDefault);
        map.put(Integer.class, DDLUtils::intDefault);
        map.put(BigDecimal.class, DDLUtils::decimalDefault);

        map.put(BigInteger.class, DDLUtils::intDefault);
        map.put(Byte.class, DDLUtils::intDefault);
        map.put(Double.class, DDLUtils::floatDefault);
        map.put(Float.class, DDLUtils::floatDefault);

        map.put(LocalTime.class, DDLUtils::timeDefault);
        map.put(Short.class, DDLUtils::intDefault);
        map.put(LocalDateTime.class, DDLUtils::dateTimeDefault);
        map.put(LocalDate.class, DDLUtils::dateDefault);

        map.put(ZonedDateTime.class, DDLUtils::zonedDateTimeDefault);
        return Collections.unmodifiableMap(map);
    }


    private static String stringDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "''";
    }

    private static String intDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "0";
    }

    private static String decimalDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
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

    private static String floatDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "0.0";
    }

    private static String timeDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "'" + LocalTime.MIDNIGHT.format(TimeUtils.TIME_FORMATTER) + "'";
    }

    private static String dateDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "'" + ZonedDateTime.ofInstant(Instant.EPOCH, zoneId)
                .toLocalDate().format(TimeUtils.DATE_FORMATTER) + "'";
    }

    private static String dateTimeDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "'" + ZonedDateTime.ofInstant(Instant.EPOCH, zoneId)
                .toLocalDateTime().format(TimeUtils.DATE_TIME_FORMATTER) + "'";
    }

    private static String zonedDateTimeDefault(FieldMeta<?, ?> fieldMeta, ZoneId zoneId) {
        return "'" + ZonedDateTime.ofInstant(Instant.EPOCH, zoneId).format(TimeUtils.ZONE_DATE_TIME_FORMATTER) + "'";
    }


}
