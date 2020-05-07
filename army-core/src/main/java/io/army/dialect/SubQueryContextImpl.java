package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.meta.FieldMeta;

class SubQueryContextImpl extends AbstractTableContextSQLContext implements SubQueryContext {

    static SubQueryContextImpl build(TableContextSQLContext parentContext, InnerSubQuery subQuery) {
        TableContext tableContext = createFromContext(subQuery.tableWrapperList());
        return new SubQueryContextImpl(parentContext, tableContext);
    }


    protected final TableContext parentTableContext;

    SubQueryContextImpl(TableContextSQLContext parentContext, TableContext tableContext) {
        super(parentContext, tableContext);
        this.parentTableContext = parentContext.tableContext();
    }


    @Override
    public final TableContext parentTableContext() {
        return this.parentTableContext;
    }

    @Override
    protected final String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        Integer count = this.parentTableContext.tableCountMap.get(fieldMeta.tableMeta());
        String tableAlias;
        if (count == null) {
            throw DialectUtils.createUnKnownFieldException(fieldMeta);
        } else if (count.equals(1)) {
            tableAlias = this.parentTableContext.tableAliasMap.get(fieldMeta.tableMeta());
        } else {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        return tableAlias;
    }
}
