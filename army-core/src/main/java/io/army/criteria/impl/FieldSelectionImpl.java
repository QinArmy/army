package io.army.criteria.impl;

import io.army.criteria.FieldSelection;
import io.army.criteria.GenericField;
import io.army.criteria.QualifiedField;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

/**
 * @see DefaultFieldMeta
 * @see QualifiedFieldImpl
 */
final class FieldSelectionImpl implements FieldSelection, _Selection {

    private final GenericField<?, ?> field;

    private final String alias;

    FieldSelectionImpl(GenericField<?, ?> field, String alias) {
        this.field = field;
        this.alias = alias;
    }

    @Override
    public ParamMeta paramMeta() {
        final GenericField<?, ?> field = this.field;
        return field instanceof FieldMeta ? (FieldMeta<?, ?>) field : field.paramMeta();
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
    public void appendSelection(final _SqlContext context) {
        final GenericField<?, ?> field = this.field;
        if (field instanceof FieldMeta) {
            context.appendField((FieldMeta<?, ?>) field);
        } else {
            ((_Expression<?>) field).appendSql(context);
        }
        context.sqlBuilder()
                .append(Constant.SPACE_AS_SPACE)
                .append(context.dialect().quoteIfNeed(this.alias));
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(Constant.SPACE);

        final GenericField<?, ?> field = this.field;

        if (field instanceof FieldMeta) {
            builder.append(field.columnName());
        } else if (field instanceof QualifiedField) {
            final QualifiedField<?, ?> qualifiedField = (QualifiedField<?, ?>) field;
            builder.append(qualifiedField.tableAlias())
                    .append(Constant.POINT)
                    .append(field.columnName());
        } else {
            throw new IllegalStateException(String.format("field[%s] error", this.field));
        }
        return builder.append(Constant.SPACE_AS_SPACE)
                .append(this.alias)
                .toString();

    }


}
