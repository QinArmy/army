package io.army.dialect;


import io.army.criteria.SelectPart;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.Collections;
import java.util.List;

final class SelectContextImpl extends AbstractTableContextSQLContext implements SelectContext {

    public static SelectContextImpl build(Dialect dialect, Visible visible, InnerSelect select) {
        TableContext tableContext = TableContext.multiTable(select.tableWrapperList());
        return new SelectContextImpl(dialect, visible, tableContext, select.selectPartList());
    }

    public static SelectContextImpl build(TableContextSQLContext original, InnerSelect select) {
        TableContext tableContext = TableContext.multiTable(select.tableWrapperList());
        return new SelectContextImpl(original, tableContext);
    }

    private final List<SelectPart> selectPartList;


    private SelectContextImpl(Dialect dialect, Visible visible, TableContext tableContext
            , List<SelectPart> selectPartList) {
        super(dialect, visible, tableContext);
        this.selectPartList = selectPartList;
    }

    private SelectContextImpl(TableContextSQLContext original, TableContext tableContext) {
        super(original, tableContext);
        this.selectPartList = Collections.emptyList();
    }

    @Override
    public SimpleSQLWrapper build() {
        Assert.state(!CollectionUtils.isEmpty(this.selectPartList), "SelectContextImpl not outer context");

        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList
                , DialectUtils.extractSelectionList(this.selectPartList));
    }
}
