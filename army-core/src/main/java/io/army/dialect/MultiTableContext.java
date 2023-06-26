package io.army.dialect;

import io.army.criteria.TabularItem;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

final class MultiTableContext implements _MultiTableContext {

    final StatementContext stmtContext;

    final Map<String, TabularItem> aliasToTable;

    final Map<TableMeta<?>, String> tableToSafeAlias;

    private final BiConsumer<String, FieldMeta<?>> outerFieldConsumer;

    private final Consumer<FieldMeta<?>> outerFieldOnlyConsumer;

    private Map<String, String> aliasToSafeAlias;


    MultiTableContext(StatementContext stmtContext, TableContext tableContext,
                      @Nullable BiConsumer<String, FieldMeta<?>> outerFieldConsumer,
                      @Nullable Consumer<FieldMeta<?>> outerFieldOnlyConsumer) {
        this.stmtContext = stmtContext;
        this.aliasToTable = tableContext.aliasToTable;
        this.tableToSafeAlias = tableContext.tableToSafeAlias;
        this.outerFieldConsumer = outerFieldConsumer;
        this.outerFieldOnlyConsumer = outerFieldOnlyConsumer;

    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        final BiConsumer<String, FieldMeta<?>> outerFieldConsumer;
        if (this.aliasToTable.get(tableAlias) == field.tableMeta()) {
            this.appendSafeField(tableAlias, field);
        } else if ((outerFieldConsumer = this.outerFieldConsumer) != null) {
            outerFieldConsumer.accept(tableAlias, field);
        } else {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
    }


    @Override
    public void appendField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final String safeTableAlias;
        safeTableAlias = this.tableToSafeAlias.get(fieldTable);
        final BiConsumer<String, FieldMeta<?>> outerFieldConsumer;
        if (safeTableAlias != null) {
            final StringBuilder sqlBuilder = this.stmtContext.sqlBuilder;
            sqlBuilder.append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);
            this.stmtContext.parser.safeObjectName(field, sqlBuilder);
        } else if (this.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else if ((outerFieldConsumer = this.outerFieldConsumer) != null) {
            outerFieldConsumer.accept(null, field);
        } else {
            throw _Exceptions.unknownColumn(field);
        }
    }


    @Override
    public void appendFieldOnly(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final Consumer<FieldMeta<?>> outerFieldOnlyConsumer;
        if (this.tableToSafeAlias.get(fieldTable) != null) {
            this.stmtContext.parser.safeObjectName(field, this.stmtContext.sqlBuilder);
        } else if (this.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else if ((outerFieldOnlyConsumer = this.outerFieldOnlyConsumer) != null) {
            outerFieldOnlyConsumer.accept(field);
        } else {
            throw _Exceptions.unknownColumn(field);
        }
    }

    @Override
    public String safeTableAlias(final TableMeta<?> table, final String alias) {
        if (this.aliasToTable.get(alias) != table) {
            throw _Exceptions.unknownTable(table, alias);
        }
        String safeAlias;
        safeAlias = this.tableToSafeAlias.get(table);
        if (safeAlias == null) {
            // table self-join
            safeAlias = getAliasToSafeAlias().computeIfAbsent(alias, this.stmtContext.parser::identifier);
        }
        return safeAlias;
    }

    @Override
    public String safeTableAlias(final String alias) {
        if (this.aliasToTable.get(alias) == null) {
            throw _Exceptions.unknownTableAlias(alias);
        }
        return this.getAliasToSafeAlias().computeIfAbsent(alias, this.stmtContext.parser::identifier);
    }

    @Override
    public final String saTableAliasOf(final TableMeta<?> table) {
        final String safeAlias;
        safeAlias = this.tableToSafeAlias.get(table);
        if (safeAlias == null) {
            if (this.aliasToTable.containsValue(table)) {
                throw _Exceptions.tableSelfJoin(table);
            } else {
                throw _Exceptions.unknownTable(table, "");
            }
        }
        return safeAlias;
    }

    @Override
    public TabularItem tableItemOf(final String tableAlias) {
        final TabularItem tableItem;
        tableItem = this.aliasToTable.get(tableAlias);
        if (tableItem == null) {
            throw _Exceptions.unknownTableAlias(tableAlias);
        }
        return tableItem;
    }


    Map<String, String> getAliasToSafeAlias() {
        Map<String, String> aliasToSafeAlias = this.aliasToSafeAlias;
        if (aliasToSafeAlias == null) {
            aliasToSafeAlias = _Collections.hashMap();
            this.aliasToSafeAlias = aliasToSafeAlias;
        }
        return aliasToSafeAlias;
    }

    void appendSafeField(final String tableAlias, final FieldMeta<?> field) {
        String safeTableAlias;
        safeTableAlias = this.tableToSafeAlias.get(field.tableMeta());
        if (safeTableAlias == null) {
            //  self-join
            safeTableAlias = getAliasToSafeAlias().computeIfAbsent(tableAlias, this.stmtContext.parser::identifier);
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.stmtContext.sqlBuilder
                .append(_Constant.SPACE)
                .append(safeTableAlias)
                .append(_Constant.PERIOD);
        this.stmtContext.parser.safeObjectName(field, sqlBuilder);
    }


}
