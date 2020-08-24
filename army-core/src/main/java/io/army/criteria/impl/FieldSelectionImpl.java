package io.army.criteria.impl;

import io.army.criteria.FieldSelection;
import io.army.criteria.SQLContext;
import io.army.meta.FieldMeta;
import io.army.meta.GenericField;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;

import java.util.Collection;

/**
 * @see DefaultFieldMeta
 * @see LogicalFieldExpImpl
 */
final class FieldSelectionImpl<E> extends AbstractExpression<E> implements FieldSelection {

    private final GenericField<?, E> fieldExp;

    private final String alias;

    FieldSelectionImpl(GenericField<?, E> fieldExp, String alias) {
        this.fieldExp = fieldExp;
        this.alias = alias;
    }

    @Override
    public final FieldMeta<?, ?> fieldMeta() {
        return this.fieldExp.fieldMeta();
    }

    @Override
    public final MappingMeta mappingMeta() {
        return this.fieldExp.mappingMeta();
    }

    @Override
    public final void appendSQL(SQLContext context) {
        this.fieldExp.appendSQL(context);
        context.sqlBuilder()
                .append(" AS ")
                .append(this.alias);
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    @Override
    public final String toString() {
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
        return false;
    }
}