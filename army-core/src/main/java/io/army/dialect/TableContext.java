package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.NestedItems;
import io.army.criteria.TableItem;
import io.army.criteria.Visible;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @since 1.0
 */
final class TableContext {

    final Map<String, TableItem> aliasToTable;

    final Map<TableMeta<?>, String> tableToSafeAlias;

    final Map<String, String> childAliasToParentAlias;

    private TableContext(Map<String, TableItem> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
            , @Nullable Map<String, String> childAliasToParentAlias) {
        this.aliasToTable = _CollectionUtils.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = _CollectionUtils.unmodifiableMap(tableToSafeAlias);
        if (childAliasToParentAlias == null) {
            this.childAliasToParentAlias = Collections.emptyMap();
        } else {
            this.childAliasToParentAlias = _CollectionUtils.unmodifiableMap(childAliasToParentAlias);
        }
    }


    static TableContext forChild(final ChildTableMeta<?> table, final String tableAlias, final ArmyDialect dialect) {
        final Map<String, TableItem> aliasToTable = new HashMap<>(4);
        aliasToTable.put(tableAlias, table);
        final String parentAlias = _DialectUtils.parentAlias(tableAlias);
        aliasToTable.put(parentAlias, table.parentMeta());

        final Map<TableMeta<?>, String> tableToSafeAlias = new HashMap<>(4);
        tableToSafeAlias.put(table, dialect.identifier(tableAlias));
        tableToSafeAlias.put(table.parentMeta(), dialect.identifier(parentAlias));

        return new TableContext(aliasToTable, tableToSafeAlias, null);
    }


    static TableContext createContext(List<? extends _TableBlock> blockList, ArmyDialect dialect
            , final Visible visible, final boolean multiDml) {
        final Context context;
        context = new Context(dialect, visible, multiDml, blockList.size());
        iterateTableReferences(blockList, context);
        return new TableContext(context.aliasToTable, context.tableToSafeAlias, context.childAliasToParentAlias);
    }


    private static void iterateTableReferences(final List<? extends _TableBlock> blockList, final Context context) {

        final Map<String, TableItem> aliasToTable = context.aliasToTable;
        final Map<TableMeta<?>, String> tableToSafeAlias = context.tableToSafeAlias;
        final Map<String, String> childAliasToParentAlias = context.childAliasToParentAlias;
        final Map<TableMeta<?>, Boolean> selfJoinMap = context.selfJoinMap;

        final ArmyDialect dialect = context.dialect;
        final Visible visible = context.visible;

        final boolean dml = childAliasToParentAlias != null;

        _TableBlock block, neighborBlock;
        String safeAlias, alias;
        TableItem tableItem;
        ParentTableMeta<?> parent;
        _JoinType joinType;
        final int blockSize = blockList.size(), lastIndex = blockSize - 1;
        for (int i = 0; i < blockSize; i++) {
            block = blockList.get(i);
            tableItem = block.tableItem();

            if (tableItem instanceof NestedItems) {
                iterateTableReferences(((_NestedItems) tableItem).tableBlockList(), context);
                continue;
            }

            alias = block.alias();
            _DialectUtils.validateTableAlias(alias);
            if (aliasToTable.putIfAbsent(alias, tableItem) != null) {
                throw _Exceptions.tableAliasDuplication(alias);
            }
            if (!(tableItem instanceof TableMeta)) {
                continue;
            }
            safeAlias = dialect.identifier(alias);
            if (!selfJoinMap.containsKey(tableItem)
                    && tableToSafeAlias.putIfAbsent((TableMeta<?>) tableItem, safeAlias) != null) {
                //this table self-join
                tableToSafeAlias.remove(tableItem);
                selfJoinMap.put((TableMeta<?>) tableItem, Boolean.TRUE);
            }

            if (!(tableItem instanceof ChildTableMeta && (dml || visible != Visible.BOTH))) {
                continue;
            }
            parent = ((ChildTableMeta<?>) tableItem).parentMeta();
            if (!(dml || parent.containField(_MetaBridge.VISIBLE))) {
                continue;
            }
            joinType = block.jointType();
            switch (joinType) {
                case JOIN:
                case STRAIGHT_JOIN: {
                    if (i == lastIndex) {
                        break;
                    }
                    neighborBlock = blockList.get(i + 1);
                    if (neighborBlock.tableItem() != parent) {
                        break;
                    }
                    if (isParentChildJoin((ChildTableMeta<?>) tableItem, neighborBlock.predicateList())) {
                        continue;
                    }
                }
                break;
                default:
                    //no-op
            }

            if (i == 0) {
                throw noInnerJoinParent((ChildTableMeta<?>) tableItem, alias, dml);
            }
            switch (joinType) {
                case JOIN:
                case STRAIGHT_JOIN: {
                    neighborBlock = blockList.get(i - 1);
                    if (neighborBlock.tableItem() != parent) {
                        throw noInnerJoinParent((ChildTableMeta<?>) tableItem, alias, dml);
                    }
                    if (!isParentChildJoin((ChildTableMeta<?>) tableItem, neighborBlock.predicateList())) {
                        throw noInnerJoinParent((ChildTableMeta<?>) tableItem, alias, dml);
                    }
                }
                break;
                default:
                    throw noInnerJoinParent((ChildTableMeta<?>) tableItem, alias, dml);
            }


        }


    }

    private static CriteriaException noInnerJoinParent(final ChildTableMeta<?> child, final String alias, final boolean dml) {
        return new CriteriaException("");
    }

    private static boolean isParentChildJoin(final ChildTableMeta<?> child, final List<_Predicate> predicateList) {
        boolean match = false;
        for (_Predicate predicate : predicateList) {
            if (predicate.isParentChildJoin()) {
                match = true;
                break;
            }
        }
        return match;
    }

    private static final class Context {

        private final ArmyDialect dialect;

        private final Visible visible;

        private final Map<String, TableItem> aliasToTable;

        private final Map<TableMeta<?>, String> tableToSafeAlias;

        private final Map<TableMeta<?>, Boolean> selfJoinMap;

        private final Map<String, String> childAliasToParentAlias;

        private Context(ArmyDialect dialect, Visible visible, boolean multiDml, int blockSize) {
            this.dialect = dialect;
            this.visible = visible;
            this.aliasToTable = new HashMap<>((int) (blockSize / 0.75F));
            this.tableToSafeAlias = new HashMap<>((int) (blockSize / 0.75F));

            this.selfJoinMap = new HashMap<>();
            if (multiDml) {
                this.childAliasToParentAlias = new HashMap<>();
            } else {
                this.childAliasToParentAlias = null;
            }


        }


    }//Context


}
