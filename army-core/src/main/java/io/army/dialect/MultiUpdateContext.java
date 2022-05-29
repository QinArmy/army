package io.army.dialect;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.criteria.impl.inner._Selection;
import io.army.meta.FieldMeta;
import io.army.stmt.BatchStmt;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

final class MultiUpdateContext extends MultiTableContext implements _MultiUpdateContext, DmlStmtParams {

    static MultiUpdateContext create(_MultiUpdate statement, ArmyDialect dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.createContext(statement.tableBlockList(), dialect, visible, true);
        return new MultiUpdateContext(statement, tableContext, dialect, visible);
    }


    private final boolean hasVersion;

    private final boolean supportQueryUpdate;

    private List<DataField> conditionFieldList;


    private MultiUpdateContext(_MultiUpdate statement, TableContext tableContext, ArmyDialect dialect, Visible visible) {
        super(tableContext, dialect, visible);
        this.hasVersion = _DmlUtils.hasOptimistic(statement.predicateList());
        this.supportQueryUpdate = dialect.supportQueryUpdate();
    }


    @Override
    public void appendSetLeftItem(final DataField dataField) {
        final UpdateMode updateMode;
        if (dataField instanceof TableField) {
            updateMode = ((TableField) dataField).updateMode();
        } else if (this.supportQueryUpdate) {
            final TableField f;
            f = ((_Selection) dataField).tableField();
            if (f == null) {
                throw _Exceptions.immutableField(dataField);
            }
            updateMode = f.updateMode();
        } else {
            throw _Exceptions.immutableField(dataField);
        }

        if (updateMode == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(dataField);
        }

        final StringBuilder sqlBuilder = this.sqlBuilder;
        if (!(dataField instanceof TableField)) {
            final DerivedField field = (DerivedField) dataField;
            final String tableAlias = field.tableAlias();
            final TableItem tableItem = this.aliasToTable.get(tableAlias);
            if (!(tableItem instanceof DerivedTable)
                    || ((DerivedTable) tableItem).selection(field.fieldName()) == null) {
                throw _Exceptions.unknownColumn(field);
            }
            final String safeTableAlias;
            safeTableAlias = this.aliasToSafeAlias.get(tableAlias);
            assert safeTableAlias != null;
            sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);
            this.dialect.safeObjectName(field.fieldName(), sqlBuilder);
        } else if (dataField instanceof FieldMeta) {
            final FieldMeta<?> field = (FieldMeta<?>) dataField;
            final String safeTableAlias;
            safeTableAlias = this.tableToSafeAlias.get(field.tableMeta());
            if (safeTableAlias == null) {
                //self-join
                throw _Exceptions.selfJoinNonQualifiedField(field);
            }
            sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);
            this.dialect.safeObjectName(field.columnName(), sqlBuilder);
        } else if (dataField instanceof QualifiedField) {
            final QualifiedField<?> field = (QualifiedField<?>) dataField;
            final String tableAlias = field.tableAlias();
            if (this.aliasToTable.get(tableAlias) != field.tableMeta()) {
                throw _Exceptions.unknownColumn(field);
            }
            final String safeTableAlias;
            safeTableAlias = this.aliasToSafeAlias.get(tableAlias);
            assert safeTableAlias != null;
            sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);
            this.dialect.safeObjectName(field.columnName(), sqlBuilder);
        } else {
            throw _Exceptions.immutableField(dataField);
        }

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.dialect.supportOnlyDefault()) {
                    throw _Exceptions.dontSupportOnlyDefault(this.dialect.dialectMode());
                }
                List<DataField> conditionFieldList = this.conditionFieldList;
                if (conditionFieldList == null) {
                    conditionFieldList = new ArrayList<>();
                    this.conditionFieldList = conditionFieldList;
                }
                conditionFieldList.add(dataField);
            }
            break;
            default:
                //no-op
        }

    }

    @Override
    public void appendConditionFields() {
        final List<DataField> conditionFieldList = this.conditionFieldList;
        if (conditionFieldList == null || conditionFieldList.size() == 0) {
            return;
        }
        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder;
        String safeTableAlias, objectName;
        UpdateMode updateMode;
        TableField tableField;
        for (DataField field : conditionFieldList) {

            if (field instanceof FieldMeta) {
                safeTableAlias = this.tableToSafeAlias.get(((FieldMeta<?>) field).tableMeta());
            } else if (field instanceof QualifiedField) {
                safeTableAlias = this.aliasToSafeAlias.get(((QualifiedField<?>) field).tableAlias());
            } else {
                safeTableAlias = this.aliasToSafeAlias.get(((DerivedField) field).tableAlias());
            }
            assert safeTableAlias != null;

            sqlBuilder.append(_Constant.SPACE_AND_SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);

            if (field instanceof TableField) {
                objectName = ((TableField) field).columnName();
                dialect.safeObjectName(objectName, sqlBuilder);
                updateMode = ((TableField) field).updateMode();
            } else {
                objectName = field.fieldName();
                dialect.safeObjectName(objectName, sqlBuilder);
                tableField = ((_Selection) field).tableField();
                assert tableField != null;
                updateMode = tableField.updateMode();
            }
            switch (updateMode) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(dialect.defaultFuncName())
                            .append(_Constant.SPACE_LEFT_PAREN)
                            .append(_Constant.SPACE)
                            .append(safeTableAlias)
                            .append(_Constant.POINT);
                    dialect.safeObjectName(objectName, sqlBuilder)
                            .append(_Constant.SPACE_RIGHT_PAREN);

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(updateMode);

            }

        }
    }

    @Override
    public BatchStmt build(List<?> paramList) {
        return Stmts.batchDml(this, paramList);
    }


    @Override
    public boolean hasVersion() {
        return this.hasVersion;
    }


}
