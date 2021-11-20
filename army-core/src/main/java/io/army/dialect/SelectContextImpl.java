package io.army.dialect;


import io.army.codec.StatementType;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Select;
import io.army.stmt.SimpleStmt;
import io.army.util.Assert;

final class SelectContextImpl extends AbstractQueryStatementContext implements SelectContext {

    public static SelectContextImpl build(_Select select, Dialect dialect, final Visible visible) {
        String primaryRouteSuffix = TableRouteUtils.selectPrimaryRouteSuffix(select, dialect);

        TableContext tableContext = TableContext.multiTable(select.tableWrapperList(), primaryRouteSuffix);
        return new SelectContextImpl(dialect, visible, tableContext, select);
    }

    public static SelectContextImpl build(_TableSqlContext original, _Select select) {

        TableContext tableContext = TableContext.multiTable(select.tableWrapperList(), original.primaryRouteSuffix());
        return new SelectContextImpl(original, tableContext, select);
    }

    private SelectContextImpl(Dialect dialect, Visible visible, TableContext tableContext
            , _Select select) {
        super(dialect, visible, tableContext, select);
    }

    private SelectContextImpl(_TableSqlContext original, TableContext tableContext, _Select select) {
        super(original, tableContext, select);
    }

    @Override
    public final SimpleStmt build() {
        Assert.state(!this.childContext, "SelectContextImpl not outer context");
        return SimpleStmt.builder()
                .sql(this.sqlBuilder.toString())
                .paramList(this.paramList)
                .statementType(StatementType.SELECT)
                .selectionList(DialectUtils.extractSelectionList(this.query.selectPartList()))
                .build();
    }
}
