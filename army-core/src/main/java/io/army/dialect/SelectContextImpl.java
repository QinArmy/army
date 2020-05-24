package io.army.dialect;


import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerSelect;

final class SelectContextImpl extends AbstractTableContextSQLContext implements SelectContext {

    public static SelectContextImpl build(Dialect dialect, Visible visible, InnerSelect select) {
        TableContext tableContext = TableContext.multiTable(select.tableWrapperList());
        return new SelectContextImpl(dialect, visible, tableContext);
    }

    public static SelectContextImpl build(TableContextSQLContext original, InnerSelect select) {
        TableContext tableContext = TableContext.multiTable(select.tableWrapperList());
        return new SelectContextImpl(original, tableContext);
    }


    private SelectContextImpl(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible, tableContext);
    }

    private SelectContextImpl(TableContextSQLContext original, TableContext tableContext) {
        super(original, tableContext);
    }

}
