package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SortPart;
import io.army.lang.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class CriteriaUtils {

    CriteriaUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * invoke after {@code asSelect()}
     *
     * @return a unmodifiable map
     */
    static Map<String, Selection> createSelectionMap(List<SelectPart> selectPartList) {

        Map<String, Selection> selectionMap = new HashMap<>();
        for (SelectPart selectPart : selectPartList) {

            if (selectPart instanceof Selection) {
                Selection selection = (Selection) selectPart;
                if (selectionMap.putIfAbsent(selection.alias(), selection) != null) {
                    throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION, "selection[%s] duplication"
                            , selection);
                }
            } else if (selectPart instanceof SelectionGroup) {
                SelectionGroup group = (SelectionGroup) selectPart;
                String tableAlias = group.tableAlias();
                for (Selection selection : group.selectionList()) {
                    if (selectionMap.putIfAbsent(tableAlias, selection) != null) {
                        throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION, "selection[%s] duplication"
                                , selection);
                    }
                }
            }

        }
        return Collections.unmodifiableMap(selectionMap);
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
                throw new IllegalStateException("no where clause.");
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


    static void addPredicates(List<IPredicate> predicates, List<_Predicate> predicateList) {
        for (IPredicate predicate : predicates) {
            predicateList.add((_Predicate) predicate);
        }
    }

    static void addSortParts(List<SortPart> sortParts, List<_SortPart> sortPartList) {
        for (SortPart sortPart : sortParts) {
            sortPartList.add((_SortPart) sortPart);
        }
    }


}
