package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._TableBlock;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class SimpleQueryContext extends _BaseSqlContext implements _SimpleQueryContext {

    static SimpleQueryContext create(_SqlContext outerContext, Query query, Dialect dialect, Visible visible) {

        final List<? extends _TableBlock> blockList = ((_Query) query).tableBlockList();
        final Map<String, TablePart> aliasToTable = new HashMap<>((int) (blockList.size() / 0.75F));
        final Map<TableMeta<?>, String> tableToSafeAlias = new HashMap<>((int) (blockList.size() / 0.75F));
        for (_TableBlock block : blockList) {
            final TablePart tablePart = block.table();
            final String alias = block.alias();
            if (tablePart instanceof TablePartGroup) {
                throw new UnsupportedOperationException();
            } else {
                _DialectUtils.validateTableAlias(alias);
                if (aliasToTable.putIfAbsent(alias, tablePart) != null) {
                    throw _Exceptions.tableAliasDuplication(block.alias());
                }
                if (!(tablePart instanceof TableMeta)) {
                    continue;
                }
                if (tableToSafeAlias.putIfAbsent((TableMeta<?>) tablePart, dialect.quoteIfNeed(alias)) != null) {
                    //this table self-join
                    tableToSafeAlias.remove(tablePart);
                }
            }
        }
        SimpleQueryContext context;
        if (query instanceof Select) {

        } else if (query instanceof SubQuery) {

        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return null;
    }

    private final Map<String, TablePart> aliasToTable;

    private final Map<TableMeta<?>, String> tableToSafeAlias;


    private SimpleQueryContext(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
            , Dialect dialect, Visible visible) {
        super(dialect, visible);
        this.aliasToTable = CollectionUtils.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = CollectionUtils.unmodifiableMap(tableToSafeAlias);
    }

    private SimpleQueryContext(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
            , _BaseSqlContext outerContext) {
        super(outerContext);
        this.aliasToTable = CollectionUtils.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = CollectionUtils.unmodifiableMap(tableToSafeAlias);
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {

    }

    @Override
    public void appendField(FieldMeta<?, ?> field) {

    }

    @Override
    public Stmt build() {
        return null;
    }

    private static final class SimpleSelect extends SimpleQueryContext implements _SelectContext {

        private SimpleSelect(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
                , Dialect dialect, Visible visible) {
            super(aliasToTable, tableToSafeAlias, dialect, visible);
        }

        private SimpleSelect(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
                , _BaseSqlContext outerContext) {
            super(aliasToTable, tableToSafeAlias, outerContext);
        }

    }// SimpleSelect

    private static final class SimpleSubQuery extends SimpleQueryContext implements _SubQueryContext {

        private SimpleSubQuery(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
                , Dialect dialect, Visible visible) {
            super(aliasToTable, tableToSafeAlias, dialect, visible);
        }

        private SimpleSubQuery(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
                , _BaseSqlContext outerContext) {
            super(aliasToTable, tableToSafeAlias, outerContext);
        }
    }//SimpleSubQuery


}
