package io.army.dialect;

import io.army.criteria.TabularItem;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DmlStatement;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Map;

/**
 * <p>
 * This class is base class of below:
 * <ul>
 *     <li>{@link MultiUpdateContext}</li>
 *     <li>{@link MultiDeleteContext}</li>
 * </ul>
 *
 * @since 0.6.0
 */
abstract class MultiTableDmlContext extends NarrowDmlStmtContext implements _MultiTableStmtContext {

    final MultiTableContext multiTableContext;

    final Map<String, String> childAliasToParentAlias;

    MultiTableDmlContext(@Nullable StatementContext outerContext, _DmlStatement stmt
            , TableContext tableContext, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.childAliasToParentAlias = tableContext.childAliasToParentAlias;
        this.multiTableContext = new MultiTableContext(this, tableContext, null, null);
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
    public final void appendFieldOnly(FieldMeta<?> field) {
        this.multiTableContext.appendFieldOnly(field);
    }


}
