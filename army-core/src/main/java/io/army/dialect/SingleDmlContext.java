package io.army.dialect;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

abstract class SingleDmlContext extends StatementContext implements DmlContext.MultiStmtBatch
        , _SingleTableContext, DmlStmtParams {

    private final SingleDmlContext parentContext;

    final TableMeta<?> table;

    final String tableAlias;

    final String safeTableAlias;

    final boolean hasVersion;

    final boolean supportAlias;

    private final String safeParentAlias;

    private final List<Selection> selectionList;

    private final List<?> paramList;

    private final ReadAccessor accessor;

    private int paramIndex;


    SingleDmlContext(@Nullable StatementContext outerContext, _SingleDml dml, ArmyParser dialect, Visible visible) {
        super(outerContext, dialect, visible);
        this.parentContext = null;
        this.table = dml.table();
        this.tableAlias = dml.tableAlias();
        this.safeTableAlias = dialect.identifier(this.tableAlias);

        if (this.table instanceof ChildTableMeta) {
            this.safeParentAlias = dialect.identifier(_DialectUtils.parentAlias(this.tableAlias));
        } else {
            this.safeParentAlias = null;
        }
        this.hasVersion = _DialectUtils.hasOptimistic(dml.wherePredicateList());
        this.supportAlias = !(this instanceof DeleteContext) || dialect.singleDeleteHasTableAlias();
        this.selectionList = null;

        if (dml instanceof _BatchDml) {
            this.paramList = ((_BatchDml) dml).paramList();
            if (outerContext instanceof _MultiStatementContext) {
                this.accessor = ObjectAccessorFactory.readOnlyFromInstance(this.paramList.get(0));
                this.paramIndex = 0;
            } else {
                this.accessor = null;
            }
        } else {
            this.paramList = null;
            this.accessor = null;
        }

    }


    SingleDmlContext(_SingleDml dml, SingleDmlContext parentContext) {
        super(null, parentContext.parser, parentContext.visible);
        this.parentContext = parentContext;

        this.table = dml.table();
        this.tableAlias = dml.tableAlias();
        this.safeTableAlias = this.parser.identifier(this.tableAlias);

        if (this.table instanceof ChildTableMeta) {
            this.safeParentAlias = this.parser.identifier(_DialectUtils.parentAlias(this.tableAlias));
        } else {
            this.safeParentAlias = null;
        }
        this.hasVersion = _DialectUtils.hasOptimistic(dml.wherePredicateList());
        this.supportAlias = !(this instanceof DeleteContext) || parser.singleDeleteHasTableAlias();
        this.selectionList = null;

        if (dml instanceof _BatchDml) {
            this.paramList = ((_BatchDml) dml).paramList();
        } else {
            this.paramList = null;
        }
        this.accessor = null;
    }

    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final DmlContext parentContext() {
        return this.parentContext;
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
                this.parser.safeObjectName(table, sqlBuilder);
            }
            sqlBuilder.append(_Constant.POINT);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (table instanceof ChildTableMeta && fieldTable == ((ChildTableMeta<?>) table).parentMeta()) {
            // parent table filed
            this.parentColumnFromSubQuery(field);
        } else {
            throw _Exceptions.unknownColumn(field);
        }

    }


    @Override
    public final void nextElement() {
        final List<?> paramList = this.paramList;
        if (paramList == null) {
            throw _Exceptions.independentDmlDontSupportNamedValue();
        }
        final int paramSize, paramIndex;
        paramSize = paramList.size();
        paramIndex = this.paramIndex;
        assert paramIndex >= 0 && paramIndex < (paramSize - 1);
        this.paramIndex++;
    }

    @Override
    public final int currentIndex() {
        return this.paramIndex;
    }

    @Override
    public final Stmt build() {
        if (this.accessor != null) {
            //now,multi-multi statement
            throw new UnsupportedOperationException();
        }
        final List<?> paramList = this.paramList;
        final Stmt stmt;
        if (paramList != null) {
            stmt = Stmts.batchDml(this, paramList);
        } else if (this.hasNamedParam()) {
            throw _Exceptions.namedParamInNonBatch();
        } else {
            stmt = Stmts.dml(this);
        }
        return stmt;
    }


    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }

    @Override
    public final List<Selection> selectionList() {
        return this.selectionList;
    }


    @Override
    final Object currentRowNamedValue(String name) {
        final List<?> paramList = this.paramList;
        final ReadAccessor accessor = this.accessor;
        if (paramList == null || accessor == null) {
            throw _Exceptions.independentDmlDontSupportNamedValue();
        }
        final int paramSize, paramIndex;
        paramSize = paramList.size();
        paramIndex = this.paramIndex;
        assert paramIndex >= 0 && paramIndex < paramSize;
        return accessor.get(paramList.get(paramIndex), name);
    }

    final void parentColumnFromSubQuery(final FieldMeta<?> parentField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) this.table;
        final String safeParentAlias = this.safeParentAlias;
        assert safeParentAlias != null;

        final ArmyParser dialect = this.parser;
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentField.tableMeta();
        final StringBuilder sqlBuilder = this.sqlBuilder
                //below sub query left bracket
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeParentAlias)
                .append(_Constant.POINT);

        dialect.safeObjectName(parentField, sqlBuilder)
                .append(_Constant.SPACE_FROM)
                .append(_Constant.SPACE);

        dialect.safeObjectName(parentTable, sqlBuilder);


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
            dialect.safeObjectName(childTable, sqlBuilder);
        }
        sqlBuilder.append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_AND_SPACE)
                //below parent table discriminator
                .append(safeParentAlias)
                .append(_Constant.POINT);

        dialect.safeObjectName(discriminator, sqlBuilder)

                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(childTable.discriminatorValue().code())
                //below sub query right paren
                .append(_Constant.SPACE_RIGHT_PAREN);


    }


}
