package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.NullHandleMode;
import io.army.criteria.SubStatement;

abstract class ValueInsetOptionClause<C, PO, OR>
        implements Insert._PreferLiteralClause<PO>, Insert._OptionClause<OR> {


    final CriteriaContext criteriaContext;

    final C criteria;

    boolean preferLiteral;

    boolean migration;

    NullHandleMode nullHandleMode = NullHandleMode.INSERT_DEFAULT;

    ValueInsetOptionClause(CriteriaContext criteriaContext) {
        this.criteriaContext = criteriaContext;
        this.criteria = criteriaContext.criteria();
        if (this instanceof SubStatement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }

    @Override
    public final PO preferLiteral(boolean prefer) {
        this.preferLiteral = prefer;
        return (PO) this;
    }

    @Override
    public final OR migration(boolean migration) {
        this.migration = migration;
        if (migration) {
            this.nullHandleMode = NullHandleMode.INSERT_NULL;
        }
        return (OR) this;
    }

    @Override
    public final OR nullHandle(NullHandleMode mode) {
        this.nullHandleMode = mode;
        return (OR) this;
    }


}
