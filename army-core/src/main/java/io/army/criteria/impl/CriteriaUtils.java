package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.*;

abstract class CriteriaUtils {

    CriteriaUtils() {
        throw new UnsupportedOperationException();
    }


    static void withClause(final boolean recursive, final Cte cte, final CriteriaContext context
            , BiConsumer<Boolean, List<Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        cteConsumer.addCte(cte);

        final List<Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        subClassConsumer.accept(recursive, cteList);
    }


    static <C> void withClause(final boolean recursive, final BiConsumer<C, Consumer<Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(context.criteria(), cteConsumer::addCte);

        final List<Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        subClassConsumer.accept(recursive, cteList);
    }

    static void withClause(final boolean recursive, final Consumer<Consumer<Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(cteConsumer::addCte);

        final List<Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        subClassConsumer.accept(recursive, cteList);
    }

    static <C> void ifWithClause(final boolean recursive, final BiConsumer<C, Consumer<Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(context.criteria(), cteConsumer::addCte);

        final List<Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() > 0) {
            subClassConsumer.accept(recursive, cteList);
        }

    }

    static void ifWithClause(final boolean recursive, final Consumer<Consumer<Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(cteConsumer::addCte);

        final List<Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() > 0) {
            subClassConsumer.accept(recursive, cteList);
        }
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

    static long asRowCount(final Object value) {
        final long rowCount;
        if (value instanceof Long) {
            rowCount = (Long) value;
        } else if (value instanceof Integer
                || value instanceof Short
                || value instanceof Byte) {
            rowCount = ((Number) value).longValue();
        } else {
            throw limitParamError(value);
        }
        return rowCount;
    }

    static long asIfRowCount(final @Nullable Object value) {
        final long rowCount;
        if (value == null) {
            rowCount = -1L;
        } else if (value instanceof Long) {
            rowCount = (Long) value;
        } else if (value instanceof Integer
                || value instanceof Short
                || value instanceof Byte) {
            rowCount = ((Number) value).longValue();
        } else {
            throw limitParamError(value);
        }
        return rowCount;
    }

    static CriteriaException limitParamError(@Nullable Object value) {
        String m = String.format("limit clause only support [%s,%s,%s,%s],but input %s"
                , Long.class.getName(), Integer.class.getName(), Short.class.getName(), Byte.class.getName(), value);
        return new CriteriaException(m);
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


    static List<Object> paramList(final @Nullable List<?> paramList) {
        final int size;
        if (paramList == null || (size = paramList.size()) == 0) {
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
            } else if (!paramJavaType.isInstance(param)) {
                throw new CriteriaException("Batch parameter must be same java type.");
            }
            wrapperList.add(param);
        }
        return Collections.unmodifiableList(wrapperList);
    }


    @SuppressWarnings("unchecked")
    @Nullable
    static <C> C getCriteria(final Query query) {
        return ((CriteriaSpec<C>) query).getCriteria();
    }

    static List<Window> asWindowList(final @Nullable List<Window> list, final Predicate<Window> predicate) {
        final int size;
        if (list == null || (size = list.size()) == 0) {
            throw _Exceptions.windowListIsEmpty();
        }
        List<Window> windowList;
        if (size == 1) {
            final Window window;
            window = list.get(0);
            if (predicate.test(window)) {
                throw illegalWindow(window);
            }
            windowList = Collections.singletonList(window);
        } else {
            windowList = new ArrayList<>(size);
            for (Window window : list) {
                if (predicate.test(window)) {
                    throw illegalWindow(window);
                }
                windowList.add(window);
            }
            windowList = Collections.unmodifiableList(windowList);
        }
        return windowList;
    }

    static <T extends Enum<T> & SQLWords.Modifier> List<T> asModifierList(final List<T> list, Predicate<T> predicate) {
        final List<T> modifierList;
        final int size = list.size();
        switch (size) {
            case 0:
                modifierList = Collections.emptyList();
                break;
            case 1: {
                List<T> tempSet = null;
                for (T modifier : list) {
                    assert modifier != null;
                    if (predicate.test(modifier)) {
                        throw modifierSyntaxError(modifier);
                    }
                    tempSet = Collections.singletonList(modifier);
                    break;
                }
                modifierList = tempSet;
            }
            break;
            default: {
                List<T> tempList = null;
                T modifier;
                for (int i = 0, preLevel = -1, level; i < size; i++) {
                    modifier = list.get(i);
                    assert modifier != null;
                    if (predicate.test(modifier)) {
                        throw modifierSyntaxError(modifier);
                    }
                    level = modifier.level();
                    if (level <= preLevel) {
                        String m = String.format("%s syntax error", modifier);
                        throw new CriteriaException(m);
                    }
                    preLevel = level;
                    if (tempList == null) {
                        tempList = new ArrayList<>(size);
                    }
                    tempList.add(modifier);
                }
                modifierList = Collections.unmodifiableList(tempList);
            }
        }
        return modifierList;
    }

    static <H extends Hint> List<H> asHintList(List<Hint> hintList, final Function<Hint, H> function) {
        final List<H> mySqlHintList;
        switch (hintList.size()) {
            case 0:
                mySqlHintList = Collections.emptyList();
                break;
            case 1: {
                final Hint hint = hintList.get(0);
                final H dialectHint;
                dialectHint = function.apply(hint);
                if (dialectHint == null) {
                    throw illegalHint(hint);
                }
                mySqlHintList = Collections.singletonList(dialectHint);
            }
            break;
            default: {
                final List<H> tempList = new ArrayList<>(hintList.size());
                H dialectHint;
                for (Hint hint : hintList) {
                    dialectHint = function.apply(hint);
                    if (dialectHint == null) {
                        throw illegalHint(hint);
                    }
                    tempList.add(dialectHint);
                }
                mySqlHintList = Collections.unmodifiableList(tempList);
            }

        }
        return mySqlHintList;
    }

    static CriteriaException nestedItemsNotMatch(NestedItems nestedItems, Enum<?> target) {
        String m = String.format("%s %s and %s not match", NestedItems.class.getName()
                , _ClassUtils.safeClassName(nestedItems), target);
        return new CriteriaException(m);
    }

    static CriteriaException illegalHint(@Nullable Hint hint) {
        String m = String.format("Hint %s is illegal."
                , _ClassUtils.safeClassName(hint));
        throw new CriteriaException(m);
    }


    private static CriteriaException modifierSyntaxError(SQLWords.Modifier modifier) {
        String m = String.format("modifier syntax error,%s isn't support.", modifier);
        throw new CriteriaException(m);
    }

    private static CriteriaException illegalWindow(Window window) {
        String m = String.format("window %s is illegal", _ClassUtils.safeClassName(window));
        return new CriteriaException(m);
    }


}
