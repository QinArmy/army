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

        _TableBlock block, parentBlock;
        String safeAlias, alias;
        TableItem tableItem;
        ParentTableMeta<?> parent;
        final int blockSize = blockList.size(), lastIndex = blockSize - 1;
        for (int i = 0; i < blockSize; i++) {
            block = blockList.get(i);
            tableItem = block.tableItem();

            if (tableItem instanceof NestedItems) {
                // recursively iterate
                iterateTableReferences(((_NestedItems) tableItem).tableBlockList(), context);
                continue;
            }

            alias = block.alias();
            _DialectUtils.validateTableAlias(alias);
            //1. create alias to tableItem map
            if (aliasToTable.putIfAbsent(alias, tableItem) != null) {
                throw _Exceptions.tableAliasDuplication(alias);
            }
            if (!(tableItem instanceof TableMeta)) {
                continue;
            }
            //2. create TableMeta to safe table alias map
            safeAlias = dialect.identifier(alias);
            if (!selfJoinMap.containsKey(tableItem)
                    && tableToSafeAlias.putIfAbsent((TableMeta<?>) tableItem, safeAlias) != null) {
                //this table self-join
                tableToSafeAlias.remove(tableItem);
                selfJoinMap.put((TableMeta<?>) tableItem, Boolean.TRUE);
            }
            //3. create child alias to parent alias map
            if (!(tableItem instanceof ChildTableMeta && (dml || visible != Visible.BOTH))) {
                continue;
            }
            parent = ((ChildTableMeta<?>) tableItem).parentMeta();
            if (!(dml || parent.containField(_MetaBridge.VISIBLE))) {
                continue;
            }
            //3.1 find parent from right
            if (i < lastIndex) {
                parentBlock = findParentFromRight(parent, blockList, i + 1, false);
                if (parentBlock != null && parentBlock != ContinueBlock.INSTANCE) {
                    //no bug,assert success
                    assert parentBlock.tableItem() == parent;
                    if (childAliasToParentAlias != null
                            && childAliasToParentAlias.putIfAbsent(alias, parentBlock.alias()) != null) {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                    continue;
                }
            }

            if (i < 1) {
                throw noInnerJoinParent((ChildTableMeta<?>) tableItem, alias, dml);
            }
            //3.2 find parent from left
            parentBlock = findParentFromLeft(parent, blockList, i - 1);

            if (parentBlock == null || parentBlock == ContinueBlock.INSTANCE) {
                throw noInnerJoinParent((ChildTableMeta<?>) tableItem, alias, dml);
            }
            //no bug,assert success
            assert parentBlock.tableItem() == parent;

            if (childAliasToParentAlias != null
                    && childAliasToParentAlias.putIfAbsent(alias, parentBlock.alias()) != null) {
                //no bug,never here
                throw new IllegalStateException();
            }


        }// for


    }


    /**
     * @return null : find failure
     */
    @Nullable
    private static _TableBlock findParentFromRight(final ParentTableMeta<?> parent
            , final List<? extends _TableBlock> blockList, final int fromIndex, final boolean nested) {
        final int blockSize = blockList.size();

        _TableBlock block, parentBlock = ContinueBlock.INSTANCE;
        TableItem tableItem;
        outerFor:
        for (int i = fromIndex; i < blockSize; i++) {
            block = blockList.get(i);
            switch (block.jointType()) {
                case JOIN:
                case STRAIGHT_JOIN:
                    break;
                case NONE: {
                    if (!(nested && i == 0)) {
                        // find failure
                        parentBlock = null;
                        break outerFor;
                    }
                }
                break;
                default:
                    // find failure
                    parentBlock = null;
                    break outerFor;
            }

            tableItem = block.tableItem();
            if (tableItem instanceof NestedItems) {
                parentBlock = findParentFromRight(parent, ((_NestedItems) tableItem).tableBlockList(), 0, true);
                if (parentBlock != ContinueBlock.INSTANCE) {
                    // success or failure
                    break;
                }
                continue;
            }

            if (!(tableItem instanceof TableMeta)) {
                // find failure
                parentBlock = null;
                break;
            }

            if (isNotIdsEquals((TableMeta<?>) tableItem, block.alias(), block.predicateList())) {
                // find failure
                parentBlock = null;
                break;
            }

            if (tableItem == parent) {
                //find success
                parentBlock = block;
                break;
            }


        }// for

        return parentBlock;
    }


    /**
     * @return null : find failure
     */
    @Nullable
    private static _TableBlock findParentFromLeft(final ParentTableMeta<?> parent
            , final List<? extends _TableBlock> blockList, final int fromIndex) {

        _TableBlock block, parentBlock = ContinueBlock.INSTANCE;
        TableItem tableItem;
        List<? extends _TableBlock> nestedBlockList;
        _JoinType joinType;
        outerFor:
        for (int i = fromIndex; i > -1; i--) {
            block = blockList.get(i);
            joinType = block.jointType();
            switch (joinType) {
                case JOIN:
                case STRAIGHT_JOIN:
                    break;
                case NONE: {
                    if (i != 0) {
                        // find failure
                        parentBlock = null;
                        break outerFor;
                    }
                }
                break;
                default:
                    // find failure
                    parentBlock = null;
                    break outerFor;
            }

            tableItem = block.tableItem();
            if (tableItem instanceof NestedItems) {
                nestedBlockList = ((_NestedItems) tableItem).tableBlockList();
                parentBlock = findParentFromLeft(parent, nestedBlockList, nestedBlockList.size() - 1);
                if (parentBlock != ContinueBlock.INSTANCE) {
                    // success or failure
                    break;
                }
                continue;
            }

            if (!(tableItem instanceof TableMeta)) {
                // find failure
                parentBlock = null;
                break;
            }

            if (joinType == _JoinType.NONE) {
                if (i < blockList.size() - 1
                        && isNotIdsEquals((TableMeta<?>) tableItem, block.alias(), blockList.get(i + 1).predicateList())) {
                    // find failure
                    parentBlock = null;
                    break;
                }
            } else if (isNotIdsEquals((TableMeta<?>) tableItem, block.alias(), block.predicateList())) {
                // find failure
                parentBlock = null;
                break;
            }

            if (tableItem == parent) {
                //find success
                parentBlock = block;
                break;
            }


        }// for

        return parentBlock;
    }


    private static boolean isNotIdsEquals(final TableMeta<?> table, final String alias
            , final List<_Predicate> predicateList) {
        boolean isNot = true;
        for (_Predicate predicate : predicateList) {
            if (predicate.isIdsEquals(table, alias)) {
                isNot = false;
                break;
            }
        }
        return isNot;
    }

    private static CriteriaException noInnerJoinParent(final ChildTableMeta<?> child, final String alias
            , final boolean dml) {
        final String reason;
        if (dml) {
            reason = String.format("%s as %s no inner join(%s) parent %s in multi-table DML statement."
                    , child, alias, "exists on predicate child.id = parent.id", child.parentMeta());
        } else {
            reason = String.format(
                    "%s mode isn't %s and %s contain %s field\n,but %s that alias is '%s' don't inner join(%s) %s."
                    , Visible.class.getSimpleName(), Visible.BOTH
                    , child.parentMeta(), _MetaBridge.VISIBLE
                    , child, alias
                    , "exists on predicate child.id = parent.id", child.parentMeta());
        }
        return new CriteriaException(reason);
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


    private static final class ContinueBlock implements _TableBlock {

        private static final ContinueBlock INSTANCE = new ContinueBlock();

        @Override
        public _JoinType jointType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableItem tableItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String alias() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<_Predicate> predicateList() {
            throw new UnsupportedOperationException();
        }

    }//ContinueBlock


}
