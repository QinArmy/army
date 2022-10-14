package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.TableMeta;
import io.army.util._Assert;


abstract class SingleDelete<I extends Item, Q extends Item, WR, WA, OR, LR> extends WhereClause<WR, WA, OR, LR>
        implements _SingleDelete
        , Statement
        , Statement._DmlDeleteSpec<I>
        , Statement._DqlReturningDeleteSpec<Q> {

    private final TableMeta<?> deleteTable;

    private final String tableAlias;

    private Boolean prepared;

    SingleDelete(CriteriaContext context, TableMeta<?> deleteTable, String tableAlias) {
        super(context);
        this.deleteTable = deleteTable;
        this.tableAlias = tableAlias;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final TableMeta<?> table() {
        return this.deleteTable;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.onClear();
        this.prepared = Boolean.FALSE;
    }

    @Override
    public final I asDelete() {
        this.endDeleteStatement();
        return this.onAsDelete();
    }

    @Override
    public final Q asReturningDelete() {
        this.endDeleteStatement();
        return this.onAsReturningDelete();
    }

    void onClear() {

    }


    abstract I onAsDelete();

    Q onAsReturningDelete() {
        throw ContextStack.castCriteriaApi(this.context);
    }


    private void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endOrderByClause();
        this.endWhereClause();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


}
