package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DomainDelete;
import io.army.lang.Nullable;

final class DomainDeleteContext extends DomainDmlStmtContext implements _SingleDeleteContext {

    static DomainDeleteContext create(@Nullable _SqlContext outerContext, _DomainDelete stmt, ArmyParser parser
            , Visible visible) {
        return new DomainDeleteContext((StatementContext) outerContext, stmt, parser, visible);
    }

    static DomainDeleteContext forChild(_DomainDelete stmt, DomainDeleteContext parentContext) {
        return new DomainDeleteContext(stmt, parentContext);
    }


    final DomainDeleteContext parentContext;

    private DomainDeleteContext(@Nullable StatementContext outerContext, _DomainDelete stmt
            , ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.parentContext = null;
    }

    private DomainDeleteContext(_DomainDelete stmt, DomainDeleteContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
