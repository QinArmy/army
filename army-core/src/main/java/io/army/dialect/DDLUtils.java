package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.MetaException;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.StringUtils;
import io.army.util.Times;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

public abstract class DDLUtils {

    protected DDLUtils() {
        throw new UnsupportedOperationException();
    }


    public static String zeroForTimeType(FieldMeta<?, ?> fieldMeta) {
        // TODO 验证 precision 大于 0 时 以 6 精度 是否能插入.
        Class<?> javaType = fieldMeta.javaType();
        String zeroValue;
        if (javaType == Month.class) {
            zeroValue = Month.JANUARY.name();
        } else if (javaType == DayOfWeek.class) {
            zeroValue = DayOfWeek.MONDAY.name();
        } else {
            int precision = fieldMeta.precision();
            if (precision > 6) {
                throw new MetaException("%s ,precision must in [0,6].", fieldMeta);
            }
            zeroValue = StringUtils.quote(
                    Times.ZERO_DATE_TIME.format(formatterForTimeTypeDefaultValue(fieldMeta.javaType(), precision))
            );
        }
        return zeroValue;
    }

    public static String constantForTimeType(Temporal temporal, int precision) {
        return StringUtils.quote(
                formatterForTimeTypeDefaultValue(temporal.getClass(), precision)
                        .format(temporal)
        );

    }


    public static String escapeQuote(String text) {
        return text.replaceAll("'", "\\\\'");
    }

    public static boolean timeTypeWithZone(Class<?> javaType) {
        return javaType == ZonedDateTime.class
                || javaType == OffsetDateTime.class
                || javaType == OffsetTime.class;
    }


    public static ExpressionSyntaxException createDefaultValueSyntaxException(FieldMeta<?, ?> fieldMeta) {
        return new ExpressionSyntaxException("%s,default expression no quote.", fieldMeta);
    }


    static boolean simpleJavaType(FieldMeta<?, ?> fieldMeta) {
        return _MetaBridge.MAYBE_NO_DEFAULT_TYPES.contains(fieldMeta.javaType())
                || (Enum.class.isAssignableFrom(fieldMeta.javaType())
                && CodeEnum.class.isAssignableFrom(fieldMeta.javaType()));
    }


    protected static void throwScaleException(FieldMeta<?, ?> fieldMeta) {
        throw new MetaException("Entity[%s].prop[%s]'s scale[%s] error."
                , fieldMeta.tableMeta().javaType()
                , fieldMeta.fieldName()
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
        if (tableMeta.containField(_MetaBridge.CREATE_TIME)) {
            fieldMetaList.add(tableMeta.getField(_MetaBridge.CREATE_TIME));
        }
        if (tableMeta.containField(_MetaBridge.UPDATE_TIME)) {
            fieldMetaList.add(tableMeta.getField(_MetaBridge.UPDATE_TIME));
        }
        if (tableMeta.containField(_MetaBridge.VERSION)) {
            fieldMetaList.add(tableMeta.getField(_MetaBridge.VERSION));
        }
        if (tableMeta.containField(_MetaBridge.VISIBLE)) {
            fieldMetaList.add(tableMeta.getField(_MetaBridge.VISIBLE));
        }
//        FieldMeta<?, ?> fieldMeta = tableMeta.discriminator();
//        if (fieldMeta != null && fieldMeta.tableMeta() == tableMeta) {
//            fieldMetaList.add(fieldMeta);
//        }
        // firstly
        fieldMetas.removeAll(fieldMetaList);
        // secondly
        fieldMetaList.addAll(fieldMetas);
        return Collections.unmodifiableList(fieldMetaList);
    }

    /**
     * @return a unmodifiable list without primary key
     */
    static List<IndexMeta<?>> sortIndexMetaCollection(TableMeta<?> tableMeta) {
        Set<IndexMeta<?>> indexMetas = new HashSet<>(tableMeta.indexCollection());

        List<IndexMeta<?>> indexMetaList = new ArrayList<>(indexMetas.size());
        for (IndexMeta<?> indexMeta : indexMetas) {
            if (!indexMeta.isPrimaryKey()) {
                indexMetaList.add(indexMeta);
            }
        }
        return Collections.unmodifiableList(indexMetaList);
    }


    private static DateTimeFormatter formatterForTimeTypeDefaultValue(Class<?> javaType, int precision) {
        if (precision > 6) {
            throw new IllegalArgumentException(String.format("Class[%s| ,precision must in [0,6]."
                    , javaType.getName()));
        }
        final boolean sixFraction = precision > 0;

        String format;
        if (javaType == LocalDateTime.class) {
            format = sixFraction ? Times.SIX_FRACTION_DATE_TIME_FORMAT : Times.DATE_TIME_FORMAT;
        } else if (javaType == LocalDate.class) {
            format = Times.DATE_FORMAT;
        } else if (javaType == LocalTime.class) {
            format = sixFraction ? Times.SIX_FRACTION_TIME_FORMAT : Times.TIME_FORMAT;
        } else if (javaType == ZonedDateTime.class || javaType == OffsetDateTime.class) {
            format = sixFraction ? Times.SIX_FRACTION_ZONE_DATE_TIME_FORMAT : Times.ZONE_DATE_TIME_FORMAT;
        } else if (javaType == OffsetTime.class) {
            format = sixFraction ? Times.SIX_FRACTION_ZONED_TIME_FORMAT : Times.ZONE_TIME_FORMAT;
        } else if (javaType == Year.class) {
            format = Times.YEAR_FORMAT;
        } else if (javaType == YearMonth.class) {
            format = Times.YEAR_MONTH_FORMAT;
        } else if (javaType == MonthDay.class) {
            format = Times.MONTH_DAY_FORMAT;
        } else {
            throw new IllegalArgumentException(String.format("Class[%s| not supported."
                    , javaType.getName()));
        }
        return Times.dateTimeFormatter(format);
    }


}
