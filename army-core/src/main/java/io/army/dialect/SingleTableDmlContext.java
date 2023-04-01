package io.army.dialect;

import io.army.annotation.UpdateMode;
import io.army.criteria.DataField;
import io.army.criteria.QualifiedField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link  DomainDmlStmtContext}</li>
 *         <li>{@link  SingleDmlContext}</li>
 *         <li>{@link  SingleJoinableDmlContext}</li>
 *     </ul>
 * </p>
 */
abstract class SingleTableDmlContext extends NarrowDmlStmtContext implements _SingleTableContext
        , _DmlContext._SetClauseContextSpec {


    final TableMeta<?> domainTable;

    final TableMeta<?> targetTable;

    final String tableAlias;

    final String safeTableAlias;

    final String safeTargetTableName;


    /**
     * <p>
     * For {@link SingleTableMeta}
     * </p>
     */
    SingleTableDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser parser,
                          Visible visible) {
        super(outerContext, stmt, parser, visible);

        this.domainTable = stmt.table();
        if (this.domainTable instanceof ChildTableMeta
                && (stmt instanceof _DomainUpdate || stmt instanceof _DomainDelete)) {
            this.targetTable = ((ChildTableMeta<?>) this.domainTable).parentMeta();
            this.tableAlias = _DialectUtils.parentAlias(stmt.tableAlias());
        } else {
            this.targetTable = this.domainTable;
            this.tableAlias = stmt.tableAlias();
        }
        this.safeTableAlias = parser.identifier(this.tableAlias);

        if ((stmt instanceof _Update && parser.supportSingleUpdateAlias)
                || (stmt instanceof _Delete && parser.supportSingleDeleteAlias)) {
            this.safeTargetTableName = null;
        } else {
            this.safeTargetTableName = parser.safeObjectName(this.targetTable);
        }

    }


    /**
     * <p>
     * For {@link  ChildTableMeta}
     * </p>
     *
     * @see #decideParentContext(SingleTableDmlContext)
     */
    SingleTableDmlContext(_SingleDml stmt, SingleTableDmlContext parentContext) {
        super(decideParentContext(parentContext), stmt, parentContext.parser, parentContext.visible);

        this.domainTable = stmt.table();
        this.targetTable = this.domainTable;
        this.tableAlias = stmt.tableAlias();
        this.safeTableAlias = this.parser.identifier(this.tableAlias);

        assert this.domainTable instanceof ChildTableMeta;
        assert parentContext.targetTable == ((ChildTableMeta<?>) this.domainTable).parentMeta()
                && parentContext.domainTable == this.domainTable;

        if ((stmt instanceof _Update && parser.supportSingleUpdateAlias)
                || (stmt instanceof _Delete && parser.supportSingleDeleteAlias)) {
            this.safeTargetTableName = null;
        } else {
            this.safeTargetTableName = parser.safeObjectName(this.targetTable);
        }
    }

    @Override
    public final TableMeta<?> domainTable() {
        return this.domainTable;
    }

    @Override
    public final TableMeta<?> targetTable() {
        return this.targetTable;
    }

    @Override
    public final String targetTableAlias() {
        return this.tableAlias;
    }

    @Override
    public final String safeTargetTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public final void appendSetLeftItem(final DataField dataField) {
        assert this instanceof _UpdateContext;
        if (!(dataField instanceof TableField)) {
            throw _Exceptions.immutableField(dataField);
        }
        final TableField field = (TableField) dataField;
        final UpdateMode updateMode;
        if (field.tableMeta() != this.targetTable) {
            throw _Exceptions.unknownColumn(field);
        } else if ((updateMode = field.updateMode()) == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        } else if (field instanceof QualifiedField
                && !this.tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
            throw _Exceptions.unknownColumn(field);
        } else if (this.targetTable instanceof SingleTableMeta) {
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                throw _Exceptions.armyManageField(field);
            }
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE);
        if (this.parser.setClauseTableAlias) {
            if (this.safeTargetTableName == null) {
                sqlBuilder.append(this.safeTableAlias);
            } else {
                sqlBuilder.append(this.safeTargetTableName);
            }
            sqlBuilder.append(_Constant.POINT);
        }
        this.parser.safeObjectName(field, sqlBuilder);

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.parser.supportOnlyDefault) {
                    throw _Exceptions.dontSupportOnlyDefault(this.parser.dialect);
                }
                this.onAddConditionField(field);
            }
            break;
            default:
                //no-op
        }
    }


    void onAddConditionField(TableField field) {
        throw new UnsupportedOperationException();
    }


    @Nullable
    private static SingleTableDmlContext decideParentContext(final SingleTableDmlContext parentContext) {
        final SingleTableDmlContext actual;
        if (parentContext.parser.childUpdateMode == _ChildUpdateMode.CTE) {
            actual = parentContext; //same StringBuilder instance
        } else {
            actual = null;
        }
        return actual;
    }


}
