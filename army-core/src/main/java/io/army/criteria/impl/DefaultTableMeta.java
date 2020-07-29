package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.sharding.Route;
import io.army.struct.CodeEnum;
import io.army.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created  on 2018/11/19.
 */
class DefaultTableMeta<T extends IDomain> implements TableMeta<T> {

    private static final ConcurrentMap<Class<?>, TableMeta<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends IDomain> ChildTableMeta<T> createChildInstance(ParentTableMeta<? super T> parentTableMeta
            , Class<T> domainClass) {
        if (INSTANCE_MAP.containsKey(domainClass)) {
            throw new IllegalStateException(
                    String.format("TableMeta Can only be created once,%s", domainClass.getName()));
        }
        ChildTableMeta<T> childTable = new DefaultChildTable<>(parentTableMeta, domainClass);

        TableMeta<?> actualTableMeta = INSTANCE_MAP.putIfAbsent(domainClass, childTable);
        if (actualTableMeta != null && actualTableMeta != childTable) {
            Assert.isInstanceOf(ChildTableMeta.class, actualTableMeta);
            childTable = (ChildTableMeta<T>) actualTableMeta;
        }
        return childTable;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain> ParentTableMeta<T> createParentInstance(Class<T> domainClass) {
        if (INSTANCE_MAP.containsKey(domainClass)) {
            throw new IllegalStateException(
                    String.format("TableMeta Can only be created once,%s", domainClass.getName()));
        }
        ParentTableMeta<T> parentTable = new DefaultParentTable<>(domainClass);

        TableMeta<?> actualTableMeta = INSTANCE_MAP.putIfAbsent(domainClass, parentTable);
        if (actualTableMeta != null && actualTableMeta != parentTable) {
            Assert.isInstanceOf(ParentTableMeta.class, actualTableMeta);
            parentTable = (ParentTableMeta<T>) actualTableMeta;
        }
        return parentTable;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain> TableMeta<T> createTableInstance(Class<T> entityClass) {
        if (INSTANCE_MAP.containsKey(entityClass)) {
            throw new IllegalStateException(
                    String.format("TableMeta Can only be created once,%s", entityClass.getName()));
        }
        TableMeta<T> tableMeta = new DefaultTableMeta<>(null, entityClass);

        TableMeta<?> actualTableMeta = INSTANCE_MAP.putIfAbsent(entityClass, tableMeta);
        if (actualTableMeta != null && actualTableMeta != tableMeta) {
            tableMeta = (TableMeta<T>) actualTableMeta;
        }
        return tableMeta;
    }

    static <T extends IDomain> TableMeta<T> getTableMeta(Class<T> entityClass) throws IllegalArgumentException {
        @SuppressWarnings("unchecked")
        TableMeta<T> tableMeta = (TableMeta<T>) INSTANCE_MAP.get(entityClass);
        if (tableMeta == null) {
            throw new IllegalArgumentException(String.format("entity[%s] no scan", entityClass.getName()));
        }
        return tableMeta;
    }

    static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(Class<T> entityClass) {
        TableMeta<T> tableMeta = getTableMeta(entityClass);
        if (!(tableMeta instanceof ParentTableMeta)) {
            throw new IllegalArgumentException(String.format("entity[%s] non ParentTableMeta or no scan"
                    , entityClass.getName()));
        }
        return (ParentTableMeta<T>) tableMeta;
    }

    static <T extends IDomain> ChildTableMeta<T> getChildTableMeta(Class<T> entityClass) {
        TableMeta<T> tableMeta = getTableMeta(entityClass);
        if (!(tableMeta instanceof ChildTableMeta)) {
            throw new IllegalArgumentException(String.format("entity[%s] non ChildTableMeta or no scan"
                    , entityClass.getName()));
        }
        return (ChildTableMeta<T>) tableMeta;
    }

    private static void assertModeAndMetaMatch(TableMeta<?> tableMeta) {
        switch (tableMeta.mappingMode()) {
            case PARENT:
                if (!(tableMeta instanceof DefaultParentTable)) {
                    throw new MetaException("domain[%s] can't invoke TableMetaFactory.createParentTableMta(Class) method"
                            , tableMeta.javaType());
                }
                break;
            case CHILD:
                if (!(tableMeta instanceof DefaultChildTable)) {
                    String m;
                    m = "domain[%s] can't invoke TableMetaFactory.createChildTableMeta(ParentTableMeta,Class) method";
                    throw new MetaException(m
                            , tableMeta.javaType());
                }
                break;
            case SIMPLE:
                if ((tableMeta instanceof DefaultParentTable) || (tableMeta instanceof DefaultChildTable)) {
                    throw new MetaException("domain[%s] can't invoke TableMetaFactory.createTableMeta(Class) method"
                            , tableMeta.javaType());
                }
                break;
            default:
                throw new MetaException("unknown MappingMode[%s]", tableMeta.mappingMode());
        }
    }


    private final Class<T> domainClass;

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
    private DefaultTableMeta(@Nullable ParentTableMeta<? super T> parentTableMeta, Class<T> domainClass) {
        Assert.notNull(domainClass, "entityClass required");
        TableMetaUtils.assertParentTableMeta(parentTableMeta, domainClass);
        Assert.state(!INSTANCE_MAP.containsKey(domainClass),
                () -> String.format("entityClass[%s] duplication", domainClass.getName()));

        this.domainClass = domainClass;
        this.parentTableMeta = parentTableMeta;
        try {

            Table table = TableMetaUtils.tableMeta(domainClass);

            this.tableName = TableMetaUtils.tableName(table, domainClass);
            this.comment = TableMetaUtils.tableComment(table, domainClass);
            this.immutable = table.immutable();
            this.schemaMeta = TableMetaUtils.schemaMeta(table);

            this.mappingMode = TableMetaUtils.tableMappingMode(domainClass);
            this.charset = table.charset();

            TableMetaUtils.FieldBean<T> fieldBean = TableMetaUtils.fieldMetaList(this, table);
            this.propNameToFieldMeta = fieldBean.getPropNameToFieldMeta();
            this.indexMetaList = fieldBean.getIndexMetaList();

            if (parentTableMeta == null) {
                this.discriminator = fieldBean.getDiscriminator();
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

            this.primaryField = (PrimaryFieldMeta<T, Object>) this.propNameToFieldMeta.get(TableMeta.ID);
            Assert.state(this.primaryField != null, () -> String.format(
                    "domain[%s] primary field meta debugSQL error.", domainClass.getName()));
            assertModeAndMetaMatch(this);
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(e, e.getMessage());
        }
    }

    @Override
    public Class<T> javaType() {
        return this.domainClass;
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
        FieldMeta<T, F> fieldMeta = getField(ID, propClass);
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
        return this.domainClass.getName();
    }


    /*################################## blow static class ##################################*/

    private static final class DefaultParentTable<T extends IDomain> extends DefaultTableMeta<T>
            implements ParentTableMeta<T> {

        private DefaultParentTable(Class<T> entityClass) {
            super(null, entityClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator() {
            FieldMeta<T, E> fieldMeta = (FieldMeta<T, E>) super.discriminator();
            Assert.state(fieldMeta != null, "discriminator is null,state error.");
            return fieldMeta;
        }

    }

    private static final class DefaultChildTable<T extends IDomain> extends DefaultTableMeta<T>
            implements ChildTableMeta<T> {

        private DefaultChildTable(ParentTableMeta<? super T> parentTableMeta, Class<T> entityClass) {
            super(parentTableMeta, entityClass);
            Assert.notNull(parentTableMeta, "parentTableMeta required");
        }

        @NonNull
        @Override
        public final ParentTableMeta<? super T> parentMeta() {
            ParentTableMeta<? super T> meta = super.parentMeta();
            Assert.state(meta != null, "parentMeta is null,state error.");
            return meta;
        }


    }


}
