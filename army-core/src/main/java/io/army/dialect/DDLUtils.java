package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class DDLUtils {

    protected DDLUtils() {
        throw new UnsupportedOperationException();
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



    /**
     * @return a unmodifiable list
     */
    static List<FieldMeta<?, ?>> sortFieldMetaCollection(TableMeta<?> tableMeta) {
        Set<FieldMeta<?, ?>> fieldMetas = new HashSet<>(tableMeta.fieldList());

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
        Set<IndexMeta<?>> indexMetas = new HashSet<>(tableMeta.indexList());

        List<IndexMeta<?>> indexMetaList = new ArrayList<>(indexMetas.size());
        for (IndexMeta<?> indexMeta : indexMetas) {
            if (!indexMeta.isPrimaryKey()) {
                indexMetaList.add(indexMeta);
            }
        }
        return Collections.unmodifiableList(indexMetaList);
    }


    private static DateTimeFormatter formatterForTimeTypeDefaultValue(Class<?> javaType, int precision) {

        throw new UnsupportedOperationException();
    }


}
