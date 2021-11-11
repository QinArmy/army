package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.criteria.SQLContext;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.sharding.Route;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.Pair;
import io.army.util.StringUtils;

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
            tableMeta = createChildTableMeta(domainClass);
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
            return new DefaultParentTable<>(domainClass);
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
            final Pair<List<Class<?>>, Class<?>> mappedClassPair;
            mappedClassPair = TableMetaUtils.mappedClassPair(domainClass);
            final Class<?> parentClass = mappedClassPair.getSecond();
            if (parentClass == null) {
                String m = String.format("Not found parent domain for domain[%s].", domainClass.getName());
                throw new IllegalArgumentException(m);
            }
            return new DefaultChildTable<>(getParentTableMeta((Class<S>) parentClass), domainClass);
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
            return new DefaultSimpleTable<>(domainClass);
        }

    }


    @Nullable
    private static <T extends IDomain> FieldMeta<? super T, ?> findDiscriminator(Class<T> javaType
            , Collection<FieldMeta<T, ?>> fieldMetas) throws MetaException {
        final Inheritance inheritance = javaType.getAnnotation(Inheritance.class);
        if (inheritance == null) {
            return null;
        }
        FieldMeta<? super T, ?> discriminator = null;
        final String discriminatorName = StringUtils.toLowerCase(inheritance.value());
        for (FieldMeta<T, ?> fieldMeta : fieldMetas) {
            if (discriminatorName.equals(fieldMeta.columnName())) {
                discriminator = fieldMeta;
                break;
            }
        }
        if (discriminator == null) {
            String m = String.format("Not found discriminator[%s] in domain[%s]."
                    , discriminatorName, javaType.getName());
            throw new MetaException(m);
        }
        return discriminator;
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

    private final MappingMode mappingMode;

    private final String charset;

    private final SchemaMeta schemaMeta;

    private final int discriminatorValue;

    private final Map<String, FieldMeta<T, ?>> propNameToFieldMeta;

    private final List<IndexMeta<T>> indexMetaList;

    private final PrimaryFieldMeta<T, Object> primaryField;

    final ParentTableMeta<? super T> parentTableMeta;

    final FieldMeta<? super T, ?> discriminator;

    private final boolean sharding;

    private final List<FieldMeta<?, ?>> databaseRouteFieldList;

    private final List<FieldMeta<?, ?>> tableRouteFieldList;

    private final Class<? extends Route> routeClass;

    @SuppressWarnings("unchecked")
    private DefaultTableMeta(@Nullable final ParentTableMeta<? super T> parentTableMeta, final Class<T> domainClass) {
        Objects.requireNonNull(domainClass, "javaType required");
        if (!IDomain.class.isAssignableFrom(domainClass)) {
            String m = String.format("Class[%s] not implements %s .", domainClass.getName(), IDomain.class.getName());
            throw new IllegalArgumentException(m);
        }
        if (parentTableMeta != null) {
            TableMetaUtils.assertParentTableMeta(parentTableMeta, domainClass);
        }
        this.javaType = domainClass;
        this.parentTableMeta = parentTableMeta;
        try {

            final Table table = TableMetaUtils.tableMeta(domainClass);

            this.tableName = TableMetaUtils.tableName(table, domainClass);
            this.comment = TableMetaUtils.tableComment(table, domainClass);
            this.immutable = table.immutable();
            this.schemaMeta = TableMetaUtils.schemaMeta(table);

            this.mappingMode = this.decideMappingMode();
            this.charset = table.charset();

            final TableMetaUtils.FieldMetaPair<T> pair;
            pair = TableMetaUtils.createFieldMetaPair(this);
            this.propNameToFieldMeta = pair.fieldMetaMap;
            this.indexMetaList = pair.indexMetaList;

            if (parentTableMeta == null) {
                this.discriminator = findDiscriminator(domainClass, this.propNameToFieldMeta.values());
            } else {
                this.discriminator = parentTableMeta.discriminator();
            }

            TableMetaUtils.RouteMeta routeMeta = TableMetaUtils.routeMeta(
                    this, this.propNameToFieldMeta);
            this.databaseRouteFieldList = routeMeta.databaseRouteFieldList;
            this.tableRouteFieldList = routeMeta.tableRouteFieldList;
            this.sharding = !this.tableRouteFieldList.isEmpty();
            this.routeClass = routeMeta.routeClass;
            this.discriminatorValue = TableMetaUtils.discriminatorValue(this.mappingMode, this);

            this.primaryField = (PrimaryFieldMeta<T, Object>) this.propNameToFieldMeta.get(_MetaBridge.ID);
            if (this.primaryField == null) {
                String m = String.format("Not found primary field meta in domain[%s]", domainClass.getName());
                throw new NullPointerException(m);
            }
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(e, e.getMessage());
        }

        if (INSTANCE_MAP.putIfAbsent(this.javaType, this) != null) {
            String m = String.format("%s[%s] duplication.", TableMeta.class.getSimpleName(), this.javaType.getName());
            throw new IllegalStateException(m);
        }

    }


    private MappingMode decideMappingMode() {
        final MappingMode mappingMode;
        if (this instanceof SimpleTableMeta) {
            mappingMode = MappingMode.SIMPLE;
        } else if (this instanceof ChildTableMeta) {
            mappingMode = MappingMode.CHILD;
        } else if (this instanceof ParentTableMeta) {
            mappingMode = MappingMode.PARENT;
        } else {
            throw new IllegalStateException("Unknown sub class.");
        }
        return mappingMode;
    }


    @Override
    public Class<T> javaType() {
        return this.javaType;
    }

    @Override
    public String tableName() {
        return this.tableName;
    }

    @Override
    public boolean immutable() {
        return this.immutable;
    }


    @Override
    public String comment() {
        return this.comment;
    }

    @Override
    public PrimaryFieldMeta<T, Object> id() {
        return this.primaryField;
    }


    @Override
    public MappingMode mappingMode() {
        return this.mappingMode;
    }

    @Override
    public int discriminatorValue() {
        return discriminatorValue;
    }


    @Override
    public List<IndexMeta<T>> indexCollection() {
        return this.indexMetaList;
    }

    @Override
    public Collection<FieldMeta<T, ?>> fieldCollection() {
        return this.propNameToFieldMeta.values();
    }


    @Override
    public String charset() {
        return this.charset;
    }


    @Override
    public SchemaMeta schema() {
        return this.schemaMeta;
    }

    @Override
    public boolean mappingField(String propName) {
        return this.propNameToFieldMeta.containsKey(propName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator() {
        return (FieldMeta<T, E>) this.discriminator;
    }

    @Nullable
    @Override
    public ParentTableMeta<? super T> parentMeta() {
        return this.parentTableMeta;
    }


    @SuppressWarnings("unchecked")
    @Override
    public FieldMeta<T, Object> getField(String propName) throws MetaException {
        FieldMeta<?, ?> fieldMeta = propNameToFieldMeta.get(propName);
        if (fieldMeta == null) {
            throw new MetaException("TableMeta[%s]'s FieldMeta[%s] not found", this, propName);
        }
        return (FieldMeta<T, Object>) fieldMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) {
        Assert.notNull(propName, "propName required");
        Assert.notNull(propName, "propClass required");

        FieldMeta<T, ?> fieldMeta = propNameToFieldMeta.get(propName);
        if (fieldMeta == null || propClass != fieldMeta.javaType()) {
            throw new MetaException("TableMeta[%s]'s FieldMeta[%s] not found", this, propName);
        }
        return (FieldMeta<T, F>) fieldMeta;
    }

    @Override
    public <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MetaException {
        Assert.notNull(propName, "propName required");
        Assert.notNull(propName, "propClass required");

        FieldMeta<T, F> fieldMeta = getField(propName, propClass);
        if (!(fieldMeta instanceof IndexFieldMeta) || propClass != fieldMeta.javaType()) {
            throw new MetaException("TableMeta[%s]'s FieldMeta[%s] not found", this, propName);
        }
        return (IndexFieldMeta<T, F>) fieldMeta;
    }

    @Override
    public <F> UniqueFieldMeta<T, F> getUniqueField(String propName, Class<F> propClass) throws MetaException {
        FieldMeta<T, F> fieldMeta = getField(propName, propClass);
        if (!(fieldMeta instanceof UniqueFieldMeta)) {
            throw new MetaException("TableMeta[%s]'s UniqueFieldMeta[%s] not found", this, propName);
        }
        return (UniqueFieldMeta<T, F>) fieldMeta;
    }

    @Override
    public <F> PrimaryFieldMeta<T, F> id(Class<F> propClass) throws MetaException {
        FieldMeta<T, F> fieldMeta = getField(_MetaBridge.ID, propClass);
        if (!(fieldMeta instanceof PrimaryFieldMeta)) {
            throw new MetaException("TableMeta[%s]'s PrimaryFieldMeta not found", this);
        }
        return (PrimaryFieldMeta<T, F>) fieldMeta;
    }

    @Override
    public final boolean sharding() {
        return this.sharding;
    }

    @Override
    public final List<FieldMeta<?, ?>> routeFieldList(boolean database) {
        return database ? this.databaseRouteFieldList : this.tableRouteFieldList;
    }

    @Nullable
    @Override
    public final Class<? extends Route> routeClass() {
        return this.routeClass;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        throw new UnsupportedOperationException(
                "please use io.army.dialect.TableContextSQLContext.appendTable(TableMeta<?>,@Nullable  String)");
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

        private DefaultSimpleTable(Class<T> domainClass) {
            super(null, domainClass);
        }

    }

    private static final class DefaultParentTable<T extends IDomain> extends DefaultTableMeta<T>
            implements ParentTableMeta<T> {

        private DefaultParentTable(Class<T> domainClass) {
            super(null, domainClass);
        }

        @NonNull
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator() {
            final FieldMeta<T, E> fieldMeta;
            fieldMeta = (FieldMeta<T, E>) this.discriminator;
            if (fieldMeta == null) {
                throw new NullPointerException("discriminator() is null");
            }
            return fieldMeta;
        }

    }

    private static final class DefaultChildTable<T extends IDomain> extends DefaultTableMeta<T>
            implements ChildTableMeta<T> {

        private DefaultChildTable(ParentTableMeta<? super T> parentTableMeta, Class<T> entityClass) {
            super(parentTableMeta, entityClass);
            Objects.requireNonNull(parentTableMeta);
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator() {
            final FieldMeta<T, E> fieldMeta;
            fieldMeta = (FieldMeta<T, E>) this.discriminator;
            if (fieldMeta == null) {
                throw new NullPointerException("discriminator() is null");
            }
            return fieldMeta;
        }

        @NonNull
        @Override
        public ParentTableMeta<? super T> parentMeta() {
            final ParentTableMeta<? super T> meta;
            meta = this.parentTableMeta;
            if (meta == null) {
                throw new NullPointerException("parentMeta() is null");
            }
            return meta;
        }


    }


}
