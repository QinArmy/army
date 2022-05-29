package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
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

import java.util.List;

abstract class SingleDmlContext extends StmtContext implements _DmlContext, _SingleTableContext, DmlStmtParams {

    final TableMeta<?> table;

    final String tableAlias;

    final String safeTableAlias;

    final boolean hasVersion;


    final boolean supportAlias;

    private final String safeParentAlias;


    SingleDmlContext(_SingleDml dml, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);

        this.table = dml.table();
        this.tableAlias = dml.tableAlias();
        this.safeTableAlias = dialect.identifier(this.tableAlias);

        if (this.table instanceof ChildTableMeta) {
            this.safeParentAlias = dialect.identifier(_DialectUtils.parentAlias(this.tableAlias));
        } else {
            this.safeParentAlias = null;
        }
        this.hasVersion = _DmlUtils.hasOptimistic(dml.predicateList());
        this.supportAlias = !(this instanceof _DeleteContext) || dialect.singleDeleteHasTableAlias();

    }

    SingleDmlContext(_SingleDml dml, StmtContext outerContext) {
        super(outerContext);

        this.table = dml.table();
        this.tableAlias = dml.tableAlias();
        this.safeTableAlias = this.dialect.identifier(this.tableAlias);

        if (this.table instanceof ChildTableMeta) {
            this.safeParentAlias = this.dialect.identifier(_DialectUtils.parentAlias(this.tableAlias));
        } else {
            this.safeParentAlias = null;
        }
        this.hasVersion = _DmlUtils.hasOptimistic(dml.predicateList());
        this.supportAlias = !(this instanceof _DeleteContext) || dialect.singleDeleteHasTableAlias();
    }

    @Override
    public final TableMeta<?> table() {
        return this.table;
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
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (!this.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {

        final TableMeta<?> fieldTable = field.tableMeta();
        final StringBuilder sqlBuilder = this.sqlBuilder;
        final TableMeta<?> table = this.table;
        if (fieldTable == table) {
            sqlBuilder.append(_Constant.SPACE);
            if (this.supportAlias) {
                sqlBuilder.append(this.safeTableAlias);
            } else {
                this.dialect.safeObjectName(table.tableName(), sqlBuilder);
            }
            sqlBuilder.append(_Constant.POINT);
            this.dialect.safeObjectName(field.columnName(), sqlBuilder);
        } else if (table instanceof ChildTableMeta && fieldTable == ((ChildTableMeta<?>) table).parentMeta()) {
            // parent table filed
            this.parentColumnFromSubQuery(field);
        } else {
            throw _Exceptions.unknownColumn(field);
        }

    }


    @Override
    public final SimpleStmt build() {
        if (this.hasNamedParam) {
            throw _Exceptions.namedParamInNonBatch();
        }
        return Stmts.dml(this);
    }

    @Override
    public final BatchStmt build(List<?> paramList) {
        if (!this.hasNamedParam) {
            throw _Exceptions.noNamedParamInBatch();
        }
        return Stmts.batchDml(this, paramList);
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }


    final void parentColumnFromSubQuery(final FieldMeta<?> parentField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) this.table;
        final String safeParentAlias = this.safeParentAlias;
        assert safeParentAlias != null;

        final ArmyDialect dialect = this.dialect;
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentField.tableMeta();
        final StringBuilder sqlBuilder = this.sqlBuilder;


        sqlBuilder
                //below sub query left bracket
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeParentAlias)
                .append(_Constant.POINT);

        dialect.safeObjectName(parentField.columnName(), sqlBuilder)
                .append(_Constant.SPACE_FROM)
                .append(_Constant.SPACE);

        dialect.safeObjectName(parentTable.tableName(), sqlBuilder);


        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(safeParentAlias);

        final FieldMeta<?> discriminator = parentTable.discriminator();

        //below where clause
        sqlBuilder.append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)

                .append(safeParentAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)

                .append(_Constant.SPACE_EQUAL_SPACE);

        //below child table id
        if (this.supportAlias) {
            sqlBuilder.append(this.safeTableAlias);
        } else {
            dialect.safeObjectName(childTable.tableName(), sqlBuilder);
        }
        sqlBuilder.append(_Constant.POINT)
                .append(_MetaBridge.ID)

                .append(_Constant.SPACE_AND_SPACE)

                //below parent table discriminator
                .append(safeParentAlias)
                .append(_Constant.POINT);

        dialect.safeObjectName(discriminator.columnName(), sqlBuilder)

                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(childTable.discriminatorValue())
                //below sub query right paren
                .append(_Constant.SPACE_RIGHT_PAREN);


    }


}
