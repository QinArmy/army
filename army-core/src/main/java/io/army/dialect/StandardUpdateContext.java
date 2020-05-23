package io.army.dialect;

import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.wrapper.SimpleSQLWrapper;

class StandardUpdateContext extends AbstractTableContextSQLContext implements UpdateContext {

    static StandardUpdateContext build(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = update.tableMeta();

        return new StandardUpdateContext(dialect, visible
                , TableContext.singleTable(tableMeta, update.tableAlias())
                , DMLUtils.hasVersionPredicate(update.predicateList()));
    }

    static StandardUpdateContext buildParent(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();

        return new DomainUpdateContext(dialect, visible
                , TableContext.singleTable(childMeta.parentMeta(), update.tableAlias())
                , DMLUtils.hasVersionPredicate(update.predicateList())
                , childMeta
                , true
        );
    }

    static StandardUpdateContext buildChild(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();

        return new DomainUpdateContext(dialect, visible
                , TableContext.singleTable(childMeta, update.tableAlias())
                , DMLUtils.hasVersionPredicate(update.predicateList())
                , childMeta
                , false
        );
    }


    private final boolean hasVersion;

    private StandardUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
            , boolean hasVersion) {
        super(dialect, visible, tableContext);
        this.hasVersion = hasVersion;
    }

    @Override
    public final SimpleSQLWrapper build() {
        return SimpleUpdateSQLWrapper.build(this.sqlBuilder.toString(), this.paramList, this.hasVersion);
    }

    private static final class DomainUpdateContext extends StandardUpdateContext {

        final TableMeta<?> primaryTable;

        final TableMeta<?> relationTable;

        final String primaryAlias;

        boolean existsClauseContext = false;

        private DomainUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
                , boolean hasVersion, ChildTableMeta<?> childMeta, boolean parent) {
            super(dialect, visible, tableContext, hasVersion);

            if (parent) {
                this.primaryTable = childMeta.parentMeta();
                this.relationTable = childMeta;
            } else {
                this.primaryTable = childMeta;
                this.relationTable = childMeta.parentMeta();
            }
            this.primaryAlias = tableContext.tableAliasMap.get(this.primaryTable);
            Assert.hasText(this.primaryAlias, "tableContext error.");
        }

        @Override
        public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
            if (!this.primaryAlias.equals(tableAlias)) {
                throw DialectUtils.createNoLogicalTableException(tableAlias);
            }
            this.appendField(fieldMeta);
        }

        @Override
        public final void appendField(FieldMeta<?, ?> fieldMeta) {
            TableMeta<?> tableOfField = fieldMeta.tableMeta();
            if (tableOfField == this.primaryTable) {
                doAppendFiled(this.primaryAlias, fieldMeta);
            } else if (tableOfField == this.relationTable) {
                if (this.existsClauseContext) {
                    doAppendFiled(obtainRelationTableAlias(), fieldMeta);
                } else {
                    doReplaceRelationFieldAsScalarSubQuery(fieldMeta);
                }
            } else {
                throw DialectUtils.createUnKnownFieldException(fieldMeta);
            }
        }

        @Override
        public final void appendFieldPredicate(SpecialPredicate predicate) {
            if (!predicate.containsSubQuery()
                    && predicate.containsFieldCount(this.relationTable) > 1) {
                this.existsClauseContext = true;
                doReplaceFieldPairWithExistsClause(predicate);
                this.existsClauseContext = false;
            } else {
                predicate.appendPredicate(this);
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

}
