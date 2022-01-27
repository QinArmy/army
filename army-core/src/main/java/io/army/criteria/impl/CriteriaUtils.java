package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.*;

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

    static void assertSelectItemSizeMatch(Query left, Query right) {
        final List<? extends SelectItem> leftList, rightList;

        leftList = ((_PartQuery) left).selectItemList();
        rightList = ((_PartQuery) right).selectItemList();

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


    static void addPredicates(List<IPredicate> predicates, List<_Predicate> predicateList) {
        for (IPredicate predicate : predicates) {
            predicateList.add((_Predicate) predicate);
        }
    }


    static List<_Predicate> onPredicates(IPredicate predicate1, IPredicate predicate2) {
        final List<_Predicate> list = new ArrayList<>(2);
        list.add((OperationPredicate) predicate1);
        list.add((OperationPredicate) predicate2);
        return Collections.unmodifiableList(list);
    }

    static List<ReadWrapper> paramMaps(List<Map<String, Object>> mapList) {
        final List<ReadWrapper> wrapperList = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            wrapperList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return Collections.unmodifiableList(wrapperList);
    }

    static List<ReadWrapper> paramBeans(List<?> beanList) {
        final List<ReadWrapper> wrapperList = new ArrayList<>(beanList.size());
        for (Object bean : beanList) {
            wrapperList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return Collections.unmodifiableList(wrapperList);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    static <C> C getCriteria(final Query query) {
        return ((CriteriaSpec<C>) query).getCriteria();
    }


    static CriteriaException unknownCriteriaContext(CriteriaContext context) {
        String m = String.format("Unknown %s[%s] type.", CriteriaContext.class.getName(), context.getClass().getName());
        return new CriteriaException(m);
    }

    static CriteriaException nonArmyExpression(Expression expression) {
        String m = String.format("%s isn't army class", expression.getClass().getName());
        return new CriteriaException(m);
    }


}
