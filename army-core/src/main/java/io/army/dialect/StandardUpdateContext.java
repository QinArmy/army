package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.criteria.FieldPairDualPredicate;
import io.army.criteria.TableAliasException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;
import io.army.wrapper.SimpleUpdateSQLWrapper;

class StandardUpdateContext extends AbstractTableContextSQLContext implements UpdateContext {

    static StandardUpdateContext build(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = update.tableMeta();

        return new StandardUpdateContext(dialect, visible
                , TableContext.singleTable(tableMeta, update.tableAlias())
                , DMLUtils.hasVersionPredicate(update.predicateList()));
    }

    static StandardUpdateContext buildParent(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();

        return new ParentUpdateContext(dialect, visible
                , TableContext.singleTable(childMeta.parentMeta(), update.tableAlias())
                , DMLUtils.hasVersionPredicate(update.predicateList())
                , childMeta
        );
    }

    static StandardUpdateContext buildChild(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();

        return new ChildUpdateContext(dialect, visible
                , TableContext.singleTable(childMeta, update.tableAlias())
                , DMLUtils.hasVersionPredicate(update.predicateList())
                , childMeta
        );
    }


    private final boolean hasVersion;

    private StandardUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
            , boolean hasVersion) {
        super(dialect, visible, tableContext);
        this.hasVersion = hasVersion;
    }


    @Override
    protected final SimpleSQLWrapper doBuild() {
        return SimpleUpdateSQLWrapper.build(this.sqlBuilder.toString(), this.paramList, this.hasVersion);
    }

    @Override
    protected final DomainSQLWrapper doBuild(DomainWrapper beanWrapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * design for {@link ParentUpdateContext} and {@link ChildUpdateContext}
     */
    final void doReplaceRelationFieldAsSubQuery(TableMeta<?> primaryTable, String primaryTableAlias
            , FieldMeta<?, ?> relationField) {
        final TableMeta<?> tableOfField = relationField.tableMeta();
        // replace parent field as sub query.
        final Dialect dialect = this.dialect;
        final String safeRelationAlias = dialect.quoteIfNeed(obtainRelationTableAlias(tableOfField, primaryTableAlias));

        StringBuilder builder = this.sqlBuilder.append(" ( SELECT ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(relationField.fieldName()))
                .append(" FROM");
        appendTable(tableOfField);
        if (dialect.tableAliasAfterAs()) {
            builder.append(" AS");
        }
        builder.append(" ")
                .append(safeRelationAlias)
                .append(" WHERE ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(tableOfField.id().fieldName()))
                .append(" = ")
                .append(dialect.quoteIfNeed(primaryTableAlias))
                .append(".")
                .append(dialect.quoteIfNeed(primaryTable.id().fieldName()))
                .append(" )")
        ;
    }

    /**
     * design for {@link ParentUpdateContext} and {@link ChildUpdateContext}
     */
    final void doAppendFieldPair(TableMeta<?> primaryTable, String primaryTableAlias
            , FieldPairDualPredicate predicate) {
        TableMeta<?> relationTable;
        if (primaryTable instanceof ChildTableMeta) {
            relationTable = ((ChildTableMeta<?>) primaryTable).parentMeta();
        } else {
            ChildTableMeta<?> childMeta = obtainChildMeta();
            Assert.state(primaryTable == childMeta.parentMeta(), "obtainChildMeta return error");
            relationTable = childMeta;
        }
        if (predicate.left().tableMeta() == relationTable
                && predicate.right().tableMeta() == relationTable) {
            // replace pair with exists clause
            doReplaceFieldPairWithExistsClause(relationTable, primaryTable, primaryTableAlias, predicate);
        } else {
            predicate.appendPredicate(this);
        }
    }

    /**
     * design for {@link ParentUpdateContext} and {@link ChildUpdateContext}
     */
    private void doReplaceFieldPairWithExistsClause(TableMeta<?> relationTable, TableMeta<?> primaryTable
            , String primaryTableAlias, FieldPairDualPredicate predicate) {

        final Dialect dialect = this.dialect;
        final String safeRelationAlias = dialect.quoteIfNeed(obtainRelationTableAlias(relationTable
                , primaryTableAlias));

        final String safeRelationId = dialect.quoteIfNeed(relationTable.id().fieldName());
        final StringBuilder builder = this.sqlBuilder
                .append(" EXISTS ( SELECT ")
                .append(safeRelationAlias)
                .append(".")
                .append(safeRelationId)
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
                .append(safeRelationId)
                .append(" = ")
                .append(dialect.quoteIfNeed(primaryTableAlias))
                .append(".")
                .append(dialect.quoteIfNeed(primaryTable.id().fieldName()))
                .append(" ADN ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(predicate.left().fieldName()))
                .append(" ")
                .append(predicate.operator().rendered())
                .append(" ")
                .append(safeRelationAlias)
                .append(".")
                .append(dialect.quoteIfNeed(predicate.right().fieldName()))
                .append(" )");
    }

    /**
     * design for {@link ParentUpdateContext} and {@link ChildUpdateContext}
     */
    ChildTableMeta<?> obtainChildMeta() {
        throw new UnsupportedOperationException();
    }


    /**
     * design for {@link ParentUpdateContext} and {@link ChildUpdateContext}
     */
    private String obtainRelationTableAlias(TableMeta<?> relationTable, String primaryTableAlias) {
        String relationTableAlias = primaryTableAlias;
        if (relationTable instanceof ChildTableMeta) {
            relationTableAlias += "_p";
        } else {
            relationTableAlias += "_c";
        }
        return relationTableAlias;
    }


    private static final class ParentUpdateContext extends StandardUpdateContext {

        private final ChildTableMeta<?> childMeta;

        private final String parentAlias;

        private ParentUpdateContext(Dialect dialect, Visible visible, TableContext tableContext, boolean hasVersion
                , ChildTableMeta<?> childMeta) {
            super(dialect, visible, tableContext, hasVersion);
            this.childMeta = childMeta;
            this.parentAlias = this.tableContext.tableAliasMap.get(this.childMeta.parentMeta());
            Assert.hasText(this.parentAlias, "tableContext error.");
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
            if (!this.parentAlias.equals(tableAlias)) {
                throw DialectUtils.createNoLogicalTableException(tableAlias);
            }
            this.appendField(fieldMeta);
        }

        @Override
        public void appendField(FieldMeta<?, ?> fieldMeta) {
            TableMeta<?> tableOfField = fieldMeta.tableMeta();
            if (tableOfField == this.childMeta.parentMeta()) {
                doAppendFiled(this.parentAlias, fieldMeta);
            } else if (tableOfField == this.childMeta) {
                doReplaceRelationFieldAsSubQuery(this.childMeta.parentMeta(), this.parentAlias, fieldMeta);
            } else {
                throw DialectUtils.createUnKnownFieldException(fieldMeta);
            }
        }

        @Override
        public void appendFieldPair(FieldPairDualPredicate predicate) {
            doAppendFieldPair(this.childMeta.parentMeta(), this.parentAlias, predicate);
        }

        @Override
        final ChildTableMeta<?> obtainChildMeta() {
            return this.childMeta;
        }
    }

    private static final class ChildUpdateContext extends StandardUpdateContext {

        private final ChildTableMeta<?> childMeta;

        private final String childAlias;

        private ChildUpdateContext(Dialect dialect, Visible visible, TableContext tableContext, boolean hasVersion
                , ChildTableMeta<?> childMeta) {
            super(dialect, visible, tableContext, hasVersion);
            this.childMeta = childMeta;
            this.childAlias = this.tableContext.tableAliasMap.get(this.childMeta);
            Assert.hasText(this.childAlias, "tableContext error.");
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
            if (!this.childAlias.equals(tableAlias)) {
                throw DialectUtils.createNoLogicalTableException(tableAlias);
            }
            this.appendField(fieldMeta);
        }

        @Override
        public void appendField(FieldMeta<?, ?> fieldMeta) {
            TableMeta<?> tableOfField = fieldMeta.tableMeta();
            if (tableOfField == this.childMeta) {
                doAppendFiled(this.childAlias, fieldMeta);
            } else if (tableOfField == this.childMeta.parentMeta()) {
                doReplaceRelationFieldAsSubQuery(this.childMeta, this.childAlias, fieldMeta);
            } else {
                throw DialectUtils.createUnKnownFieldException(fieldMeta);
            }
        }

        @Override
        public void appendFieldPair(FieldPairDualPredicate predicate) {
            doAppendFieldPair(this.childMeta.parentMeta(), this.childAlias, predicate);
        }

        @Override
        final ChildTableMeta<?> obtainChildMeta() {
            return this.childMeta;
        }

        /*################################## blow private method ##################################*/

    }
}
