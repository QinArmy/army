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
@Deprecated
abstract class SingleDelete0<C, WR, WA, D extends DmlStatement.DmlDelete>
        extends DmlWhereClause<C, Void, Void, Void, Void, Void, Void, Void, WR, WA>
        implements DmlStatement._DmlDeleteSpec<D>, _SingleDelete, DmlStatement.DmlDelete {


    private Boolean prepared;

    SingleDelete0(CriteriaContext criteriaContext) {
        super(criteriaContext, JoinableClause.voidClauseCreator());
        if (this instanceof SubStatement) {
            ContextStack.push(this.context);
        } else {
            ContextStack.setContextStack(this.context);
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
        this.context.endContext();
        if (this instanceof SubStatement) {
            ContextStack.pop(this.context);
        } else {
            ContextStack.clearContextStack(this.context);
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


}