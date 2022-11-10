package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.criteria.Item;
import io.army.criteria.Selection;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

import java.util.Objects;
import java.util.function.Function;


final class QualifiedFieldImpl<T, I extends Item> extends OperationDataField<I>
        implements ItemField<T, I>, _Selection {

    static <T, I extends Item> QualifiedFieldImpl<T, I> create(final String tableAlias, final FieldMeta<T> field
            , final Function<Selection, I> function) {
        return new QualifiedFieldImpl<>(tableAlias, field, function);
    }

    private final String tableAlias;

    private final TableFieldMeta<T> field;

    private QualifiedFieldImpl(String tableAlias, FieldMeta<T> field, Function<Selection, I> function) {
        super(function);
        this.field = (TableFieldMeta<T>) field;
        this.tableAlias = tableAlias;
    }

    @Override
    public UpdateMode updateMode() {
        return this.field.updateMode;
    }

    @Override
    public boolean codec() {
        return this.field.codec();
    }

    @Override
    public boolean nullable() {
        return this.field.nullable;
    }

    @Override
    public TypeMeta typeMeta() {
        return this.field;
    }

    @Override
    public TableField tableField() {
        // return this
        return this;
    }

    @Override
    public _Expression selectionExp() {
        return this;
    }

    @Override
    public void appendSelection(final _SqlContext context) {
        context.appendField(this.tableAlias, this.field);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_AS_SPACE);

        context.parser().identifier(this.field.fieldName, sqlBuilder);
    }

    @Override
    public void appendSql(final _SqlContext context) {
        if (context.visible() != Visible.BOTH && _MetaBridge.VISIBLE.equals(this.field.fieldName)) {
            throw _Exceptions.visibleField(context.visible(), this);
        }
        context.appendField(this.tableAlias, this.field);
    }

    @Override
    public String alias() {
        return this.field.fieldName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tableAlias, this.field);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof QualifiedFieldImpl) {
            final QualifiedFieldImpl<?> o = (QualifiedFieldImpl<?>) obj;
            match = o.field == this.field && o.tableAlias.equals(this.tableAlias);
        } else {
            match = false;
        }
        return match;
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
    public FieldMeta<T> fieldMeta() {
        return this.field;
    }

    @Override
    public TableMeta<T> tableMeta() {
        return this.field.table;
    }

    @Override
    public Class<?> javaType() {
        return this.field.javaType;
    }

    @Override
    public String fieldName() {
        return this.field.fieldName;
    }

    @Override
    public String columnName() {
        return this.field.columnName;
    }

    @Override
    public String objectName() {
        return this.field.columnName;
    }

    @Override
    public MappingType mappingType() {
        return this.field.mappingType;
    }

    @Override
    public boolean insertable() {
        return this.field.insertable;
    }

    @Override
    public GeneratorType generatorType() {
        return this.field.generatorType;
    }


}
