package io.army.dialect;


import io.army.codec.StatementType;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Select;
import io.army.sharding._TableRouteUtils;
import io.army.stmt.SimpleStmt;
import io.army.util._Assert;

final class SelectContextImpl extends AbstractQueryStatementContext implements SelectContext {

    public static SelectContextImpl build(_Select select, Dialect dialect, final Visible visible) {
        String primaryRouteSuffix = _TableRouteUtils.selectPrimaryRouteSuffix(select, dialect);

        TablesContext tableContext = TablesContext.multiTable(select.tableWrapperList(), primaryRouteSuffix);
        return new SelectContextImpl(dialect, visible, tableContext, select);
    }

    public static SelectContextImpl build(_TablesSqlContext original, _Select select) {

        TablesContext tableContext = TablesContext.multiTable(select.tableWrapperList(), original.primaryRouteSuffix());
        return new SelectContextImpl(original, tableContext, select);
    }

    private SelectContextImpl(Dialect dialect, Visible visible, TablesContext tableContext
            , _Select select) {
        super(dialect, visible, tableContext, select);
    }

    private SelectContextImpl(_TablesSqlContext original, TablesContext tableContext, _Select select) {
        super(original, tableContext, select);
    }

    @Override
    public final SimpleStmt build() {
        _Assert.state(!this.childContext, "SelectContextImpl not outer context");
        return SimpleStmt.builder()
                .sql(this.sqlBuilder.toString())
                .paramList(this.paramList)
                .statementType(StatementType.SELECT)
                .selectionList(_DialectUtils.extractSelectionList(this.query.selectPartList()))
                .build();
    }
}
