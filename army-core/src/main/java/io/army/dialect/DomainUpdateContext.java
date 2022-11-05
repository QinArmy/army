package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.lang.Nullable;

final class DomainUpdateContext extends DomainDmlStmtContext implements _SingleUpdateContext {

    static DomainUpdateContext create(@Nullable _SqlContext outerContext, _DomainUpdate stmt, ArmyParser parser
            , Visible visible) {
        return new DomainUpdateContext((StatementContext) outerContext, stmt, parser, visible);
    }

    static DomainUpdateContext forChild(_DomainUpdate stmt, DomainUpdateContext parentContext) {
        return new DomainUpdateContext(stmt, parentContext);
    }

    final DomainUpdateContext parentContext;

    private DomainUpdateContext(@Nullable StatementContext outerContext, _DomainUpdate stmt, ArmyParser parser
            , Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.parentContext = null;
    }

    private DomainUpdateContext(_DomainUpdate stmt, DomainUpdateContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }

    @Override
    public _UpdateContext parentContext() {
        return this.parentContext;
    }

    @Override
    public void appendConditionFields() {

    }


}
