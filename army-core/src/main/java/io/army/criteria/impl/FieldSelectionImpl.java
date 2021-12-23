package io.army.criteria.impl;

import io.army.criteria.FieldSelection;
import io.army.criteria.GenericField;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

/**
 * @see DefaultFieldMeta
 * @see QualifiedFieldImpl
 */
final class FieldSelectionImpl<E> extends OperationExpression<E> implements FieldSelection {

    private final GenericField<?, E> field;

    private final String alias;

    FieldSelectionImpl(GenericField<?, E> field, String alias) {
        this.field = field;
        this.alias = alias;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.field;
    }

    @Override
    public boolean nullable() {
        return this.field.nullable();
    }

    @Override
    public FieldMeta<?, ?> fieldMeta() {
        final GenericField<?, ?> field = this.field;
        return field instanceof FieldMeta ? (FieldMeta<?, ?>) field : field.fieldMeta();
    }


    @Override
    public void appendSql(final _SqlContext context) {
        final GenericField<?, E> field = this.field;
        if (field instanceof FieldMeta) {
            context.appendField((FieldMeta<?, ?>) field);
        } else {
            ((_Expression<?>) field).appendSql(context);
        }
        context.sqlBuilder()
                .append(" AS ")
                .append(context.dialect().quoteIfNeed(this.alias));
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public String toString() {
        return this.field.toString();
    }


    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return ((_Expression<?>) this.field).containsField(fieldMetas);
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        return ((_Expression<?>) this.field).containsFieldOf(tableMeta);
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        return ((_Expression<?>) this.field).containsFieldCount(tableMeta);
    }

    @Override
    public boolean containsSubQuery() {
        return false;
    }
}
