package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Query;

import java.util.List;

final class SimpleSelectContext extends MultiTableContext implements _SimpleQueryContext, _SelectContext {


    static SimpleSelectContext create(Select select, ArmyDialect dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.createContext(((_Query) select).tableBlockList(), dialect, visible, false);
        return new SimpleSelectContext(select, tableContext, dialect, visible);
    }

    static SimpleSelectContext create(Select select, _SelectContext outerContext) {
        final TableContext tableContext;
        tableContext = TableContext.createContext(((_Query) select).tableBlockList()
                , outerContext.dialect(), outerContext.visible(), false);
        return new SimpleSelectContext(select, tableContext, outerContext);
    }

    private final List<Selection> selectionList;

    private SimpleSelectContext(Select select, TableContext tableContext, ArmyDialect dialect, Visible visible) {
        super(tableContext, dialect, visible);
        this.selectionList = _DqlUtils.flatSelectItem(((_Query) select).selectItemList());
    }

    private SimpleSelectContext(Select select, TableContext tableContext, _SelectContext outerContext) {
        super(tableContext, (StmtContext) outerContext);
        this.selectionList = _DqlUtils.flatSelectItem(((_Query) select).selectItemList());
    }


    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }


}
