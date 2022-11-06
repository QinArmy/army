package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

abstract class DomainDmlStmtContext extends SingleTableDmlContext implements _SingleTableContext {


    private final String safeParentAlias;

    DomainDmlStmtContext(@Nullable StatementContext outerContext, _SingleDml stmt
            , ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        if (this.domainTable instanceof ChildTableMeta) {
            this.safeParentAlias = parser.identifier(_DialectUtils.parentAlias(this.tableAlias));
        } else {
            this.safeParentAlias = null;
        }

    }

    DomainDmlStmtContext(_SingleDml stmt, DomainDmlStmtContext parentContext) {
        super(stmt, parentContext);
        this.safeParentAlias = null;//TODO
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
        final TableMeta<?> table = this.domainTable;
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


    final void parentColumnFromSubQuery(final FieldMeta<?> parentField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) this.domainTable;
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


        if (dialect.singleDmlAliasAfterAs) {
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
