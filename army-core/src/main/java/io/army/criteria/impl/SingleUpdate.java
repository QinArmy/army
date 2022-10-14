package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.TableField;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.List;


abstract class SingleUpdate<I extends Item, Q extends Item, F extends TableField, PS extends Update._ItemPairBuilder, SR, SD, WR, WA, OR, LR>
        extends SetWhereClause<F, PS, SR, SD, WR, WA, OR, LR>
        implements Statement
        , Statement._DmlUpdateSpec<I>
        , Statement._DqlUpdateSpec<Q>
        , _Update, _SingleUpdate {

    private final TableMeta<?> updateTable;

    private final String tableAlias;

    private Boolean prepared;

    SingleUpdate(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context);
        this.updateTable = updateTable;
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
    public final I asUpdate() {
        this.endUpdateStatement();
        return this.onAsUpdate();
    }

    @Override
    public final Q asReturningUpdate() {
        this.endUpdateStatement();
        return this.onAsReturningUpdate();
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

    @Override
    public final TableMeta<?> table() {
        return this.updateTable;
    }

    @Override
    public final List<_ItemPair> itemPairList() {
        return null;
    }


    void onClear() {
        //no-op
    }


    void onAddChildItem(SQLs.FieldItemPair pair) {
        throw new UnsupportedOperationException();
    }

    abstract I onAsUpdate();

    Q onAsReturningUpdate() {
        throw ContextStack.castCriteriaApi(this.context);
    }


    private void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);
        if (this.updateTable == null || this.tableAlias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.endOrderByClause();
        this.endWhereClause();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


}
