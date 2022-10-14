package io.army.criteria.impl;

import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.TableMeta;

abstract class SingleDelete<WR, WA, OR, LR> extends WhereClause<WR, WA, OR, LR>
        implements _SingleDelete {

    private TableMeta<?> deleteTable;

    private String tableAlias;

    SingleDelete(CriteriaContext context, TableMeta<?> deleteTable, String tableAlias) {
        super(context);
        this.deleteTable = deleteTable;
        this.tableAlias = tableAlias;
    }

    @Override
    public final String tableAlias() {
        return null;
    }

    @Override
    public final TableMeta<?> table() {
        return null;
    }

    @Override
    public final void clear() {

    }



}
