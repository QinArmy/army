package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.*;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.struct.CodeEnum;
import io.army.util.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created  on 2019-02-21.
 */
abstract class MetaUtils {

    private static final String PRIMARY_FIELD = TableMeta.ID;

    private static final String ASC = "ASC";

    private static final String DESC = "DESC";

    private static final ConcurrentMap<String, SchemaMeta> SCHEMA_HOLDER = new ConcurrentHashMap<>();

    private MetaUtils() {

    }

    static Column columnMeta(@Nonnull Class<? extends IDomain> entityClass, @Nonnull Field field) throws MetaException {
        Column column = AnnotationUtils.getAnnotation(field, Column.class);
        if (column == null) {
            throw createNonAnnotationException(entityClass, Column.class);
        }
        return column;
    }

    @NonNull
    static Table tableMeta(@Nonnull Class<? extends IDomain> entityClass) {
        Table table = AnnotationUtils.getAnnotation(entityClass, Table.class);
        if (table == null) {
            throw createNonAnnotationException(entityClass, Table.class);
        }
        return table;
    }

    static <T extends IDomain> void assertParentTableMeta(TableMeta<? super T> parentTableMeta, Class<T> entityClass) {
        if (parentTableMeta != null) {
            Assert.isAssignable(parentTableMeta.javaType(), entityClass);
        }
    }

    static SchemaMeta schemaMeta(Table table) {
        return SCHEMA_HOLDER.computeIfAbsent(
                StringUtils.toLowerCase(table.catalog() + "." + table.schema())
                , key -> new SchemaMetaImpl(table.catalog(), table.schema())
        );
    }

    static boolean columnNullable(Column column, FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.primary() && column.nullable()) {
            throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] columnNullable must be false.",
                    fieldMeta.table().javaType(),
                    fieldMeta.fieldName()
            );
        }
        return column.nullable();
    }

    @NonNull
    static String columnComment(Column column, FieldMeta<?, ?> fieldMeta) {
        if (!TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                && !StringUtils.hasText(column.comment())) {
            throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] columnNullable must be false.",
                    fieldMeta.table().javaType(),
                    fieldMeta.fieldName()
            );
        }
        return fieldMeta.comment();
    }

    @Nonnull
    static MappingType columnMappingType(@Nonnull Field field) {
        Mapping mapping = AnnotationUtils.getAnnotation(field, Mapping.class);

        Class<?> mappingClass;
        if (mapping == null) {
            mappingClass = null;
        } else if (mapping.mapping() != AnnotationUtils.getDefaultValue(Mapping.class, "mapping")) {
            mappingClass = mapping.mapping();
        } else {
            try {
                mappingClass = ClassUtils.forName(mapping.value(), ClassUtils.getDefaultClassLoader());
            } catch (ClassNotFoundException e) {
                throw new MetaException(ErrorCode.META_ERROR, "%s.value() not a %s type."
                        , Mapping.class.getName()
                        , MappingType.class.getName()
                );
            }
        }

        MappingFactory mappingFactory = mappingFactory();
        MappingType mappingType;
        if (mappingClass == null) {
            mappingType = mappingFactory.getMapping(field.getType());
        } else {
            mappingType = mappingFactory.getMapping(field.getType(), mappingClass);
        }
        return mappingType;

    }

    static String columnDefault(Column column, FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.nullable()
                && String.class != fieldMeta.javaType()
                && !StringUtils.hasText(column.defaultValue())
                && !isManagedByArmy(fieldMeta)) {
            throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] no defaultValue.",
                    fieldMeta.table().javaType(),
                    fieldMeta.fieldName()
            );
        }
        return column.defaultValue().trim();
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
                    throw new MetaException(ErrorCode.META_ERROR, "parent's %s#value() must equals 0 . ",
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


    static <T extends IDomain> FieldBean<T> fieldMetaList(final @Nonnull TableMeta<T> table, Table tableMeta)
            throws MetaException {

        final List<Class<?>> mappedClassList = mappedClassList(table.javaType());
        // this table's column to filed map, contains id ,key is lower case
        final Map<String, Field> columnToFieldMap = columnToFieldMap(table, mappedClassList);

        // 1. create indexMap meta
        final List<IndexMeta<T>> indexMetaList = indexMetaList(table, tableMeta, columnToFieldMap);

        // exclude indexMetaList column part
        final Map<String, Field> subMap = notIndexColumnToField(indexMetaList, columnToFieldMap);

        Map<String, FieldMeta<T, ?>> propNameToFieldMeta = new HashMap<>((int) (columnToFieldMap.size() / 0.75f));

        Set<String> columnNameSet = new HashSet<>(), propNameSet = new HashSet<>();
        //2. append indexMap field meta to propNameToFieldMeta
        for (IndexMeta<T> indexMeta : indexMetaList) {
            String lowerCaseColumnName;
            for (IndexFieldMeta<T, ?> fieldMeta : indexMeta.fieldList()) {
                lowerCaseColumnName = StringUtils.toLowerCase(fieldMeta.fieldName());
                assertFieldMetaNotDuplication(lowerCaseColumnName, fieldMeta, columnNameSet, propNameSet);
                propNameToFieldMeta.put(fieldMeta.propertyName(), fieldMeta);
                columnNameSet.add(lowerCaseColumnName);
                propNameSet.add(fieldMeta.propertyName());
            }
        }
        //3. append rest field meta to propNameToFieldMeta
        FieldMeta<T, ?> fieldMeta;
        for (Map.Entry<String, Field> entry : subMap.entrySet()) {
            fieldMeta = new DefaultFieldMeta<>(table, entry.getValue(), false, false);

            String lowerCaseColumnName = StringUtils.toLowerCase(fieldMeta.fieldName());
            assertFieldMetaNotDuplication(lowerCaseColumnName, fieldMeta, columnNameSet, propNameSet);

            propNameToFieldMeta.put(fieldMeta.propertyName(), fieldMeta);
            columnNameSet.add(lowerCaseColumnName);
            propNameSet.add(fieldMeta.propertyName());
        }
        propNameToFieldMeta = Collections.unmodifiableMap(propNameToFieldMeta);
        return new FieldBean<T>(
                propNameToFieldMeta,
                indexMetaList,
                // 4. craete discriminator
                discriminator(table, propNameToFieldMeta, columnToFieldMap)
        );
    }


    static MappingMode mappingMode(@Nonnull Class<? extends IDomain> entityClass) throws MetaException {
        int tableCount = 0, inheritCount = 0;
        for (Class<?> superClass = entityClass; superClass != null; superClass = superClass.getSuperclass()) {
            if (AnnotationUtils.getAnnotation(superClass, Table.class) != null) {
                tableCount++;
                if (AnnotationUtils.getAnnotation(superClass, Inheritance.class) != null) {
                    inheritCount++;
                }
            } else if (AnnotationUtils.getAnnotation(superClass, MappedSuperclass.class) == null) {
                break;
            }
        }

        MappingMode mappingMode;
        if (inheritCount == 0) {
            mappingMode = MappingMode.SIMPLE;
        } else if (inheritCount == 1) {
            if (AnnotationUtils.getAnnotation(entityClass, Inheritance.class) != null) {
                mappingMode = MappingMode.PARENT;
            } else if (tableCount < 3) {
                mappingMode = MappingMode.CHILD;
            } else {
                mappingMode = null;
            }
        } else {
            mappingMode = null;
        }

        if (mappingMode == null) {
            throw new MetaException(ErrorCode.META_ERROR, "%s duplication for extending relation of %s",
                    Inheritance.class.getName(), entityClass.getName());
        }
        return mappingMode;

    }


    static String columnName(Column column, Field field) throws MetaException {
        if (TableMeta.VERSION_PROPS.contains(field.getName())
                && StringUtils.hasText(column.name())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "mapped class [%s] required prop[%s] column name must use default value.",
                    field.getDeclaringClass().getName(),
                    field.getName()
            );
        }
        String columnName = column.name();
        if (!StringUtils.hasText(columnName)) {
            columnName = StringUtils.camelToLowerCase(field.getName());
        }
        return columnName;
    }




    /*################################ private method ####################################*/

    private static boolean isManagedByArmy(FieldMeta<?, ?> fieldMeta) {
        Inheritance inheritance = AnnotationUtils.getAnnotation(fieldMeta.table().javaType(), Inheritance.class);

        return TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                || (inheritance != null
                && inheritance.value().equalsIgnoreCase(fieldMeta.fieldName()))
                ;
    }

    /**
     * @return mapping class list(unmodifiable) ,order by extends relation,entityClass is the last one.
     */
    private static List<Class<?>> mappedClassList(Class<?> entityClass) throws MetaException {
        List<Class<?>> list = new ArrayList<>(6);
        // add entity class firstly
        list.add(entityClass);

        for (Class<?> superClass = entityClass.getSuperclass(); superClass != null;
             superClass = superClass.getSuperclass()) {
            if (AnnotationUtils.getAnnotation(superClass, Inheritance.class) != null) {
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

    private static Stack<Class<?>> superMappingClassStack(Class<?> topMappedClass) throws MetaException {
        Stack<Class<?>> stack = new Stack<>();

        for (Class<?> superClass = topMappedClass; superClass != null;
             superClass = superClass.getSuperclass()) {
            if (AnnotationUtils.getAnnotation(superClass, MappedSuperclass.class) != null) {
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
                //make key lower case
                map.remove(StringUtils.toLowerCase(fieldMeta.fieldName()));
            }
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * @return map(unmodifiable) ,key: column name,value : {@link Field} ,
     */
    private static Map<String, Field> columnToFieldMap(TableMeta<?> tableMeta, List<Class<?>> mappedClassList) {
        final Map<String, Field> map = new HashMap<>();
        final Set<String> fieldNameSet = new HashSet<>();

        for (Class<?> mappingClass : mappedClassList) {
            ReflectionUtils.doWithLocalFields(mappingClass, field -> {
                Column column = AnnotationUtils.getAnnotation(field, Column.class);
                if (column == null) {
                    return;
                }
                // make column name lower case
                String columnName = StringUtils.toLowerCase(columnName(column, field));
                if (map.containsKey(columnName)) {
                    throw new MetaException(ErrorCode.META_ERROR, "mapping class[%s] column[%s] definition duplication"
                            , mappingClass.getName()
                            , columnName
                    );
                }
                if (fieldNameSet.contains(field.getName())) {
                    throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] mapping property[%s] duplication"
                            , mappingClass.getName()
                            , field.getName()
                    );
                }
                map.put(columnName, field);
                fieldNameSet.add(field.getName());
            });

        }
        if (!map.containsKey(PRIMARY_FIELD)) {
            map.put(PRIMARY_FIELD, findIdField(tableMeta, mappedClassList.get(0)));
        }
        return Collections.unmodifiableMap(map);
    }


    private static Field findIdField(TableMeta<?> tableMeta, Class<?> topMappedClass) {
        Stack<Class<?>> superMappingStack = superMappingClassStack(topMappedClass);
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
                if (column == null) {
                    throw new MetaException(ErrorCode.META_ERROR, "table[%s] not found primary column definition",
                            tableMeta.tableName());
                }
                return field;
            }
        }
        throw new MetaException(ErrorCode.META_ERROR, "entity[%s] not found primary key column definition",
                tableMeta.javaType());
    }


    private static MappingFactory mappingFactory() {
        return MappingFactory.build();
    }

    private static MetaException createNonAnnotationException(Class<? extends IDomain> entityClass
            , Class<?> annotationClass) {
        return new MetaException(ErrorCode.META_ERROR, "class[%s] isn't annotated by %s "
                , entityClass.getName()
                , annotationClass.getName());
    }


    private static void assertFieldMetaNotDuplication(String lowerColumnName, FieldMeta<?, ?> fieldMeta
            , Set<String> columnNameSet
            , Set<String> propNameSet) {

        if (columnNameSet.contains(lowerColumnName)) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] column[%s] create duplication",
                    fieldMeta.table().javaType(),
                    fieldMeta.fieldName()
            );
        }
        if (propNameSet.contains(fieldMeta.propertyName())) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] property[%s] create duplication",
                    fieldMeta.table().javaType(),
                    fieldMeta.propertyName()
            );
        }
    }

    /**
     * @param propNameToFieldMeta a unmodifiable map
     * @param columnToFieldMap    a unmodifiable map
     * @return discriminator FieldMeta
     */
    @Nullable
    private static <T extends IDomain> FieldMeta<T, ?> discriminator(TableMeta<T> tableMeta,
                                                                     Map<String, FieldMeta<T, ?>> propNameToFieldMeta,
                                                                     Map<String, Field> columnToFieldMap) {

        Inheritance inheritance = AnnotationUtils.getAnnotation(tableMeta.javaType(), Inheritance.class);
        if (inheritance == null) {
            return null;
        }
        // make key lower case
        Field field = columnToFieldMap.get(StringUtils.toLowerCase(inheritance.value()));
        if (field == null) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] discriminator column[%s] not found",
                    tableMeta.javaType().getName(),
                    inheritance.value()
            );
        }
        if (!field.getDeclaringClass().isAssignableFrom(tableMeta.javaType())) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] discriminator property[%s,%s] not match.",
                    tableMeta.javaType().getName(),
                    field.getDeclaringClass().getName(),
                    inheritance.value()
            );
        }
        FieldMeta<T, ?> fieldMeta = propNameToFieldMeta.get(field.getName());
        if (fieldMeta == null
                || !fieldMeta.fieldName().equals(inheritance.value())
                || fieldMeta.table() != tableMeta) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] discriminator column[%s] not found",
                    tableMeta.javaType().getName(),
                    inheritance.value()
            );
        }
        if (!fieldMeta.javaType().isEnum()
                || !CodeEnum.class.isAssignableFrom(fieldMeta.javaType())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "entity[%s] discriminator property java class[%s] isn't a Enum that implements %s",
                    tableMeta.javaType().getName(),
                    fieldMeta.javaType().getName(),
                    CodeEnum.class.getName()
            );
        }
        return fieldMeta;
    }



    /*################################# indexMap meta part private method  start ###################################*/

    /**
     * @param <T>              entity java class
     * @param columnToFieldMap a unmodifiable map
     * @return indexMap meta list(unmodifiable) of table,
     */
    private static <T extends IDomain> List<IndexMeta<T>> indexMetaList(final @Nonnull TableMeta<T> tableMeta,
                                                                        Table table,
                                                                        Map<String, Field> columnToFieldMap) {

        Index[] indexArray = table.indexes();
        List<IndexMeta<T>> indexMetaList = new ArrayList<>(indexArray.length + 1);

        IndexMeta<T> indexMeta;
        // column set to avoid duplication
        Set<String> createdColumnSet = new HashSet<>();

        for (Index index : indexArray) {
            indexMeta = new DefaultIndexMeta<>(tableMeta, index, columnToFieldMap, createdColumnSet);

            indexMetaList.add(indexMeta);
        }

        // handle primary key indexMap, and primary key indexMap must be  first element of list.
        if (!createdColumnSet.contains(PRIMARY_FIELD)) {
            indexMeta = new DefaultIndexMeta<>(tableMeta, null, columnToFieldMap, createdColumnSet);
            indexMetaList.add(indexMeta);
        }
        return Collections.unmodifiableList(indexMetaList);
    }


    /**
     * create {@link Index}'s {@link IndexFieldMeta}
     *
     * @param createdColumnSet created column set  in  other indexMap
     * @param <T>              entity java class
     * @return param indexMap's {@link IndexFieldMeta}
     */
    private static <T extends IDomain> List<IndexFieldMeta<T, ?>> indexFieldMetaList(
            final TableMeta<T> tableMeta,
            final String[] indexColumns,
            final IndexMeta<T> indexMeta,
            final Map<String, Field> columnToFieldMap,
            final Set<String> createdColumnSet) {

        List<IndexFieldMeta<T, ?>> list = new ArrayList<>(indexColumns.length);

        StringTokenizer tokenizer;
        IndexFieldMeta<T, ?> indexFieldMeta;
        Field field;
        String lowerCaseColumnName;
        boolean uniqueColumn;
        Boolean columnAsc;
        for (String indexColumnDefinition : indexColumns) {
            tokenizer = new StringTokenizer(indexColumnDefinition.trim(), " ", false);
            int tokenCount = tokenizer.countTokens();
            lowerCaseColumnName = StringUtils.toLowerCase(tokenizer.nextToken());
            assertIndexColumnNotDuplication(indexMeta, createdColumnSet, lowerCaseColumnName);

            if (tokenCount == 1) {
                columnAsc = null;
            } else if (tokenCount == 2) {
                columnAsc = isAscIndexColumn(tokenizer.nextToken(), indexMeta, indexColumnDefinition);
            } else {
                throw new MetaException(ErrorCode.META_ERROR, "entity[%s] index map[%s] column definition[%s] error",
                        tableMeta.javaType().getName(), indexMeta.name(), indexColumnDefinition);
            }

            if (PRIMARY_FIELD.equals(lowerCaseColumnName)) {
                assertPrimaryKeyIndex(indexMeta, indexColumnDefinition, indexColumns.length);
            }
            field = indexField(lowerCaseColumnName, tableMeta, columnToFieldMap);
            uniqueColumn = indexMeta.isUnique() && indexColumns.length == 1;
            indexFieldMeta = new DefaultIndexFieldMeta<>(tableMeta, field, indexMeta, uniqueColumn, columnAsc);

            list.add(indexFieldMeta);
            createdColumnSet.add(lowerCaseColumnName);
        }
        return Collections.unmodifiableList(list);
    }

    private static <T extends IDomain> void assertPrimaryKeyIndex(IndexMeta<T> indexMeta, String indexColumn,
                                                                  final int columnCount) {
        if (!indexMeta.isUnique() || columnCount != 1) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "entity[%s] indexMap[%s] indexMap column[%s] is error primary key,or not unique .",
                    indexMeta.table().javaType(), indexMeta.name(), indexColumn);
        }

    }

    private static boolean isAscIndexColumn(String order, IndexMeta<?> indexMeta, String indexColumnDefinition) {
        boolean asc;
        if (ASC.equalsIgnoreCase(order)) {
            asc = true;
        } else if (DESC.equalsIgnoreCase(order)) {
            asc = false;
        } else {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] indexMap[%s] column[%s] order error",
                    indexMeta.table().javaType(), indexMeta.name(), indexColumnDefinition);
        }
        return asc;
    }

    private static Field indexField(String columnName, TableMeta<?> table, Map<String, Field> fieldMap) {
        Field field = fieldMap.get(columnName);
        if (field == null) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] not found indexMap column[%s]",
                    table.javaType().getName(),
                    columnName
            );
        }
        if (!field.getDeclaringClass().isAssignableFrom(table.javaType())) {
            throw new MetaException(ErrorCode.META_ERROR, "mapping property[%s] not mapping in entity[%s]",
                    field.getName(),
                    table.javaType().getName()
            );
        }
        return field;
    }


    private static void assertIndexColumnNotDuplication(IndexMeta<?> indexMeta,
                                                        Set<String> createdIndexColumnSet, String columnName) {
        if (createdIndexColumnSet.contains(columnName)) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] indexMap[%s] column[%s] duplication",
                    indexMeta, indexMeta.name(), columnName);
        }
    }

    private static class DefaultIndexMeta<T extends IDomain> implements IndexMeta<T> {

        private final TableMeta<T> table;

        private final String name;

        private final boolean unique;

        private final String type;

        private final List<IndexFieldMeta<T, ?>> fieldList;

        private final boolean primaryKey;

        /**
         * @param index            indexMap or null ( when create primary key for which user don't definite {@link Index})
         * @param columnToFieldMap a unmodifiable map
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
                primaryKey = true;
            } else {
                this.name = index.name();
                this.unique = index.unique();
                this.type = index.type();
                columnArray = index.columnList();
                primaryKey = columnArray.length == 1 && TableMeta.ID.equals(columnArray[0]);
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
        public boolean isPrimaryKey() {
            return primaryKey;
        }

        @Override
        public boolean isUnique() {
            return this.unique;
        }

        @Override
        public String type() {
            return this.type;
        }
    }

    private static class DefaultIndexFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F>
            implements IndexFieldMeta<T, F> {

        private final IndexMeta<T> indexMeta;

        private final Boolean fieldAsc;

        DefaultIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta, boolean fieldUnique,
                              Boolean fieldAsc) throws MetaException {
            super(table, field, fieldUnique, true);
            this.indexMeta = indexMeta;
            this.fieldAsc = fieldAsc;
        }

        @Override
        public IndexMeta<T> indexMeta() {
            return this.indexMeta;
        }

        @Nullable
        @Override
        public Boolean fieldAsc() {
            return this.fieldAsc;
        }
    }


    static class FieldBean<T extends IDomain> {


        private final Map<String, FieldMeta<T, ?>> propNameToFieldMeta;

        private final List<IndexMeta<T>> indexMetaList;

        private final FieldMeta<T, ?> discriminator;

        private FieldBean(Map<String, FieldMeta<T, ?>> propNameToFieldMeta, List<IndexMeta<T>> indexMetaList,
                          FieldMeta<T, ?> discriminator) {
            this.propNameToFieldMeta = propNameToFieldMeta;
            this.indexMetaList = indexMetaList;
            this.discriminator = discriminator;
        }

        Map<String, FieldMeta<T, ?>> getPropNameToFieldMeta() {
            return propNameToFieldMeta;
        }

        List<IndexMeta<T>> getIndexMetaList() {
            return indexMetaList;
        }

        FieldMeta<T, ?> getDiscriminator() {
            return discriminator;
        }
    }

}
