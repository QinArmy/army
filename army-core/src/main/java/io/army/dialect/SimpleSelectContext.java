package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.TablePart;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Query;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class SimpleSelectContext extends _BaseSqlContext implements _SimpleQueryContext, _SelectContext {


    static SimpleSelectContext create(Select select, Dialect dialect, Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.createMapPair(select, dialect);
        return new SimpleSelectContext(select, tableContext, dialect, visible);
    }

    static SimpleSelectContext create(Select select, _SelectContext outerContext) {
        final TableContext tableContext;
        tableContext = TableContext.createMapPair(select, outerContext.dialect());
        return new SimpleSelectContext(tableContext, outerContext);
    }

    private final List<Selection> selectionList;

    private final Map<String, TablePart> aliasToTable;

    private final Map<TableMeta<?>, String> tableToSafeAlias;

    private final _SelectContext outerContext;

    private SimpleSelectContext(Select select, TableContext tableContext, Dialect dialect, Visible visible) {
        super(dialect, visible);
        this.outerContext = null;
        this.aliasToTable = tableContext.aliasToTable;
        this.tableToSafeAlias = tableContext.tableToSafeAlias;
        this.selectionList = _DqlUtils.flatSelectParts(((_Query) select).selectPartList());
    }

    private SimpleSelectContext(TableContext tableContext, _SelectContext outerContext) {
        super((_BaseSqlContext) outerContext);
        this.outerContext = outerContext;
        this.aliasToTable = tableContext.aliasToTable;
        this.tableToSafeAlias = tableContext.tableToSafeAlias;
        this.selectionList = Collections.emptyList();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {
        final TablePart tablePart = this.aliasToTable.get(tableAlias);
        if (!(tablePart instanceof TableMeta) || field.tableMeta() != tablePart) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        final Dialect dialect = this.dialect;
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(dialect.quoteIfNeed(tableAlias))
                .append(Constant.POINT)
                .append(dialect.quoteIfNeed(field.columnName()));
    }

    @Override
    public void appendField(FieldMeta<?, ?> field) {
        final String safeAlias = this.tableToSafeAlias.get(field.tableMeta());
        if (safeAlias == null) {
            throw _Exceptions.selfJoinNoLogicField(field);
        }
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(safeAlias)
                .append(Constant.POINT)
                .append(this.dialect.quoteIfNeed(field.columnName()));
    }

    @Override
    public SimpleStmt build() {
        final _SelectContext outerContext = this.outerContext;
        if (outerContext != null) {
            throw new IllegalStateException("This context is inner context, don't support create Stmt.");
        }
        return Stmts.selectStmt(this.sqlBuilder.toString(), this.paramList, this.selectionList);
    }


}
