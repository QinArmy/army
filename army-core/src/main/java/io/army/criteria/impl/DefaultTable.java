package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.util.Assert;

import java.util.ArrayList;
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

    private final int discriminatorValue;

    private final List<FieldMeta<T, ?>> fieldList;

    private final List<IndexMeta<T>> indexList;

    private final IndexFieldMeta<? super T, ?> primaryField;

    private final List<TableMeta<? super T>> parentList;


    public DefaultTable(List<TableMeta<? super T>> parentList, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.parentList = Collections.unmodifiableList(parentList);
        MetaUtils.assertParentList(this.parentList, entityClass);
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
        this(Collections.emptyList(), entityClass);
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
    public List<TableMeta<? super T>> parentList() {
        return parentList;
    }


    @Override
    public <S extends T> List<TableMeta<? super S>> tableList(Class<S> sunClass) {
        Assert.isAssignable(javaType(), sunClass);

        List<TableMeta<? super S>> list = new ArrayList<>(parentList().size() + 1);
        list.addAll(parentList());
        list.add(this);
        return list;
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


}
