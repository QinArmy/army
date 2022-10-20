package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.TableField;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._Exceptions;


abstract class SingleUpdate<I extends Item, Q extends Item, F extends TableField, PS extends Update._ItemPairBuilder, SR, SD, WR, WA, OR, LR>
        extends SetWhereClause<F, PS, SR, SD, WR, WA, OR, LR>
        implements Statement
        , Statement._DmlUpdateSpec<I>
        , Statement._DqlUpdateSpec<Q>
        , _Update, _SingleUpdate {


    private Boolean prepared;

    SingleUpdate(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
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


    void onClear() {
        //no-op
    }


    abstract I onAsUpdate();

    Q onAsReturningUpdate() {
        throw ContextStack.castCriteriaApi(this.context);
    }


    private void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endUpdateSetClause();
        if (this.endWhereClause().size() == 0) {
            throw ContextStack.criteriaError(this.context, _Exceptions::dmlNoWhereClause);
        }
        this.endOrderByClause();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


}
