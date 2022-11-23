package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

abstract class DomainDmlStmtContext extends SingleTableDmlContext implements _SingleTableContext {


    private final String safeRelatedAlias;

    DomainDmlStmtContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        if (this.targetTable instanceof ParentTableMeta) {
            this.safeRelatedAlias = parser.identifier(stmt.tableAlias());
        } else {
            this.safeRelatedAlias = null;
        }

    }

    DomainDmlStmtContext(_SingleDml stmt, DomainDmlStmtContext parentContext) {
        super(stmt, parentContext);
        this.safeRelatedAlias = this.parser.identifier(_DialectUtils.parentAlias(this.tableAlias));
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
        final TableMeta<?> targetTable = this.targetTable;
        if (fieldTable == targetTable
                || (fieldTable == this.domainTable && field instanceof PrimaryFieldMeta)) {
            sqlBuilder.append(_Constant.SPACE);
            if (this.supportAlias) {
                sqlBuilder.append(this.safeTableAlias);
            } else {
                this.parser.safeObjectName(targetTable, sqlBuilder);
            }
            sqlBuilder.append(_Constant.POINT);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (targetTable instanceof ChildTableMeta
                && fieldTable == ((ChildTableMeta<?>) targetTable).parentMeta()) {
            // parent table filed
            this.parentColumnFromSubQuery(field);
        } else if (targetTable instanceof ParentTableMeta && fieldTable == this.domainTable) {
            this.childColumnFromSubQuery(field);
        } else {
            throw _Exceptions.unknownColumn(field);
        }

    }


    final void parentColumnFromSubQuery(final FieldMeta<?> parentField) {
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentField.tableMeta();
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) this.domainTable;
        assert childTable == this.targetTable && childTable.parentMeta() == parentTable;

        final String safeParentAlias = this.safeRelatedAlias;
        assert safeParentAlias != null;

        final ArmyParser parser = this.parser;

        final StringBuilder sqlBuilder = this.sqlBuilder
                //below sub query left bracket
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeParentAlias)
                .append(_Constant.POINT);

        parser.safeObjectName(parentField, sqlBuilder)
                .append(_Constant.SPACE_FROM_SPACE);

        parser.safeObjectName(parentTable, sqlBuilder);


        if (parser.aliasAfterAs) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(safeParentAlias);


        //below where clause
        sqlBuilder.append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)
                .append(safeParentAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_EQUAL_SPACE)

                .append(this.safeTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)

                .append(_Constant.SPACE_AND_SPACE)
                .append(safeParentAlias)
                .append(_Constant.POINT);

        final FieldMeta<?> discriminator = parentTable.discriminator();
        parser.safeObjectName(discriminator, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(childTable.discriminatorValue().code())
                //below sub query right paren
                .append(_Constant.SPACE_RIGHT_PAREN);


    }//parentColumnFromSubQuery


    private void childColumnFromSubQuery(final FieldMeta<?> childField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childField.tableMeta();
        assert childTable == this.domainTable && childTable.parentMeta() == this.targetTable;

        final String safeChildAlias = this.safeRelatedAlias;
        assert safeChildAlias != null;

        final ArmyParser parser = this.parser;

        final StringBuilder sqlBuilder = this.sqlBuilder;
        //below sub query left bracket
        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeChildAlias)
                .append(_Constant.POINT);

        parser.safeObjectName(childField, sqlBuilder)
                .append(_Constant.SPACE_FROM_SPACE);

        parser.safeObjectName(childTable, sqlBuilder);

        if (parser.aliasAfterAs) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(safeChildAlias)
                .append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)
                .append(safeChildAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(this.safeTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_RIGHT_PAREN);


    }//childColumnFromSubQuery


}
