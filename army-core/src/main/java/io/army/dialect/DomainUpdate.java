package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;

final class DomainUpdate extends DomainDmlStmtContext implements _SingleUpdateContext {


    private DomainUpdate(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser0 parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
    }


    @Override
    public _UpdateContext parentContext() {
        return null;
    }

    @Override
    public void appendConditionFields() {

    }


}
