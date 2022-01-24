package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.QualifiedField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Assert;
import io.army.util._Exceptions;


final class QualifiedFieldImpl<T extends IDomain, F> extends OperationField<T, F>
        implements QualifiedField<T, F>, _Selection {


    private final String tableAlias;

    private final FieldMeta<T, F> field;

    QualifiedFieldImpl(final String tableAlias, final FieldMeta<T, F> field) {
        _Assert.notNull(field, "fieldMeta required");
        _Assert.hasText(tableAlias, "tableAlias required");
        _Assert.isTrue(!tableAlias.contains("."), "tableAlias must no '.'");

        this.field = field;
        this.tableAlias = tableAlias;
    }

    @Override
    public UpdateMode updateMode() {
        return this.field.updateMode();
    }

    @Override
    public boolean codec() {
        return this.field.codec();
    }

    @Override
    public boolean nullable() {
        return this.field.nullable();
    }

    @Override
    public ParamMeta paramMeta() {
        return this.field;
    }

    @Override
    public void appendSelection(_SqlContext context) {
        context.appendField(this.tableAlias, this.field);

        context.sqlBuilder()
                .append(Constant.SPACE_AS_SPACE)
                .append(context.dialect().quoteIfNeed(this.field.fieldName()));
    }

    @Override
    public void appendSql(final _SqlContext context) {
        if (context.visible() != Visible.BOTH && _MetaBridge.VISIBLE.equals(this.field.fieldName())) {
            throw _Exceptions.visibleField(context.visible(), this);
        }
        context.appendField(this.tableAlias, this.field);
    }

    @Override
    public String alias() {
        return this.field.fieldName();
    }


    @Override
    public String toString() {
        return String.format(" %s.%s", this.tableAlias, this.field);
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


}
