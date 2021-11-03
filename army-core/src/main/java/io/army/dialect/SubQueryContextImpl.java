package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;


class SubQueryContextImpl extends AbstractQueryStatementContext implements SubQueryContext {

    static SubQueryContextImpl build(TableContextSQLContext parentContext, InnerSubQuery subQuery) {
        TableContext tableContext = TableContext.multiTable(subQuery.tableWrapperList()
                , parentContext.primaryRouteSuffix());
        return new SubQueryContextImpl(parentContext, tableContext, subQuery);
    }

    private SubQueryContextImpl(TableContextSQLContext parentContext, TableContext tableContext
            , InnerSubQuery subQuery) {
        super(parentContext, tableContext, subQuery);
    }

    @Override
    protected final String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        TableContext parentTableContext = this.parentTableContext();
        if(parentTableContext == null){
            throw new IllegalStateException(String.format("SubQuery[%s] no parent table context.",this.query));
        }
        Integer count = parentTableContext.tableCountMap.get(fieldMeta.tableMeta());
        String tableAlias;
        if (count == null) {
            throw DialectUtils.createUnKnownFieldException(fieldMeta);
        } else if (count.equals(1)) {
            tableAlias = parentTableContext.tableAliasMap.get(fieldMeta.tableMeta());
        } else {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        return tableAlias;
    }


    @Override
    public SimpleStmt build() {
        return null;
    }
}
