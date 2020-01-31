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

import java.util.*;

/**
 * created  on 2018/11/19.
 */
final class DefaultTableMeta<T extends IDomain> implements TableMeta<T> {


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

    private final int hashCode;

    @SuppressWarnings("unchecked")
    DefaultTableMeta(@Nullable TableMeta<? super T> parentTableMeta, Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass required");
        MetaUtils.assertParentTableMeta(parentTableMeta, entityClass);

        this.entityClass = entityClass;
        this.parentTableMeta = parentTableMeta;
        try {

            Table table = MetaUtils.tableMeta(entityClass);

            this.tableName = MetaUtils.tableName(table, entityClass);
            this.comment = MetaUtils.tableComment(table, entityClass);
            this.immutable = table.immutable();
            this.schemaMeta = MetaUtils.schemaMeta(table);

            this.charset = table.charset();
            // before create field meta create hash code
            this.hashCode = createHashCode();

            MetaUtils.FieldBean<T> fieldBean = MetaUtils.fieldMetaList(this, table);
            this.propNameToFieldMeta = fieldBean.getPropNameToFieldMeta();
            this.indexMetaList = fieldBean.getIndexMetaList();
            this.discriminator = fieldBean.getDiscriminator();

            this.mappingMode = MetaUtils.mappingMode(entityClass);
            this.discriminatorValue = MetaUtils.discriminatorValue(this.mappingMode, this);

            this.primaryField = (IndexFieldMeta<T, ?>) this.propNameToFieldMeta.get(TableMeta.ID);
            Assert.state(this.primaryField != null, () -> String.format(
                    "entity[%s] primary field meta build error.", entityClass.getName()));
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, e.getMessage());
        }
    }

    public DefaultTableMeta(Class<T> entityClass) throws MetaException {
        this(null, entityClass);
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

    @Override
    public FieldMeta<?, ?> getField(String propName) throws MetaException {
        FieldMeta<?, ?> fieldMeta = propNameToFieldMeta.get(propName);
        if (fieldMeta == null ) {
            throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
        }
        return fieldMeta;
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

    @Nullable
    @Override
    public TableMeta<? super T> parent() {
        return parentTableMeta;
    }

    @Override
    public <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MetaException {
        FieldMeta<T, F> fieldMeta = getField(propName, propClass);
        if (!(fieldMeta instanceof IndexFieldMeta)) {
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
        return hashCode;
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

    private int createHashCode() {
        return Objects.hash(this.schemaMeta, this.javaType(), this.tableName);
    }

}
