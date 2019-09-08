package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.*;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingException;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.struct.CodeEnum;
import io.army.util.Precision;
import org.qinarmy.foundation.util.*;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * created  on 2019-02-21.
 */
abstract class MetaUtils {


    public static final Map<Class<?>, JDBCType> DEFAULT_MAPPING = createDefaultMapping();

    public static final Map<Class<?>, Integer> DEFAULT_LENGTH = createDefaultLength();

    public static final Set<Class<?>> VARIABLE_LENGTH = ArrayUtils.asUnmodifiableSet(
            String.class
    );


    private static Map<Class<?>, JDBCType> createDefaultMapping() {
        Map<Class<?>, JDBCType> map = new HashMap<>();

        map.put(String.class, JDBCType.VARCHAR);
        map.put(Long.class, JDBCType.BIGINT);
        map.put(Integer.class, JDBCType.INTEGER);
        map.put(Boolean.class, JDBCType.CHAR);

        map.put(BigDecimal.class, JDBCType.DECIMAL);
        map.put(CodeEnum.class, JDBCType.INTEGER);
        map.put(LocalDateTime.class, JDBCType.TIMESTAMP);
        map.put(LocalDate.class, JDBCType.DATE);

        map.put(LocalTime.class, JDBCType.TIME);

        return Collections.unmodifiableMap(map);
    }

    private static Map<Class<?>, Integer> createDefaultLength() {
        Map<Class<?>, Integer> map = new HashMap<>();

        map.put(String.class, 255);
        map.put(Long.class, 20);
        map.put(Integer.class, 11);
        map.put(Boolean.class, 1);

        map.put(CodeEnum.class, 11);

        return Collections.unmodifiableMap(map);
    }

    static Column columnMeta(@NonNull Class<? extends IDomain> entityClass, @NonNull Field field) throws MetaException {
        Column column = AnnotationUtils.getAnnotation(field, Column.class);
        if (column == null) {
            throw createNonAnnotationException(entityClass, Column.class);
        }
        return column;
    }

    @NonNull
    static Table tableMeta(@NonNull Class<? extends IDomain> entityClass) {
        Table table = AnnotationUtils.getAnnotation(entityClass, Table.class);
        if (table == null) {
            throw createNonAnnotationException(entityClass, Table.class);
        }
        return table;
    }


    @NonNull
    static MappingType mappingType(@NonNull Field field) {
        Mapping mapping = AnnotationUtils.getAnnotation(field, Mapping.class);
        MappingFactory mappingFactory = mappingFactory();
        return mapping == null
                ? mappingFactory.getMapping(field.getType())
                : mappingFactory.getMapping(field.getType(), mapping.value());
    }


    static int precision(@NonNull Column column, Precision precision) {
        Integer defaultPrecision = (Integer) AnnotationUtils.getDefaultValue(column, "precision");
        if (defaultPrecision == null) {
            // never this
            throw new RuntimeException();
        }
        return column.precision() == defaultPrecision ? precision.getPrecision() : column.precision();
    }

    static int scale(@NonNull Column column, Precision precision) {
        Integer defaultScale = (Integer) AnnotationUtils.getDefaultValue(column, "scale");
        if (defaultScale == null) {
            // never this
            throw new RuntimeException();
        }
        return column.scale() == defaultScale ? precision.getScale() : column.scale();
    }

    static <F> String columnDefaultValue(@NonNull FieldMeta<?, F> fieldMeta, @NonNull Column column) {
        String value;
        if (fieldMeta.javaType() == String.class) {
            value = '\'' + column.defaultValue() + '\'';
        } else {
            value = column.defaultValue();
        }
        return value;
    }


    @NonNull
    static String tableName(@NonNull Class<? extends IDomain> entityClass) throws MetaException {
        return tableMeta(entityClass).name();
    }


    static <T extends IDomain> FieldBean<T> fieldMetaList(final @NonNull TableMeta<T> table, Table tableMeta,
                                                          List<TableMeta<? super T>> supperList)
            throws MetaException {

        Set<String> propSet = createdPropNameSet(supperList);
        Stack<Class<?>> superStack = mappingClassStack(table.javaType());

        Pair<Set<String>, Set<String>> indexPair = indexPair(tableMeta);
        final Set<String> indexSet = indexPair.getFirst();
        final Set<String> uniqueSet = indexPair.getSecond();

        List<FieldMeta<T, ?>> fieldList = new ArrayList<>(), indexList = new ArrayList<>(),
                uniqueList = new ArrayList<>();

        for (Class<?> mappingClass; !superStack.empty(); ) {
            mappingClass = superStack.pop();
            ReflectionUtils.doWithLocalFields(mappingClass, fc -> {
                Column column = AnnotationUtils.getAnnotation(fc, Column.class);
                if (column == null) {
                    return;
                }
                if (propSet.contains(fc.getName())) {
                    throw new MappingException(ErrorCode.META_ERROR,
                            "property[%s.%s] definition duplication", fc.getDeclaringClass().getName(), fc.getName());
                }
                FieldMeta<T, ?> fieldMeta = new DefaultFieldMeta<>(table, fc,
                        uniqueSet.contains(column.name()),
                        indexSet.contains(column.name()));

                fieldList.add(fieldMeta);
                if (fieldMeta.isUnique()) {
                    uniqueList.add(fieldMeta);
                } else if (fieldMeta.isIndex()) {
                    indexList.add(fieldMeta);
                }
                propSet.add(fc.getName());
            });

        }

        return new FieldBean<T>()
                .setFieldMetaList(Collections.unmodifiableList(new ArrayList<>(fieldList)))
                .setIndexList(Collections.unmodifiableList(new ArrayList<>(indexList)))
                .setUniqueList(Collections.unmodifiableList(new ArrayList<>(uniqueList)))
                ;
    }

    static <T extends IDomain> FieldMeta<? super T, ?> primaryField(List<TableMeta<? super T>> supperList,
                                                                    List<FieldMeta<T, ?>> fieldMetaList,
                                                                    TableMeta<T> tableMeta) {
        if (!CollectionUtils.isEmpty(supperList)) {
            return supperList.get(0).primaryKey();
        }
        for (FieldMeta<T, ?> fieldMeta : fieldMetaList) {
            if (fieldMeta.isPrimary()) {
                return fieldMeta;
            }
        }
        throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] not found primary field"
                , tableMeta.javaType().getName());
    }


    static MappingMode mappingMode(@NonNull Class<? extends IDomain> entityClass) {
        Class<?> clazz = entityClass;
        int count = 0;
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            if (AnnotationUtils.getAnnotation(clazz, Table.class) != null) {
                count++;
            }
        }
        MappingMode mappingMode;
        switch (count) {
            case 0:
                throw createNonAnnotationException(entityClass, Table.class);
            case 1:
                if (AnnotationUtils.getAnnotation(entityClass, Inheritance.class) == null) {
                    mappingMode = MappingMode.SIMPLE;
                } else {
                    mappingMode = MappingMode.PARENT;
                }
                break;
            default:
                mappingMode = MappingMode.CHILD;
                break;
        }
        return mappingMode;

    }


    /*################################ private method ####################################*/

    private static Stack<Class<?>> mappingClassStack(Class<?> entityClass) throws MetaException {
        Stack<Class<?>> stack = new Stack<>();
        if (AnnotationUtils.getAnnotation(entityClass, Table.class) != null) {
            stack.push(entityClass);
        } else {
            throw new MetaException(ErrorCode.META_ERROR, "entity class isn't Entity,class[]", entityClass.getName());
        }

        for (Class<?> superClass = entityClass.getSuperclass(); superClass != null;
             superClass = superClass.getSuperclass()) {

            if (AnnotationUtils.getAnnotation(superClass, Table.class) != null) {
                break;
            }
            if (AnnotationUtils.getAnnotation(superClass, MappedSuperclass.class) != null) {
                stack.push(superClass);
            } else {
                break;
            }
        }
        return stack;
    }

    private static Pair<Set<String>, Set<String>> indexPair(Table table) {
        Index[] indexArray = table.indexes();
        Set<String> indexSet = new HashSet<>(), uniqueSet = new HashSet<>();
        String[] columnArray;
        for (Index index : indexArray) {
            columnArray = index.columnList();
            if (index.unique() && columnArray.length == 1) {
                uniqueSet.add(columnArray[0]);
            } else {
                Collections.addAll(indexSet, columnArray);
            }
        }
        return new Pair<>(Collections.unmodifiableSet(indexSet), Collections.unmodifiableSet(uniqueSet));
    }


    private static <T extends IDomain> Set<String> createdPropNameSet(List<TableMeta<? super T>> supperList) {
        if (CollectionUtils.isEmpty(supperList)) {
            return Collections.emptySet();
        }
        Set<String> propSet = new HashSet<>();
        for (TableMeta<? super T> tableMeta : supperList) {
            for (FieldMeta<? super T, ?> fieldMeta : tableMeta.fieldList()) {
                propSet.add(fieldMeta.propertyName());
            }
        }
        return propSet;
    }


    private static MappingFactory mappingFactory() {
        return MappingFactory.getDefaultInstance();
    }

    private static MetaException createNonAnnotationException(Class<? extends IDomain> entityClass
            , Class<?> annotationClass) {
        return new MetaException(ErrorCode.META_ERROR, "class[%s] isn't annotated by %s "
                , entityClass.getName()
                , annotationClass.getName());
    }


    static class FieldBean<T extends IDomain> {

        private List<FieldMeta<T, ?>> fieldMetaList;

        private List<FieldMeta<T, ?>> indexList;

        private List<FieldMeta<T, ?>> uniqueList;


        List<FieldMeta<T, ?>> getFieldMetaList() {
            return fieldMetaList;
        }

        FieldBean<T> setFieldMetaList(List<FieldMeta<T, ?>> fieldMetaList) {
            this.fieldMetaList = fieldMetaList;
            return this;
        }

        List<FieldMeta<T, ?>> getIndexList() {
            return indexList;
        }

        FieldBean<T> setIndexList(List<FieldMeta<T, ?>> indexList) {
            this.indexList = indexList;
            return this;
        }

        List<FieldMeta<T, ?>> getUniqueList() {
            return uniqueList;
        }

        FieldBean<T> setUniqueList(List<FieldMeta<T, ?>> uniqueList) {
            this.uniqueList = uniqueList;
            return this;
        }

    }

}
