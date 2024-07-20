/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @since 0.6.0
 */
final class TableContext {

    final Map<String, TabularItem> aliasToTable;

    final Map<TableMeta<?>, String> tableToSafeAlias;

    final Map<String, String> childAliasToParentAlias;

    private TableContext(Map<String, TabularItem> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias,
                         @Nullable Map<String, String> childAliasToParentAlias) {
        this.aliasToTable = _Collections.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = _Collections.unmodifiableMap(tableToSafeAlias);
        if (childAliasToParentAlias == null) {
            this.childAliasToParentAlias = Collections.emptyMap();
        } else {
            this.childAliasToParentAlias = _Collections.unmodifiableMap(childAliasToParentAlias);
        }
    }


    static TableContext forChild(final ChildTableMeta<?> table, final String tableAlias, final ArmyParser dialect) {
        final Map<String, TabularItem> aliasToTable = _Collections.hashMap(4);
        aliasToTable.put(tableAlias, table);
        final String parentAlias = ArmyParser.parentAlias(tableAlias);
        aliasToTable.put(parentAlias, table.parentMeta());

        final Map<TableMeta<?>, String> tableToSafeAlias = _Collections.hashMap(4);
        tableToSafeAlias.put(table, dialect.identifier(tableAlias));
        tableToSafeAlias.put(table.parentMeta(), dialect.identifier(parentAlias));

        return new TableContext(aliasToTable, tableToSafeAlias, null);
    }


    static TableContext forMerge(final _Merge stmt, final ArmyParser parser) {
        final TableMeta<?> targetTable = stmt.targetTable();
        final String targetAlias = stmt.targetAlias();

        final _TabularBlock sourceBlock = stmt.sourceBlock();
        final TabularItem sourceTabularItem = sourceBlock.tableItem();
        final String sourceAlias = sourceBlock.alias();

        final Map<String, TabularItem> aliasToTable = _Collections.hashMap(4);
        aliasToTable.put(targetAlias, targetTable);

        if (aliasToTable.putIfAbsent(sourceAlias, sourceTabularItem) != null) {
            throw _Exceptions.tableAliasDuplication(sourceAlias);
        }

        final Map<TableMeta<?>, String> tableToSafeAlias = _Collections.hashMap(4);
        tableToSafeAlias.put(targetTable, parser.identifier(targetAlias));

        if (sourceTabularItem instanceof TableMeta
                && tableToSafeAlias.putIfAbsent((TableMeta<?>) sourceTabularItem, parser.identifier(sourceAlias)) != null) {
            tableToSafeAlias.remove(sourceTabularItem);
        }
        return new TableContext(aliasToTable, tableToSafeAlias, null);
    }

    static TableContext forUpdate(final _JoinableUpdate stmt, final ArmyParser dialect, final Visible visible) {

        Map<ChildTableMeta<?>, Boolean> childMap = null;
        for (_ItemPair pair : stmt.itemPairList()) {
            if (pair instanceof _ItemPair._FieldItemPair) {
                childMap = handleUpdateField(((_ItemPair._FieldItemPair) pair).field(), childMap);
            } else if (pair instanceof _ItemPair._RowItemPair) {
                for (SqlField dataField : ((_ItemPair._RowItemPair) pair).rowFieldList()) {
                    childMap = handleUpdateField(dataField, childMap);
                }
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
        }
        final List<? extends _TabularBlock> blockList;
        blockList = stmt.tableBlockList();

        final Context context;
        context = new Context(dialect, childMap, visible, blockList.size());
        iterateTableReferences(blockList, context);

        if (stmt instanceof _SingleUpdate) {
            final TableMeta<?> targetTable;
            targetTable = ((_SingleUpdate) stmt).table();
            final String tableAlias;
            tableAlias = ((_SingleUpdate) stmt).tableAlias();
            if (context.aliasToTable.putIfAbsent(tableAlias, targetTable) != null) {
                throw _Exceptions.tableAliasDuplication(tableAlias);
            }

            context.tableToSafeAlias.putIfAbsent(targetTable, dialect.identifier(tableAlias));
        }
        return new TableContext(context.aliasToTable, context.tableToSafeAlias, context.childAliasToParentAlias);
    }


    static TableContext forDelete(final _JoinableDelete stmt, final ArmyParser dialect, final Visible visible) {
        final List<_Pair<String, TableMeta<?>>> pairList;
        if (stmt instanceof _MultiDelete) {
            pairList = ((_MultiDelete) stmt).deleteTableList();
            assert pairList.size() > 0;
        } else {
            pairList = Collections.emptyList();
        }

        Map<ChildTableMeta<?>, Boolean> childMap = null;
        TableMeta<?> table;
        for (_Pair<String, TableMeta<?>> pair : pairList) {
            table = pair.second;
            if (!(table instanceof ChildTableMeta)) {
                continue;
            }
            if (childMap == null) {
                childMap = _Collections.hashMap();
            }
            childMap.putIfAbsent((ChildTableMeta<?>) table, Boolean.TRUE);
        }

        final List<? extends _TabularBlock> blockList = stmt.tableBlockList();
        final Context context;
        context = new Context(dialect, childMap, visible, blockList.size());
        iterateTableReferences(blockList, context);

        if (stmt instanceof _SingleDelete) {
            final TableMeta<?> targetTable;
            targetTable = ((_SingleDelete) stmt).table();
            final String tableAlias;
            tableAlias = ((_SingleDelete) stmt).tableAlias();
            if (context.aliasToTable.putIfAbsent(tableAlias, targetTable) != null) {
                throw _Exceptions.tableAliasDuplication(tableAlias);
            }

            context.tableToSafeAlias.putIfAbsent(targetTable, dialect.identifier(tableAlias));
        }
        return new TableContext(context.aliasToTable, context.tableToSafeAlias, context.childAliasToParentAlias);
    }


    static TableContext forQuery(List<? extends _TabularBlock> blockList, ArmyParser dialect, final Visible visible) {
        final Context context;
        context = new Context(dialect, null, visible, blockList.size());
        iterateTableReferences(blockList, context);
        return new TableContext(context.aliasToTable, context.tableToSafeAlias, context.childAliasToParentAlias);
    }


    /**
     * @see #forUpdate(_JoinableUpdate, ArmyParser, Visible)
     */
    @Nullable
    private static Map<ChildTableMeta<?>, Boolean> handleUpdateField(final SqlField dataField
            , @Nullable Map<ChildTableMeta<?>, Boolean> childMap) {
        if (!(dataField instanceof TableField)) {
            //TODO
            throw new UnsupportedOperationException();
        }
        final TableMeta<?> table;
        table = ((TableField) dataField).tableMeta();
        if (!(table instanceof ChildTableMeta)) {
            return childMap;
        }
        if (childMap == null) {
            childMap = _Collections.hashMap();
        }
        childMap.putIfAbsent((ChildTableMeta<?>) table, Boolean.TRUE);
        return childMap;
    }


    private static void iterateTableReferences(final List<? extends _TabularBlock> blockList, final Context context) {

        final Map<String, TabularItem> aliasToTable = context.aliasToTable;
        final Map<TableMeta<?>, String> tableToSafeAlias = context.tableToSafeAlias;
        final Map<String, String> childAliasToParentAlias = context.childAliasToParentAlias;
        final Map<ChildTableMeta<?>, Boolean> childMap = context.childMap;

        final Map<TableMeta<?>, Boolean> selfJoinMap = context.selfJoinMap;

        final ArmyParser dialect = context.dialect;
        final Visible visible = context.visible;

        _TabularBlock block, parentBlock;
        String safeAlias, alias, parentAlias;
        TabularItem tableItem;
        ParentTableMeta<?> parent;
        ChildTableMeta<?> child;
        TableField parentId;
        final int blockSize = blockList.size(), lastIndex = blockSize - 1;
        for (int i = 0; i < blockSize; i++) {
            block = blockList.get(i);
            tableItem = block.tableItem();

            if (tableItem instanceof _NestedItems) {
                // recursively iterate
                iterateTableReferences(((_NestedItems) tableItem).tableBlockList(), context);
                continue;
            }

            alias = block.alias();
            if (!(tableItem instanceof _Cte)) {
                _DialectUtils.validateTableAlias(alias);
            }
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
            if (!(tableItem instanceof ChildTableMeta)) {
                continue;
            }
            child = (ChildTableMeta<?>) tableItem;
            parent = child.parentMeta();
            if (!childMap.containsKey(child)
                    && !(visible != Visible.BOTH && parent.containField(_MetaBridge.VISIBLE))) {
                continue;
            }
            //3.1 find parent from right
            if (i < lastIndex && nextIsParent(child, alias, (parentBlock = blockList.get(i + 1)))) {
                if (childAliasToParentAlias != null) {
                    childAliasToParentAlias.putIfAbsent(alias, parentBlock.alias());
                }
                continue;
            }

            //3.2 find parent from left
            if (i == 0 || (parentId = findParentId(child, alias, block.onClauseList())) == null) {
                throw noInnerJoinParent(child, alias, context);
            }
            if (parentId instanceof FieldMeta) {
                parentAlias = null;
            } else {
                parentAlias = ((QualifiedField<?>) parentId).tableAlias();
            }

            parentBlock = findParentFromLeft(child, parentAlias, blockList, i - 1);
            if (parentBlock == null) {
                throw noInnerJoinParent(child, alias, context);
            }
            assert parentBlock.tableItem() == parent;

            if (childAliasToParentAlias != null) {
                childAliasToParentAlias.putIfAbsent(alias, parentBlock.alias());
            }

        }// for


    }


    @Nullable
    private static boolean nextIsParent(final ChildTableMeta<?> child, final String childAlias
            , final _TabularBlock block) {
        final boolean match;
        switch (block.jointType()) {
            case JOIN:
            case STRAIGHT_JOIN: {
                match = block.tableItem() == child.parentMeta()
                        && findParentId(child, childAlias, block.onClauseList()) != null;
            }
            break;
            default://no-op
                match = false;
        }
        return match;

    }


    /**
     * @return null : finding failure
     */
    @Nullable
    private static _TabularBlock findParentFromLeft(final ChildTableMeta<?> child, final @Nullable String parentAlias
            , final List<? extends _TabularBlock> blockList, final int fromIndex) {

        final ParentTableMeta<?> parent = child.parentMeta();
        _TabularBlock block, parentBlock = null;
        TabularItem tableItem;
        _JoinType joinType;
        TableField parentId;
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
                        // finding failure
                        break outerFor;
                    }
                }
                break;
                default:
                    // finding failure
                    break outerFor;
            }

            tableItem = block.tableItem();
            if (!(tableItem instanceof TableMeta)) {
                // finding failure
                break;
            }
            if (tableItem instanceof ChildTableMeta && ((ChildTableMeta<?>) tableItem).parentMeta() == parent) {
                //brother table
                parentId = findParentId((ChildTableMeta<?>) tableItem, block.alias(), block.onClauseList());
                if (parentId == null) {
                    // finding failure
                    break;
                }
                if (!(parentId instanceof QualifiedField)) {
                    continue;
                }
                if (parentAlias != null
                        && !parentAlias.equals(((QualifiedField<?>) parentId).tableAlias())) {
                    // finding failure
                    break;
                }
                continue;
            }

            if (tableItem != parent || (parentAlias != null && !parentAlias.equals(block.alias()))) {
                // finding failure
                break;
            }
            parentBlock = block;
            break;

        }// for

        return parentBlock;
    }


    @Nullable
    private static TableField findParentId(final ChildTableMeta<?> child, final String alias,
                                           final List<_Predicate> predicateList) {
        TableField parentId = null;
        for (_Predicate predicate : predicateList) {
            parentId = predicate.findParentId(child, alias);
            if (parentId != null) {
                break;
            }
        }
        return parentId;
    }

    private static CriteriaException noInnerJoinParent(final ChildTableMeta<?> child, final String alias
            , final Context context) {
        final String reason;
        if (context.childAliasToParentAlias != null) {
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

        private final ArmyParser dialect;

        private final Visible visible;

        private final Map<String, TabularItem> aliasToTable;

        private final Map<TableMeta<?>, String> tableToSafeAlias;

        private final Map<TableMeta<?>, Boolean> selfJoinMap;

        private final Map<ChildTableMeta<?>, Boolean> childMap;

        private final Map<String, String> childAliasToParentAlias;

        private Context(ArmyParser dialect, @Nullable Map<ChildTableMeta<?>, Boolean> childMap, Visible visible
                , int blockSize) {
            this.dialect = dialect;
            this.visible = visible;
            this.aliasToTable = new HashMap<>((int) (blockSize / 0.75F));
            this.tableToSafeAlias = new HashMap<>((int) (blockSize / 0.75F));

            this.selfJoinMap = _Collections.hashMap();
            if (childMap == null) {
                this.childAliasToParentAlias = null;
                this.childMap = Collections.emptyMap();
            } else {
                this.childAliasToParentAlias = _Collections.hashMap();
                this.childMap = _Collections.unmodifiableMap(childMap);
            }

        }


    }//Context


}
