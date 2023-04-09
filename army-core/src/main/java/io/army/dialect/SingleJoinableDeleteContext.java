package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl._JoinableDelete;
import io.army.criteria.impl.inner._DomainDelete;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;

final class SingleJoinableDeleteContext extends SingleJoinableDmlContext implements _SingleDeleteContext {


    static SingleJoinableDeleteContext create(@Nullable _SqlContext outerContext, _SingleDelete stmt
            , ArmyParser parser, Visible visible) {
        final TableContext tableContext;
        if (stmt instanceof _JoinableDelete) {
            tableContext = TableContext.forDelete((_JoinableDelete) stmt, parser, visible);
        } else if (stmt instanceof _DomainDelete && stmt.table() instanceof ChildTableMeta) {
            tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), parser);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableDeleteContext((StatementContext) outerContext, stmt, tableContext, parser, visible);
    }


    static SingleJoinableDeleteContext forParent(_SingleDelete._ChildDelete stmt, ArmyParser parser,
                                                 Visible visible) {
        //TODO
        throw new UnsupportedOperationException();
    }


    static SingleJoinableDeleteContext forChild(_SingleDelete._ChildDelete stmt
            , SingleJoinableDeleteContext parentContext) {
        //TODO
        throw new UnsupportedOperationException();
    }


    final SingleJoinableDeleteContext parentContext;


    private SingleJoinableDeleteContext(@Nullable StatementContext outerContext, _SingleDelete stmt,
                                        TableContext tableContext, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, tableContext, parser, visible);
        this.parentContext = null;
    }

    private SingleJoinableDeleteContext(_SingleDelete stmt, SingleJoinableDeleteContext parentContext,
                                        TableContext tableContext) {
        super(stmt, parentContext, tableContext);
        this.parentContext = parentContext;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
