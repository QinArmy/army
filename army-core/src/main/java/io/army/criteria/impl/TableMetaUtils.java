package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.*;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.sharding.Route;
import io.army.sharding.TableRoute;
import io.army.struct.CodeEnum;
import io.army.util.AnnotationUtils;
import io.army.util.Assert;
import io.army.util.ReflectionUtils;
import io.army.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 */
abstract class TableMetaUtils {

    private static final String PRIMARY_FIELD = TableMeta.ID;

    private static final String ASC = "ASC";

    private static final String DESC = "DESC";

    static Map<Class<?>, Map<Integer, Class<?>>> discriminatorCodeMap = new HashMap<>();

    protected TableMetaUtils() {

    }


    @NonNull
    static Table tableMeta(@NonNull Class<? extends IDomain> entityClass) {
        Table table = AnnotationUtils.getAnnotation(entityClass, Table.class);
        if (table == null) {
            throw createNonAnnotationException(entityClass, Table.class);
        }
        return table;
    }

    static String tableName(Table table, Class<? extends IDomain> entityClass) {
        Assert.hasText(table.name(), () -> String.format("Entity[%s] no tableMeta name", entityClass.getName()));
        return table.name();
    }

    static String tableComment(Table table, Class<? extends IDomain> entityClass) {
        Assert.hasText(table.comment(), () -> String.format("Entity[%s] no tableMeta comment", entityClass.getName()));
        return table.comment();
    }

    static <T extends IDomain> void assertParentTableMeta(@Nullable TableMeta<? super T> parentTableMeta
            , Class<T> entityClass) {
        if (parentTableMeta != null) {
            Assert.isAssignable(parentTableMeta.javaType(), entityClass);
        }
    }

    static SchemaMeta schemaMeta(Table table) {
        return SchemaMetaFactory.getSchema(table.catalog(), table.schema());
    }


    static int discriminatorValue(MappingMode mappingMode, TableMeta<?> tableMeta) {
        int value;
        DiscriminatorValue discriminatorValue;
        discriminatorValue = AnnotationUtils.getAnnotation(tableMeta.javaType(), DiscriminatorValue.class);
        switch (mappingMode) {
            case CHILD:
                value = extractModeChildDiscriminatorValue(tableMeta, discriminatorValue);
                TableMeta<?> parentMeta = tableMeta.parentMeta();
                Assert.notNull(parentMeta, () -> String.format("domain[%s] no parentMeta.", tableMeta.javaType().getName()));
                assertDiscriminatorValueIsEnumCode(parentMeta, value);
                break;
            case PARENT:
                if (discriminatorValue != null && discriminatorValue.value() != 0) {
                    throw new MetaException(ErrorCode.META_ERROR, "parentMeta domain[%s] DiscriminatorValue must equals 0"
                            , tableMeta.javaType().getName());
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

        final List<Class<?>> mappedClassList = mappedClassList(table.javaType());
        // this tableMeta's column to filed map, contains id ,key is lower case
        final Map<String, Field> columnToFieldMap = columnToFieldMap(table, mappedClassList);

        // 1.  indexMap meta
        final List<IndexMeta<T>> indexMetaList = indexMetaList(table, tableMeta, columnToFieldMap);

        // exclude indexMetaList column part
        final Map<String, Field> subMap = new HashMap<>(columnToFieldMap);

        Map<String, FieldMeta<T, ?>> propNameToFieldMeta = new HashMap<>((int) (columnToFieldMap.size() / 0.75f));

        Set<String> columnNameSet = new HashSet<>(), propNameSet = new HashSet<>();
        //2. appendText indexMap field meta to propNameToFieldMeta
        for (IndexMeta<T> indexMeta : indexMetaList) {
            String lowerCaseColumnName;
            for (IndexFieldMeta<T, ?> fieldMeta : indexMeta.fieldList()) {
                lowerCaseColumnName = StringUtils.toLowerCase(fieldMeta.fieldName());
                assertFieldMetaNotDuplication(lowerCaseColumnName, fieldMeta, columnNameSet, propNameSet);
                propNameToFieldMeta.put(fieldMeta.propertyName(), fieldMeta);
                columnNameSet.add(lowerCaseColumnName);

                propNameSet.add(fieldMeta.propertyName());
                subMap.remove(lowerCaseColumnName);
            }
        }
        //3. appendText rest field meta to propNameToFieldMeta
        FieldMeta<T, ?> fieldMeta;
        for (Map.Entry<String, Field> entry : subMap.entrySet()) {
            fieldMeta = DefaultFieldMeta.createFieldMeta(table, entry.getValue());

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


    static MappingMode tableMappingMode(@NonNull Class<? extends IDomain> entityClass) throws MetaException {
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
        Assert.isTrue(tableCount > 0, () -> String.format("class[%s] not entity", entityClass.getName()));

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
        if (TableMeta.RESERVED_PROPS.contains(field.getName()) && StringUtils.hasText(column.name())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "mapped class [%s] required prop[%s] column name must use default value.",
                    field.getDeclaringClass().getName(),
                    field.getName()
            );
        }
        String columnName = column.name();
        if (StringUtils.hasText(columnName)) {
            if (!columnName.trim().equals(columnName)) {
                throw new MetaException(ErrorCode.META_ERROR,
                        "mapped class [%s] required prop[%s] column name contain space.",
                        field.getDeclaringClass().getName(),
                        field.getName());
            }
        } else {
            columnName = StringUtils.camelToLowerCase(field.getName());
        }
        return columnName;
    }

    static <T extends IDomain> RouteMeta routeMeta(
            TableMeta<?> tableMeta, Map<String, FieldMeta<T, ?>> fieldMetaMap) {
        ShardingRoute shardingRoute = AnnotationUtils.getAnnotation(tableMeta.javaType(), ShardingRoute.class);
        if (shardingRoute == null) {
            return new RouteMeta(Collections.emptyList(), Collections.emptyList(), null);
        }
        Class<? extends Route> routeClass = loadShardingRouteClass(tableMeta, shardingRoute.value());
        List<FieldMeta<?, ?>> databaseRouteFieldList, tableRouteFieldList;
        if (io.army.sharding.ShardingRoute.class.isAssignableFrom(routeClass)) {
            String[] routeFields = shardingRoute.routeFields();
            if (routeFields.length == 0) {
                String[] databaseRouteFields = shardingRoute.databaseRouteFields();
                String[] tableRouteFields = shardingRoute.tableRouteFields();
                databaseRouteFieldList = getRouteFieldList(tableMeta, fieldMetaMap, databaseRouteFields);
                tableRouteFieldList = getRouteFieldList(tableMeta, fieldMetaMap, tableRouteFields);
            } else {
                databaseRouteFieldList = getRouteFieldList(tableMeta, fieldMetaMap, routeFields);
                tableRouteFieldList = databaseRouteFieldList;
            }
        } else if (TableRoute.class.isAssignableFrom(routeClass)) {
            String[] tableRouteFields = shardingRoute.routeFields();
            if (tableRouteFields.length == 0) {
                tableRouteFields = shardingRoute.tableRouteFields();
            }
            databaseRouteFieldList = Collections.emptyList();
            tableRouteFieldList = getRouteFieldList(tableMeta, fieldMetaMap, tableRouteFields);
        } else {
            throw new MetaException("TableMeta[%s] route class isn't %s or %s implementation."
                    , io.army.sharding.ShardingRoute.class.getName()
                    , TableRoute.class.getName());
        }
        return new RouteMeta(databaseRouteFieldList, tableRouteFieldList, routeClass);
    }

    /*################################ private method ####################################*/

    @SuppressWarnings("unchecked")
    private static Class<? extends Route> loadShardingRouteClass(TableMeta<?> tableMeta, String className) {
        try {
            Class<?> routeClass = Class.forName(className);
            if (!Route.class.isAssignableFrom(routeClass)) {
                throw new MetaException("TableMeta[%s] route class isn't %s type.", tableMeta, Route.class.getName());
            }
            return (Class<? extends Route>) routeClass;
        } catch (ClassNotFoundException e) {
            throw new MetaException(e, "TableMeta[%s] not found route implementation class[%s]", tableMeta, className);
        }
    }

    /**
     * @return a unmodifiable list
     */
    private static <T extends IDomain> List<FieldMeta<?, ?>> getRouteFieldList(TableMeta<?> tableMeta
            , Map<String, FieldMeta<T, ?>> fieldMetaMap, String[] routeFields) {
        if (routeFields.length == 0) {
            throw new MetaException("TableMeta[%s] not specified route fields", tableMeta);
        }
        List<FieldMeta<?, ?>> fieldMetaList = new ArrayList<>(routeFields.length);
        for (String propName : routeFields) {
            FieldMeta<T, ?> fieldMeta = fieldMetaMap.get(propName);
            if (fieldMeta == null) {
                throw new MetaException("TableMeta[%s] sharding field[%s] not found.", tableMeta, propName);
            }
            fieldMetaList.add(fieldMeta);
        }
        return Collections.unmodifiableList(fieldMetaList);
    }


    /**
     * @return mapping class list(unmodifiable) ,asSort by extends relation,entityClass is the last one.
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
                    throw new MetaException(ErrorCode.META_ERROR, "Domain[%s] mapping property[%s] duplication"
                            , mappingClass.getName()
                            , field.getName()
                    );
                }
                map.put(columnName, field);
                fieldNameSet.add(field.getName());
            });

        }
        if (!map.containsKey(PRIMARY_FIELD)) {
            // child MappingMode
            map.put(PRIMARY_FIELD, findIdFieldFromParent(tableMeta, mappedClassList.get(0)));
        }
        return Collections.unmodifiableMap(map);
    }


    private static Field findIdFieldFromParent(TableMeta<?> tableMeta, Class<?> topMappedClass) {
        Stack<Class<?>> superMappingStack = superMappingClassStack(topMappedClass);
        Field field;
        for (Class<?> superMapping; !superMappingStack.empty(); ) {
            superMapping = superMappingStack.pop();
            try {
                field = superMapping.getDeclaredField(PRIMARY_FIELD);
            } catch (NoSuchFieldException e) {
                continue;
            }
            Column column = AnnotationUtils.getAnnotation(field, Column.class);
            if (column == null) {
                throw new MetaException(ErrorCode.META_ERROR, "tableMeta[%s] not found primary column definition",
                        tableMeta.tableName());
            }
            return field;
        }
        throw new MetaException(ErrorCode.META_ERROR, "domain[%s] not found primary key column definition",
                tableMeta.javaType());
    }


    static MetaException createNonAnnotationException(Class<? extends IDomain> entityClass
            , Class<?> annotationClass) {
        return new MetaException(ErrorCode.META_ERROR, "class[%s] isn'field annotated by %s "
                , entityClass.getName()
                , annotationClass.getName());
    }


    private static void assertFieldMetaNotDuplication(String lowerColumnName, FieldMeta<?, ?> fieldMeta
            , Set<String> columnNameSet
            , Set<String> propNameSet) {

        if (columnNameSet.contains(lowerColumnName)) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] column[%s]  duplication",
                    fieldMeta.tableMeta().javaType(),
                    fieldMeta.fieldName()
            );
        }
        if (propNameSet.contains(fieldMeta.propertyName())) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] property[%s]  duplication",
                    fieldMeta.tableMeta().javaType(),
                    fieldMeta.propertyName()
            );
        }
    }

    private static int extractModeChildDiscriminatorValue(TableMeta<?> tableMeta
            , @Nullable DiscriminatorValue discriminatorValue) {

        int value;
        if (discriminatorValue == null) {
            throw new MetaException(ErrorCode.META_ERROR, "child domain[%s] no %s"
                    , tableMeta.javaType().getName()
                    , DiscriminatorValue.class.getName());
        }
        value = discriminatorValue.value();
        if (value == 0) {
            throw new MetaException(ErrorCode.META_ERROR, "child domain[%s] DiscriminatorValue cannot equals 0"
                    , tableMeta.javaType().getName());

        }
        TableMeta<?> parentMeta = tableMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("domain[%s] parentMeta error", tableMeta.javaType().getName()));

        Map<Integer, Class<?>> codeMap;
        if (discriminatorCodeMap == null) {
            discriminatorCodeMap = new HashMap<>();
        }
        codeMap = discriminatorCodeMap.computeIfAbsent(parentMeta.javaType(), key -> new HashMap<>());
        Class<?> actualClass = codeMap.get(value);
        if (actualClass != null && actualClass != tableMeta.javaType()) {
            throw new MetaException(ErrorCode.META_ERROR, "child domain[%s] DiscriminatorValue duplication. ",
                    tableMeta.javaType().getName());
        } else {
            codeMap.putIfAbsent(value, tableMeta.javaType());
        }
        return value;
    }


    /**
     * @param propNameToFieldMeta a unmodifiable map
     * @param columnToFieldMap    a unmodifiable map
     * @return discriminator FieldMeta
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private static <T extends IDomain> FieldMeta<? super T, ?> discriminator(TableMeta<T> tableMeta,
                                                                             Map<String, FieldMeta<T, ?>> propNameToFieldMeta,
                                                                             Map<String, Field> columnToFieldMap) {

        Inheritance inheritance;
        if (tableMeta instanceof ChildTableMeta) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
            return (FieldMeta<? super T, ?>) childMeta.parentMeta().discriminator();
        } else {
            inheritance = AnnotationUtils.getAnnotation(tableMeta.javaType(), Inheritance.class);
        }
        if (inheritance == null) {
            return null;
        }
        // make key lower case
        Field field = columnToFieldMap.get(StringUtils.toLowerCase(inheritance.value()));
        if (field == null) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] discriminator column[%s] not found",
                    tableMeta.javaType().getName(),
                    inheritance.value()
            );
        }
        if (field.getDeclaringClass() != tableMeta.javaType()) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] discriminator property[%s.%s] not match.",
                    tableMeta.javaType().getName(),
                    field.getDeclaringClass().getName(),
                    field.getName()
            );
        }
        FieldMeta<T, ?> fieldMeta = propNameToFieldMeta.get(field.getName());
        if (fieldMeta == null
                || !fieldMeta.fieldName().equals(inheritance.value())
                || fieldMeta.tableMeta() != tableMeta) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] discriminator column[%s] not found",
                    tableMeta.javaType().getName(),
                    inheritance.value()
            );
        }
        if (!fieldMeta.javaType().isEnum()
                || !CodeEnum.class.isAssignableFrom(fieldMeta.javaType())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "domain[%s] discriminator property java class[%s] isn'field a Enum that implements %s",
                    tableMeta.javaType().getName(),
                    fieldMeta.javaType().getName(),
                    CodeEnum.class.getName()
            );
        }
        return fieldMeta;
    }



    /*################################# indexMap meta part private method  start ###################################*/

    private static <E extends Enum<E> & CodeEnum, T extends IDomain> void assertDiscriminatorValueIsEnumCode(
            TableMeta<T> tableMeta, int value) {
        FieldMeta<?, E> fieldMeta = tableMeta.discriminator();
        Assert.notNull(fieldMeta, () -> String.format("domain[%s] no discriminator", tableMeta.javaType().getName()));

        if (CodeEnum.resolve(fieldMeta.javaType(), value) == null) {
            throw new MetaException("domain[%s] DiscriminatorValue not %s's code"
                    , tableMeta.javaType().getName()
                    , fieldMeta.javaType().getName()
            );
        }
    }


    /**
     * @param <T>              entity java class
     * @param columnToFieldMap a unmodifiable map
     * @return indexMap meta list(unmodifiable) of tableMeta,
     */
    private static <T extends IDomain> List<IndexMeta<T>> indexMetaList(final @NonNull TableMeta<T> tableMeta,
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

        // handle primary key indexMap, then primary key indexMap must be  first element of list.
        if (!createdColumnSet.contains(PRIMARY_FIELD)) {
            indexMeta = new DefaultIndexMeta<>(tableMeta, null, columnToFieldMap, createdColumnSet);
            indexMetaList.add(indexMeta);
        }
        return Collections.unmodifiableList(indexMetaList);
    }


    /**
     * debugSQL {@link Index}'s {@link IndexFieldMeta}
     *
     * @param createdColumnSet created column set  in  other indexMap
     * @param <T>              entity java class
     * @return value indexMap's {@link IndexFieldMeta}
     */
    private static <T extends IDomain> List<IndexFieldMeta<T, ?>> createIndexFieldMetaList(
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
                throw new MetaException("domain[%s] index map[%s] column definition[%s] error",
                        tableMeta.javaType().getName(), indexMeta.name(), indexColumnDefinition);
            }

            if (PRIMARY_FIELD.equals(lowerCaseColumnName)) {
                assertPrimaryKeyIndex(indexMeta, indexColumnDefinition, indexColumns.length);
            }
            field = indexField(lowerCaseColumnName, tableMeta, columnToFieldMap);
            indexFieldMeta = DefaultFieldMeta.createIndexFieldMeta(tableMeta, field, indexMeta, indexColumns.length
                    , columnAsc);

            list.add(indexFieldMeta);
            createdColumnSet.add(lowerCaseColumnName);
        }
        return Collections.unmodifiableList(list);
    }

    private static <T extends IDomain> void assertPrimaryKeyIndex(IndexMeta<T> indexMeta, String indexColumn,
                                                                  final int columnCount) {
        if (!indexMeta.unique() || columnCount != 1) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "domain[%s] indexMap[%s] indexMap column[%s] is error primary key,or not unique .",
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
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] indexMap[%s] column[%s] asSort error",
                    indexMeta.table().javaType(), indexMeta.name(), indexColumnDefinition);
        }
        return asc;
    }

    private static Field indexField(String columnName, TableMeta<?> table, Map<String, Field> fieldMap) {
        Field field = fieldMap.get(columnName);
        if (field == null) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] not found indexMap column[%s]",
                    table.javaType().getName(),
                    columnName
            );
        }
        if (!field.getDeclaringClass().isAssignableFrom(table.javaType())) {
            throw new MetaException(ErrorCode.META_ERROR, "mapping property[%s] not mapping in domain[%s]",
                    field.getName(),
                    table.javaType().getName()
            );
        }
        return field;
    }


    private static void assertIndexColumnNotDuplication(IndexMeta<?> indexMeta,
                                                        Set<String> createdIndexColumnSet, String columnName) {
        if (createdIndexColumnSet.contains(columnName)) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] indexMap[%s] column[%s] duplication",
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
         * @param index            indexMap or null ( when debugSQL primary key for which user don'field definite {@link Index})
         * @param columnToFieldMap a unmodifiable map
         */
        private DefaultIndexMeta(TableMeta<T> table, @Nullable Index index, Map<String, Field> columnToFieldMap,
                                 Set<String> createdColumnSet) {
            this.table = table;

            String[] columnArray;
            if (index == null) {
                this.name = "PRIMARY";
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
            this.fieldList = createIndexFieldMetaList(table, columnArray, this, columnToFieldMap, createdColumnSet);
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
        public boolean unique() {
            return this.unique;
        }

        @Override
        public String type() {
            return this.type;
        }
    }


    static class FieldBean<T extends IDomain> {


        private final Map<String, FieldMeta<T, ?>> propNameToFieldMeta;

        private final List<IndexMeta<T>> indexMetaList;

        private final FieldMeta<? super T, ?> discriminator;

        private FieldBean(Map<String, FieldMeta<T, ?>> propNameToFieldMeta, List<IndexMeta<T>> indexMetaList,
                          @Nullable FieldMeta<? super T, ?> discriminator) {
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

        @Nullable
        FieldMeta<? super T, ?> getDiscriminator() {
            return discriminator;
        }
    }

    static final class RouteMeta {

        final List<FieldMeta<?, ?>> databaseRouteFieldList;

        final List<FieldMeta<?, ?>> tableRouteFieldList;

        final Class<? extends Route> routeClass;

        RouteMeta(List<FieldMeta<?, ?>> databaseRouteFieldList, List<FieldMeta<?, ?>> tableRouteFieldList
                , @Nullable Class<? extends Route> routeClass) {
            this.databaseRouteFieldList = databaseRouteFieldList;
            this.tableRouteFieldList = tableRouteFieldList;
            this.routeClass = routeClass;
        }
    }

}