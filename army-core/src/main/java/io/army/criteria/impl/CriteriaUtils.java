package io.army.criteria.impl;

import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;

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
