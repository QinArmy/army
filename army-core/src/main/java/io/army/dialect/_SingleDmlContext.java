package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

public abstract class _SingleDmlContext extends StmtContext implements _DmlContext, _Block {

    protected final SingleTableMeta<?> table;

    protected final String tableAlias;

    protected final String safeTableAlias;

    protected final List<_Predicate> predicateList;

    protected final boolean multiTableUpdateChild;


    protected _SingleDmlContext(_SingleDml dml, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);
        final TableMeta<?> table = dml.table();
        final String tableAlias = dml.tableAlias();
        if (table instanceof ChildTableMeta) {
            this.table = ((ChildTableMeta<?>) table).parentMeta();
            this.tableAlias = _DialectUtils.parentAlias(tableAlias);
            this.safeTableAlias = this.tableAlias;
        } else {
            this.table = (SingleTableMeta<?>) table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = dialect.quoteIfNeed(tableAlias);
        }
        this.predicateList = dml.predicateList();
        this.multiTableUpdateChild = dialect.multiTableUpdateChild();
    }


    @Override
    public final SingleTableMeta<?> table() {
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
    public final List<_Predicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        final ChildBlock childBlock = childBlock();
        if (childBlock == null) {
            if (!this.tableAlias.equals(tableAlias)) {
                throw _Exceptions.unknownColumn(tableAlias, field);
            }
        } else if (!childBlock.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        final TableMeta<?> belongOf = field.tableMeta();
        final ChildBlock childBlock;
        if (belongOf == this.table) {// field is parent table column.
            this.sqlBuilder
                    .append(Constant.SPACE)
                    .append(this.safeTableAlias)
                    .append(Constant.POINT);

            this.dialect.safeObjectName(field.columnName(), this.sqlBuilder);

        } else if ((childBlock = childBlock()) == null || belongOf != childBlock.table) {
            throw _Exceptions.unknownColumn(null, field);
        } else if (this.multiTableUpdateChild) {// parent and child table in multi-table update statement,eg: MySQL multi-table update
            this.sqlBuilder
                    .append(Constant.SPACE)
                    .append(childBlock.safeTableAlias)
                    .append(Constant.POINT);

            this.dialect.safeObjectName(field.columnName(), sqlBuilder);

        } else {
            //non multi-table update,so convert child table filed as sub query.
            childColumnFromSubQuery(this, childBlock, field);
        }

    }

    @Override
    public final SimpleStmt build() {
        return Stmts.dml(this.sqlBuilder.toString(), this.paramList, _DmlUtils.hasOptimistic(this.predicateList));
    }

    @Nullable
    public abstract ChildBlock childBlock();


    protected static class ChildBlock implements _Block {

        protected final ChildTableMeta<?> table;

        protected final String tableAlias;

        protected final String safeTableAlias;

        protected final _SingleDmlContext parentContext;

        protected ChildBlock(ChildTableMeta<?> table, final String tableAlias, _SingleDmlContext parentContext) {
            this.table = table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = parentContext.dialect.quoteIfNeed(tableAlias);
            this.parentContext = parentContext;
        }


        @Override
        public final ChildTableMeta<?> table() {
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
            final TableMeta<?> belongOf = field.tableMeta();
            final StringBuilder sqlBuilder = this.parentContext.sqlBuilder;
            final _Dialect dialect = this.parentContext.dialect;

            if (belongOf == this.table) {// field is child table column.
                sqlBuilder
                        .append(Constant.SPACE)
                        .append(this.safeTableAlias)
                        .append(Constant.POINT);
                dialect.safeObjectName(field.columnName(), sqlBuilder);
            } else if (belongOf == this.parentContext.table) {// field is parent table column.
                if (this.parentContext.multiTableUpdateChild) {// parent and child table in multi-table update statement,eg: MySQL multi-table update
                    sqlBuilder
                            .append(Constant.SPACE)
                            .append(this.parentContext.safeTableAlias)
                            .append(Constant.POINT);
                    dialect.safeObjectName(field.columnName(), sqlBuilder);
                } else {
                    //non multi-table update,so convert parent filed as sub query.
                    parentColumnFromSubQuery(this, field);
                }
            } else {
                throw _Exceptions.unknownColumn(null, field);
            }
        }


        @Override
        public final _Dialect dialect() {
            return this.parentContext.dialect;
        }

        @Override
        public final StringBuilder sqlBuilder() {
            //for dml statement,parent update and child update must in same statement.
            return this.parentContext.sqlBuilder;
        }

        @Override
        public final void appendParam(ParamValue paramValue) {
            //for dml statement,parent update and child update must in same statement.
            this.parentContext.appendParam(paramValue);
        }

        @Override
        public final Visible visible() {
            return this.parentContext.visible;
        }

        @Override
        public String toString() {
            return this.parentContext instanceof _DomainUpdateContext
                    ? "single update child context"
                    : "single delete child context";
        }


    } // ChildBlock


    private static void childColumnFromSubQuery(final _Block parentContext, final _Block childBlock
            , final FieldMeta<?> childField) {

        if (!(parentContext.table() instanceof ParentTableMeta)) {
            throw new IllegalArgumentException("parentContext error");
        }

        final String childSafeTableAlias = Constant.FORBID_ALIAS + "temp_c_of_" + childBlock.tableAlias();
        final _Dialect dialect = parentContext.dialect();
        // convert for validate childBlock
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childBlock.table();

        final StringBuilder sqlBuilder = parentContext.sqlBuilder();
        sqlBuilder
                //below sub query left bracket
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET)

                .append(Constant.SPACE)
                .append(Constant.SELECT)
                .append(Constant.SPACE)

                .append(childSafeTableAlias)
                .append(Constant.POINT);

        dialect.safeObjectName(childField.columnName(), sqlBuilder)
                .append(Constant.SPACE)
                .append(Constant.SPACE_FROM)
                .append(Constant.SPACE);

        dialect.safeObjectName(childTable.tableName(), sqlBuilder);

        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.SPACE_AS_SPACE);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(childSafeTableAlias);

        sqlBuilder.append(Constant.SPACE)
                .append(Constant.SPACE_WHERE)
                .append(Constant.SPACE)

                .append(childSafeTableAlias)
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                .append(Constant.SPACE_EQUAL_SPACE)

                .append(parentContext.safeTableAlias())
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                //below sub query right bracket
                .append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }


    private static void parentColumnFromSubQuery(final _Block childContext, final FieldMeta<?> parentField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childContext.table();
        final String parentSafeTable = Constant.FORBID_ALIAS + "temp_p_of_" + childContext.tableAlias();

        final _Dialect dialect = childContext.dialect();
        final ParentTableMeta<?> parentTable = childTable.parentMeta();
        final StringBuilder sqlBuilder = childContext.sqlBuilder();

        sqlBuilder
                //below sub query left bracket
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET)

                .append(Constant.SPACE)
                .append(Constant.SELECT)
                .append(Constant.SPACE)

                //below target parent column
                .append(parentSafeTable)
                .append(Constant.POINT);

        dialect.safeObjectName(parentField.columnName(), sqlBuilder)

                .append(Constant.SPACE)
                .append(Constant.SPACE_FROM)
                .append(Constant.SPACE);

        dialect.safeObjectName(parentTable.tableName(), sqlBuilder);


        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.SPACE_AS_SPACE);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(parentSafeTable);

        final FieldMeta<?> discriminator = parentTable.discriminator();

        sqlBuilder.append(Constant.SPACE)
                //below where clause
                .append(Constant.SPACE_WHERE)
                .append(Constant.SPACE)

                .append(parentSafeTable)
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                .append(Constant.SPACE_EQUAL_SPACE)

                //below child table id
                .append(childContext.safeTableAlias())
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                .append(Constant.SPACE_AND)
                .append(Constant.SPACE)

                //below parent table discriminator
                .append(parentSafeTable)
                .append(Constant.POINT);

        dialect.safeObjectName(discriminator.columnName(), sqlBuilder)

                .append(Constant.SPACE_EQUAL_SPACE)

                //below child table discriminator literal
                .append(dialect.literal(discriminator, childTable.discriminatorValue()))

                //below sub query right bracket
                .append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);


    }


}
