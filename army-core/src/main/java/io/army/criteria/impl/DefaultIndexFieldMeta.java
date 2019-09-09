package io.army.criteria.impl;

import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.lang.reflect.Field;

class DefaultIndexFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F> implements IndexFieldMeta<T, F> {

    private final IndexMeta<T> indexMeta;

    private final boolean fieldAsc;

    DefaultIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta, boolean fieldUnique,
                          boolean fieldAsc) throws MetaException {
        super(table, field, fieldUnique, true);
        this.indexMeta = indexMeta;
        this.fieldAsc = fieldAsc;
    }

    @Override
    public IndexMeta<T> indexMeta() {
        return this.indexMeta;
    }

    @Override
    public boolean fieldAsc() {
        return this.fieldAsc;
    }
}
