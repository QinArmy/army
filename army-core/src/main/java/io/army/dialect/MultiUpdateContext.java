package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class MultiUpdateContext extends MultiTableContext implements _MultiUpdateContext {

    static MultiUpdateContext create(_MultiUpdate statement, ArmyDialect dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.createContext(statement.tableBlockList(), dialect, visible, true);
        return new MultiUpdateContext(statement, tableContext, dialect, visible);
    }

    private final _MultiUpdate statement;


    private final Map<String, String> childSafeAliasToParentSafeAlias;

    private final Map<String, SingleTableMeta<?>> safeAliasToSingleTable = new HashMap<>();


    private MultiUpdateContext(_MultiUpdate statement, TableContext tableContext, ArmyDialect dialect, Visible visible) {
        super(tableContext, dialect, visible);
        this.childSafeAliasToParentSafeAlias = tableContext.childSafeAliasToParentSafeAlias;
        this.statement = statement;
    }


    @Override
    public void appendAfterSetClause() {
        final Map<String, SingleTableMeta<?>> safeAliasToSingleTable = this.safeAliasToSingleTable;
        assert safeAliasToSingleTable.size() > 0;
        final ArmyDialect dialect = this.dialect;
        for (Map.Entry<String, SingleTableMeta<?>> entry : safeAliasToSingleTable.entrySet()) {
            dialect.appendArmyManageFieldsToSetClause(entry.getValue(), entry.getKey(), this);
        }
        safeAliasToSingleTable.clear();
    }

    @Override
    public _MultiUpdate statement() {
        return this.statement;
    }

    @Override
    public _SqlContext context() {
        return this;
    }

    @Override
    public List<? extends SetLeftItem> leftItemList() {
        return this.statement.leftItemList();
    }

    @Override
    public List<? extends SetRightItem> rightItemList() {
        return this.statement.rightItemList();
    }

    @Override
    public boolean supportTableAlias() {
        // multi-table update syntax must support table alias
        return true;
    }

    @Override
    public boolean supportRow() {
        return this.dialect.setClauseSupportRow();
    }

    @Override
    public String validateField(final TableField<?> field) {
        final TableMeta<?> belongOf = field.tableMeta();
        //1. get safe table alias of belongOf table
        String safeTableAlias;
        safeTableAlias = this.tableToSafeAlias.get(belongOf);
        if (safeTableAlias != null) {
            if (field instanceof QualifiedField
                    && this.aliasToTable.get(((QualifiedField<?>) field).tableAlias()) != belongOf) {
                throw _Exceptions.unknownColumn(field);
            }
        } else if (!(field instanceof QualifiedField)) {
            //belongOf table self-join,but isn't QualifiedField
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else {
            final String tableAlias;
            tableAlias = ((QualifiedField<?>) field).tableAlias();
            if (this.aliasToTable.get(tableAlias) != belongOf) {
                throw _Exceptions.unknownColumn(field);
            }
            final Map<String, String> aliasToSafeAlias = getAliasToSafeAlias();
            safeTableAlias = aliasToSafeAlias.get(tableAlias);
            if (safeTableAlias == null) {
                safeTableAlias = this.dialect.quoteIfNeed(tableAlias);
                aliasToSafeAlias.put(tableAlias, safeTableAlias);
            }
        }
        //2.get SingleTable and safe table alias for update updateTime and version field
        if (belongOf instanceof ChildTableMeta) {
            final ParentTableMeta<?> parent = ((ChildTableMeta<?>) belongOf).parentMeta();
            final String parentSafeAlias;
            parentSafeAlias = this.childSafeAliasToParentSafeAlias.get(safeTableAlias);
            if (parentSafeAlias == null) {
                String m = String.format("Update %s %s but no inner join %s .", belongOf, safeTableAlias, parent);
                throw new CriteriaException(m);
            }
            this.safeAliasToSingleTable.putIfAbsent(parentSafeAlias, parent);
        } else {
            this.safeAliasToSingleTable.putIfAbsent(safeTableAlias, (SingleTableMeta<?>) belongOf);
        }
        return safeTableAlias;
    }

    @Override
    public SimpleStmt build() {
        final boolean optimistic;
        optimistic = _DmlUtils.hasOptimistic(this.statement.predicateList());
        return Stmts.dml(this.sqlBuilder.toString(), this.paramList, optimistic);
    }


}
