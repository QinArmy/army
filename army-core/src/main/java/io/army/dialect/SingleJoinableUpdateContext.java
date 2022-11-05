package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;

class SingleJoinableUpdateContext extends SingleJoinableDmlContext implements _SingleUpdateContext {


    static SingleJoinableUpdateContext create(@Nullable _SqlContext outerContext, _SingleUpdate stmt
            , ArmyParser0 parser, Visible visible) {
        return new SingleJoinableUpdateContext((StatementContext) outerContext, stmt, parser, visible);
    }


    static SingleJoinableUpdateContext forChild(_SingleUpdate stmt, SingleJoinableUpdateContext parentContext) {
        return new SingleJoinableUpdateContext(stmt, parentContext);
    }


    private SingleJoinableUpdateContext(@Nullable StatementContext outerContext, _SingleDml stmt
            , ArmyParser0 parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
    }

    private SingleJoinableUpdateContext(_SingleUpdate stmt, SingleJoinableUpdateContext parentContext) {
        super(stmt, parentContext);
    }


    @Override
    public _UpdateContext parentContext() {
        return null;
    }

    @Override
    public void appendConditionFields() {

    }


}
