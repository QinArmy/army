package io.army.dialect;

import io.army.criteria.TabularItem;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

abstract class SingleJoinableDmlContext extends SingleTableDmlContext implements _MultiTableStmtContext {


    final MultiTableContext multiTableContext;

    SingleJoinableDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt,
                             TableContext tableContext, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.multiTableContext = new MultiTableContext(this, tableContext, null);
    }

    SingleJoinableDmlContext(_SingleDml stmt, SingleTableDmlContext parentOrOuterContext, TableContext tableContext) {
        super(stmt, parentOrOuterContext);
        this.multiTableContext = new MultiTableContext(this, tableContext, null);
    }


    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        this.multiTableContext.appendField(tableAlias, field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        this.multiTableContext.appendField(field);
    }

    @Override
    public final String safeTableAlias(TableMeta<?> table, String alias) {
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


}
