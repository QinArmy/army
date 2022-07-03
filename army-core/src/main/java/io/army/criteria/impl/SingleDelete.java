package io.army.criteria.impl;

import io.army.criteria.DmlStatement;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.util._Assert;

/**
 * <p>
 * This class is base class of all single table delete statement.
 * </p>
 *
 * @since 1.0
 */
abstract class SingleDelete<C, WR, WA, D extends DmlStatement.DmlDelete>
        extends DmlWhereClause<C, Void, Void, Void, Void, Void, Void, WR, WA>
        implements DmlStatement._DmlDeleteSpec<D>, _SingleDelete, DmlStatement.DmlDelete {

    final CriteriaContext criteriaContext;

    private Boolean prepared;

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

    @SuppressWarnings("unchecked")
    @Override
    public final D asDelete() {
        _Assert.nonPrepared(this.prepared);
        this.criteriaContext.clear();
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        super.asDmlStatement();
        this.onAsDelete();
        this.prepared = Boolean.TRUE;
        return (D) this;
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        super.clearWherePredicate();
        this.onClear();
    }

    void onAsDelete() {

    }

    void onClear() {

    }

    @Override
    final void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }


}
