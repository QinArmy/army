package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.lang.Nullable;

final class SingleJoinableDeleteContext extends SingleDmlContext implements _SingleDeleteContext {


    static SingleJoinableDeleteContext create(@Nullable _SqlContext outerContext, _SingleDelete stmt
            , ArmyParser parser, Visible visible) {
        return new SingleJoinableDeleteContext((StatementContext) outerContext, stmt, parser, visible);
    }


    static SingleJoinableDeleteContext forParent(_SingleDelete._ChildDelete stmt, ArmyParser parser
            , Visible visible) {
        return new SingleJoinableDeleteContext(null, stmt, parser, visible);
    }


    static SingleJoinableDeleteContext forChild(_SingleDelete._ChildDelete stmt
            , SingleJoinableDeleteContext parentContext) {
        return new SingleJoinableDeleteContext(stmt, parentContext);
    }


    final SingleJoinableDeleteContext parentContext;


    private SingleJoinableDeleteContext(@Nullable StatementContext outerContext, _SingleDelete stmt, ArmyParser parser
            , Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.parentContext = null;
    }

    private SingleJoinableDeleteContext(_SingleDelete stmt, SingleJoinableDeleteContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
