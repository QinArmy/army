package io.army.criteria.impl;

import io.army.criteria.FieldSelection;
import io.army.criteria.GenericField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

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
    public final MappingType mappingMeta() {
        return this.fieldExp.mappingMeta();
    }

    @Override
    public final void appendSql(_SqlContext context) {
        ((_Expression<?>) this.fieldExp).appendSql(context);
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
        return ((_Expression<?>) this.fieldExp).containsField(fieldMetas);
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        return ((_Expression<?>) this.fieldExp).containsFieldOf(tableMeta);
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        return ((_Expression<?>) this.fieldExp).containsFieldCount(tableMeta);
    }

    @Override
    public boolean containsSubQuery() {
        return false;
    }
}
