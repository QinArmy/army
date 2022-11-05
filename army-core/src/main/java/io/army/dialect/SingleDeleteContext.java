package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.lang.Nullable;

final class SingleDeleteContext extends SingleDmlContext implements _SingleDeleteContext {


    static SingleDeleteContext create(@Nullable _SqlContext outerContext, _SingleDelete stmt
            , ArmyParser dialect, Visible visible) {
        return new SingleDeleteContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static SingleDeleteContext forParent(_SingleDelete._ChildDelete stmt, ArmyParser dialect, Visible visible) {
        return new SingleDeleteContext(null, stmt, dialect, visible);
    }

    static SingleDeleteContext forChild(_SingleDelete._ChildDelete stmt, SingleDeleteContext parentContext) {
        return new SingleDeleteContext(stmt, parentContext);
    }


    final SingleDeleteContext parentContext;

    private SingleDeleteContext(@Nullable StatementContext outerContext, _SingleDelete dml
            , ArmyParser dialect, Visible visible) {
        super(outerContext, dml, dialect, visible);
        this.parentContext = null;
    }

    private SingleDeleteContext(_SingleDelete stmt, SingleDeleteContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
