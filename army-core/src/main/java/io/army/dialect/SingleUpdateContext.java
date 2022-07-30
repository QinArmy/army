package io.army.dialect;

import io.army.annotation.UpdateMode;
import io.army.criteria.DataField;
import io.army.criteria.QualifiedField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.DmlStmtParams;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

final class SingleUpdateContext extends SingleDmlContext implements _SingleUpdateContext, DmlStmtParams {

    static SingleUpdateContext create(_SingleUpdate stmt, ArmyParser dialect, Visible visible) {
        return new SingleUpdateContext(stmt, dialect, visible);
    }

    static SingleUpdateContext create(_SingleUpdate stmt, StatementContext outerContext) {
        return new SingleUpdateContext(stmt, outerContext);
    }


    private List<TableField> conditionFieldList;

    private SingleUpdateContext(_SingleUpdate stmt, ArmyParser dialect, Visible visible) {
        super(stmt, dialect, visible);
    }

    private SingleUpdateContext(_SingleUpdate stmt, StatementContext outerContext) {
        super(stmt, outerContext);
    }


    @Override
    public void appendSetLeftItem(final DataField dataField) {
        if (!(dataField instanceof TableField)) {
            throw _Exceptions.immutableField(dataField);
        }
        final TableField field = (TableField) dataField;
        if (field.tableMeta() != this.table) {
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
    public void appendConditionFields() {
       _DialectUtils.appendConditionFields(this, this.conditionFieldList);
    }

    @Override
    public void appendParentField(final FieldMeta<?> parentField) {
        if (this.table instanceof ChildTableMeta
                && parentField.tableMeta() == ((ChildTableMeta<?>) this.table).parentMeta()) {
            this.parentColumnFromSubQuery(parentField);
        } else {
            throw _Exceptions.unknownColumn(parentField);
        }
    }


}
