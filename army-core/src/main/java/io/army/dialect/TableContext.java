package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.NestedItems;
import io.army.criteria.TableItem;
import io.army.criteria.Visible;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;


/**
 * @since 1.0
 */
final class TableContext {

    final Map<String, TableItem> aliasToTable;

    final Map<TableMeta<?>, String> tableToSafeAlias;

    final Map<String, String> childSafeAliasToParentSafeAlias;

    private TableContext(Map<String, TableItem> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias
            , @Nullable Map<String, String> childSafeAliasToParentSafeAlias) {
        this.aliasToTable = _CollectionUtils.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = _CollectionUtils.unmodifiableMap(tableToSafeAlias);
        if (childSafeAliasToParentSafeAlias == null) {
            this.childSafeAliasToParentSafeAlias = Collections.emptyMap();
        } else {
            this.childSafeAliasToParentSafeAlias = Collections.unmodifiableMap(childSafeAliasToParentSafeAlias);
        }
    }


    static TableContext createContext(List<? extends _TableBlock> blockList, _Dialect dialect
            , final Visible visible, final boolean multiUpdate) {
        final Map<String, TableItem> aliasToTable = new HashMap<>((int) (blockList.size() / 0.75F));
        final Map<TableMeta<?>, String> tableToSafeAlias = new HashMap<>((int) (blockList.size() / 0.75F));

        final Map<String, String> childSafeAliasToParentSafeAlias;
        if (multiUpdate) {
            childSafeAliasToParentSafeAlias = new HashMap<>();
        } else {
            childSafeAliasToParentSafeAlias = null;
        }

        final Set<TableMeta<?>> selfJoinSet = new HashSet<>();
        final int size = blockList.size();
        _TableBlock block, parentBlock;
        String safeAlias;
        for (int i = 0; i < size; i++) {
            block = blockList.get(i);
            final TableItem tableItem = block.tableItem();
            final String alias = block.alias();
            if (tableItem instanceof NestedItems) {
                throw new UnsupportedOperationException();
            }
            _DialectUtils.validateTableAlias(alias);
            if (aliasToTable.putIfAbsent(alias, tableItem) != null) {
                throw _Exceptions.tableAliasDuplication(block.alias());
            }
            if (!(tableItem instanceof TableMeta)) {
                continue;
            }
            safeAlias = dialect.quoteIfNeed(alias);
            if (tableItem instanceof ChildTableMeta && (multiUpdate || visible != Visible.BOTH)) {
                parentBlock = checkParent((ChildTableMeta<?>) tableItem, alias, blockList, i, multiUpdate);
                if (parentBlock != null && childSafeAliasToParentSafeAlias != null) {
                    childSafeAliasToParentSafeAlias.putIfAbsent(safeAlias, dialect.quoteIfNeed(parentBlock.alias()));
                }
            }

            if (selfJoinSet.contains(tableItem)) {
                continue;
            }
            if (tableToSafeAlias.putIfAbsent((TableMeta<?>) tableItem, safeAlias) != null) {
                //this table self-join
                tableToSafeAlias.remove(tableItem);
                selfJoinSet.add((TableMeta<?>) tableItem);
            }
        }
        return new TableContext(aliasToTable, tableToSafeAlias, childSafeAliasToParentSafeAlias);
    }

    @Nullable
    private static _TableBlock checkParent(final ChildTableMeta<?> child, final String childAlias
            , final List<? extends _TableBlock> blockList, final int index, final boolean multiUpdate) {

        final ParentTableMeta<?> parent = child.parentMeta();
        if (!multiUpdate && !parent.containField(_MetaBridge.VISIBLE)) {
            return null;
        }

        final int size = blockList.size();
        _TableBlock block;
        rightFor:
        for (int i = index + 1; i < size; i++) {
            block = blockList.get(i);

            switch (block.jointType()) {
                case JOIN:
                case STRAIGHT_JOIN:
                case CROSS_JOIN:
                    //non out join
                    break;
                case LEFT_JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                    break rightFor;
                case NONE:
                    String m = String.format("Table that alias is %s join type[%s] error."
                            , block.alias(), _JoinType.NONE);
                    throw new CriteriaException(m);
                default:
                    throw _Exceptions.unexpectedEnum(block.jointType());

            }
            if (block.tableItem() == parent && isParentChildJoin(block.predicates())) {
                // validate success.
                return block;
            }

        }// for

        _TableBlock parentBlock = null;

        leftFor:
        for (int i = index - 1; i > -1; i--) {
            block = blockList.get(i);
            switch (block.jointType()) {
                case JOIN:
                case STRAIGHT_JOIN:
                case CROSS_JOIN:
                    //no-op,not out join
                    break;
                case NONE: {
                    if (i != 0) {
                        String m = String.format("Table that alias is %s join type[%s] error."
                                , block.alias(), _JoinType.NONE);
                        throw new CriteriaException(m);
                    }
                }
                break;
                case LEFT_JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                    break leftFor;
                default:
                    throw _Exceptions.unexpectedEnum(block.jointType());

            }
            if (block.tableItem() == parent && isParentChildJoin(block.predicates())) {
                // validate success.
                parentBlock = block;
                break;
            }

        }// for

        if (parentBlock == null && parent.containField(_MetaBridge.VISIBLE)) {
            String m;
            m = String.format(
                    "%s mode isn't %s and %s contain %s field\n,but %s that alias is '%s' don't inner join(%s) %s."
                    , Visible.class.getSimpleName(), Visible.BOTH
                    , parent, _MetaBridge.VISIBLE
                    , child, childAlias
                    , "exists on predicate child.id = parent.id", parent);
            throw new CriteriaException(m);
        }
        return parentBlock;
    }

    private static boolean isParentChildJoin(final List<_Predicate> onPredicates) {
        boolean match = false;
        for (_Predicate predicate : onPredicates) {
            if (predicate.isParentChildJoin()) {
                match = true;
                break;
            }
        }
        return match;
    }


}
