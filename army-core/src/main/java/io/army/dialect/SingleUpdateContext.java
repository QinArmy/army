package io.army.dialect;

import io.army.annotation.UpdateMode;
import io.army.criteria.DataField;
import io.army.criteria.QualifiedField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.stmt.DmlStmtParams;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

final class SingleUpdateContext extends SingleDmlContext implements _SingleUpdateContext, DmlStmtParams {

    static SingleUpdateContext create(_SingleUpdate stmt, ArmyDialect dialect, Visible visible) {
        return new SingleUpdateContext(stmt, dialect, visible);
    }

    static SingleUpdateContext create(_SingleUpdate stmt, StmtContext outerContext) {
        return new SingleUpdateContext(stmt, outerContext);
    }


    private List<TableField> conditionFieldList;

    private SingleUpdateContext(_SingleUpdate stmt, ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);
    }

    private SingleUpdateContext(_SingleUpdate stmt, StmtContext outerContext) {
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
        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder
                .append(_Constant.SPACE)
                .append(this.safeTableAlias);
        this.dialect.safeObjectName(field.columnName(), sqlBuilder);

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.dialect.supportOnlyDefault()) {
                    throw _Exceptions.dontSupportOnlyDefault(this.dialect.dialectMode());
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
        final List<TableField> conditionFieldList = this.conditionFieldList;
        if (conditionFieldList == null || conditionFieldList.size() == 0) {
            return;
        }
        final String safeTableAlias = this.safeTableAlias;
        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        for (TableField field : conditionFieldList) {
            sqlBuilder.append(_Constant.SPACE_AND_SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);

            dialect.safeObjectName(field.columnName(), sqlBuilder);

            switch (field.updateMode()) {
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
                    dialect.safeObjectName(field.columnName(), sqlBuilder)
                            .append(_Constant.SPACE_RIGHT_PAREN);

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());

            }

        }
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
