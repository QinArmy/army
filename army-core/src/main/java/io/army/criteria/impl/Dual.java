package io.army.criteria.impl;

import io.army.criteria.MetaException;
import io.army.criteria.SQLContext;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.struct.CodeEnum;

import java.util.Collection;
import java.util.Collections;

final class Dual implements IDomain {

    private Dual() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getId() {
        throw new UnsupportedOperationException();
    }

    static final class DualTableMeta implements TableMeta<Dual> {

        static final DualTableMeta INSTANCE = new DualTableMeta();

        private DualTableMeta() {

        }

        @Override
        public String toString() {
            return "DUAL";
        }

        @Override
        public void appendSQL(SQLContext context) {
            context.appendTable(this);
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
        public ParentTableMeta<? super Dual> parentMeta() {
            return null;
        }

        @Override
        public PrimaryFieldMeta<? super Dual, Object> id() {
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
            return SchemaMetaFactory.getSchema("", "");
        }

        @Override
        public boolean mappingProp(String propName) {
            return false;
        }

        @Override
        public FieldMeta<Dual, Object> getField(String propName) throws MetaException {
            throw new MetaException("no field[%s]", propName);
        }

        @Override
        public <F> FieldMeta<Dual, F> getField(String propName, Class<F> propClass) throws MetaException {
            throw new MetaException("no field[%s]", propName);
        }

        @Override
        public <F> IndexFieldMeta<Dual, F> getIndexField(String propName, Class<F> propClass) throws MetaException {
            throw new MetaException("no field[%s]", propName);
        }

        @Override
        public <F> UniqueFieldMeta<Dual, F> getUniqueField(String propName, Class<F> propClass) throws MetaException {
            throw new MetaException("no field[%s]", propName);
        }

        @Override
        public <F> PrimaryFieldMeta<Dual, F> getPrimaryField(Class<F> propClass) throws MetaException {
            throw new MetaException("no field[%s]", TableMeta.ID);
        }
    }


}
