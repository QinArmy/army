package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

abstract class AbstractQueryStatementContext extends AbstractTableContextSQLContext{

    protected final _Query query;


    AbstractQueryStatementContext(Dialect dialect, Visible visible, TablesContext tableContext, _Query query) {
        super(dialect, visible, tableContext);
        this.query = query;
    }

    AbstractQueryStatementContext(_TablesSqlContext original, TablesContext tableContext, _Query query) {
        super(original, tableContext);
        this.query = query;
    }

    @Override
    protected final String parseTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        if (tableAlias == null) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Query[%s] TableMeta[%s] no tableAlias."
                    , this.query, tableMeta);
        }
        Integer tableIndex = this.tableContext.tableIndexMap.get(tableAlias);
        if (tableIndex == null) {
            throw new IllegalStateException(String.format("Query[%s] TableMeta[%s] table index is null."
                    ,this.query, tableMeta));
        }
        String routeSuffix = TableRouteUtils.findRouteSuffixForTable(tableMeta, tableIndex
                , this.query.predicateList(), this.dialect);
        if (routeSuffix == null) {
            routeSuffix = this.primaryRouteSuffix();
        }
        return routeSuffix;
    }

}
