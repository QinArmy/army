package io.army.dialect;


import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.util.Assert;
import io.army.wrapper.SimpleSQLWrapper;

final class SelectContextImpl extends AbstractQueryStatementContext implements SelectContext {

    public static SelectContextImpl build(InnerSelect select, Dialect dialect, final Visible visible) {
        String primaryRouteSuffix = TableRouteUtils.selectPrimaryRouteSuffix(select, dialect);

        TableContext tableContext = TableContext.multiTable(select.tableWrapperList(), primaryRouteSuffix);
        return new SelectContextImpl(dialect, visible, tableContext, select);
    }

    public static SelectContextImpl build(TableContextSQLContext original, InnerSelect select) {

        TableContext tableContext = TableContext.multiTable(select.tableWrapperList(), original.primaryRouteSuffix());
        return new SelectContextImpl(original, tableContext,select);
    }

    private SelectContextImpl(Dialect dialect, Visible visible, TableContext tableContext
            , InnerSelect select) {
        super(dialect, visible, tableContext,select);
    }

    private SelectContextImpl(TableContextSQLContext original, TableContext tableContext, InnerSelect select) {
        super(original, tableContext,select);
    }

    @Override
    public final SimpleSQLWrapper build() {
        Assert.state(!this.childContext, "SelectContextImpl not outer context");
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList
                , DialectUtils.extractSelectionList(this.query.selectPartList()));
    }
}
