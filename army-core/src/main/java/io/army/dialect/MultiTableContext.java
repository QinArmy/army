package io.army.dialect;

import io.army.criteria.TableItem;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.HashMap;
import java.util.Map;

abstract class MultiTableContext extends StmtContext implements _StmtContext {

    final Map<String, TableItem> aliasToTable;

    final Map<TableMeta<?>, String> tableToSafeAlias;

    Map<String, String> aliasToSafeAlias;


    MultiTableContext(TableContext tableContext, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);
        this.aliasToTable = tableContext.aliasToTable;
        this.tableToSafeAlias = tableContext.tableToSafeAlias;

    }

    MultiTableContext(TableContext tableContext, StmtContext outerContext) {
        super(outerContext);
        this.aliasToTable = tableContext.aliasToTable;
        this.tableToSafeAlias = tableContext.tableToSafeAlias;
    }

    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (this.aliasToTable.get(tableAlias) == field.tableMeta()) {
            this.appendSafeField(tableAlias, field);
        } else if (this instanceof _SubQueryContext) {
            this.appendOuterField(tableAlias, field);
        } else {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
    }


    @Override
    public final void appendField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final String safeTableAlias;
        safeTableAlias = this.tableToSafeAlias.get(fieldTable);
        if (safeTableAlias != null) {
            final StringBuilder sqlBuilder;
            sqlBuilder = this.sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);
            this.dialect.safeObjectName(field.columnName(), sqlBuilder);
        } else if (this instanceof _SubQueryContext) {
            this.appendOuterField(field);
        } else if (this.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else {
            throw _Exceptions.unknownColumn(field);
        }
    }


    @Override
    public final String safeTableAlias(final TableMeta<?> table, final String alias) {
        if (this.aliasToTable.get(alias) != table) {
            throw _Exceptions.unknownTable(table, alias);
        }
        String safeAlias;
        safeAlias = this.tableToSafeAlias.get(table);
        if (safeAlias == null) {
            // table self-join
            final Map<String, String> aliasToSafeAlias = getAliasToSafeAlias();
            safeAlias = aliasToSafeAlias.get(alias);
            if (safeAlias == null) {
                safeAlias = this.dialect.quoteIfNeed(alias);
                aliasToSafeAlias.put(alias, safeAlias);
            }
        }
        return safeAlias;
    }


    void appendOuterField(String tableAlias, FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }

    void appendOuterField(FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }

    final Map<String, String> getAliasToSafeAlias() {
        Map<String, String> aliasToSafeAlias = this.aliasToSafeAlias;
        if (aliasToSafeAlias == null) {
            aliasToSafeAlias = new HashMap<>();
            this.aliasToSafeAlias = aliasToSafeAlias;
        }
        return aliasToSafeAlias;
    }

    final void appendSafeField(final String tableAlias, final FieldMeta<?> field) {
        String safeTableAlias;
        safeTableAlias = this.tableToSafeAlias.get(field.tableMeta());
        if (safeTableAlias == null) {
            // belongOf self-join
            final Map<String, String> aliasToSafeAlias = getAliasToSafeAlias();
            safeTableAlias = aliasToSafeAlias.get(tableAlias);
            if (safeTableAlias == null) {
                safeTableAlias = this.dialect.quoteIfNeed(tableAlias);
                aliasToSafeAlias.put(tableAlias, safeTableAlias);
            }
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE)
                .append(safeTableAlias)
                .append(_Constant.POINT);
        this.dialect.safeObjectName(field.columnName(), sqlBuilder);
    }


}
