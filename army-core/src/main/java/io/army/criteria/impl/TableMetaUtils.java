package io.army.criteria.impl;

import io.army.annotation.*;
import io.army.dialect._Constant;
import io.army.generator.FieldGenerator;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util._StringUtils;
import io.qinarmy.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


abstract class TableMetaUtils {

    TableMetaUtils() {
        throw new UnsupportedOperationException();
    }

    private static final String PRIMARY_FIELD = _MetaBridge.ID;

    private static final String ASC = "ASC";

    private static final String DESC = "DESC";

    private static Map<Class<?>, Map<Integer, Class<?>>> discriminatorCodeMap = new HashMap<>();

    private static Map<Class<?>, Pair<Set<String>, Field>> parentFieldPairCache = new ConcurrentHashMap<>();


    static synchronized void clearCache() {
        final Map<Class<?>, Map<Integer, Class<?>>> discriminatorCodeMap = TableMetaUtils.discriminatorCodeMap;
        if (discriminatorCodeMap != null) {
            discriminatorCodeMap.clear();
            TableMetaUtils.discriminatorCodeMap = null;
        }
        final Map<Class<?>, Pair<Set<String>, Field>> parentFieldPairCache = TableMetaUtils.parentFieldPairCache;
        if (parentFieldPairCache != null) {
            parentFieldPairCache.clear();
            TableMetaUtils.parentFieldPairCache = null;
        }

    }

    private static synchronized Map<Class<?>, Map<Integer, Class<?>>> createDiscriminatorCodeMap() {
        Map<Class<?>, Map<Integer, Class<?>>> map = TableMetaUtils.discriminatorCodeMap;
        if (map == null) {
            map = new ConcurrentHashMap<>();
            TableMetaUtils.discriminatorCodeMap = map;
        }
        return map;
    }

    private static synchronized Map<Class<?>, Pair<Set<String>, Field>> createParentFieldPairCache() {
        Map<Class<?>, Pair<Set<String>, Field>> map = TableMetaUtils.parentFieldPairCache;
        if (map == null) {
            map = new ConcurrentHashMap<>();
            TableMetaUtils.parentFieldPairCache = map;
        }
        return map;
    }


    @NonNull
    static Table tableMeta(@NonNull Class<?> entityClass) {
        final Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            throw createNonAnnotationException(entityClass, Table.class);
        }
        return table;
    }

    static String tableName(Table table, Class<?> domainClass) {
        final String name = table.name();
        if (!_StringUtils.hasText(name)) {
            String m = String.format("Domain[%s] table name required", domainClass.getName());
            throw new MetaException(m);
        }
        return name;
    }

    static String tableComment(Table table, Class<?> domainClass) {
        final String comment = table.comment();
        if (!_StringUtils.hasText(comment)) {
            String m = String.format("Domain[%s] no tableMeta comment", domainClass.getName());
            throw new MetaException(m);
        }
        return comment;
    }

    static boolean immutable(Table table, Class<?> domainClass) {
        final boolean immutable = table.immutable();
        if (immutable && domainClass.getAnnotation(Inheritance.class) != null) {
            String m = String.format("Parent Domain[%s] couldn't be immutable.", domainClass.getName());
            throw new MetaException(m);
        }
        return immutable;
    }

    static <T> void assertParentTableMeta(ParentTableMeta<? super T> parentTableMeta
            , Class<T> domainClass) {
        if (!(parentTableMeta instanceof DefaultTableMeta)) {
            String m = String.format("%s isn't instance of %s", TableMeta.class.getName()
                    , DefaultTableMeta.class.getName());
            throw new MetaException(m);
        }
        if (!parentTableMeta.javaType().isAssignableFrom(domainClass)) {
            String m = String.format("%s java type[%s] isn't isAssignable from of %s", TableMeta.class.getName()
                    , parentTableMeta.javaType().getName()
                    , domainClass.getName());
            throw new MetaException(m);
        }
    }

    static int discriminatorValue(final Class<?> domainClass) {

        final DiscriminatorValue discriminatorValue;
        discriminatorValue = domainClass.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue == null) {
            String m = String.format("Domain[%s] isn't annotated by %s."
                    , domainClass.getName(), DiscriminatorValue.class.getName());
            throw new MetaException(m);
        }

        final int value;
        value = discriminatorValue.value();
        if (value == 0) {
            String m = String.format("Child Domain[%s] must not 0 .", domainClass.getName());
            throw new MetaException(m);
        }
        return value;
    }

    static <T> FieldMeta<T> discriminator(final Map<String, FieldMeta<T>> fieldMetaMap
            , final Class<T> domainClass) {
        final Inheritance inheritance = domainClass.getAnnotation(Inheritance.class);
        assert inheritance != null;
        final String fieldName = inheritance.value();
        final FieldMeta<T> discriminator = fieldMetaMap.get(fieldName);
        if (discriminator == null) {
            throw notFoundDiscriminator(fieldName, domainClass);
        }
        final Class<?> fieldJavaType = discriminator.javaType();
        if (!fieldJavaType.isEnum() || !CodeEnum.class.isAssignableFrom(fieldJavaType)) {
            String m = String.format("Discriminator[%s] in domain[%s] isn't %s type."
                    , fieldName, domainClass.getName(), CodeEnum.class.getName());
            throw new MetaException(m);
        }
        final DiscriminatorValue discriminatorValue = domainClass.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null && discriminatorValue.value() != 0) {
            String m = String.format("%s.value[%s] of parent must be 0 ."
                    , DiscriminatorValue.class.getName(), discriminatorValue.value());
            throw new MetaException(m);
        }
        return discriminator;
    }


    static MetaException notFoundDiscriminator(String fieldName, Class<?> domainClass) {
        String m = String.format("Not found discriminator[%s] in domain[%s].", fieldName, domainClass.getName());
        return new MetaException(m);
    }


    /**
     * @see #createFieldMetaPair(TableMeta)
     */
    static DomainPair mappedClassPair(final Class<?> domainClass) throws MetaException {
        List<Class<?>> list = new ArrayList<>(6);
        // add entity class firstly
        list.add(domainClass);
        Class<?> parentDomainClass = null;

        final boolean inheritance = domainClass.getAnnotation(Inheritance.class) != null;
        for (Class<?> superClass = domainClass.getSuperclass(); superClass != null;
             superClass = superClass.getSuperclass()) {
            if (superClass.getAnnotation(Inheritance.class) != null) {
                if (inheritance) {
                    throw inheritanceDuplication(domainClass);
                }
                parentDomainClass = superClass;
                break;
            }
            if (superClass.getAnnotation(MappedSuperclass.class) != null
                    || superClass.getAnnotation(Table.class) != null) {
                list.add(superClass);
            } else {
                break;
            }
        }

        if (list.size() == 1) {
            list = Collections.singletonList(list.get(0));
        } else {
            // reverse class list
            Collections.reverse(list);
            list = Collections.unmodifiableList(list);
        }
        return new DomainPair(list, parentDomainClass);
    }


    static <T> FieldMetaPair<T> createFieldMetaPair(final TableMeta<T> tableMeta) {

        final Class<T> domainClass = tableMeta.javaType();
        // 1. create columnNameSet
        final Table table = domainClass.getAnnotation(Table.class);
        final Index[] indices = table.indexes();
        final Set<String> indexFieldNameSet = new HashSet<>();

        for (Index index : indices) {
            for (String fieldName : index.fieldList()) {
                if (fieldName.length() < 1) {
                    String m = String.format("%s index[%s] must not empty.", table, index.name());
                    throw new MetaException(m);
                }
                if (Character.isWhitespace(fieldName.charAt(0))) {
                    String m = String.format("%s index[%s] couldn't start with white space.", table, index.name());
                    throw new MetaException(m);
                }
                if (fieldName.indexOf(_Constant.SPACE) < 0) {
                    indexFieldNameSet.add(fieldName);
                } else {
                    final StringTokenizer tokenizer;
                    tokenizer = new StringTokenizer(fieldName.trim(), " ", false);
                    indexFieldNameSet.add(tokenizer.nextToken());
                }
            }
        }
        // 2. create mapped super class pair for domain class
        final DomainPair pair;
        pair = mappedClassPair(domainClass);
        final List<Class<?>> mappedClassList = pair.mappedList;
        final Class<?> parentClass = pair.parent;

        // 3. get field name set and parent id field, if need
        final Set<String> fieldNameSet = new HashSet<>(); // for check field override.
        final Field parentIdField;
        if (parentClass == null) {
            parentIdField = null;
        } else {
            final Pair<Set<String>, Field> parentFieldPair;
            parentFieldPair = parentFieldPair(parentClass);
            fieldNameSet.addAll(parentFieldPair.getFirst());
            parentIdField = parentFieldPair.getSecond();
        }
        // 4. create non-index filed meta and get index filed map
        final Map<String, Field> indexFieldToFieldMap = new HashMap<>();
        final Set<String> columnNameSet = new HashSet<>(); // for check column name duplication
        final Map<String, FieldMeta<T>> fieldMetaMap = new HashMap<>();
        for (Class<?> mappedClass : mappedClassList) {
            for (Field field : mappedClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                final Column column = field.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                final String fieldName = field.getName();
                if (fieldNameSet.contains(fieldName)) { //check field override.
                    throw fieldOverride(mappedClass, field);
                }
                fieldNameSet.add(fieldName);

                final String columnName = columnName(column, field);
                if (_MetaBridge.ID.equals(fieldName) || indexFieldNameSet.contains(fieldName)) {
                    if (indexFieldToFieldMap.putIfAbsent(fieldName, field) != null) {
                        throw columnNameDuplication(mappedClass, columnName);
                    }
                } else if (columnNameSet.contains(columnName)) {// check column name duplication
                    throw columnNameDuplication(mappedClass, columnName);
                } else {
                    columnNameSet.add(columnName);
                    fieldMetaMap.put(fieldName, DefaultFieldMeta.createFieldMeta(tableMeta, field));
                }

            }
        }
        if (parentIdField != null
                && indexFieldToFieldMap.putIfAbsent(parentIdField.getName(), parentIdField) != null) {
            throw fieldOverride(domainClass, parentIdField);
        }

        // 5. create index meta and  index filed meta(s)
        final List<IndexMeta<T>> indexMetaList = new ArrayList<>(indices.length);
        boolean createdPrimaryIndex = false;
        for (Index index : indices) {
            final IndexMeta<T> indexMeta;
            indexMeta = new DefaultIndexMeta<>(tableMeta, index, indexFieldToFieldMap, columnNameSet);
            indexMetaList.add(indexMeta);
            if (indexMeta.isPrimaryKey()) {
                createdPrimaryIndex = true;
            }
            for (IndexFieldMeta<T> fieldMeta : indexMeta.fieldList()) {
                if (fieldMetaMap.putIfAbsent(fieldMeta.fieldName(), fieldMeta) != null) {
                    throw fieldMetaDuplication(fieldMeta);
                }
            }
        }
        if (!createdPrimaryIndex) {
            final IndexMeta<T> indexMeta;
            indexMeta = new DefaultIndexMeta<>(tableMeta, null, indexFieldToFieldMap, columnNameSet);
            final IndexFieldMeta<T> fieldMeta = indexMeta.fieldList().get(0);
            if (fieldMetaMap.putIfAbsent(fieldMeta.fieldName(), fieldMeta) != null) {
                throw fieldMetaDuplication(fieldMeta);
            }
            indexMetaList.add(indexMeta);
        }
        return new FieldMetaPair<>(indexMetaList, fieldMetaMap);
    }


    static String columnName(final Column column, final Field field) throws MetaException {
        final String customColumnName = _StringUtils.toLowerCase(column.name()), fieldName = field.getName();
        final String columnName;
        if (customColumnName.isEmpty()) {
            columnName = _MetaBridge.camelToLowerCase(fieldName);
        } else if (_MetaBridge.RESERVED_FIELDS.contains(fieldName)) {
            columnName = _MetaBridge.camelToLowerCase(fieldName);
            if (_StringUtils.hasText(customColumnName) && !customColumnName.equals(columnName)) {
                String m = String.format("Mapped class[%s] reserved filed[%s] column name must use default value.",
                        field.getDeclaringClass().getName(), fieldName);
                throw new MetaException(m);
            }
        } else if (!customColumnName.trim().equals(customColumnName)) {
            String m = String.format("Mapped class [%s] required prop[%s] column name contain space.",
                    field.getDeclaringClass().getName(),
                    fieldName);
            throw new MetaException(m);
        } else {
            columnName = customColumnName;
        }
        return columnName;
    }

    /*################################ private method ####################################*/


    /**
     * @param parentDomainClass domain class annotated by {@link Inheritance}.
     * @return first: field name set; second: id field.
     * @see #createFieldMetaPair(TableMeta)
     */
    private static Pair<Set<String>, Field> parentFieldPair(final Class<?> parentDomainClass) throws MetaException {
        Map<Class<?>, Pair<Set<String>, Field>> parentFieldPairCache = TableMetaUtils.parentFieldPairCache;

        final Pair<Set<String>, Field> cache;
        if (parentFieldPairCache == null) {
            cache = null;
            parentFieldPairCache = createParentFieldPairCache();
        } else {
            cache = parentFieldPairCache.get(parentDomainClass);
        }
        if (cache != null) {
            return cache;
        }
        final DomainPair pair;
        pair = mappedClassPair(parentDomainClass);
        if (pair.parent != null) {
            throw new IllegalStateException("mappedClassPair(Class) method error");
        }

        final Set<String> fieldNameSet = new HashSet<>();
        Field idField = null;
        for (Class<?> mappedClass : pair.mappedList) {
            for (Field field : mappedClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.getAnnotation(Column.class) == null) {
                    continue;
                }
                final String fieldName = field.getName();
                if (_MetaBridge.ID.equals(fieldName)) {
                    if (idField != null) {
                        throw fieldOverride(mappedClass, field);
                    }
                    idField = field;
                }
                fieldNameSet.add(fieldName);
            }
        }
        if (idField == null) {
            throw missingProperties(parentDomainClass, Collections.singleton(_MetaBridge.ID));
        }
        final Pair<Set<String>, Field> fieldPair;
        fieldPair = new Pair<>(Collections.unmodifiableSet(fieldNameSet), idField);
        parentFieldPairCache.putIfAbsent(parentDomainClass, fieldPair);
        return fieldPair;
    }


    private static Stack<Class<?>> superMappingClassStack(Class<?> topMappedClass) throws MetaException {
        Stack<Class<?>> stack = new Stack<>();

        for (Class<?> superClass = topMappedClass; superClass != null;
             superClass = superClass.getSuperclass()) {
            if (superClass.getAnnotation(MappedSuperclass.class) != null) {
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
            for (Field field : mappingClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                final Column column = field.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                // make column name lower case
                final String columnName = columnName(column, field);
                if (map.containsKey(columnName)) {
                    String m = String.format("Mapped class[%s] column[%s] definition duplication"
                            , mappingClass.getName()
                            , columnName);
                    throw new MetaException(m);
                }
                final String fieldName = field.getName();
                if (fieldNameSet.contains(fieldName)) {
                    String m = String.format("Mapped[%s] mapping property[%s] duplication"
                            , mappingClass.getName()
                            , fieldName);
                    throw new MetaException(m);
                }
                map.put(columnName, field);
                fieldNameSet.add(fieldName);
            }

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
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                String m = String.format("tableMeta[%s] not found primary column definition", tableMeta.tableName());
                throw new MetaException(m);
            }
            return field;
        }
        String m = String.format("domain[%s] not found primary key column definition", tableMeta.javaType());
        throw new MetaException(m);
    }


    static MetaException createNonAnnotationException(Class<?> entityClass
            , Class<?> annotationClass) {
        String m = String.format("class[%s] isn't annotated by %s "
                , entityClass.getName()
                , annotationClass.getName());
        return new MetaException(m);
    }

    static <T> List<FieldMeta<?>> createGeneratorChain(final Map<String, FieldMeta<T>> nameToField)
            throws MetaException {

        final List<Pair<FieldMeta<T>, Integer>> levelList = new ArrayList<>(4);
        for (FieldMeta<T> field : nameToField.values()) {

            GeneratorMeta generatorMeta = field.generator();
            if (generatorMeta == null) {
                continue;
            }
            String dependName;
            dependName = generatorMeta.params().get(FieldGenerator.DEPEND_FIELD_NAME);
            int level = 0;
            for (FieldMeta<?> dependField; dependName != null; ) {
                dependField = nameToField.get(dependName);
                if (dependField == null) {
                    String m = String.format("%s depend %s,but not found dependent field[%s] in %s"
                            , field, dependName, dependName, field.tableMeta());
                    throw new MetaException(m);
                }
                level++;
                generatorMeta = dependField.generator();
                if (generatorMeta == null) {
                    break;
                }
                dependName = generatorMeta.params().get(FieldGenerator.DEPEND_FIELD_NAME);
            }
            levelList.add(new Pair<>(field, level));
        }

        final List<FieldMeta<?>> generatorChain;
        switch (levelList.size()) {
            case 0:
                generatorChain = Collections.emptyList();
                break;
            case 1:
                generatorChain = Collections.singletonList(levelList.get(0).getFirst());
                break;
            default: {
                levelList.sort(Comparator.comparingInt(Pair::getSecond));
                final List<FieldMeta<T>> list = new ArrayList<>(levelList.size());
                for (Pair<FieldMeta<T>, Integer> f : levelList) {
                    list.add(f.getFirst());
                }
                generatorChain = Collections.unmodifiableList(list);
            }
        }
        return generatorChain;
    }




    /*################################# indexMap meta part private method  start ###################################*/


    /**
     * debugSQL {@link Index}'s {@link IndexFieldMeta}
     *
     * @param createdColumnSet created column set  in  other indexMap
     * @param <T>              entity java class
     * @return value indexMap's {@link IndexFieldMeta}
     */
    private static <T> List<IndexFieldMeta<T>> createIndexFieldMetaList(
            final String[] indexColumns,
            final IndexMeta<T> indexMeta,
            final Map<String, Field> nameToFieldMap,
            final Set<String> createdColumnSet) {

        final TableMeta<T> tableMeta = indexMeta.table();
        List<IndexFieldMeta<T>> list = new ArrayList<>(indexColumns.length);

        StringTokenizer tokenizer;
        IndexFieldMeta<T> indexFieldMeta;
        Boolean columnAsc;
        for (String indexColumnDefinition : indexColumns) {
            tokenizer = new StringTokenizer(indexColumnDefinition.trim(), " ", false);
            final int tokenCount = tokenizer.countTokens();
            final String fieldName = tokenizer.nextToken();
            if (createdColumnSet.contains(fieldName)) {
                throw indexColumnCreatedError(tableMeta, fieldName);
            }
            if (tokenCount == 1) {
                columnAsc = null;
            } else if (tokenCount == 2) {
                columnAsc = isAscIndexColumn(tokenizer.nextToken(), indexMeta, indexColumnDefinition);
            } else {
                throw indexColumnDefinitionError(indexMeta, indexColumnDefinition);
            }

            if (PRIMARY_FIELD.equals(fieldName) && (!indexMeta.unique() || indexColumns.length != 1)) {
                throw indexColumnDefinitionError(indexMeta, indexColumnDefinition);
            }
            final Field field = nameToFieldMap.get(fieldName);
            if (field == null) {
                throw notFoundIndexColumn(indexMeta, fieldName);
            }
            indexFieldMeta = DefaultFieldMeta.createIndexFieldMeta(tableMeta, field, indexMeta, indexColumns.length
                    , columnAsc);

            list.add(indexFieldMeta);
            createdColumnSet.add(fieldName);
        }

        if (list.size() == 1) {
            list = Collections.singletonList(list.get(0));
        } else {
            list = Collections.unmodifiableList(list);
        }
        return list;
    }


    /**
     * @see #createIndexFieldMetaList(String[], IndexMeta, Map, Set)
     */
    private static boolean isAscIndexColumn(final String order, IndexMeta<?> indexMeta, String indexColumnDefinition) {
        final boolean asc;
        if (ASC.equalsIgnoreCase(order)) {
            asc = true;
        } else if (DESC.equalsIgnoreCase(order)) {
            asc = false;
        } else {
            throw indexColumnDefinitionError(indexMeta, indexColumnDefinition);
        }
        return asc;
    }


    /**
     * @see #mappedClassPair(Class)
     */
    private static MetaException inheritanceDuplication(Class<?> domainClass) {
        String m = String.format("Domain[%s] extends link %s count great than 1 in link of extends",
                domainClass.getName(),
                Inheritance.class.getName());
        return new MetaException(m);
    }

    /**
     * @see #createFieldMetaPair(TableMeta)
     */
    private static MetaException columnNameDuplication(Class<?> mappedClass, String columnName) {
        String m = String.format("Mapped class[%s] column name[%s] duplication.", mappedClass.getName(), columnName);
        return new MetaException(m);
    }

    /**
     * @see #parentFieldPair(Class)
     */
    private static MetaException fieldOverride(Class<?> mappedClass, Field field) {
        String m = String.format("Mapped class[%s] property[%s] override.", mappedClass.getName(), field.getName());
        return new MetaException(m);
    }

    /**
     * @see #parentFieldPair(Class)
     */
    private static MetaException missingProperties(Class<?> domainClass, Set<String> missingProps) {
        String m = String.format("Domain class[%s] missing properties %s.", domainClass.getName(), missingProps);
        return new MetaException(m);
    }

    /**
     * @see #createIndexFieldMetaList(String[], IndexMeta, Map, Set)
     */
    private static MetaException indexColumnCreatedError(TableMeta<?> tableMeta, String columnName) {
        String m = String.format("Domain[%s] index column[%s] duplication in index."
                , tableMeta.javaType().getName(), columnName);
        return new MetaException(m);
    }

    /**
     * @see #createIndexFieldMetaList(String[], IndexMeta, Map, Set)
     * @see #isAscIndexColumn(String, IndexMeta, String)
     */
    private static MetaException indexColumnDefinitionError(IndexMeta<?> indexMeta, String indexColumnDefinition) {
        String m = String.format("Domain[%s] index[%s] column definition[%s] error",
                indexMeta.table().javaType().getName(), indexMeta.name(), indexColumnDefinition);
        throw new MetaException(m);
    }

    /**
     * @see #createIndexFieldMetaList(String[], IndexMeta, Map, Set)
     */
    private static MetaException notFoundIndexColumn(IndexMeta<?> indexMeta, String columnName) {
        String m = String.format("Not found index column[%s] in Domain[%s] for index[%s]"
                , columnName, indexMeta.table().javaType().getName(), indexMeta.name());
        throw new MetaException(m);
    }

    /**
     * @see #createFieldMetaPair(TableMeta)
     */
    private static IllegalStateException fieldMetaDuplication(IndexFieldMeta<?> fieldMeta) {
        String m = String.format("Domain[%s] filed meta[%s] duplication.",
                fieldMeta.tableMeta().javaType().getName(), fieldMeta.fieldName());
        throw new MetaException(m);
    }


    /**
     * @param <T> domain java type
     * @see #createFieldMetaPair(TableMeta)
     */
    private static final class DefaultIndexMeta<T> implements IndexMeta<T> {

        private final TableMeta<T> table;

        private final String name;

        private final boolean unique;

        private final String type;

        private final List<IndexFieldMeta<T>> fieldList;

        private final boolean primaryKey;

        /**
         * @param index            indexMap or null ( when debugSQL primary key for which user don'field definite {@link Index})
         * @param columnToFieldMap a unmodifiable map
         */
        private DefaultIndexMeta(final TableMeta<T> table, @Nullable Index index, Map<String, Field> columnToFieldMap,
                                 Set<String> createdColumnSet) {
            this.table = table;

            if (index == null) {
                this.name = "";
                this.unique = true;
                this.type = "";
                primaryKey = true;
                final Field field = Objects.requireNonNull(columnToFieldMap.get(_MetaBridge.ID));
                final IndexFieldMeta<T> idFieldMeta;
                idFieldMeta = DefaultFieldMeta.createIndexFieldMeta(table, field, this, 1, null);
                this.fieldList = Collections.singletonList(idFieldMeta);
            } else {
                this.name = index.name();
                final String[] columnArray = index.fieldList();
                this.primaryKey = columnArray.length == 1 && _MetaBridge.ID.equals(columnArray[0].split(" ")[0]);
                this.unique = this.primaryKey || index.unique();
                this.type = index.type();
                this.fieldList = createIndexFieldMetaList(columnArray, this, columnToFieldMap, createdColumnSet);
            }

        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Index[")
                    .append(this.name)
                    .append("] on Domain[")
                    .append(this.table.javaType().getName())
                    .append("](");
            int index = 0;
            for (IndexFieldMeta<T> fieldMeta : fieldList) {
                if (index > 0) {
                    builder.append(',');
                }
                builder.append(fieldMeta.fieldName());
                index++;
            }
            return builder
                    .append(')')
                    .toString();
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
        public List<IndexFieldMeta<T>> fieldList() {
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

    static final class DomainPair {

        final List<Class<?>> mappedList;

        final Class<?> parent;

        private DomainPair(List<Class<?>> mappedList, @Nullable Class<?> parent) {
            this.mappedList = mappedList;
            this.parent = parent;
        }


    }

    static final class FieldMetaPair<T> {

        final List<IndexMeta<T>> indexMetaList;

        final Map<String, FieldMeta<T>> fieldMap;

        private FieldMetaPair(List<IndexMeta<T>> indexMetaList, Map<String, FieldMeta<T>> fieldMap) {
            if (indexMetaList.size() == 1) {
                this.indexMetaList = Collections.singletonList(indexMetaList.get(0));
            } else {
                this.indexMetaList = Collections.unmodifiableList(indexMetaList);
            }
            this.fieldMap = Collections.unmodifiableMap(fieldMap);
        }

    }


}
