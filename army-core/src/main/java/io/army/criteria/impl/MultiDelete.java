package io.army.criteria.impl;

import io.army.criteria.DmlStatement;
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
abstract class MultiDelete<C, FT, FS, FP, FJ, JT, JS, JP, WR, WA, D extends DmlStatement.DmlDelete>
        extends DmlWhereClause<C, FT, FS, FP, FJ, JT, JS, JP, WR, WA>
        implements DmlStatement.DmlDelete, DmlStatement._DmlDeleteSpec<D>
        , _MultiDelete, JoinableClause.ClauseCreator<FP, JT, JS, JP> {


    private Boolean prepared;

    private List<_TableBlock> tableBlockList;


    MultiDelete(CriteriaContext criteriaContext) {
        super(criteriaContext);
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

    @SuppressWarnings("unchecked")
    @Override
    public final D asDelete() {
        _Assert.nonPrepared(this.prepared);
        this.validateBeforeClearContext();
        if (this instanceof SubStatement) {
            ContextStack.pop(this.context);
        } else {
            ContextStack.clearContextStack(this.context);
        }
        this.tableBlockList = this.context.endContext();
        this.asDmlStatement();
        this.onAsDelete();
        this.prepared = Boolean.TRUE;
        return (D) this;
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
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
