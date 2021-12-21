package io.army.criteria.impl;

import io.army.criteria.LogicalField;
import io.army.criteria.Selection;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collection;


final class LogicalFieldExpImpl<T extends IDomain, F> extends OperationExpression<F>
        implements LogicalField<T, F> {

    private final FieldMeta<T, F> field;

    private final String tableAlias;

    LogicalFieldExpImpl(String tableAlias, FieldMeta<T, F> field) {
        Assert.notNull(field, "fieldMeta required");
        Assert.hasText(tableAlias, "tableAlias required");
        Assert.isTrue(!tableAlias.contains("."), "tableAlias must no '.'");

        this.field = field;
        this.tableAlias = tableAlias;
    }

    @Override
    public final void appendSql(_SqlContext context) {
        context.appendField(this.tableAlias, this.field);
    }

    @Override
    public final String alias() {
        return this.field.fieldName();
    }

    @Override
    public final Selection as(String alias) {
        return new FieldSelectionImpl<>(this, alias);
    }

    @Override
    public String toString() {
        return tableAlias + "." + field.columnName();
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public FieldMeta<T, F> fieldMeta() {
        return this.field;
    }

    @Override
    public TableMeta<T> tableMeta() {
        return this.field.tableMeta();
    }

    @Override
    public Class<F> javaType() {
        return this.field.javaType();
    }

    @Override
    public String fieldName() {
        return this.field.fieldName();
    }

    @Override
    public String columnName() {
        return this.field.columnName();
    }

    @Override
    public MappingType mappingType() {
        return this.field.mappingType();
    }


    @Override
    public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return ((_Expression<?>) this.field).containsField(fieldMetas);
    }

    @Override
    public final boolean containsFieldOf(TableMeta<?> tableMeta) {
        return ((_Expression<?>) this.field).containsFieldOf(tableMeta);
    }

    @Override
    public final int containsFieldCount(TableMeta<?> tableMeta) {
        return ((_Expression<?>) this.field).containsFieldCount(tableMeta);
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }
}
