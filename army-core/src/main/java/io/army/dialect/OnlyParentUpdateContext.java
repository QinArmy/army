package io.army.dialect;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.BatchStmt;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class OnlyParentUpdateContext extends StatementContext implements DmlContext, _SingleUpdateContext
        , DmlStmtParams {

    static OnlyParentUpdateContext create(_SingleUpdate update, ArmyParser dialect, Visible visible) {
        return new OnlyParentUpdateContext(update, dialect, visible);
    }

    private final ChildTableMeta<?> table;

    private final String tableAlias;

    private final String safeTableAlias;

    private final boolean hasVersion;

    private final List<Selection> selectionList;

    private List<TableField> conditionFieldList;


    private OnlyParentUpdateContext(_SingleUpdate update, ArmyParser dialect, Visible visible) {
        super(dialect, visible);

        this.table = ((ChildTableMeta<?>) update.table());
        this.tableAlias = update.tableAlias();
        this.safeTableAlias = dialect.identifier(this.tableAlias);
        this.hasVersion = _DialectUtils.hasOptimistic(update.predicateList());

        this.selectionList = Collections.emptyList();

    }


    @Override
    public ChildTableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public String safeTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (!this.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();

        final ParentTableMeta<?> parentTable = this.table.parentMeta();
        if (fieldTable != parentTable) {
            String m = String.format("Single table syntax don't support %s,its' child filed.", field);
            throw new CriteriaException(m);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE)
                .append(this.safeTableAlias)
                .append(_Constant.POINT);

        this.parser.safeObjectName(field, sqlBuilder);

    }

    @Override
    public void appendConditionFields() {
        _DialectUtils.appendConditionFields(this, this.conditionFieldList);
    }

    @Override
    public void appendSetLeftItem(final DataField dataField) {
        if (!(dataField instanceof TableField)) {
            throw _Exceptions.immutableField(dataField);
        }
        final TableField field = (TableField) dataField;
        if (field.tableMeta() != this.table.parentMeta()) { // must be parent field
            throw _Exceptions.unknownColumn(field);
        }
        final UpdateMode updateMode = field.updateMode();
        if (updateMode == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        }
        if (field instanceof QualifiedField
                && !this.tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
            throw _Exceptions.unknownColumn(field);
        }
        final String fieldName = field.fieldName();
        if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
            throw _Exceptions.armyManageField(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE)
                .append(this.safeTableAlias)
                .append(_Constant.POINT);
        this.parser.safeObjectName(field, sqlBuilder);

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.parser.supportOnlyDefault()) {
                    throw _Exceptions.dontSupportOnlyDefault(this.parser.dialectMode());
                }
                List<TableField> conditionFieldList = this.conditionFieldList;
                if (conditionFieldList == null) {
                    conditionFieldList = new ArrayList<>();
                    this.conditionFieldList = conditionFieldList;
                }
                conditionFieldList.add(field);
            }
            break;
            default:
                //no-op
        }
    }

    @Override
    public void appendParentField(FieldMeta<?> parentField) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasVersion() {
        return this.hasVersion;
    }

    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public BatchStmt build(List<?> paramList) {
        return Stmts.batchDml(this, paramList);
    }

    @Override
    public SimpleStmt build() {
        return Stmts.dml(this);
    }


}
