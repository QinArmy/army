package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.*;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.AnnotationUtils;
import io.army.util.Assert;
import io.army.util.Precision;
import io.army.util.ReflectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * created  on 2019-02-21.
 */
abstract class MetaUtils {

    private static final String PRIMARY_FIELD = "id";

    private static final String ASC = "ASC";

    private static final String DESC = "DESC";

    private MetaUtils() {

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

    static <T extends IDomain> void assertParentList(List<TableMeta<? super T>> parentList,
                                                     Class<? extends IDomain> entityClass) {
        for (TableMeta<? super T> tableMeta : parentList) {
            Assert.isAssignable(tableMeta.javaType(), entityClass);
        }
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

    static int discriminatorValue(MappingMode mappingMode, Class<?> entityClass) {
        int value;
        DiscriminatorValue discriminatorValue = AnnotationUtils.getAnnotation(entityClass, DiscriminatorValue.class);
        switch (mappingMode) {
            case CHILD:
                if (discriminatorValue == null) {
                    throw new MetaException(ErrorCode.META_ERROR, "child's %s required ",
                            DiscriminatorValue.class.getName());
                }
                value = discriminatorValue.value();
                if (value == 0) {
                    throw new MetaException(ErrorCode.META_ERROR, "child's %s#value() cannot equals 0 . ",
                            DiscriminatorValue.class.getName());
                }
                break;
            case PARENT:
                if (discriminatorValue != null && discriminatorValue.value() != 0) {
                    throw new MetaException(ErrorCode.META_ERROR, "child's %s required ",
                            DiscriminatorValue.class.getName());
                }
                value = 0;
                break;
            case SIMPLE:
                value = 0;
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s]", mappingMode));

        }
        return value;
    }


    static <T extends IDomain> FieldBean<T> fieldMetaList(final @NonNull TableMeta<T> table, Table tableMeta)
            throws MetaException {

        final List<Class<?>> mappingClassList = mappingClassList(table.javaType());
        // this table's column to filed map, but contains id .
        final Map<String, Field> columnToFieldMap = columnToFieldMap(table, mappingClassList);

        final List<IndexMeta<T>> indexMetaList = indexMetaList(table, tableMeta, columnToFieldMap);

        // exclude indexMetaList column part
        final Map<String, Field> subMap = notIndexColumnToField(indexMetaList, columnToFieldMap);

        List<FieldMeta<T, ?>> fieldList = new ArrayList<>();

        for (Map.Entry<String, Field> entry : subMap.entrySet()) {
            fieldList.add(
                    new DefaultFieldMeta<>(table, entry.getValue(), false, false)
            );
        }

        for (IndexMeta<T> indexMeta : indexMetaList) {
            // append index field meta
            fieldList.addAll(indexMeta.fieldList());
        }
        return new FieldBean<T>().setIndexMetaList(indexMetaList)
                .setFieldMetaList(Collections.unmodifiableList(fieldList));
    }


    static <T extends IDomain> IndexFieldMeta<? super T, ?> primaryField(List<IndexMeta<T>> indexList,
                                                                         TableMeta<T> tableMeta) {
        for (IndexMeta<T> indexMeta : indexList) {
            for (IndexFieldMeta<T, ?> fieldMeta : indexMeta.fieldList()) {
                if (PRIMARY_FIELD.equals(fieldMeta.fieldName())) {
                    return fieldMeta;
                }
            }
        }
        throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] not found primary key definition"
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

        if (count == 0) {
            throw createNonAnnotationException(entityClass, Table.class);
        } else if (count == 1) {
            mappingMode = MappingMode.SIMPLE;
        } else {
            if (AnnotationUtils.getAnnotation(entityClass, Inheritance.class) == null) {
                mappingMode = MappingMode.CHILD;
            } else {
                mappingMode = MappingMode.PARENT;
            }
        }
        return mappingMode;

    }


    /*################################ private method ####################################*/

    /**
     * @return mapping class list(unmodifiable) ,order by extends relation,entityClass is the last one.
     */
    private static List<Class<?>> mappingClassList(Class<?> entityClass) throws MetaException {
        List<Class<?>> list = new ArrayList<>(5);
        if (AnnotationUtils.getAnnotation(entityClass, Table.class) != null) {
            list.add(entityClass);
        } else {
            throw new MetaException(ErrorCode.META_ERROR, "entity class isn't Entity,class[]", entityClass.getName());
        }

        for (Class<?> superClass = entityClass.getSuperclass(); superClass != null;
             superClass = superClass.getSuperclass()) {

            if (AnnotationUtils.getAnnotation(superClass, Table.class) != null) {
                break;
            }
            if (AnnotationUtils.getAnnotation(superClass, MappedSuperclass.class) != null) {
                list.add(superClass);
            } else {
                break;
            }
        }
        // reverse class list
        Collections.reverse(list);
        return Collections.unmodifiableList(list);
    }

    private static Stack<Class<?>> superMappingClassStack(Class<?> mappingClass) throws MetaException {
        Stack<Class<?>> stack = new Stack<>();

        for (Class<?> superClass = mappingClass.getSuperclass(); superClass != null;
             superClass = superClass.getSuperclass()) {

            if (AnnotationUtils.getAnnotation(superClass, Table.class) != null) {
                stack.push(superClass);
            } else if (AnnotationUtils.getAnnotation(superClass, MappedSuperclass.class) != null) {
                stack.push(superClass);
            } else {
                break;
            }
        }
        return stack;
    }

    /**
     * @param <T> entity java class
     * @return map(unmodifiable)
     */
    private static <T extends IDomain> Map<String, Field> notIndexColumnToField(List<IndexMeta<T>> indexMetaList,
                                                                                Map<String, Field> columnToFieldMap) {
        Map<String, Field> map = new HashMap<>(columnToFieldMap);

        for (IndexMeta<T> indexMeta : indexMetaList) {
            for (IndexFieldMeta<T, ?> fieldMeta : indexMeta.fieldList()) {
                map.remove(fieldMeta.fieldName());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * @return map(unmodifiable) ,key: column name,value : {@link Field} ,
     */
    private static Map<String, Field> columnToFieldMap(TableMeta<?> tableMeta, List<Class<?>> mappingClassList) {
        final Map<String, Field> map = new HashMap<>();
        for (Class<?> mappingClass : mappingClassList) {
            ReflectionUtils.doWithLocalFields(mappingClass, field -> {
                Column column = AnnotationUtils.getAnnotation(field, Column.class);
                if (column == null) {
                    return;
                }
                if (map.containsKey(column.name())) {
                    throw new MetaException(ErrorCode.META_ERROR, "mapping class[%s] column definition duplication",
                            mappingClass.getName());
                }
                map.put(column.name(), field);
            });

        }
        if (!map.containsKey(PRIMARY_FIELD)) {
            map.put(PRIMARY_FIELD, findIdField(tableMeta, mappingClassList.get(0)));
        }
        return Collections.unmodifiableMap(map);
    }

    private static Field findIdField(TableMeta<?> tableMeta, Class<?> mappingClass) {
        Stack<Class<?>> superMappingStack = superMappingClassStack(mappingClass);
        Field field;
        for (Class<?> superMapping; !superMappingStack.empty(); ) {
            superMapping = superMappingStack.pop();
            try {
                field = superMapping.getDeclaredField(PRIMARY_FIELD);
            } catch (NoSuchFieldException e) {
                continue;
            }
            if (field != null) {
                Column column = AnnotationUtils.getAnnotation(field, Column.class);
                if (column == null || !PRIMARY_FIELD.equals(column.name())) {
                    throw new MetaException(ErrorCode.META_ERROR, "table[%s] not found primary column definition",
                            tableMeta.tableName());
                }
                return field;
            }
        }
        throw new MetaException(ErrorCode.META_ERROR, "table[%s] not found primary column definition",
                tableMeta.tableName());
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

        private List<IndexMeta<T>> indexMetaList;

        List<FieldMeta<T, ?>> getFieldMetaList() {
            return fieldMetaList;
        }

        FieldBean<T> setFieldMetaList(List<FieldMeta<T, ?>> fieldMetaList) {
            this.fieldMetaList = fieldMetaList;
            return this;
        }

        List<IndexMeta<T>> getIndexMetaList() {
            return indexMetaList;
        }

        FieldBean<T> setIndexMetaList(List<IndexMeta<T>> indexMetaList) {
            this.indexMetaList = indexMetaList;
            return this;
        }
    }



    /*################################# index meta part private method  start ###################################*/

    /**
     * @param <T> entity java class
     * @return index meta list(unmodifiable) of table,
     */
    private static <T extends IDomain> List<IndexMeta<T>> indexMetaList(final @NonNull TableMeta<T> table,
                                                                        Table tableMeta,
                                                                        Map<String, Field> columnToFieldMap) {

        Index[] indexArray = tableMeta.indexes();
        List<IndexMeta<T>> indexMetaList = new ArrayList<>(indexArray.length);

        IndexMeta<T> indexMeta;
        Set<String> createdColumnSet = new HashSet<>();

        int idIndex = -1;
        for (Index index : indexArray) {
            indexMeta = new DefaultIndexMeta<>(table, index, columnToFieldMap,
                    Collections.unmodifiableSet(createdColumnSet));

            indexMetaList.add(indexMeta);
            for (IndexFieldMeta<T, ?> fieldMeta : indexMeta.fieldList()) {
                if (createdColumnSet.contains(fieldMeta.fieldName())) {
                    // if army code no bug ,never execute this line.
                    throw new RuntimeException(String.format("army code error,column[%s,%s] duplication",
                            table.tableName(), fieldMeta.fieldAsc()));
                }
                createdColumnSet.add(fieldMeta.fieldName());
                if (PRIMARY_FIELD.equals(fieldMeta.fieldName())) {
                    idIndex = indexMetaList.size() - 1;
                }
            }
        }

        // handle primary key index, and primary key index must be  first element of list.
        if (createdColumnSet.contains(PRIMARY_FIELD)) {
            if (idIndex != 0) {
                switchPrimaryKeyIndexMeta(idIndex, indexMetaList);
            }
        } else {
            indexMeta = new DefaultIndexMeta<>(table, null, columnToFieldMap,
                    Collections.unmodifiableSet(createdColumnSet));
            List<IndexMeta<T>> list = new ArrayList<>(indexArray.length + 1);
            // first,add  primary key index
            list.add(indexMeta);
            list.addAll(indexMetaList);
            // replace indexMetaList
            indexMetaList = list;
        }
        return Collections.unmodifiableList(indexMetaList);
    }


    private static <T extends IDomain> void switchPrimaryKeyIndexMeta(final int idIndex,
                                                                      List<IndexMeta<T>> indexMetaList) {
        IndexMeta<T> primaryKeyIndex;
        primaryKeyIndex = indexMetaList.get(idIndex);
        indexMetaList.set(idIndex, indexMetaList.get(0));
        indexMetaList.set(0, primaryKeyIndex);
    }

    /**
     * create {@link Index}'s {@link IndexFieldMeta}
     *
     * @param createdColumnSet created column set  in  other index
     * @param <T>              entity java class
     * @return param index's {@link IndexFieldMeta}
     */
    private static <T extends IDomain> List<IndexFieldMeta<T, ?>> indexFieldMetaList(
            final TableMeta<T> table,
            final String[] indexColumns,
            final IndexMeta<T> indexMeta,
            final Map<String, Field> columnToFieldMap,
            final Set<String> createdColumnSet) {

        List<IndexFieldMeta<T, ?>> list = new ArrayList<>(indexColumns.length);

        StringTokenizer tokenizer;
        IndexFieldMeta<T, ?> indexFieldMeta;
        Field field;
        String columnName;
        boolean columnAsc, uniqueColumn;

        // created column set  in this  index
        final Set<String> createdIndexColumnSet = new HashSet<>((int) (indexColumns.length / 0.75));

        for (String indexColumn : indexColumns) {
            tokenizer = new StringTokenizer(indexColumn, " ", false);
            // assert index column definition
            assertIndexColumn(tokenizer.countTokens(), table.tableName(), indexMeta.name(), indexColumn);

            columnName = tokenizer.nextToken();
            if (createdIndexColumnSet.contains(columnName)) {
                throw new MetaException(ErrorCode.META_ERROR, "index[%s] column[%s.%s] duplication",
                        indexMeta.name(), table.tableName(), columnName);
            }
            if (tokenizer.countTokens() == 2) {
                columnAsc = isAscIndexColumn(tokenizer.nextToken(), table.tableName(), indexMeta.name(), indexColumn);
            } else {
                columnAsc = true;
            }

            if (PRIMARY_FIELD.equals(columnName)) {
                assertPrimaryKeyIndex(indexMeta, indexColumn, indexColumns.length);
            }

            field = indexField(columnName, table, columnToFieldMap);

            if (createdColumnSet.contains(columnName)) {
                // created column set  in  other index
                continue;
            }

            uniqueColumn = indexMeta.unique() && indexColumns.length == 1;
            indexFieldMeta = new DefaultIndexFieldMeta<>(table, field, indexMeta, uniqueColumn, columnAsc);

            list.add(indexFieldMeta);
            createdIndexColumnSet.add(columnName);
        }
        return Collections.unmodifiableList(list);
    }

    private static <T extends IDomain> void assertPrimaryKeyIndex(IndexMeta<T> indexMeta, String indexColumn,
                                                                  final int columnCount) {
        if (!indexMeta.unique() || columnCount != 1) {
            throw new MetaException(ErrorCode.META_ERROR, "index[%s] indexColumn[%s.%s] is error primary key.",
                    indexMeta.name(), indexMeta.table().tableName(), indexColumn);
        }

    }

    private static boolean isAscIndexColumn(String order, String tableName, String indexName, String column) {
        boolean asc;
        if (ASC.equalsIgnoreCase(order)) {
            asc = true;
        } else if (DESC.equalsIgnoreCase(order)) {
            asc = false;
        } else {
            throw new MetaException(ErrorCode.META_ERROR, "table[%s] index[%s] column[%s] order error",
                    tableName, indexName, column);
        }
        return asc;
    }

    private static Field indexField(String columnName, TableMeta<?> table, Map<String, Field> fieldMap) {
        Field field = fieldMap.get(columnName);
        if (field == null || !field.getDeclaringClass().isAssignableFrom(table.javaType())) {
            throw new MetaException(ErrorCode.META_ERROR, "not found index column in table[%s]",
                    table.tableName());
        }
        Column column = AnnotationUtils.getAnnotation(field, Column.class);
        if (column == null || column.name().equals(columnName)) {
            throw new MetaException(ErrorCode.META_ERROR, "not found index column in table[%s]",
                    table.tableName());
        }
        return field;
    }

    private static void assertIndexColumn(int tokensCount, String tableName, String indexName, String column)
            throws MetaException {
        if (tokensCount < 1 || tokensCount > 2) {
            throw new MetaException(ErrorCode.META_ERROR, "table[%s] index[%s] column[%s] error",
                    tableName, indexName, column);
        }
    }

    private static class DefaultIndexMeta<T extends IDomain> implements IndexMeta<T> {

        private final TableMeta<T> table;

        private final String name;

        private final boolean unique;

        private final String type;

        private final List<IndexFieldMeta<T, ?>> fieldList;

        /**
         * @param index index or null ( when create primary key for which user don't definite {@link Index})
         */
        private DefaultIndexMeta(TableMeta<T> table, @Nullable Index index, Map<String, Field> columnToFieldMap,
                                 Set<String> createdColumnSet) {
            this.table = table;

            String[] columnArray;
            if (index == null) {
                this.name = PRIMARY_FIELD;
                this.unique = true;
                this.type = "";
                columnArray = new String[]{PRIMARY_FIELD};
            } else {
                this.name = index.name();
                this.unique = index.unique();
                this.type = index.type();
                columnArray = index.columnList();
            }
            this.fieldList = indexFieldMetaList(table, columnArray, this, columnToFieldMap, createdColumnSet);
        }


        @Override
        public TableMeta<T> table() {
            return this.table;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public List<IndexFieldMeta<T, ?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public boolean unique() {
            return this.unique;
        }

        @Override
        public String type() {
            return this.type;
        }
    }

}
