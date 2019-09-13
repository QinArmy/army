package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import javafx.scene.media.MediaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * created  on 2018/11/19.
 */
public final class DefaultTable<T extends IDomain> implements TableMeta<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTable.class);

    private final Class<T> entityClass;

    private final String tableName;

    private final boolean immutable;

    private final String comment;

    private final MappingMode mappingMode;

    private final String charset;

    private final String schema;

    private final int discriminatorValue;

    private final List<FieldMeta<T, ?>> fieldList;

    private final List<IndexMeta<T>> indexList;

    private final IndexFieldMeta<T, ?> primaryField;

    private final TableMeta<? super T> parentTableMeta;


    public DefaultTable(@Nullable TableMeta<? super T> parentTableMeta, Class<T> entityClass) {
        MetaUtils.assertParentTableMeta(parentTableMeta, entityClass);

        this.entityClass = entityClass;
        this.parentTableMeta = parentTableMeta;
        try {

            Table tableMeta = MetaUtils.tableMeta(entityClass);

            this.tableName = tableMeta.name();
            this.comment = tableMeta.comment();
            this.immutable = tableMeta.immutable();
            this.schema = tableMeta.schema();

            MetaUtils.FieldBean<T> fieldBean = MetaUtils.fieldMetaList(this, tableMeta);
            this.fieldList = fieldBean.getFieldMetaList();
            this.indexList = fieldBean.getIndexMetaList();

            this.mappingMode = MetaUtils.mappingMode(entityClass);
            this.discriminatorValue = MetaUtils.discriminatorValue(this.mappingMode, this.entityClass);
            this.charset = tableMeta.charset();
            this.primaryField = MetaUtils.primaryField(indexList, this);
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, e.getMessage());
        }
    }

    public DefaultTable(Class<T> entityClass) throws MetaException {
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
    public int fieldCount() {
        return this.fieldList.size();
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
    public List<IndexMeta<T>> indexList() {
        return this.indexList;
    }

    @Override
    public List<FieldMeta<T, ?>> fieldList() {
        return this.fieldList;
    }


    @Override
    public String charset() {
        return this.charset;
    }


    @Override
    public String schema() {
        return this.schema;
    }

    @Override
    public <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) {
        for (FieldMeta<T, ?> fieldMeta : fieldList()) {
            if (fieldMeta.propertyName().equals(propName) && fieldMeta.javaType() == propClass) {
                return (FieldMeta<T, F>) fieldMeta;
            }
        }
        throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
    }

    @Nullable
    @Override
    public TableMeta<? super T> parent() {
        return null;
    }

    @Override
    public <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MediaException {
        return null;
    }
}
