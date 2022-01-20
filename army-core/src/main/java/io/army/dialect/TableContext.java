package io.army.dialect;

import io.army.criteria.Query;
import io.army.criteria.TablePart;
import io.army.criteria.TablePartGroup;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._TableBlock;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TableContext {

    final Map<String, TablePart> aliasToTable;

    final Map<TableMeta<?>, String> tableToSafeAlias;

    private TableContext(Map<String, TablePart> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias) {
        this.aliasToTable = CollectionUtils.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = CollectionUtils.unmodifiableMap(tableToSafeAlias);
    }


    static TableContext createContext(Query query, _Dialect dialect) {
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
        return new TableContext(aliasToTable, tableToSafeAlias);
    }


}
