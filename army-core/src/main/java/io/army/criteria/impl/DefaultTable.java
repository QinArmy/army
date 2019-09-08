package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2018/11/19.
 */
public final class DefaultTable<T extends IDomain> implements TableMeta<T> {

    private final Class<T> entityClass;

    private final String tableName;

    private final boolean immutable;

    private final String comment;

    private final MappingMode mappingMode;

    private final String charset;

    private final String schema;

    private final List<FieldMeta<T, ?>> fieldList;

    private final List<FieldMeta<T, ?>> indexList;

    private final List<FieldMeta<T, ?>> uniqueList;

    private final FieldMeta<? super T, ?> primaryField;


    public DefaultTable(List<TableMeta<? super T>> supperList, Class<T> entityClass) {
        this.entityClass = entityClass;

        try {
            Table tableMeta = MetaUtils.tableMeta(entityClass);

            this.tableName = tableMeta.name();
            this.comment = tableMeta.comment();
            this.immutable = tableMeta.immutable();
            this.schema = tableMeta.schema();

            MetaUtils.FieldBean<T> fieldBean = MetaUtils.fieldMetaList(this, tableMeta, supperList);
            this.fieldList = fieldBean.getFieldMetaList();
            this.indexList = fieldBean.getIndexList();
            this.uniqueList = fieldBean.getUniqueList();

            this.mappingMode = MetaUtils.mappingMode(entityClass);
            this.charset = tableMeta.charset();
            this.primaryField = MetaUtils.primaryField(supperList, uniqueList, this);
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, e.getMessage());
        }
    }

    public DefaultTable(Class<T> entityClass) throws MetaException {
        this(Collections.emptyList(), entityClass);
    }


    @Override
    public List<FieldMeta<T, ?>> indexPropList() {
        return this.indexList;
    }

    @Override
    public List<FieldMeta<T, ?>> uniquePropList() {
        return this.uniqueList;
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
    public FieldMeta<? super T, ?> primaryKey() {
        return this.primaryField;
    }


    @Override
    public MappingMode mappingMode() {
        return this.mappingMode;
    }

    @Override
    public List<FieldMeta<T, ?>> fieldList() {
        return this.fieldList;
    }


    @Override
    public String createSql(Dialect dialect) {
        return null;
    }

    @Override
    public String charset() {
        return this.charset;
    }


    @Override
    public String schema() {
        return this.schema;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) {
        for (FieldMeta<T, ?> fieldMeta : fieldList) {
            if (fieldMeta.propertyName().equals(propName)) {
                return (FieldMeta<T, F>) fieldMeta;
            }
        }
        throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] not found", propName);
    }


}
