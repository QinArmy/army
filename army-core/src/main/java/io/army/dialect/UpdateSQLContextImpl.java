package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.NonUpdateAbleException;
import io.army.criteria.SQLStatement;
import io.army.criteria.TableAliasException;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

class UpdateSQLContextImpl extends DefaultSQLContext implements UpdateSQLContext {

    protected final TableMeta<?> updateTable;

    protected final String tableAlias;

    protected final String safeAlias;

    UpdateSQLContextImpl(DML dml, SQLStatement sqlStatement, TableMeta<?> updateTable, String tableAlias) {
        super(dml, sqlStatement);
        this.updateTable = updateTable;

        Assert.isTrue(sqlStatement.isUpdate(), "sqlStatement no object dml");
        Assert.hasText(tableAlias, "tableAlisa required");

        this.tableAlias = tableAlias;

        if (StringUtils.hasText(this.tableAlias)) {
            this.safeAlias = this.dml.quoteIfNeed(this.tableAlias);
        } else {
            this.safeAlias = "";
        }
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return updateTable;
    }

    @Override
    public final String safeAlias() {
        return safeAlias;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
       if(fieldMeta.tableMeta() == updateTable
               && StringUtils.hasText(tableAlias)
               && !this.tableAlias.equals(tableAlias)){
           throwTableAliasError(tableAlias,fieldMeta);
       }else {
           builder.append(this.safeAlias)
                   .append(".")
                   .append(this.dml.quoteIfNeed(fieldMeta.fieldName()));
       }
    }

    @Override
    public void assertField(FieldMeta<?, ?> targetField)
            throws CriteriaException {
        if (targetField.tableMeta() != updateTable) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "mapping prop[%s] don't belong to %s", targetField.propertyName(), updateTable.javaType().getName());
        }
        assertTargetFieldUpdatable(targetField);
    }

    @Override
    public FieldMeta<?, ?> versionField() {
        return updateTable.getField(TableMeta.VERSION);
    }

    @Override
    public FieldMeta<?, ?> updateTimeField() {
        return updateTable.getField(TableMeta.UPDATE_TIME);
    }

    /*################################## blow protected method ##################################*/

    protected final void assertTargetFieldUpdatable(FieldMeta<?, ?> targetField) {
        if (!targetField.updatable()) {
            throw new NonUpdateAbleException(ErrorCode.NON_UPDATABLE
                    , String.format("domain[%s] field[%s] is non-updatable"
                    , targetField.tableMeta().javaType().getName(), targetField.propertyName()));
        }
        if (TableMeta.VERSION.equals(targetField.propertyName())
                || TableMeta.UPDATE_TIME.equals(targetField.propertyName())) {
            throw new CriteriaException(ErrorCode.NON_UPDATABLE, "version or updateTime is managed by army.");
        }
    }

    /*################################## blow private method ##################################*/


}
