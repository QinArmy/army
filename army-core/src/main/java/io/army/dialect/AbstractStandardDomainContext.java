package io.army.dialect;

import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.util.Assert;

abstract class AbstractStandardDomainContext extends AbstractTableContextSQLContext implements SingleTableDMLContext {

    final TableMeta<?> primaryTable;

    final TableMeta<?> relationTable;

    final String primaryAlias;

    final String relationAlias;

    private boolean existsClauseContext = false;

    private boolean relationSubQueryContext = false;

    AbstractStandardDomainContext(Dialect dialect, Visible visible, TablesContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable) {
        super(dialect, visible, tableContext);
        this.primaryTable = primaryTable;
        this.relationTable = relationTable;
        this.primaryAlias = tableContext.tableAliasMap.get(this.primaryTable);
        Assert.hasText(this.primaryAlias, "tableContext error.");

        this.relationAlias = obtainRelationTableAlias(this.primaryAlias, this.relationTable);

    }

    @Override
    public final String relationAlias() {
        return this.relationAlias;
    }

    @Override
    protected final String parseTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        if (tableMeta == this.primaryTable || tableMeta == this.relationTable) {
            return this.primaryRouteSuffix();
        }
        throw _DialectUtils.createUnKnownTableException(tableMeta);
    }



    final void appendDomainTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        if (tableMeta == this.relationTable) {
            if (this.existsClauseContext || this.relationSubQueryContext) {
                doAppendTable(tableMeta, tableAlias);
            } else {
                throw _DialectUtils.createUnKnownTableException(tableMeta);
            }
        } else {
            super.appendTable(tableMeta, tableAlias);
        }
    }

    @Override
    protected final void validateTableAndAlias(TableMeta<?> tableMeta, String tableAlias) {
        if (this.existsClauseContext || this.relationSubQueryContext) {
            if (!tableAlias.equals(this.relationAlias)) {
                throw new IllegalArgumentException(String.format(
                        "Domain update relation TableMeta[%s] and tableAlias[%s] not match."
                        , tableMeta, tableAlias));
            }
        } else {
            super.validateTableAndAlias(tableMeta, tableAlias);
        }
    }

    final void appendDomainField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (!this.primaryAlias.equals(tableAlias)) {
            throw _DialectUtils.createNoLogicalTableException(tableAlias);
        }
        this.appendDomainField(fieldMeta);
    }

    @Override
    public SimpleStmt build() {
        return null;
    }

    final void appendDomainField(FieldMeta<?, ?> fieldMeta) {
        TableMeta<?> tableOfField = fieldMeta.tableMeta();
        if (tableOfField == this.primaryTable) {
            doAppendField(this.primaryAlias, fieldMeta);
        } else if (tableOfField == this.relationTable) {
            if (fieldMeta instanceof PrimaryFieldMeta) {
                doAppendField(this.primaryAlias, fieldMeta);
            } else if (this.existsClauseContext) {
                doAppendField(this.relationAlias, fieldMeta);
            } else {
                this.relationSubQueryContext = true;
                doReplaceRelationFieldAsScalarSubQuery(fieldMeta);
                this.relationSubQueryContext = false;
            }
        } else {
            throw _DialectUtils.createUnKnownFieldException(fieldMeta);
        }
    }


    private void doReplaceRelationFieldAsScalarSubQuery(FieldMeta<?, ?> relationField) {
        Assert.isTrue(relationField.tableMeta() == this.relationTable, "");

        final TableMeta<?> relationTable = this.relationTable;
        // replace parent field as sub query.
        final Dialect dialect = this.dialect;
        final String safeRelationAlias = dialect.quoteIfNeed(this.relationAlias);

        StringBuilder builder = this.sqlBuilder.append(" ( SELECT ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(relationField.columnName()))
                .append(" FROM");
        appendTable(relationTable, this.relationAlias);
        if (dialect.tableAliasAfterAs()) {
            builder.append(" AS");
        }
        builder.append(" ")
                .append(safeRelationAlias)
                .append(" WHERE ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(relationTable.id().columnName()))
                .append(" = ")
                .append(dialect.quoteIfNeed(this.primaryAlias))
                .append(".")
                .append(dialect.quoteIfNeed(this.primaryTable.id().columnName()))
                .append(" )")
        ;
    }



    private static String obtainRelationTableAlias(String primaryAlias, TableMeta<?> relationTable) {
        String relationTableAlias = primaryAlias;
        if (relationTable instanceof ChildTableMeta) {
            relationTableAlias += "_c";
        } else {
            relationTableAlias += "_p";
        }
        return relationTableAlias;
    }

}
