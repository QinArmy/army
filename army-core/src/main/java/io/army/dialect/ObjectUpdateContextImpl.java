package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.TableAliasException;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

final class ObjectUpdateContextImpl extends UpdateSQLContextImpl {

    private final String parentAliasOfChild;

    private final String safeParentAlias;


    ObjectUpdateContextImpl(DML dml, DQL dql, TableMeta<?> childMeta, String tableAlias) {
        super(dml, dql, childMeta, tableAlias);

        Assert.notNull(childMeta.parentMeta(), "childMeta not child mode");
        this.parentAliasOfChild = "parentOf" + tableAlias;
        this.safeParentAlias = this.dml.quoteIfNeed(this.parentAliasOfChild);

    }

    @Override
    public FieldMeta<?, ?> versionField() {
        TableMeta<?> parentMeta = this.updateTable.parentMeta();
        Assert.state(parentMeta != null
                , () -> String.format("domain[%s] no parent meta", this.updateTable.javaType().getName()));
        return parentMeta.getField(TableMeta.VERSION);
    }

    @Override
    public FieldMeta<?, ?> updateTimeField() {
        TableMeta<?> parentMeta = this.updateTable.parentMeta();
        Assert.state(parentMeta != null
                , () -> String.format("domain[%s] no parent meta", this.updateTable.javaType().getName()));
        return parentMeta.getField(TableMeta.UPDATE_TIME);
    }


    @Override
    public final void assertField(FieldMeta<?, ?> targetField) throws CriteriaException {
        if (targetField.tableMeta() != this.updateTable
                && targetField.tableMeta() != this.updateTable.parentMeta()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "set clause field[%s] error"
                    , targetField.propertyName());
        }
        assertTargetFieldUpdatable(targetField);
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        if (fieldMeta.tableMeta() == this.updateTable) {
            assertTableAlias(tableAlias, fieldMeta);

            if (StringUtils.hasText(this.safeAlias)) {
                builder.append(this.safeAlias)
                        .append(".");
            }
        } else if (fieldMeta.tableMeta() == this.updateTable.parentMeta()) {
            assertParentAlias(tableAlias, fieldMeta);
            builder.append(this.safeParentAlias)
                    .append(".");
        } else if (StringUtils.hasText(tableAlias)) {
            builder.append(this.dml.quoteIfNeed(tableAlias))
                    .append(".");
        }
        builder.append(this.dml.quoteIfNeed(fieldMeta.fieldName()));
    }


    String safeParentAlias() {
        return safeParentAlias;
    }


    private void assertTableAlias(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (StringUtils.hasText(tableAlias)
                && !tableAlias.equals(this.tableAlias)) {
            throw new TableAliasException(ErrorCode.CRITERIA_ERROR, "object dml field[%s] alias[%s] error"
                    , fieldMeta.tableMeta().tableName(), tableAlias);
        }
    }

    private void assertParentAlias(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (StringUtils.hasText(tableAlias)
                && !tableAlias.equals(this.parentAliasOfChild)) {
            throw new TableAliasException(ErrorCode.CRITERIA_ERROR
                    , "object dml field[%s] alias[%s] error,must be [%s]"
                    , fieldMeta.tableMeta()
                    , tableAlias
                    ,safeParentAlias
            );
        }
    }
}
