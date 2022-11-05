package io.army.dialect;

import io.army.annotation.UpdateMode;
import io.army.criteria.DataField;
import io.army.criteria.QualifiedField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._SingleUpdate;
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
abstract class SingleDmlStmtContext extends DmlStmtContext implements _SingleTableContext
        , _SqlContext._SetClauseContextSpec {


    final TableMeta<?> domainTable;

    final TableMeta<?> targetTable;

    final String tableAlias;

    final String safeTableAlias;

    final boolean supportAlias;


    SingleDmlStmtContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser0 parser
            , Visible visible) {
        super(outerContext, stmt, parser, visible);

        this.domainTable = stmt.table();
        if (!(stmt instanceof _SingleUpdate) || !(this.domainTable instanceof ChildTableMeta)) {
            this.targetTable = this.domainTable;
        } else if (!(stmt instanceof _DomainUpdate) || ((_DomainUpdate) stmt).childItemPairList().size() == 0) {
            this.targetTable = ((ChildTableMeta<?>) this.domainTable).parentMeta();
        } else {
            this.targetTable = this.domainTable;
        }

        if (this.targetTable == this.domainTable) {
            this.tableAlias = stmt.tableAlias();
        } else {
            this.tableAlias = _DialectUtils.parentAlias(stmt.tableAlias());
        }
        this.safeTableAlias = parser.identifier(this.tableAlias);

        this.supportAlias = !(this instanceof _DeleteContext) || parser.singleDeleteHasTableAlias();
    }

    SingleDmlStmtContext(_SingleDml stmt, SingleDmlStmtContext parentContext) {
        super(null, stmt, parentContext.parser, parentContext.visible);

        this.domainTable = stmt.table();
        this.targetTable = this.domainTable;
        this.tableAlias = stmt.tableAlias();
        this.safeTableAlias = this.parser.identifier(this.tableAlias);

        assert this.domainTable instanceof ChildTableMeta;
        assert parentContext.domainTable == ((ChildTableMeta<?>) this.domainTable).parentMeta();

        this.supportAlias = !(this instanceof _DeleteContext) || this.parser.singleDeleteHasTableAlias();
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
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final String safeTableAlias() {
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
        if (field.tableMeta() != this.domainTable) {
            throw _Exceptions.unknownColumn(field);
        } else if ((updateMode = field.updateMode()) == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        } else if (field instanceof QualifiedField
                && !this.tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
            throw _Exceptions.unknownColumn(field);
        } else if (this.domainTable instanceof SingleTableMeta) {
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                throw _Exceptions.armyManageField(field);
            }
        }

        final StringBuilder sqlBuilder = this.sqlBuilder;
        if (this.supportAlias) {
            sqlBuilder.append(_Constant.SPACE)
                    .append(this.safeTableAlias)
                    .append(_Constant.POINT);
        }
        this.parser.safeObjectName(field, sqlBuilder);

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.parser.supportOnlyDefault()) {
                    throw _Exceptions.dontSupportOnlyDefault(this.parser.dialectMode());
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


}
