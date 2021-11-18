package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.sharding.Route;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

        private static final String TABLE_NAME = "DUAL";

        private DualTableMeta() {

        }

        @Override
        public String toString() {
            return TABLE_NAME;
        }

        @Override
        public Class<Dual> javaType() {
            return Dual.class;
        }

        @Override
        public String tableName() {
            return TABLE_NAME;
        }

        @Override
        public boolean immutable() {
            return true;
        }

        @Override
        public String comment() {
            return TABLE_NAME;
        }

        @Override
        public void appendSQL(SQLContext context) {
            context.sqlBuilder()
                    .append(" ")
                    .append(TABLE_NAME);
        }

        @Override
        public final boolean sharding() {
            return false;
        }

        @Nullable
        @Override
        public Class<? extends Route> routeClass() {
            return null;
        }

        @Override
        public PrimaryFieldMeta<Dual, Object> id() {
            throw new UnsupportedOperationException();
        }


        @Override
        public List<FieldMeta<?, ?>> routeFieldList(boolean database) {
            throw new UnsupportedOperationException();
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
            return _SchemaMetaFactory.getSchema("", "");
        }

        @Override
        public boolean mappingField(String fieldName) {
            return false;
        }

        @Override
        public FieldMeta<Dual, Object> getField(String fieldName) throws MetaException {
            throw new MetaException("no field[%s]", fieldName);
        }

        @Override
        public <F> FieldMeta<Dual, F> getField(String fieldName, Class<F> fieldClass) throws MetaException {
            throw new MetaException("no field[%s]", fieldName);
        }

        @Override
        public <F> IndexFieldMeta<Dual, F> getIndexField(String fieldName, Class<F> fieldClass) throws MetaException {
            throw new MetaException("no field[%s]", fieldName);
        }

        @Override
        public <F> UniqueFieldMeta<Dual, F> getUniqueField(String fieldName, Class<F> fieldClass) throws MetaException {
            throw new MetaException("no field[%s]", fieldName);
        }

        @Override
        public <F> PrimaryFieldMeta<Dual, F> id(Class<F> idClass) throws MetaException {
            throw new MetaException("no field[%s]", _MetaBridge.ID);
        }

        @Override
        public List<FieldMeta<Dual, ?>> generatorChain() {
            return null;
        }
    }


}
