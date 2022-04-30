package io.army.criteria.impl;

import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class CriteriaUtils {

    CriteriaUtils() {
        throw new UnsupportedOperationException();
    }


    /**
     * invoke after {@code asSelect()}
     *
     * @return a unmodifiable map
     */
    static Map<String, Selection> createSelectionMap(List<? extends SelectItem> selectItemList) {

        final Map<String, Selection> selectionMap = new HashMap<>();
        for (SelectItem item : selectItemList) {

            if (item instanceof Selection) {
                selectionMap.put(((Selection) item).alias(), (Selection) item); // if alias duplication then override. Be consistent with  statement executor.
            } else if (item instanceof SelectionGroup) {
                for (Selection selection : ((SelectionGroup) item).selectionList()) {
                    selectionMap.put(selection.alias(), selection); // if alias duplication then override.Be consistent with  statement executor.
                }
            }
        }
        return Collections.unmodifiableMap(selectionMap);
    }

    static void assertSelectItemSizeMatch(RowSet left, RowSet right) {
        final List<? extends SelectItem> leftList, rightList;

        leftList = ((_PartRowSet) left).selectItemList();
        rightList = ((_PartRowSet) right).selectItemList();

        int leftSize = 0, rightSize = 0;

        for (SelectItem item : leftList) {
            if (item instanceof Selection) {
                leftSize++;
            } else if (item instanceof SelectionGroup) {
                leftSize += ((SelectionGroup) item).selectionList().size();
            } else {
                throw _Exceptions.unknownSelectItem(item);
            }
        }

        for (SelectItem item : rightList) {
            if (item instanceof Selection) {
                rightSize++;
            } else if (item instanceof SelectionGroup) {
                rightSize += ((SelectionGroup) item).selectionList().size();
            } else {
                throw _Exceptions.unknownSelectItem(item);
            }
        }

        if (leftSize != rightSize) {
            String m = String.format("Left select list size[%s] and right select list size[%s] not match."
                    , leftSize, rightSize);
            throw new CriteriaException(m);
        }

    }


    static <T> List<T> unmodifiableList(@Nullable List<T> original) {
        return original == null ? Collections.emptyList() : Collections.unmodifiableList(original);
    }

    static <K, V> Map<K, V> unmodifiableMap(@Nullable Map<K, V> original) {
        return original == null ? Collections.emptyMap() : Collections.unmodifiableMap(original);
    }

    static List<_Predicate> predicateList(final List<_Predicate> predicateList) {
        final List<_Predicate> list;
        switch (predicateList.size()) {
            case 0:
                throw new IllegalStateException("no predicate clause.");
            case 1:
                list = Collections.singletonList(predicateList.get(0));
                break;
            default:
                list = Collections.unmodifiableList(predicateList);
        }
        return list;
    }

    static List<ReadWrapper> namedParamList(final List<ReadWrapper> wrapperList) {
        final List<ReadWrapper> list;
        switch (wrapperList.size()) {
            case 0:
                throw new IllegalStateException("no any name param.");
            case 1:
                list = Collections.singletonList(wrapperList.get(0));
                break;
            default:
                list = Collections.unmodifiableList(wrapperList);
        }
        return list;
    }

    static List<_Predicate> asPredicateList(final @Nullable List<IPredicate> predicateList
            , final @Nullable Supplier<CriteriaException> supplier) {
        final int size;
        if (predicateList == null || (size = predicateList.size()) == 0) {
            if (supplier != null) {
                throw supplier.get();
            }
            return Collections.emptyList();
        }

        List<_Predicate> list;
        if (size == 1) {
            list = Collections.singletonList((OperationPredicate) predicateList.get(0));
        } else {
            list = new ArrayList<>(size);
            for (IPredicate predicate : predicateList) {
                list.add((OperationPredicate) predicate);
            }
            list = Collections.unmodifiableList(list);
        }
        return list;
    }


    static <E extends Expression> List<_Expression> asExpressionList(final List<E> expressionList) {
        final int size = expressionList.size();
        List<_Expression> expList;
        switch (size) {
            case 0:
                throw new CriteriaException("expression list must not empty.");
            case 1:
                expList = Collections.singletonList((ArmyExpression) expressionList.get(0));
                break;
            default: {
                expList = new ArrayList<>(size);
                for (E exp : expressionList) {
                    expList.add((ArmyExpression) exp);
                }
                expList = Collections.unmodifiableList(expList);
            }

        }
        return expList;
    }

    static <S extends SortItem> List<ArmySortItem> asSortItemList(final List<S> sortItemList) {
        final int size = sortItemList.size();
        List<ArmySortItem> itemList;
        switch (size) {
            case 0:
                throw _Exceptions.sortItemListIsEmpty();
            case 1:
                itemList = Collections.singletonList((ArmySortItem) sortItemList.get(0));
                break;
            default: {
                itemList = new ArrayList<>(size);
                for (S sortItem : sortItemList) {
                    itemList.add((ArmySortItem) sortItem);
                }
                itemList = Collections.unmodifiableList(itemList);
            }

        }
        return itemList;
    }

    static List<_Predicate> onPredicates(IPredicate predicate1, IPredicate predicate2) {
        final List<_Predicate> list = new ArrayList<>(2);
        list.add((OperationPredicate) predicate1);
        list.add((OperationPredicate) predicate2);
        return Collections.unmodifiableList(list);
    }


    static List<Object> paramList(final List<?> paramList) {
        final int size = paramList.size();
        if (size == 0) {
            throw new CriteriaException("Batch dml parameter list must not empty.");
        }
        final Object firstParam = paramList.get(0);
        if (firstParam == null) {
            throw new NullPointerException("Batch parameter must non-null.");
        }
        final boolean isMap = firstParam instanceof Map;
        final Class<?> paramJavaType = firstParam.getClass();
        final List<Object> wrapperList = new ArrayList<>(size);
        for (Object param : paramList) {
            if (param == null) {
                throw new NullPointerException("Batch parameter must non-null.");
            }
            if (isMap) {
                if (!(param instanceof Map)) {
                    throw new CriteriaException("Batch parameter must be same java type.");
                }
            } else if (param.getClass() != paramJavaType) {
                throw new CriteriaException("Batch parameter must be same java type.");
            }
            wrapperList.add(param);
        }
        return Collections.unmodifiableList(wrapperList);
    }


    static List<?> paramList(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (!(value instanceof List)) {
            String m = String.format("%s key[%s] return isn't %s."
                    , Function.class.getName(), keyName, Long.class.getName());
            throw new CriteriaException(m);
        }
        return paramList((List<?>) value);
    }

    static _TableBlock leftBracketBlock() {
        return LeftBracketTableBlock.INSTANCE;
    }

    static _TableBlock rightBracketBlock() {
        return RightBracketTableBlock.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    static <C> C getCriteria(final Query query) {
        return ((CriteriaSpec<C>) query).getCriteria();
    }

    static <T extends Enum<T> & SQLWords> Set<T> asModifierSet(final Set<T> set, Consumer<T> consumer) {
        final Set<T> modifierSet;
        switch (set.size()) {
            case 0:
                modifierSet = Collections.emptySet();
                break;
            case 1: {
                Set<T> tempSet = null;
                for (T modifier : set) {
                    consumer.accept(modifier);
                    tempSet = Collections.singleton(modifier);
                    break;
                }
                modifierSet = tempSet;
            }
            break;
            default: {
                Set<T> tempSet = null;
                for (T modifier : set) {
                    if (tempSet == null) {
                        tempSet = EnumSet.of(modifier);
                    } else {
                        tempSet.add(modifier);
                    }
                }
                modifierSet = Collections.unmodifiableSet(tempSet);
            }
        }
        return modifierSet;
    }


    static CriteriaException unknownCriteriaContext(CriteriaContext context) {
        String m = String.format("Unknown %s[%s] type.", CriteriaContext.class.getName(), context.getClass().getName());
        return new CriteriaException(m);
    }

    static CriteriaException nonArmyExpression(Expression expression) {
        String m = String.format("%s isn't army class", expression.getClass().getName());
        return new CriteriaException(m);
    }


    private static final class LeftBracketTableBlock implements _TableBlock, _LeftBracketBlock {

        private static final LeftBracketTableBlock INSTANCE = new LeftBracketTableBlock();

        private LeftBracketTableBlock() {
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
        public _JoinType jointType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<_Predicate> predicates() {
            throw new UnsupportedOperationException();
        }

    }//LeftBracketTableBlock

    private static final class RightBracketTableBlock implements _TableBlock, _RightBracketBlock {

        private static final RightBracketTableBlock INSTANCE = new RightBracketTableBlock();

        private RightBracketTableBlock() {
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
        public _JoinType jointType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<_Predicate> predicates() {
            throw new UnsupportedOperationException();
        }

    }//RightBracketTableBlock

}
