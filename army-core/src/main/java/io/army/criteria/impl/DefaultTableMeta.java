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
import io.army.struct.CodeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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


    private final Class<T> javaType;

    private final String tableName;

    private final boolean immutable;

    private final String comment;

    private final String charset;

    private final SchemaMeta schemaMeta;

    final Map<String, FieldMeta<T, ?>> fieldToFieldMeta;

    private final List<IndexMeta<T>> indexMetaList;

    private final PrimaryFieldMeta<T, Object> primaryField;


    private final List<FieldMeta<T, ?>> generatorChain;

    @SuppressWarnings("unchecked")
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
            this.fieldToFieldMeta = pair.fieldMetaMap;
            this.indexMetaList = pair.indexMetaList;

            this.generatorChain = TableMetaUtils.createGeneratorChain(this.fieldToFieldMeta);

            this.primaryField = (PrimaryFieldMeta<T, Object>) this.fieldToFieldMeta.get(_MetaBridge.ID);
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
    public final PrimaryFieldMeta<T, Object> id() {
        return this.primaryField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <F> PrimaryFieldMeta<T, F> id(final Class<F> idClass) {
        final PrimaryFieldMeta<T, ?> idFieldMeta = this.primaryField;
        if (idClass != idFieldMeta.javaType()) {
            String m = String.format("%s's %s[%s] java type not match", this
                    , UniqueFieldMeta.class.getName(), idFieldMeta.fieldName());
            throw new IllegalArgumentException(m);
        }
        return (PrimaryFieldMeta<T, F>) idFieldMeta;
    }


    @Override
    public final List<IndexMeta<T>> indexCollection() {
        return this.indexMetaList;
    }

    @Override
    public final Collection<FieldMeta<T, ?>> fieldCollection() {
        return this.fieldToFieldMeta.values();
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
        return this.fieldToFieldMeta.containsKey(fieldName);
    }


    @SuppressWarnings("unchecked")
    @Override
    public final FieldMeta<T, Object> getField(final String fieldName) throws MetaException {
        final FieldMeta<T, ?> fieldMeta;
        fieldMeta = this.fieldToFieldMeta.get(fieldName);
        if (fieldMeta == null) {
            String m = String.format("%s's %s[%s] not found", this, FieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return (FieldMeta<T, Object>) fieldMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <F> FieldMeta<T, F> getField(final String fieldName, final Class<F> fieldClass) {
        final FieldMeta<T, ?> fieldMeta;
        fieldMeta = getField(fieldName);
        if (fieldClass != fieldMeta.javaType()) {
            String m = String.format("%s's %s[%s] java type not match", this, FieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return (FieldMeta<T, F>) fieldMeta;
    }

    @Override
    public final <F> IndexFieldMeta<T, F> getIndexField(final String fieldName, final Class<F> fieldClass) {
        final FieldMeta<T, F> fieldMeta;
        fieldMeta = getField(fieldName, fieldClass);
        if (!(fieldMeta instanceof IndexFieldMeta)) {
            String m = String.format("%s's %s[%s] java type not match", this
                    , IndexFieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return (IndexFieldMeta<T, F>) fieldMeta;
    }

    @Override
    public final <F> UniqueFieldMeta<T, F> getUniqueField(final String fieldName, final Class<F> fieldClass) {
        final IndexFieldMeta<T, F> fieldMeta;
        fieldMeta = getIndexField(fieldName, fieldClass);
        if (!(fieldMeta instanceof UniqueFieldMeta)) {
            String m = String.format("%s's %s[%s] java type not match", this
                    , UniqueFieldMeta.class.getName(), fieldName);
            throw new IllegalArgumentException(m);
        }
        return (UniqueFieldMeta<T, F>) fieldMeta;
    }


    @Override
    public final List<FieldMeta<T, ?>> generatorChain() {
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
        return Objects.hash(this.javaType);
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
        public <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator() {
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

        private final FieldMeta<T, ?> discriminator;

        private DefaultParentTable(final Class<T> domainClass) {
            super(domainClass);
            this.discriminator = TableMetaUtils.discriminator(this.fieldToFieldMeta, domainClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator() {
            return (FieldMeta<T, E>) this.discriminator;
        }

        @Override
        public int discriminatorValue() {
            // always 0
            return 0;
        }


    }

    private static final class DefaultChildTable<P extends IDomain, T extends P> extends DefaultTableMeta<T>
            implements ChildDomain<P, T> {

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
        public <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator() {
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
