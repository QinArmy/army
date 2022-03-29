package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.TableItem;
import io.army.criteria.TableItemGroup;
import io.army.criteria.Visible;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
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

    private TableContext(Map<String, TableItem> aliasToTable, Map<TableMeta<?>, String> tableToSafeAlias) {
        this.aliasToTable = _CollectionUtils.unmodifiableMap(aliasToTable);
        this.tableToSafeAlias = _CollectionUtils.unmodifiableMap(tableToSafeAlias);
    }


    static TableContext createContext(List<? extends _TableBlock> blockList, _Dialect dialect, final Visible visible) {
        final Map<String, TableItem> aliasToTable = new HashMap<>((int) (blockList.size() / 0.75F));
        final Map<TableMeta<?>, String> tableToSafeAlias = new HashMap<>((int) (blockList.size() / 0.75F));

        final Set<TableMeta<?>> selfJoinSet = new HashSet<>();
        final int size = blockList.size();
        _TableBlock block;
        for (int i = 0; i < size; i++) {
            block = blockList.get(i);
            final TableItem tableItem = block.tableItem();
            final String alias = block.alias();
            if (tableItem instanceof TableItemGroup) {
                throw new UnsupportedOperationException();
            }
            _DialectUtils.validateTableAlias(alias);
            if (aliasToTable.putIfAbsent(alias, tableItem) != null) {
                throw _Exceptions.tableAliasDuplication(block.alias());
            }
            if (!(tableItem instanceof TableMeta)) {
                continue;
            }
            if (visible != Visible.BOTH && tableItem instanceof ChildTableMeta) {
                checkParent((ChildTableMeta<?>) tableItem, alias, blockList, i);
            }
            if (selfJoinSet.contains(tableItem)) {
                continue;
            }
            if (tableToSafeAlias.putIfAbsent((TableMeta<?>) tableItem, dialect.quoteIfNeed(alias)) != null) {
                //this table self-join
                tableToSafeAlias.remove(tableItem);
                selfJoinSet.add((TableMeta<?>) tableItem);
            }
        }
        return new TableContext(aliasToTable, tableToSafeAlias);
    }

    private static void checkParent(final ChildTableMeta<?> child, final String childAlias
            , final List<? extends _TableBlock> blockList, final int index) {

        final ParentTableMeta<?> parent = child.parentMeta();
        if (!parent.containField(_MetaBridge.VISIBLE)) {
            return;
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
                return;
            }

        }// for

        boolean match = false;

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
                match = true;
                break;
            }

        }// for

        if (!match) {
            String m;
            m = String.format(
                    "%s mode isn't %s and %s contain %s field\n,but %s that alias is '%s' don't inner join(%s) %s."
                    , Visible.class.getSimpleName(), Visible.BOTH
                    , parent, _MetaBridge.VISIBLE
                    , child, childAlias
                    , "exists on predicate child.id = parent.id", parent);
            throw new CriteriaException(m);
        }


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
