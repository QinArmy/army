package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.struct.CodeEnum;

import java.util.Collection;
import java.util.Collections;

final class Dual implements TableMeta<Dual>, IDomain {

    static final Dual INSTANCE = new Dual();

    private Dual() {

    }

    @Override
    public Boolean getVisible() {
        return Boolean.FALSE;
    }

    @Override
    public Class<Dual> javaType() {
        return Dual.class;
    }

    @Override
    public String tableName() {
        return "DUAL";
    }

    @Override
    public boolean immutable() {
        return true;
    }

    @Override
    public String comment() {
        return "DUAL";
    }

    @Override
    public TableMeta<? super Dual> parentMeta() {
        return null;
    }

    @Override
    public IndexFieldMeta<? super Dual, ?> primaryKey() {
       throw new UnsupportedOperationException();
    }

    @Override
    public MappingMode mappingMode() {
        return MappingMode.SIMPLE;
    }

    @Override
    public <E extends Enum<E> & CodeEnum> FieldMeta<Dual, E> discriminator() {
        return null;
    }

    @Override
    public int discriminatorValue() {
        return 0;
    }

    @Override
    public Collection<IndexMeta<Dual>> indexCollection() {
        return Collections.emptyList();
    }

    @Override
    public Collection<FieldMeta<Dual, ?>> fieldCollection() {
        return Collections.emptyList();
    }

    @Override
    public String charset() {
        return "";
    }

    @Override
    public SchemaMeta schema() {
        return SchemaMetaFactory.getSchema("","");
    }

    @Override
    public boolean isMappingProp(String propName) {
        return false;
    }

    @Override
    public FieldMeta<Dual, ?> getField(String propName) throws MetaException {
       throw new MetaException(ErrorCode.META_ERROR,"no field[%s]",propName);
    }

    @Override
    public <F> FieldMeta<Dual, F> getField(String propName, Class<F> propClass) throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,"no field[%s]",propName);
    }

    @Override
    public <F> IndexFieldMeta<Dual, F> getIndexField(String propName, Class<F> propClass) throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,"no field[%s]",propName);
    }
}