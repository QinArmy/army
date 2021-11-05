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
import io.army.modelgen.MetaBridge;
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

    private static final ConcurrentMap<Class<?>, TableMeta<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends IDomain> TableMeta<T> getTableMeta(final Class<T> domainClass) {
        TableMeta<T> tableMeta = (TableMeta<T>) INSTANCE_MAP.get(domainClass);
        if (tableMeta != null) {
            if (tableMeta.javaType() != domainClass) {
                throw new IllegalStateException("INSTANCE_MAP state error");
            }
            return tableMeta;
        }
        if (domainClass.getAnnotation(Inheritance.class) != null) {
            tableMeta = getParentTableMeta(domainClass);
        } else if (domainClass.getAnnotation(DiscriminatorValue.class) != null) {
            tableMeta = getChildTableMeta(domainClass);
        } else {
            tableMeta = getSimpleTableMeta(domainClass);
        }
        return tableMeta;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain> SimpleTableMeta<T> getSimpleTableMeta(final Class<T> domainClass) {
        final TableMeta<?> tableMeta;
        tableMeta = INSTANCE_MAP.get(domainClass);
        final SimpleTableMeta<T> simpleTableMeta;
        if (tableMeta == null) {
            simpleTableMeta = createSimpleTableMeta(domainClass);
        } else if (tableMeta instanceof DefaultSimpleTable) {
            simpleTableMeta = (SimpleTableMeta<T>) tableMeta;
        } else {
            String m = String.format("Domain[%s] couldn't mapping to %s."
                    , domainClass.getName(), SimpleTableMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        return simpleTableMeta;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(final Class<T> domainClass) {
        final TableMeta<?> tableMeta;
        tableMeta = INSTANCE_MAP.get(domainClass);
        final ParentTableMeta<T> parentTableMeta;
        if (tableMeta == null) {
            parentTableMeta = createParentTableMeta(domainClass);
        } else if (tableMeta instanceof DefaultParentTable) {
            parentTableMeta = (ParentTableMeta<T>) tableMeta;
        } else {
            String m = String.format("Domain[%s] couldn't mapping to %s."
                    , domainClass.getName(), ParentTableMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        return parentTableMeta;
    }

    @SuppressWarnings("unchecked")
    static <S extends IDomain, T extends S> ChildTableMeta<T> getChildTableMeta(final ParentTableMeta<S> parentTableMeta
            , final Class<T> domainClass) {
        if (!(parentTableMeta instanceof DefaultParentTable)
                || !parentTableMeta.javaType().isAssignableFrom(domainClass)) {
            throw new IllegalArgumentException("parentTableMeta error");
        }
        final TableMeta<?> tableMeta;
        tableMeta = INSTANCE_MAP.get(domainClass);
        final ChildTableMeta<T> childTableMeta;
        if (tableMeta == null) {
            childTableMeta = doCreateChildTableMeta(parentTableMeta, domainClass);
        } else if (tableMeta instanceof DefaultChildTable) {
            childTableMeta = (ChildTableMeta<T>) tableMeta;
        } else {
            String m = String.format("Domain[%s] couldn't mapping to %s."
                    , domainClass.getName(), ChildTableMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        return childTableMeta;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain> ChildTableMeta<T> getChildTableMeta(final Class<T> domainClass) {
        final TableMeta<?> tableMeta;
        tableMeta = INSTANCE_MAP.get(domainClass);
        final ChildTableMeta<T> childTableMeta;
        if (tableMeta == null) {
            childTableMeta = createChildTableMeta(domainClass);
        } else if (tableMeta instanceof DefaultChildTable) {
            childTableMeta = (ChildTableMeta<T>) tableMeta;
        } else {
            String m = String.format("Domain[%s] couldn't mapping to %s."
                    , domainClass.getName(), ChildTableMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        return childTableMeta;
    }

    @SuppressWarnings("unchecked")
    private static <S extends IDomain, T extends S> ChildTableMeta<T> createChildTableMeta(final Class<T> domainClass) {
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
        return doCreateChildTableMeta(getParentTableMeta((Class<S>) parentClass), domainClass);
    }

    @SuppressWarnings("unchecked")
    private static <S extends IDomain, T extends S> ChildTableMeta<T> doCreateChildTableMeta(
            final ParentTableMeta<S> parentTableMeta, final Class<T> domainClass) {
        ChildTableMeta<T> childTableMeta;
        childTableMeta = new DefaultChildTable<>(parentTableMeta, domainClass);

        final TableMeta<?> oldTableMeta;
        oldTableMeta = INSTANCE_MAP.putIfAbsent(domainClass, childTableMeta);
        if (oldTableMeta != null) {
            if (!(oldTableMeta instanceof DefaultChildTable) || oldTableMeta.javaType() != domainClass) {
                throw new IllegalStateException("INSTANCE_MAP state error");
            }
            // return old value created by other thead.
            childTableMeta = (ChildTableMeta<T>) oldTableMeta;
        }
        return childTableMeta;
    }

    @SuppressWarnings("unchecked")
    private static <T extends IDomain> ParentTableMeta<T> createParentTableMeta(final Class<T> domainClass) {
        if (domainClass.getAnnotation(Table.class) == null
                || domainClass.getAnnotation(Inheritance.class) == null) {
            String m = String.format("Class[%s] isn't parent domain.", domainClass.getName());
            throw new IllegalArgumentException(m);
        }
        ParentTableMeta<T> parentTableMeta;
        parentTableMeta = new DefaultParentTable<>(domainClass);
        final TableMeta<?> oldTableMeta;
        oldTableMeta = INSTANCE_MAP.putIfAbsent(domainClass, parentTableMeta);
        if (oldTableMeta != null) {
            if (!(oldTableMeta instanceof DefaultParentTable) || oldTableMeta.javaType() != domainClass) {
                throw new IllegalStateException("INSTANCE_MAP state error");
            }
            // return old value created by other thead.
            parentTableMeta = (ParentTableMeta<T>) oldTableMeta;
        }
        return parentTableMeta;
    }

    @SuppressWarnings("unchecked")
    private static <T extends IDomain> SimpleTableMeta<T> createSimpleTableMeta(final Class<T> domainClass) {
        if (domainClass.getAnnotation(Table.class) == null
                || domainClass.getAnnotation(Inheritance.class) != null
                || domainClass.getAnnotation(DiscriminatorValue.class) != null) {
            String m = String.format("Class[%s] isn't simple domain.", domainClass.getName());
            throw new IllegalArgumentException(m);
        }

        SimpleTableMeta<T> simpleTableMeta;
        simpleTableMeta = new DefaultSimpleTable<>(domainClass);
        final TableMeta<?> oldTableMeta;
        oldTableMeta = INSTANCE_MAP.putIfAbsent(domainClass, simpleTableMeta);
        if (oldTableMeta != null) {
            if (!(oldTableMeta instanceof DefaultSimpleTable) || oldTableMeta.javaType() != domainClass) {
                throw new IllegalStateException("INSTANCE_MAP state error");
            }
            // return old value created by other thead.
            simpleTableMeta = (SimpleTableMeta<T>) oldTableMeta;
        }
        return simpleTableMeta;
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
            if (discriminatorName.equals(fieldMeta.fieldName())) {
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

            this.primaryField = (PrimaryFieldMeta<T, Object>) this.propNameToFieldMeta.get(MetaBridge.ID);
            if (this.primaryField == null) {
                String m = String.format("Not found primary field meta in domain[%s]", domainClass.getName());
                throw new NullPointerException(m);
            }
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(e, e.getMessage());
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
    public PrimaryFieldMeta<? super T, Object> id() {
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
    public boolean mappingProp(String propName) {
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
        FieldMeta<T, F> fieldMeta = getField(MetaBridge.ID, propClass);
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
    public final boolean equals(Object o) {
        // save column only one FieldMeta instance
        return this == o;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        if (this instanceof ChildTableMeta) {
            builder.append("ChildTableMeta[");
        } else if (this instanceof ParentTableMeta) {
            builder.append("ParentTableMeta[");
        } else {
            builder.append("TableMeta[");
        }
        builder.append(this.javaType.getName())
                .append("]");
        return builder.toString();
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
