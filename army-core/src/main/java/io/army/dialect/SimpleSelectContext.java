package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Query;

import java.util.List;

final class SimpleSelectContext extends MultiTableContext implements _SimpleQueryContext, _SelectContext {


    static SimpleSelectContext create(Select select, ArmyParser0 dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) select).tableBlockList(), dialect, visible);
        return new SimpleSelectContext(select, tableContext, dialect, visible);
    }

    static SimpleSelectContext create(_SqlContext outerContext, Select select) {
        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) select).tableBlockList()
                , (ArmyParser0) outerContext.parser(), outerContext.visible());
        return new SimpleSelectContext(select, tableContext, outerContext);
    }

    private final List<Selection> selectionList;

    private SimpleSelectContext(Select select, TableContext tableContext, ArmyParser0 dialect, Visible visible) {
        super(tableContext, dialect, visible);
        this.selectionList = _DialectUtils.flatSelectItem(((_Query) select).selectItemList());
    }

    private SimpleSelectContext(Select select, TableContext tableContext, _SelectContext outerContext) {
        super(tableContext, (StatementContext) outerContext);
        this.selectionList = _DialectUtils.flatSelectItem(((_Query) select).selectItemList());
    }


    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }


}
