package io.army.criteria.impl;

import io.army.ArmyException;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
abstract class DefaultTableMeta<T extends IDomain> implements TableMeta<T> {

    private static final ConcurrentMap<Class<?>, DefaultTableMeta<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends IDomain> TableMeta<T> getTableMeta(final Class<T> domainClass) {
        final TableMeta<T> cache = (TableMeta<T>) INSTANCE_MAP.get(domainClass);
        final TableMeta<T> tableMeta;
        if (cache != null) {
            if (cache.javaType() != domainClass) {
                throw instanceMapError();
            }
            tableMeta = cache;
        } else if (domainClass.getAnnotation(Table.class) == null) {
            throw mappingError(TableMeta.class, domainClass);
        } else if (domainClass.getAnnotation(Inheritance.class) != null) {
            tableMeta = createParentTableMeta(domainClass);
        } else if (domainClass.getAnnotation(DiscriminatorValue.class) != null) {
            final ChildTableMeta<T> child;
            child = createChildTableMeta(domainClass);
            // cache
            TableMetaUtils.discriminatorValue(child.parentMeta(), domainClass);
            tableMeta = child;
        } else {
            tableMeta = createSimpleTableMeta(domainClass);
        }
        return tableMeta;
    }

    static <T extends IDomain> SimpleTableMeta<T> getSimpleTableMeta(final Class<T> domainClass) {
        SimpleTableMeta<T> simple;
        simple = getSimpleFromCache(domainClass);
        if (simple == null) {
            simple = createSimpleTableMeta(domainClass);
        }
        return simple;
    }


    static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(final Class<T> domainClass) {
        ParentTableMeta<T> parent;
        parent = getParentFromCache(domainClass);
        if (parent == null) {
            parent = createParentTableMeta(domainClass);
        }
        return parent;
    }

    static <S extends IDomain, T extends S> ChildTableMeta<T> getChildTableMeta(final ParentTableMeta<S> parent
            , final Class<T> domainClass) {
        if (!(parent instanceof DefaultParentTable) || !parent.javaType().isAssignableFrom(domainClass)) {
            throw new IllegalArgumentException("parentTableMeta error");
        }
        ChildTableMeta<T> child;
        child = getChildFromCache(domainClass);
        if (child == null) {
            child = createChildTableMeta(domainClass);
            if (child.parentMeta() != parent) {
                throw new IllegalArgumentException("parentTableMeta error");
            }
        }
        return child;
    }


    @SuppressWarnings("unchecked")
    @Nullable
    private static <T extends IDomain> ChildTableMeta<T> getChildFromCache(final Class<T> domainClass) {
        final TableMeta<?> tableMeta = INSTANCE_MAP.get(domainClass);
        final ChildTableMeta<T> child;
        if (tableMeta == null) {
            child = null;
        } else if (tableMeta.javaType() != domainClass) {
            throw instanceMapError();
        } else if (tableMeta instanceof ChildTableMeta) {
            child = (ChildTableMeta<T>) tableMeta;
        } else {
            throw mappingError(ChildTableMeta.class, domainClass);
        }
        return child;
    }


    @SuppressWarnings("unchecked")
    @Nullable
    private static <T extends IDomain> ParentTableMeta<T> getParentFromCache(final Class<T> domainClass) {
        final TableMeta<?> tableMeta = INSTANCE_MAP.get(domainClass);
        final ParentTableMeta<T> parent;
        if (tableMeta == null) {
            parent = null;
        } else if (tableMeta.javaType() != domainClass) {
            throw instanceMapError();
        } else if (tableMeta instanceof ParentTableMeta) {
            parent = (ParentTableMeta<T>) tableMeta;
        } else {
            throw mappingError(ParentTableMeta.class, domainClass);
        }
        return parent;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T extends IDomain> SimpleTableMeta<T> getSimpleFromCache(final Class<T> domainClass) {
        final TableMeta<?> tableMeta = INSTANCE_MAP.get(domainClass);
        final SimpleTableMeta<T> simple;
        if (tableMeta == null) {
            simple = null;
        } else if (tableMeta.javaType() != domainClass) {
            throw instanceMapError();
        } else if (tableMeta instanceof SimpleTableMeta) {
            simple = (SimpleTableMeta<T>) tableMeta;
        } else {
            throw mappingError(SimpleTableMeta.class, domainClass);
        }
        return simple;
    }

    private static <T extends IDomain> ParentTableMeta<T> createParentTableMeta(final Class<T> domainClass) {
        synchronized (DefaultParentTable.class) {
            final ParentTableMeta<T> parent;
            parent = getParentFromCache(domainClass);
            if (parent != null) {
                return parent;
            }
            if (domainClass.getAnnotation(Table.class) == null
                    || domainClass.getAnnotation(Inheritance.class) == null) {
                String m = String.format("Class[%s] isn't parent domain.", domainClass.getName());
                throw new IllegalArgumentException(m);
            }
            final DefaultParentTable<T> parentTable;
            parentTable = new DefaultParentTable<>(domainClass);
            if (INSTANCE_MAP.putIfAbsent(domainClass, parentTable) != null) {
                String m = String.format("Domain[%s] duplication.", domainClass);
                throw new MetaException(m);
            }
            return parentTable;
        }
    }

    @SuppressWarnings("unchecked")
    private static <S extends IDomain, T extends S> ChildTableMeta<T> createChildTableMeta(final Class<T> domainClass) {
        synchronized (DefaultChildTable.class) {
            final ChildTableMeta<T> child;
            child = getChildFromCache(domainClass);
            if (child != null) {
                return child;
            }
            if (domainClass.getAnnotation(Table.class) == null
                    || domainClass.getAnnotation(DiscriminatorValue.class) == null) {
                String m = String.format("Class[%s] isn't child domain.", domainClass.getName());
                throw new IllegalArgumentException(m);
            }
            final TableMetaUtils.DomainPair pair;
            pair = TableMetaUtils.mappedClassPair(domainClass);
            final Class<?> parentClass = pair.parent;
            if (parentClass == null) {
                String m = String.format("Not found parent domain for domain[%s].", domainClass.getName());
                throw new IllegalArgumentException(m);
            }
            final DefaultChildTable<S, T> childTable;
            childTable = new DefaultChildTable<>(getParentTableMeta((Class<S>) parentClass), domainClass);
            if (INSTANCE_MAP.putIfAbsent(domainClass, childTable) != null) {
                String m = String.format("Domain[%s] duplication.", domainClass);
                throw new MetaException(m);
            }
            return childTable;
        }
    }


    private static <T extends IDomain> SimpleTableMeta<T> createSimpleTableMeta(final Class<T> domainClass) {
        synchronized (DefaultSimpleTable.class) {
            final SimpleTableMeta<T> simple;
            simple = getSimpleFromCache(domainClass);
            if (simple != null) {
                return simple;
            }
            if (domainClass.getAnnotation(Table.class) == null
                    || domainClass.getAnnotation(Inheritance.class) != null
                    || domainClass.getAnnotation(DiscriminatorValue.class) != null) {
                String m = String.format("Class[%s] isn't simple domain.", domainClass.getName());
                throw new IllegalArgumentException(m);
            }
            final DefaultSimpleTable<T> simpleTable;
            simpleTable = new DefaultSimpleTable<>(domainClass);
            if (INSTANCE_MAP.putIfAbsent(domainClass, simpleTable) != null) {
                String m = String.format("Domain[%s] duplication.", domainClass);
                throw new MetaException(m);
            }
            return simpleTable;
        }

    }


    private static IllegalStateException instanceMapError() {
        return new IllegalStateException("INSTANCE_MAP state error.");
    }

    private static IllegalArgumentException mappingError(Class<?> tableMetaClass, Class<?> domainClass) {
        String m = String.format("Domain class %s couldn't mapping to %s.", domainClass.getName()
                , tableMetaClass.getName());
        return new IllegalArgumentException(m);
    }

    private static <T extends IDomain> List<FieldMeta<T>> createFieldList(final Class<T> domainClass
            , final Map<String, FieldMeta<T>> fieldNameToField) {

        final List<FieldMeta<T>> fieldList = new ArrayList<>(fieldNameToField.size());

        for (String fieldName : _MetaBridge.RESERVED_PROPS) {
            final FieldMeta<T> reservedField;
            reservedField = fieldNameToField.get(fieldName);
            if (reservedField != null) {
                fieldList.add(reservedField);
            }
        }

        final Inheritance inheritance;
        inheritance = domainClass.getAnnotation(Inheritance.class);
        final FieldMeta<T> discriminatorField;
        if (inheritance != null) {
            discriminatorField = fieldNameToField.get(inheritance.value());
            if (discriminatorField == null) {
                throw TableMetaUtils.notFoundDiscriminator(inheritance.value(), domainClass);
            }
            fieldList.add(discriminatorField);
        } else {
            discriminatorField = null;
        }

        for (FieldMeta<T> field : fieldNameToField.values()) {
            if (field == discriminatorField
                    || _MetaBridge.RESERVED_PROPS.contains(field.fieldName())) {
                continue;
            }
            fieldList.add(field);
        }
        if (fieldList.size() != fieldNameToField.size()) {
            throw new IllegalStateException("field count not match.");
        }
        return Collections.unmodifiableList(fieldList);
    }


    final Class<T> javaType;

    private final String tableName;

    private final boolean immutable;

    private final String comment;

    private final String charset;

    private final SchemaMeta schemaMeta;

    final Map<String, FieldMeta<T>> fieldNameToFields;

    private final List<FieldMeta<T>> fieldList;

    private final List<IndexMeta<T>> indexMetaList;

    private final PrimaryFieldMeta<T> primaryField;


    private final List<FieldMeta<T>> generatorChain;

    private DefaultTableMeta(final Class<T> domainClass) {
        Objects.requireNonNull(domainClass, "javaType required");
        if (!IDomain.class.isAssignableFrom(domainClass)) {
            String m = String.format("Class[%s] not implements %s .", domainClass.getName(), IDomain.class.getName());
            throw new IllegalArgumentException(m);
        }
        this.javaType = domainClass;
        try {

            final Table table = TableMetaUtils.tableMeta(domainClass);

            this.tableName = TableMetaUtils.tableName(table, domainClass);
            this.comment = TableMetaUtils.tableComment(table, domainClass);
            this.immutable = TableMetaUtils.immutable(table, domainClass);
            this.schemaMeta = _SchemaMetaFactory.getSchema(table.catalog(), table.schema());

            this.charset = table.charset();

            final TableMetaUtils.FieldMetaPair<T> pair;
            pair = TableMetaUtils.createFieldMetaPair(this);

            this.fieldNameToFields = pair.fieldMap;
            this.indexMetaList = pair.indexMetaList;
            this.fieldList = createFieldList(domainClass, this.fieldNameToFields);
            this.generatorChain = TableMetaUtils.createGeneratorChain(this.fieldNameToFields);

            this.primaryField = (PrimaryFieldMeta<T>) this.fieldNameToFields.get(_MetaBridge.ID);
            if (this.primaryField == null) {
                String m = String.format("Not found primary field meta in domain[%s]", domainClass.getName());
                throw new NullPointerException(m);
            }
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(e.getMessage(), e);
        }

        if (INSTANCE_MAP.containsKey(domainClass)) {
            String m = String.format("Domain[%s] duplication.", domainClass);
            throw new MetaException(m);
        }

    }

    @Override
    public final Class<T> javaType() {
        return this.javaType;
    }

    @Override
    public final String tableName() {
        return this.tableName;
    }

    @Override
    public final boolean immutable() {
        return this.immutable;
    }


    @Override
    public final String comment() {
        return this.comment;
    }

    @Override
    public final PrimaryFieldMeta<T> id() {
        return this.primaryField;
    }


    @Override
    public final List<IndexMeta<T>> indexList() {
        return this.indexMetaList;
    }

    @Override
    public final List<FieldMeta<T>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final String charset() {
        return this.charset;
    }

    @Override
    public final SchemaMeta schema() {
        return this.schemaMeta;
    }

    @Override
    public final boolean containField(final String fieldName) {
        return this.fieldNameToFields.containsKey(fieldName);
    }


    @Override
    public final FieldMeta<T> getField(final String fieldName) throws IllegalArgumentException {
        final FieldMeta<T> fieldMeta;
        fieldMeta = this.fieldNameToFields.get(fieldName);
        if (fieldMeta == null) {
            String m = String.format("%s's %s[%s] not found", this, FieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return fieldMeta;
    }


    @Override
    public final IndexFieldMeta<T> getIndexField(final String fieldName) {
        final FieldMeta<T> fieldMeta;
        fieldMeta = getField(fieldName);
        if (!(fieldMeta instanceof IndexFieldMeta)) {
            String m = String.format("%s's %s[%s] java type not match", this
                    , IndexFieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return (IndexFieldMeta<T>) fieldMeta;
    }

    @Override
    public final UniqueFieldMeta<T> getUniqueField(final String fieldName) {
        final IndexFieldMeta<T> fieldMeta;
        fieldMeta = getIndexField(fieldName);
        if (!(fieldMeta instanceof UniqueFieldMeta)) {
            String m = String.format("%s's %s[%s] java type not match", this
                    , UniqueFieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return (UniqueFieldMeta<T>) fieldMeta;
    }


    @Override
    public final List<FieldMeta<T>> generatorChain() {
        return this.generatorChain;
    }


    @Override
    public final boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof DefaultTableMeta) {
            match = this.javaType == ((DefaultTableMeta<?>) obj).javaType;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final int hashCode() {
        return this.javaType.hashCode();
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this instanceof ChildTableMeta) {
            builder.append(ChildTableMeta.class.getSimpleName());
        } else if (this instanceof ParentTableMeta) {
            builder.append(ParentTableMeta.class.getSimpleName());
        } else {
            builder.append(SimpleTableMeta.class.getSimpleName());
        }
        return builder.append('[')
                .append(this.javaType.getName())
                .append(']').toString();
    }


    /*################################## blow static class ##################################*/

    private static final class DefaultSimpleTable<T extends IDomain> extends DefaultTableMeta<T>
            implements SimpleTableMeta<T> {

        private DefaultSimpleTable(final Class<T> domainClass) {
            super(domainClass);
        }

        @Override
        public FieldMeta<? super T> discriminator() {
            // always null
            return null;
        }

        @Override
        public int discriminatorValue() {
            // always 0
            return 0;
        }


    }

    private static final class DefaultParentTable<T extends IDomain> extends DefaultTableMeta<T>
            implements ParentTableMeta<T> {

        private final FieldMeta<T> discriminator;

        private DefaultParentTable(final Class<T> domainClass) {
            super(domainClass);
            this.discriminator = TableMetaUtils.discriminator(this.fieldNameToFields, domainClass);
        }

        @Override
        public FieldMeta<T> discriminator() {
            return this.discriminator;
        }

        @Override
        public int discriminatorValue() {
            // always 0
            return 0;
        }


    }

    private static final class DefaultChildTable<P extends IDomain, T extends P> extends DefaultTableMeta<T>
            implements ChildTableMeta<T> {

        private final ParentTableMeta<P> parentTableMeta;

        private final int discriminatorValue;

        private DefaultChildTable(final ParentTableMeta<P> parentTableMeta, final Class<T> domainClass) {
            super(domainClass);
            TableMetaUtils.assertParentTableMeta(parentTableMeta, domainClass);
            this.parentTableMeta = parentTableMeta;
            this.discriminatorValue = TableMetaUtils.discriminatorValue(parentTableMeta, domainClass);
        }

        @NonNull
        @Override
        public FieldMeta<? super T> discriminator() {
            return this.parentTableMeta.discriminator();
        }

        @Override
        public ParentTableMeta<? super T> parentMeta() {
            return this.parentTableMeta;
        }

        @Override
        public int discriminatorValue() {
            return this.discriminatorValue;
        }


    }


}
