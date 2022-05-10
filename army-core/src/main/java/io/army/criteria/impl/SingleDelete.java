package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.util._Assert;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is base class of all single table delete statement.
 * </p>
 *
 * @since 1.0
 */
abstract class SingleDelete<C, WR, WA> extends DmlWhereClause<C, Void, Void, Void, Void, Void, Void, WR, WA>
        implements Delete, Delete._DeleteSpec, _SingleDelete {

    final CriteriaContext criteriaContext;

    private boolean prepared;

    SingleDelete(CriteriaContext criteriaContext) {
        super(JoinableClause.voidClauseSuppler(), criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
        if (this instanceof SubStatement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
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
        this.criteriaContext.clear();
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        super.asDmlStatement();
        this.onAsDelete();
        this.prepared = true;
        return this;
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;
        super.clearWherePredicate();
        this.onClear();
    }

    void onAsDelete() {

    }

    void onClear() {

    }

    @Override
    final void crossJoinEvent(boolean success) {
        throw _Exceptions.castCriteriaApi();
    }


}
