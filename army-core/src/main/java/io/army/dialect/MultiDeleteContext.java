package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.ChildTableMeta;
import io.army.stmt.BatchStmt;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class MultiDeleteContext extends MultiTableContext implements _MultiDeleteContext, DmlStmtParams {

    static MultiDeleteContext create(_MultiDelete stmt, ArmyParser dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.forDelete(stmt, dialect, visible);

        return new MultiDeleteContext(stmt, tableContext, dialect, visible);
    }

    static MultiDeleteContext forChild(_SingleDelete stmt, ArmyParser dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), dialect);
        return new MultiDeleteContext(stmt, tableContext, dialect, visible);
    }


    private final Map<String, String> childAliasToParentAlias;
    private final boolean hasVersion;


    private MultiDeleteContext(_Delete delete, TableContext tableContext, ArmyParser dialect, Visible visible) {
        super(tableContext, dialect, visible);
        this.childAliasToParentAlias = tableContext.childAliasToParentAlias;
        this.hasVersion = _DialectUtils.hasOptimistic(delete.predicateList());
    }


    @Override
    public String parentAlias(final String childAlias) {
        final String parentAlias;
        parentAlias = this.childAliasToParentAlias.get(childAlias);
        if (parentAlias == null) {
            // no bug,never here
            if (this.aliasToTable.containsKey(childAlias)) {
                //TableContext no bug,never here
                throw new IllegalStateException(String.format("Not found parent alias for %s.", childAlias));
            } else {
                throw _Exceptions.unknownTableAlias(childAlias);
            }
        }
        return parentAlias;
    }

    @Override
    public BatchStmt build(List<?> paramList) {
        if (!this.hasNamedParam()) {
            throw _Exceptions.noNamedParamInBatch();
        }
        return Stmts.batchDml(this, paramList);
    }

    @Override
    public boolean hasVersion() {
        return this.hasVersion;
    }

    @Override
    public List<Selection> selectionList() {
        return Collections.emptyList();
    }


}
