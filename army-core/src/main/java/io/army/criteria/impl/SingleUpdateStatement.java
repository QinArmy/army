package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.meta.TableMeta;
import io.army.util._Assert;


abstract class SingleUpdateStatement<I extends Item, F extends TableField, SR, WR, WA, OR, OD, LR, LO, LF>
        extends SetWhereClause<F, SR, WR, WA, OR, OD, LR, LO, LF>
        implements Statement
        , Statement._DmlUpdateSpec<I>
        , _Update, _SingleUpdate {


    private Boolean prepared;

    SingleUpdateStatement(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context, updateTable, tableAlias);
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
    public final I asUpdate() {
        this.endUpdateStatement();
        return this.onAsUpdate();
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        this.onClear();
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    void onClear() {
        //no-op
    }


    abstract I onAsUpdate();


    final void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endUpdateSetClause();
        this.endWhereClause();
        this.endOrderByClause();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


}
