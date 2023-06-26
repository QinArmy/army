package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FieldSelection;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

final class PostgreExcludedField extends OperationDataField implements _Selection, FieldSelection {


    static PostgreExcludedField excludedField(FieldMeta<?> field) {
        return new PostgreExcludedField(field);
    }

    private static final String SPACE_EXCLUDED = " EXCLUDED";

    private final FieldMeta<?> field;

    private PostgreExcludedField(FieldMeta<?> field) {
        this.field = field;
    }

    @Override
    public String fieldName() {
        return this.field.fieldName();
    }

    @Override
    public TypeMeta typeMeta() {
        return this.field;
    }

    @Override
    public String alias() {
        return this.field.fieldName();
    }

    @Override
    public TableField tableField() {
        return this.field;
    }

    @Override
    public Expression underlyingExp() {
        return this.field;
    }

    @Override
    public FieldMeta<?> fieldMeta() {
        return this.field;
    }

    @Override
    public void appendSelectItem(final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(SPACE_EXCLUDED)
                .append(_Constant.POINT);

        context.appendFieldOnly(this.field);

        sqlBuilder.append(_Constant.SPACE_AS_SPACE);

        context.parser().identifier(this.field.fieldName(), sqlBuilder);

    }


    @Override
    public void appendSql(final _SqlContext context) {
        if (context.visible() != Visible.BOTH && _MetaBridge.VISIBLE.equals(this.field.fieldName())) {
            throw _Exceptions.visibleField(context.visible(), this.field);
        }

        context.sqlBuilder()
                .append(SPACE_EXCLUDED)
                .append(_Constant.POINT);

        context.appendFieldOnly(this.field);
    }



}
