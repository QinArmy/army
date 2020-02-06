package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.ClassUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

/**
 * created  on 2018/11/19.
 */
final class DefaultTableMeta<T extends IDomain> implements TableMeta<T> {

    private static final ConcurrentMap<Class<?>, TableMeta<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends IDomain> TableMeta<T> createInstance(@Nullable TableMeta<? super T> parentTableMeta
            , Class<T> entityClass) {
        if (INSTANCE_MAP.containsKey(entityClass)) {
            throw new IllegalStateException(
                    String.format("TableMeta Can only be created once,%s", entityClass.getName()));
        }
        TableMeta<T> tableMeta = new DefaultTableMeta<>(parentTableMeta, entityClass);
        TableMeta<?> actualTableMeta = INSTANCE_MAP.putIfAbsent(entityClass, tableMeta);
        if (actualTableMeta != null && actualTableMeta != tableMeta) {
            tableMeta = (TableMeta<T>) actualTableMeta;
        }
        return tableMeta;
    }

    static <T extends IDomain> TableMeta<T> getMeta(Class<T> entityClass) throws IllegalArgumentException {
        @SuppressWarnings("unchecked")
        TableMeta<T> tableMeta = (TableMeta<T>) INSTANCE_MAP.get(entityClass);
        if (tableMeta == null) {
            throw new IllegalArgumentException(String.format("entity[%s] no scan", entityClass.getName()));
        }
        return tableMeta;
    }


    private final Class<T> entityClass;

    private final String tableName;

    private final boolean immutable;

    private final String comment;

    private final MappingMode mappingMode;

    private final String charset;

    private final SchemaMeta schemaMeta;

    private final int discriminatorValue;

    private final Map<String, FieldMeta<T, ?>> propNameToFieldMeta;

    private final List<IndexMeta<T>> indexMetaList;

    private final IndexFieldMeta<T, ?> primaryField;

    private final TableMeta<? super T> parentTableMeta;

    private final FieldMeta<T, ?> discriminator;

    @SuppressWarnings("unchecked")
    private DefaultTableMeta(@Nullable TableMeta<? super T> parentTableMeta, Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass required");
        MetaUtils.assertParentTableMeta(parentTableMeta, entityClass);
        Assert.state(!INSTANCE_MAP.containsKey(entityClass),
                () -> String.format("entityClass[%s] duplication", entityClass.getName()));

        this.entityClass = entityClass;
        this.parentTableMeta = parentTableMeta;
        try {

            Table table = MetaUtils.tableMeta(entityClass);

            this.tableName = MetaUtils.tableName(table, entityClass);
            this.comment = MetaUtils.tableComment(table, entityClass);
            this.immutable = table.immutable();
            this.schemaMeta = MetaUtils.schemaMeta(table);

            this.mappingMode = MetaUtils.tableMappingMode(entityClass);
            this.charset = table.charset();

            MetaUtils.FieldBean<T> fieldBean = MetaUtils.fieldMetaList(this, table);
            this.propNameToFieldMeta = fieldBean.getPropNameToFieldMeta();
            this.indexMetaList = fieldBean.getIndexMetaList();
            this.discriminator = fieldBean.getDiscriminator();


            this.discriminatorValue = MetaUtils.discriminatorValue(this.mappingMode, this);

            this.primaryField = (IndexFieldMeta<T, ?>) this.propNameToFieldMeta.get(TableMeta.ID);
            Assert.state(this.primaryField != null, () -> String.format(
                    "entity[%s] primary field meta debugSQL error.", entityClass.getName()));
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, e.getMessage());
        }
    }

    @Override
    public Class<T> javaType() {
        return this.entityClass;
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
    public IndexFieldMeta<? super T, ?> primaryKey() {
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
    public boolean isMappingProp(String propName) {
        return this.propNameToFieldMeta.containsKey(propName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator() {
        return (FieldMeta<T, E>) this.discriminator;
    }

    @Nullable
    @Override
    public TableMeta<? super T> parentMeta() {
        return parentTableMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FieldMeta<T, ?> getField(String propName) throws MetaException {
        FieldMeta<?, ?> fieldMeta = propNameToFieldMeta.get(propName);
        if (fieldMeta == null) {
            throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
        }
        return (FieldMeta<T, ?>) fieldMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) {
        Assert.notNull(propName, "propName required");
        Assert.notNull(propName, "propClass required");

        FieldMeta<T, ?> fieldMeta = propNameToFieldMeta.get(propName);
        if (fieldMeta == null || propClass != fieldMeta.javaType()) {
            throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
        }
        return (FieldMeta<T, F>) fieldMeta;
    }

    @Override
    public <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MetaException {
        Assert.notNull(propName, "propName required");
        Assert.notNull(propName, "propClass required");

        FieldMeta<T, F> fieldMeta = getField(propName, propClass);
        if (!(fieldMeta instanceof IndexFieldMeta) || propClass != fieldMeta.javaType()) {
            throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
        }
        return (IndexFieldMeta<T, F>) fieldMeta;
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
    public String toString() {
        StringBuilder builder = new StringBuilder(this.javaType().getName());
        builder.append(" mapping ");
        if (!schemaMeta.defaultSchema()) {
            builder.append(this.schema())
                    .append(".")
            ;
        }
        builder.append(this.tableName())
                .append("[\n");
        Iterator<FieldMeta<T, ?>> iterator = propNameToFieldMeta.values().iterator();
        for (FieldMeta<T, ?> fieldMeta; iterator.hasNext(); ) {
            fieldMeta = iterator.next();
            builder.append(fieldMeta.propertyName())
                    .append(" mapping ")
                    .append(fieldMeta.fieldName())
            ;
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }
        return builder.append("\n]")
                .toString()
                ;
    }


    /*################################## blow private method ##################################*/


}
