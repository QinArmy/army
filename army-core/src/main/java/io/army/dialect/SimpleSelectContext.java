package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Query;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;

import java.util.Collections;
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
        return new SimpleSelectContext(tableContext, outerContext);
    }

    private final List<Selection> selectionList;

    private final _SelectContext outerContext;

    private SimpleSelectContext(Select select, TableContext tableContext, ArmyDialect dialect, Visible visible) {
        super(tableContext, dialect, visible);
        this.outerContext = null;
        this.selectionList = _DqlUtils.flatSelectParts(((_Query) select).selectItemList());
    }

    private SimpleSelectContext(TableContext tableContext, _SelectContext outerContext) {
        super(tableContext, (StmtContext) outerContext);
        this.outerContext = outerContext;
        this.selectionList = Collections.emptyList();
    }


    @Override
    public SimpleStmt build() {
        final _SelectContext outerContext = this.outerContext;
        if (outerContext != null) {
            throw new IllegalStateException("This context is inner context, don't support create Stmt.");
        }
        return Stmts.selectStmt(this.sqlBuilder.toString(), this.paramList, this.selectionList);
    }


}
