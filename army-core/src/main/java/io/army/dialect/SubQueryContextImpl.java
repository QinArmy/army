package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;


class SubQueryContextImpl extends AbstractTableContextSQLContext implements SubQueryContext {

    static SubQueryContextImpl build(TableContextSQLContext parentContext, InnerSubQuery subQuery) {
        TableContext tableContext = TableContext.multiTable(subQuery.tableWrapperList()
                , parentContext.primaryRouteSuffix());

        return new SubQueryContextImpl(parentContext, tableContext, subQuery);
    }

    protected final TableContext parentTableContext;

    private final InnerSubQuery subQuery;

    private SubQueryContextImpl(TableContextSQLContext parentContext, TableContext tableContext, InnerSubQuery subQuery) {
        super(parentContext, tableContext);
        this.parentTableContext = parentContext.tableContext();
        this.subQuery = subQuery;
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

    @Override
    protected final void parseTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias
            , StringBuilder builder) {
        if (tableAlias == null) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "SubQuery[%s] TableMeta[%s] no tableAlias."
                    , this.subQuery, tableMeta);
        }
        Integer tableIndex = this.tableContext.tableIndexMap.get(tableAlias);
        if (tableIndex == null) {
            throw new IllegalStateException(String.format("SubQuery[%s] TableMeta[%s] table index is null."
                    , this.subQuery, tableMeta));
        }
        String tableSuffix = TableRouteUtils.findTableSuffix(tableMeta, tableIndex
                , this.subQuery.predicateList(), this.dialect);
        if (tableSuffix == null) {
            tableSuffix = this.primaryRouteSuffix();
        }
        builder.append(tableSuffix);
    }
}
