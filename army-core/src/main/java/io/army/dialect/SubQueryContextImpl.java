package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.meta.FieldMeta;


class SubQueryContextImpl extends AbstractQueryStatementContext implements SubQueryContext {

    static SubQueryContextImpl build(TableContextSQLContext parentContext, InnerSubQuery subQuery) {
        TableContext tableContext = TableContext.multiTable(subQuery.tableWrapperList()
                , parentContext.primaryRouteSuffix());
        return new SubQueryContextImpl(parentContext, tableContext, subQuery);
    }

    protected final TableContext parentTableContext;

    private SubQueryContextImpl(TableContextSQLContext parentContext, TableContext tableContext
            , InnerSubQuery subQuery) {
        super(parentContext, tableContext, subQuery);
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
