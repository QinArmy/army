package io.army.dialect;

import io.army.criteria.Query;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;

final class SimpleSelectContext extends MultiTableQueryContext implements  _SelectContext {


    static SimpleSelectContext create(@Nullable _SqlContext outerContext, Select select, ArmyParser dialect
            , Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) select).tableBlockList(), dialect, visible);
        return new SimpleSelectContext((StatementContext)outerContext, select, tableContext, dialect, visible);
    }

    static SimpleSelectContext create(  final _SqlContext outerCtx,  final Select select) {
         final StatementContext  outerContext = (StatementContext) outerCtx;
         final ArmyParser parser = outerContext.parser;

        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) select).tableBlockList(), parser,outerContext.visible);
        return new SimpleSelectContext(outerContext, select, tableContext, parser,outerContext.visible);
    }


    private SimpleSelectContext(@Nullable  StatementContext outerContext, Query query
            , TableContext tableContext, ArmyParser parser, Visible visible) {
        super(outerContext, query, tableContext, parser, visible);
    }




}
