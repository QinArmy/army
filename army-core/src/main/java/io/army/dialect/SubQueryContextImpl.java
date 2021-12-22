package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner._SubQuery;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;


class SubQueryContextImpl extends AbstractQueryStatementContext implements SubQueryContext {

    static SubQueryContextImpl build(_TablesSqlContext parentContext, _SubQuery subQuery) {
        TablesContext tableContext = TablesContext.multiTable(subQuery.tableWrapperList()
                , parentContext.primaryRouteSuffix());
        return new SubQueryContextImpl(parentContext, tableContext, subQuery);
    }

    private SubQueryContextImpl(_TablesSqlContext parentContext, TablesContext tableContext
            , _SubQuery subQuery) {
        super(parentContext, tableContext, subQuery);
    }

    @Override
    protected final String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        TablesContext parentTableContext = this.parentTableContext();
        if(parentTableContext == null){
            throw new IllegalStateException(String.format("SubQuery[%s] no parent table context.",this.query));
        }
        Integer count = parentTableContext.tableCountMap.get(fieldMeta.tableMeta());
        String tableAlias;
        if (count == null) {
            throw _DialectUtils.createUnKnownFieldException(fieldMeta);
        } else if (count.equals(1)) {
            tableAlias = parentTableContext.tableAliasMap.get(fieldMeta.tableMeta());
        } else {
            throw _DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        return tableAlias;
    }


    @Override
    public SimpleStmt build() {
        return null;
    }
}
