package io.army.dialect;

import io.army.criteria.SubQuery;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Query;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

import java.util.Map;

final class SimpleSubQueryContext extends _BaseSqlContext implements _SimpleQueryContext, _SubQueryContext {


    static SimpleSubQueryContext create(SubQuery subQuery, _SqlContext outerContext) {
        final TableContext tableContext;
        tableContext = TableContext.createContext(((_Query) subQuery).tableBlockList()
                , outerContext.dialect(), outerContext.visible());
        return new SimpleSubQueryContext(tableContext, outerContext);
    }


    // private final List<Selection> selectionList;

    private final Map<String, TableItem> aliasToTable;

    private final Map<TableMeta<?>, String> tableToSafeAlias;

    private final _SqlContext outerContext;

    private SimpleSubQueryContext(TableContext tableContext, _SqlContext outerContext) {
        super((_BaseSqlContext) outerContext);
        this.outerContext = outerContext;
        this.aliasToTable = tableContext.aliasToTable;
        this.tableToSafeAlias = tableContext.tableToSafeAlias;
        // this.selectionList = Collections.emptyList();
    }


    @Override
    public void appendField(final String tableAlias, final FieldMeta<?, ?> field) {
        final TableItem tableItem;
        tableItem = this.aliasToTable.get(tableAlias);
        final _SqlContext outerContext;
        if (tableItem instanceof TableMeta) {
            if (field.tableMeta() != tableItem) {
                throw _Exceptions.unknownColumn(tableAlias, field);
            }
            // field belong of this sub query context.
            final _Dialect dialect = this.dialect;
            this.sqlBuilder
                    .append(Constant.SPACE)
                    .append(dialect.quoteIfNeed(tableAlias))
                    .append(Constant.POINT)
                    .append(dialect.quoteIfNeed(field.columnName()));
        } else if ((outerContext = this.outerContext) instanceof _UnionQueryContext) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        } else if (outerContext instanceof _SubQueryContext) {
            ((_SubQueryContext) outerContext).appendOuterField(tableAlias, field);
        } else {
            outerContext.appendField(tableAlias, field);
        }

    }

    @Override
    public void appendField(final FieldMeta<?, ?> field) {
        final String safeAlias;
        safeAlias = this.tableToSafeAlias.get(field.tableMeta());
        final _SqlContext outerContext;
        if (safeAlias != null) {
            this.sqlBuilder
                    .append(Constant.SPACE)
                    .append(safeAlias)
                    .append(Constant.POINT)
                    .append(this.dialect.quoteIfNeed(field.columnName()));
        } else if ((outerContext = this.outerContext) instanceof _UnionQueryContext) {
            throw _Exceptions.unknownColumn(null, field);
        } else if (outerContext instanceof _SubQueryContext) {
            ((_SubQueryContext) outerContext).appendOuterField(field);
        } else {
            outerContext.appendField(field);
        }

    }

    @Override
    public void appendOuterField(final String tableAlias, final FieldMeta<?, ?> field) {
        final TableItem tableItem;
        tableItem = this.aliasToTable.get(tableAlias);
        if (!(tableItem instanceof TableMeta && field.tableMeta() == tableItem)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        final _Dialect dialect = this.dialect;
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(dialect.quoteIfNeed(tableAlias))
                .append(Constant.POINT)
                .append(dialect.quoteIfNeed(field.columnName()));
    }

    @Override
    public void appendOuterField(final FieldMeta<?, ?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final String safeAlias = this.tableToSafeAlias.get(fieldTable);
        if (safeAlias == null) {
            if (this.aliasToTable.containsValue(fieldTable)) {
                throw _Exceptions.selfJoinNonQualifiedField(field);
            } else {
                throw _Exceptions.unknownColumn(null, field);
            }
        }
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(safeAlias)
                .append(Constant.POINT)
                .append(this.dialect.quoteIfNeed(field.columnName()));
    }

    @Override
    public Stmt build() {
        throw dontSupportBuild();
    }

    static UnsupportedOperationException dontSupportBuild() {
        return new UnsupportedOperationException("Sub query context don't support build operation.");
    }

}
