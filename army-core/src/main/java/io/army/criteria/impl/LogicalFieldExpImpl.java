package io.army.criteria.impl;

import io.army.criteria.LogicalField;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;

import java.util.Collection;


final class LogicalFieldExpImpl<T extends IDomain, F> extends AbstractExpression<F>
        implements LogicalField<T, F> {

    private final FieldMeta<T, F> fieldMeta;

    private final String tableAlias;

    LogicalFieldExpImpl(String tableAlias, FieldMeta<T, F> fieldMeta) {
        Assert.notNull(fieldMeta, "fieldMeta required");
        Assert.hasText(tableAlias, "tableAlias required");
        Assert.isTrue(!tableAlias.contains("."), "tableAlias must no '.'");

        this.fieldMeta = fieldMeta;
        this.tableAlias = tableAlias;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.appendField(this.tableAlias, this.fieldMeta);
    }

    @Override
    public final String alias() {
        return this.fieldMeta.propertyName();
    }

    @Override
    public final Selection as(String alias) {
        return new FieldSelectionImpl<>(this, alias);
    }

    @Override
    public String toString() {
        return tableAlias + "." + fieldMeta.fieldName();
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public FieldMeta<T, F> fieldMeta() {
        return this.fieldMeta;
    }

    @Override
    public TableMeta<T> tableMeta() {
        return fieldMeta.tableMeta();
    }

    @Override
    public Class<F> javaType() {
        return fieldMeta.javaType();
    }

    @Override
    public String propertyName() {
        return fieldMeta.propertyName();
    }

    @Override
    public MappingMeta mappingMeta() {
        return fieldMeta.mappingMeta();
    }


    @Override
    public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return this.fieldMeta.containsField(fieldMetas);
    }

    @Override
    public final boolean containsFieldOf(TableMeta<?> tableMeta) {
        return this.fieldMeta.containsFieldOf(tableMeta);
    }

    @Override
    public final int containsFieldCount(TableMeta<?> tableMeta) {
        return this.fieldMeta.containsFieldCount(tableMeta);
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }
}
