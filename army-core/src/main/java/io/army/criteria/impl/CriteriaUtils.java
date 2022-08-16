package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class CriteriaUtils {

    CriteriaUtils() {
        throw new UnsupportedOperationException();
    }


    static CriteriaContext getCriteriaContext(final Object statement) {
        return ((CriteriaContextSpec) statement).getCriteriaContext();
    }


    static ArmyExpression constantLiteral(final CriteriaContext criteriaContext, final @Nullable Object value) {
        if (value == null) {
            throw CriteriaContextStack.nullPointer(criteriaContext);
        }
        if (value instanceof DataField) {
            String m = "constant must be non-field";
            throw CriteriaContextStack.criteriaError(criteriaContext, m);
        }
        final ArmyExpression expression;
        if (value instanceof Expression) {
            if (value instanceof ParamExpression) {
                throw CriteriaContextStack.criteriaError(criteriaContext, _Exceptions::valuesStatementDontSupportParam);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(criteriaContext);
            }
            expression = (ArmyExpression) value;
        } else {
            final MappingType type;
            type = _MappingFactory.getDefaultIfMatch(value.getClass());
            if (type == null) {
                throw noDefaultMappingType(criteriaContext, value);
            }
            expression = (ArmyExpression) SQLs.literal(type, value);
        }
        return expression;
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


    static void assertSelectionSize(final RowSet left, final RowSet right) {
        final int leftSize, rightSize;
        leftSize = ((_PartRowSet) left).selectionSize();
        rightSize = ((_PartRowSet) right).selectionSize();

        if (leftSize != rightSize) {
            String m = String.format("left row set column count[%s] and right row set column count[%s] not match"
                    , leftSize, rightSize);
            throw unionTypeError(left, m);
        }
    }

    static void assertTypeMatch(final RowSet left, final RowSet right, final Function<RowSet, String> function) {
        if (left instanceof Select || left instanceof Values) {
            if (!(right instanceof Select || right instanceof Values)) {
                String m = String.format("union right item isn't %s or %s"
                        , Select.class.getName(), Values.class.getName());
                throw unionTypeError(left, m);
            }
        } else if (left instanceof SubQuery || left instanceof SubValues) {
            if (!(right instanceof SubQuery || right instanceof SubValues)) {
                String m = String.format("union right item isn't %s or %s"
                        , SubQuery.class.getName(), SubValues.class.getName());
                throw unionTypeError(left, m);
            }
        } else {
            //no bug,never here
            throw new IllegalStateException();
        }

        final String message;
        message = function.apply(right);
        if (message != null) {
            throw unionTypeError(left, message);
        }

    }


    static CriteriaException unsupportedUnionType(final RowSet left, final UnionType unionType) {
        return unionTypeError(left, String.format("unsupported %s", unionType));
    }

    static CriteriaException unionTypeError(final RowSet left, final String message) {
        final CriteriaContext leftContext, outerContext;
        leftContext = ((CriteriaContextSpec) left).getCriteriaContext();
        outerContext = ((CriteriaContext.OuterContextSpec) leftContext).getOuterContext();
        final CriteriaException e;
        if (outerContext == null) {
            e = new CriteriaException(message);
        } else {
            e = CriteriaContextStack.criteriaError(outerContext, message);
        }
        return e;
    }


    /**
     * invoke after {@code asSelect()}
     *
     * @return a unmodifiable map
     */
    static Map<String, Selection> createSelectionMap(final List<? extends SelectItem> selectItemList) {

        final int selectItemSize = selectItemList.size();

        final Map<String, Selection> selectionMap;

        if (selectItemSize == 1 && selectItemList.get(0) instanceof Selection) {
            final Selection selection = (Selection) selectItemList.get(0);
            selectionMap = Collections.singletonMap(selection.alias(), selection);
        } else {
            final Map<String, Selection> map = new HashMap<>((int) (selectItemSize / 0.75F));
            for (SelectItem item : selectItemList) {

                if (item instanceof Selection) {
                    map.put(((Selection) item).alias(), (Selection) item); // if alias duplication then override. Be consistent with  statement executor.
                } else if (item instanceof SelectionGroup) {
                    for (Selection selection : ((SelectionGroup) item).selectionList()) {
                        map.put(selection.alias(), selection); // if alias duplication then override.Be consistent with  statement executor.
                    }
                }
            }
            selectionMap = Collections.unmodifiableMap(map);
        }
        return selectionMap;
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

    static void limitPair(final CriteriaContext criteriaContext, final @Nullable Object offsetValue
            , final @Nullable Object rowCountValue, final BiConsumer<Long, Long> consumer) {
        final long offset, rowCount;
        offset = asLimitParam(criteriaContext, offsetValue);
        rowCount = asLimitParam(criteriaContext, rowCountValue);
        consumer.accept(offset, rowCount);
    }

    static void ifLimitPair(final CriteriaContext criteriaContext, final @Nullable Object offsetValue
            , final @Nullable Object rowCountValue, final BiConsumer<Long, Long> consumer) {
        final long offset, rowCount;
        offset = asIfLimitParam(criteriaContext, offsetValue);
        rowCount = asIfLimitParam(criteriaContext, rowCountValue);
        if (offset >= 0L && rowCount >= 0L) {
            consumer.accept(offset, rowCount);
        }
    }


    static long asLimitParam(final CriteriaContext criteriaContext, final @Nullable Object value) {
        final long rowCount;
        if (value instanceof Long) {
            rowCount = (Long) value;
        } else if (value instanceof Integer
                || value instanceof Short
                || value instanceof Byte) {
            rowCount = ((Number) value).longValue();
        } else {
            throw limitParamError(criteriaContext, value);
        }
        if (rowCount < 0) {
            throw limitParamError(criteriaContext, value);
        }
        return rowCount;
    }

    static long asIfLimitParam(final CriteriaContext criteriaContext, final @Nullable Object value) {
        final long rowCount;
        if (value == null) {
            rowCount = -1L;
        } else if (value instanceof Long) {
            rowCount = (Long) value;
        } else if (value instanceof Integer
                || value instanceof Short
                || value instanceof Byte) {
            rowCount = ((Number) value).longValue();
            if (rowCount < 0L) {
                throw limitParamError(criteriaContext, value);
            }
        } else {
            throw limitParamError(criteriaContext, value);
        }
        return rowCount;
    }

    static int standardModifier(final Distinct distinct) {
        final int level;
        switch (distinct) {
            case DISTINCT:
            case DISTINCTROW:
            case ALL:
                level = 1;
                break;
            default:
                level = -1;
        }
        return level;
    }

    static CriteriaException limitParamError(CriteriaContext criteriaContext, @Nullable Object value) {
        String m = String.format("limit clause only support [%s,%s,%s,%s] and non-negative,but input %s"
                , Long.class.getName(), Integer.class.getName(), Short.class.getName(), Byte.class.getName(), value);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException limitBiConsumerError(CriteriaContext criteriaContext) {
        String m = "You must specified limit clause";
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException ifLimitBiConsumerError(CriteriaContext criteriaContext) {
        String m = "limit clause must specified non-negative parameters";
        return CriteriaContextStack.criteriaError(criteriaContext, m);
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

    static <T extends Enum<T> & SQLWords> List<T> asModifierList(final CriteriaContext criteriaContext
            , final @Nullable List<T> list, final Function<T, Integer> function) {
        if (list == null) {
            throw CriteriaContextStack.criteriaError(criteriaContext, "modifier list must non-null");
        }
        final List<T> modifierList;
        final int size = list.size();
        switch (size) {
            case 0:
                modifierList = Collections.emptyList();
                break;
            case 1: {
                final T modifier;
                modifier = list.get(0);
                if (modifier == null) {
                    throw CriteriaContextStack.criteriaError(criteriaContext, "Modifier list element must non-null");
                }
                if (function.apply(modifier) < 0) {
                    String m = String.format("%s syntax error", modifier);
                    throw CriteriaContextStack.criteriaError(criteriaContext, m);
                }
                modifierList = Collections.singletonList(modifier);
            }
            break;
            default: {
                List<T> tempList = null;
                T modifier;
                for (int i = 0, preLevel = -1, level; i < size; i++) {
                    modifier = list.get(i);
                    if (modifier == null) {
                        String m = "Modifier list element must non-null";
                        throw CriteriaContextStack.criteriaError(criteriaContext, m);
                    }
                    level = function.apply(modifier);
                    if (level <= preLevel) {
                        String m = String.format("%s syntax error", modifier);
                        throw CriteriaContextStack.criteriaError(criteriaContext, m);
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

    static <H extends Hint> List<H> asHintList(final CriteriaContext criteriaContext, final @Nullable List<Hint> hintList
            , final Function<Hint, H> function) {
        if (hintList == null) {
            return Collections.emptyList();
        }
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
                    throw CriteriaContextStack.criteriaError(criteriaContext, CriteriaUtils::illegalHint, hint);
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
                        throw CriteriaContextStack.criteriaError(criteriaContext, CriteriaUtils::illegalHint, hint);
                    }
                    tempList.add(dialectHint);
                }
                mySqlHintList = Collections.unmodifiableList(tempList);
            }

        }
        return mySqlHintList;
    }


    static _TableBlock createStandardDynamicBlock(final _JoinType joinType, final DynamicBlock<?> block) {
        if (!(block instanceof DynamicBlock.StandardDynamicBlock)) {
            String m = "not standard dynamic block";
            throw CriteriaContextStack.criteriaError(block.criteriaContext, m);
        }
        return new TableBlock.DynamicTableBlock(joinType, block);
    }


    static CriteriaException illegalHint(@Nullable Hint hint) {
        String m = String.format("Hint %s is illegal."
                , _ClassUtils.safeClassName(hint));
        return new CriteriaException(m);
    }


    private static CriteriaException illegalWindow(Window window) {
        String m = String.format("window %s is illegal", _ClassUtils.safeClassName(window));
        return new CriteriaException(m);
    }


    static int selectionCount(final RowSet rowSet) {
        int count = 0;
        for (SelectItem selectItem : ((_PartRowSet) rowSet).selectItemList()) {
            if (selectItem instanceof Selection) {
                count++;
            } else if (selectItem instanceof SelectionGroup) {
                count += ((SelectionGroup) selectItem).selectionList().size();
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }
        }
        return count;
    }


    static CriteriaException unknownSelection(CriteriaContext context, String selectionAlias) {
        String m = String.format("unknown %s[%s]", Selection.class.getName(), selectionAlias);
        return CriteriaContextStack.criteriaError(context, m);
    }

    static CriteriaException criteriaNotMatch(CriteriaContext criteriaContext) {
        String m = "criteria not match.";
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException criteriaContextNotMatch(CriteriaContext criteriaContext) {
        String m = "criteria context not match.";
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }


    private static CriteriaException noDefaultMappingType(CriteriaContext criteriaContext, final Object value) {
        String m = String.format("Not found default %s for %s."
                , MappingType.class.getName(), value.getClass().getName());
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException orderByIsEmpty(CriteriaContext criteriaContext) {
        return CriteriaContextStack.criteriaError(criteriaContext, "you don't add any item");
    }

    static CriteriaException funcArgError(String funcName, @Nullable Object errorArg) {
        String m = String.format("%s don't support %s", funcName, errorArg);
        throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
    }


}
