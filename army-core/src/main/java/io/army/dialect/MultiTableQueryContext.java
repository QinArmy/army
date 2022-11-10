package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;

import java.util.List;

abstract class MultiTableQueryContext extends StatementContext implements _MultiTableStmtContext, _SimpleQueryContext {

    final MultiTableContext multiTableContext;

    private final List<Selection> selectionList;

    MultiTableQueryContext(@Nullable StatementContext outerContext, Query query, TableContext tableContext
            , ArmyParser parser, Visible visible) {
        super(outerContext, parser, visible);

        if (query instanceof SubQuery) {
            this.selectionList = null;
            this.multiTableContext = new MultiTableContext(this, tableContext, this::appendOuterField);
        } else {
            this.selectionList = _DialectUtils.flatSelectItem(((_Query) query).selectItemList());
            this.multiTableContext = new MultiTableContext(this, tableContext, null);
        }

    }

    @Override
    public final String safeTableAlias(final TableMeta<?> table, final String alias) {
        return this.multiTableContext.safeTableAlias(table, alias);
    }

    @Override
    public final String safeTableAlias(String alias) {
        return this.multiTableContext.safeTableAlias(alias);
    }

    @Override
    public final String saTableAliasOf(TableMeta<?> table) {
        return this.multiTableContext.saTableAliasOf(table);
    }

    @Override
    public final TabularItem tableItemOf(String tableAlias) {
        return this.multiTableContext.tableItemOf(tableAlias);
    }

    @Override
    public final void appendField(String tableAlias, FieldMeta<?> field) {
        this.multiTableContext.appendField(tableAlias, field);
    }

    @Override
    public final void appendField(FieldMeta<?> field) {
        this.multiTableContext.appendField(field);
    }


    @Override
    public final List<Selection> selectionList() {
        final List<Selection> list = this.selectionList;
        assert list != null;
        return list;
    }

    @Override
    public final SimpleStmt build() {
        if (this.selectionList == null) {
            //sub query don't support,no bug,never here
            String m = String.format("%s don't support build() method", this.getClass().getName());
            throw new UnsupportedOperationException();
        } else if (this.hasNamedParam()) {
            throw new CriteriaException("Query statement don't support named value");
        }
        return Stmts.queryStmt(this);
    }

    void appendOuterField(@Nullable String tableAlias, FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }


}
