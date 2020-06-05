package io.army.dialect;

import io.army.criteria.PrimaryValueEqualPredicate;
import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

abstract class AbstractStandardDomainContext extends AbstractTableContextSQLContext {

    final TableMeta<?> primaryTable;

    final TableMeta<?> relationTable;

    final String primaryAlias;

    boolean existsClauseContext = false;

    AbstractStandardDomainContext(Dialect dialect, Visible visible, TableContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable) {
        super(dialect, visible, tableContext);
        this.primaryTable = primaryTable;
        this.relationTable = relationTable;
        this.primaryAlias = tableContext.tableAliasMap.get(this.primaryTable);
        Assert.hasText(this.primaryAlias, "tableContext error.");
    }

    final void appendDomainFieldPredicate(SpecialPredicate predicate) {
        if (predicate instanceof PrimaryValueEqualPredicate) {
            predicate.appendPredicate(this);
        } else if (!predicate.containsSubQuery()
                && predicate.containsFieldCount(this.relationTable) > 1) {
            this.existsClauseContext = true;
            doReplaceFieldPairWithExistsClause(predicate);
            this.existsClauseContext = false;
        } else {
            predicate.appendPredicate(this);
        }
    }


    final void appendDomainField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (!this.primaryAlias.equals(tableAlias)) {
            throw DialectUtils.createNoLogicalTableException(tableAlias);
        }
        this.appendDomainField(fieldMeta);
    }

    final void appendDomainField(FieldMeta<?, ?> fieldMeta) {
        TableMeta<?> tableOfField = fieldMeta.tableMeta();
        if (tableOfField == this.primaryTable) {
            doAppendFiled(this.primaryAlias, fieldMeta);
        } else if (tableOfField == this.relationTable) {
            if (fieldMeta instanceof PrimaryFieldMeta) {
                doAppendFiled(this.primaryAlias, fieldMeta);
            } else if (this.existsClauseContext) {
                doAppendFiled(obtainRelationTableAlias(), fieldMeta);
            } else {
                doReplaceRelationFieldAsScalarSubQuery(fieldMeta);
            }
        } else {
            throw DialectUtils.createUnKnownFieldException(fieldMeta);
        }
    }

    private void doReplaceRelationFieldAsScalarSubQuery(FieldMeta<?, ?> relationField) {
        Assert.isTrue(relationField.tableMeta() == this.relationTable, "");

        final TableMeta<?> relationTable = this.relationTable;
        // replace parent field as sub query.
        final Dialect dialect = this.dialect;
        final String safeRelationAlias = dialect.quoteIfNeed(obtainRelationTableAlias());

        StringBuilder builder = this.sqlBuilder.append(" ( SELECT ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(relationField.fieldName()))
                .append(" FROM");
        appendTable(relationTable);
        if (dialect.tableAliasAfterAs()) {
            builder.append(" AS");
        }
        builder.append(" ")
                .append(safeRelationAlias)
                .append(" WHERE ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(relationTable.id().fieldName()))
                .append(" = ")
                .append(dialect.quoteIfNeed(this.primaryAlias))
                .append(".")
                .append(dialect.quoteIfNeed(this.primaryTable.id().fieldName()))
                .append(" )")
        ;
    }

    private void doReplaceFieldPairWithExistsClause(SpecialPredicate predicate) {

        final Dialect dialect = this.dialect;
        final String safeRelationAlias = dialect.quoteIfNeed(obtainRelationTableAlias());

        final String safeRelationId = dialect.quoteIfNeed(this.relationTable.id().fieldName());
        final StringBuilder builder = this.sqlBuilder
                .append(" EXISTS ( SELECT ")
                .append(safeRelationAlias)
                .append(".")
                .append(safeRelationId)
                .append(" FROM");
        appendTable(this.relationTable);
        if (dialect.tableAliasAfterAs()) {
            builder.append(" AS");
        }
        builder.append(" ")
                .append(safeRelationAlias)
                .append(" WHERE ")
                .append(safeRelationAlias)
                .append(".")
                .append(safeRelationId)
                .append(" = ")
                .append(dialect.quoteIfNeed(this.primaryAlias))
                .append(".")
                .append(dialect.quoteIfNeed(this.primaryTable.id().fieldName()))
                .append(" ADN");
        // append special predicate
        predicate.appendPredicate(this);

        builder.append(" )");
    }

    private String obtainRelationTableAlias() {
        String relationTableAlias = this.primaryAlias;
        if (this.relationTable instanceof ChildTableMeta) {
            relationTableAlias += "_p";
        } else {
            relationTableAlias += "_c";
        }
        return relationTableAlias;
    }

}
