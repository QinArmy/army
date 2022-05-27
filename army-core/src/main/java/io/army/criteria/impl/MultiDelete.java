package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._TableBlock;
import io.army.util._Assert;

import java.util.List;


/**
 * <p>
 * This class is base class of multi-table delete implementation.
 * </p>
 */
abstract class MultiDelete<C, FT, FS, FP, JT, JS, JP, WR, WA> extends DmlWhereClause<C, FT, FS, FP, JT, JS, JP, WR, WA>
        implements Delete, Delete._DeleteSpec, _MultiDelete, JoinableClause.ClauseSupplier {

    final CriteriaContext criteriaContext;

    private boolean prepared;

    private List<_TableBlock> tableBlockList;


    MultiDelete(CriteriaContext criteriaContext) {
        super(criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final Delete asDelete() {
        _Assert.nonPrepared(this.prepared);
        this.validateBeforeClearContext();
        this.tableBlockList = this.criteriaContext.clear();
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        this.asDmlStatement();
        this.onAsDelete();
        this.prepared = true;
        return this;
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;
        this.clearWherePredicate();
        this.tableBlockList = null;
        this.onClear();
    }

    @Override
    public final List<_TableBlock> tableBlockList() {
        return this.tableBlockList;
    }


    abstract void onAsDelete();

    abstract void onClear();

    void validateBeforeClearContext() {
        // no-op
    }


}
