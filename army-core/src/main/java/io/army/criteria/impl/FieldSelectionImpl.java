package io.army.criteria.impl;

import io.army.criteria.FieldSelection;
import io.army.criteria.SQLContext;
import io.army.meta.GenericField;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;

import java.util.Collection;

/**
 *
 */
final class FieldSelectionImpl<E> extends AbstractExpression<E> implements FieldSelection {

    private final GenericField<?, E> fieldExp;

    FieldSelectionImpl(GenericField<?, E> fieldExp, String alias) {
        this.fieldExp = fieldExp;
        this.as(alias);
    }

    @Override
    public FieldMeta<?, ?> fieldMeta() {
        return this.fieldExp.fieldMeta();
    }

    @Override
    public MappingMeta mappingMeta() {
        return this.fieldExp.mappingMeta();
    }

    @Override
    protected final void appendSQL(SQLContext context) {
        this.fieldExp.appendSQL(context);
    }

    @Override
    public String toString() {
        return this.fieldExp.toString();
    }


    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return this.fieldExp.containsField(fieldMetas);
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        return this.fieldExp.containsFieldOf(tableMeta);
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        return this.fieldExp.containsFieldCount(tableMeta);
    }

    @Override
    public boolean containsSubQuery() {
        return this.fieldExp.containsSubQuery();
    }
}
