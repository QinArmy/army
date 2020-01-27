package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.StringUtils;
import javafx.scene.media.MediaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;

/**
 * created  on 2018/11/19.
 */
final class DefaultTableMeta<T extends IDomain> implements TableMeta<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTableMeta.class);

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
    DefaultTableMeta(@Nullable TableMeta<? super T> parentTableMeta, Class<T> entityClass) {
        Assert.notNull(entityClass,"entityClass required");
        MetaUtils.assertParentTableMeta(parentTableMeta, entityClass);

        this.entityClass = entityClass;
        this.parentTableMeta = parentTableMeta;
        try {

            Table tableMeta = MetaUtils.tableMeta(entityClass);

            this.tableName = tableMeta.name();
            this.comment = tableMeta.comment();
            this.immutable = tableMeta.immutable();
            this.schemaMeta = MetaUtils.schemaMeta(tableMeta);

            this.charset = tableMeta.charset();

            MetaUtils.FieldBean<T> fieldBean = MetaUtils.fieldMetaList(this, tableMeta);
            this.propNameToFieldMeta = fieldBean.getPropNameToFieldMeta();
            this.indexMetaList = fieldBean.getIndexMetaList();
            this.discriminator = fieldBean.getDiscriminator();

            this.mappingMode = MetaUtils.mappingMode(entityClass);
            this.discriminatorValue = MetaUtils.discriminatorValue(this.mappingMode, this.entityClass);

            this.primaryField = (IndexFieldMeta<T, ?>) this.propNameToFieldMeta.get(TableMeta.ID);
            Assert.state(this.primaryField != null, () -> String.format(
                    "entity[%s] primary field meta create error.", entityClass.getName()));
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
    public <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MediaException {
        FieldMeta<T, F> fieldMeta = getField(propName, propClass);
        if (!(fieldMeta instanceof IndexFieldMeta)) {
            throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
        }
        return (IndexFieldMeta<T, F>) fieldMeta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultTableMeta)) {
            return false;
        }
        DefaultTableMeta<?> other = (DefaultTableMeta<?>) o;
        return this.javaType() == other.javaType()
                && this.schema().equals(other.schema())
                && this.tableName().equals(other.tableName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.javaType(), this.schema(), this.tableName());
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
}
