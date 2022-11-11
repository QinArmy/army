package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.ChildTableMeta;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class CriteriaUtils {

    CriteriaUtils() {
        throw new UnsupportedOperationException();
    }


    static void createAndAddCte(final CriteriaContext context, final @Nullable String name
            , final @Nullable List<String> columnAliasList, final SubStatement subStatement) {
        if (name == null) {
            throw ContextStack.castCriteriaApi(context);
        }
        final SQLs.CteImpl cte;
        if (columnAliasList == null) {
            cte = new SQLs.CteImpl(name, subStatement);
        } else {
            cte = new SQLs.CteImpl(name, columnAliasList, subStatement);
        }
        context.onAddCte(cte);
    }

    static CriteriaContext getCriteriaContext(final Object statement) {
        return ((CriteriaContextSpec) statement).getContext();
    }


    static ArmyExpression constantLiteral(final CriteriaContext criteriaContext, final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.nullPointer(criteriaContext);
        }
        if (value instanceof DataField) {
            String m = "constant must be non-field";
            throw ContextStack.criteriaError(criteriaContext, m);
        }
        final ArmyExpression expression;
        if (value instanceof Expression) {
            if (value instanceof ParamExpression) {
                throw ContextStack.criteriaError(criteriaContext, _Exceptions::valuesStatementDontSupportParam);
            }
            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(criteriaContext);
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

    static void withClause(final boolean recursive, final _Cte cte, final CriteriaContext context
            , BiConsumer<Boolean, List<_Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        cteConsumer.addCte(cte);

        final List<_Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        subClassConsumer.accept(recursive, cteList);
    }


    static <C> void withClause(final boolean recursive, final BiConsumer<C, Consumer<_Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<_Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(context.criteria(), cteConsumer::addCte);

        final List<_Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        subClassConsumer.accept(recursive, cteList);
    }

    static void withClause(final boolean recursive, final Consumer<Consumer<_Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<_Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(cteConsumer::addCte);

        final List<_Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        subClassConsumer.accept(recursive, cteList);
    }

    static <C> void ifWithClause(final boolean recursive, final BiConsumer<C, Consumer<_Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<_Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(context.criteria(), cteConsumer::addCte);

        final List<_Cte> cteList;
        cteList = cteConsumer.end();
        if (cteList.size() > 0) {
            subClassConsumer.accept(recursive, cteList);
        }

    }

    static void ifWithClause(final boolean recursive, final Consumer<Consumer<_Cte>> consumer
            , final CriteriaContext context, BiConsumer<Boolean, List<_Cte>> subClassConsumer) {

        final CriteriaContext.CteConsumer cteConsumer;
        cteConsumer = context.onBeforeWithClause(recursive);
        consumer.accept(cteConsumer::addCte);

        final List<_Cte> cteList;
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


    static List<_Predicate> asPredicateList(CriteriaContext context, final List<IPredicate> list) {
        final List<_Predicate> predicateList;
        final int size = list.size();
        switch (size) {
            case 0:
                throw ContextStack.criteriaError(context, _Exceptions::predicateListIsEmpty);
            case 1:
                predicateList = Collections.singletonList((OperationPredicate) list.get(0));
                break;
            default: {
                final List<_Predicate> tempList = new ArrayList<>(size);
                for (IPredicate predicate : list) {
                    tempList.add((OperationPredicate) predicate);
                }
                predicateList = Collections.unmodifiableList(tempList);
            }
        }
        return predicateList;
    }


    static CriteriaException unsupportedUnionType(final RowSet left, final UnionType unionType) {
        return unionTypeError(left, String.format("unsupported %s", unionType));
    }

    static CriteriaException unionTypeError(final RowSet left, final String message) {
        final CriteriaContext leftContext, outerContext;
        leftContext = ((CriteriaContextSpec) left).getContext();
        outerContext = ((CriteriaContext.OuterContextSpec) leftContext).getOuterContext();
        final CriteriaException e;
        if (outerContext == null) {
            e = new CriteriaException(message);
        } else {
            e = ContextStack.criteriaError(outerContext, message);
        }
        return e;
    }

    static CriteriaException ofTableListIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "of table list must non-empty.");
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

    static void assertSelectItemSizeMatch(final RowSet left, final RowSet right) {
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
                throw unknownSelectItem(left, item);
            }
        }

        for (SelectItem item : rightList) {
            if (item instanceof Selection) {
                rightSize++;
            } else if (item instanceof SelectionGroup) {
                rightSize += ((SelectionGroup) item).selectionList().size();
            } else {
                throw unknownSelectItem(left, item);
            }
        }

        if (leftSize != rightSize) {
            String m = String.format("Left select list size[%s] and right select list size[%s] not match."
                    , leftSize, rightSize);
            throw criteriaError(left, m);
        }

    }

    static CriteriaException criteriaError(final RowSet left, final String m) {
        return ContextStack.criteriaError(((CriteriaContextSpec) left).getContext(), m);
    }

    static <T> CriteriaException criteriaError(RowSet left, Function<T, CriteriaException> function, T item) {
        return ContextStack.criteriaError(((CriteriaContextSpec) left).getContext(), function, item);
    }


    static void limitPair(final CriteriaContext context, final BiFunction<MappingType, Number, Expression> operator
            , final @Nullable Object offsetValue, final @Nullable Object rowCountValue
            , final BiConsumer<Expression, Expression> consumer) {

        final long offset, rowCount;
        if (offsetValue instanceof Long) {
            offset = (Long) offsetValue;
        } else if (offsetValue instanceof Integer
                || offsetValue instanceof Short
                || offsetValue instanceof Byte) {
            offset = ((Number) offsetValue).longValue();
        } else {
            throw limitParamError(context, offsetValue);
        }

        if (rowCountValue instanceof Long) {
            rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer
                || rowCountValue instanceof Short
                || rowCountValue instanceof Byte) {
            rowCount = ((Number) rowCountValue).longValue();
        } else {
            throw limitParamError(context, rowCountValue);
        }

        if (offset < 0) {
            throw limitParamError(context, offsetValue);
        } else if (rowCount < 0) {
            throw limitParamError(context, rowCountValue);
        }

        consumer.accept(operator.apply(LongType.INSTANCE, offset), operator.apply(LongType.INSTANCE, rowCount));
    }

    static void ifLimitPair(final BiFunction<MappingType, Number, Expression> operator
            , final @Nullable Object offsetValue, final @Nullable Object rowCountValue
            , final BiConsumer<Expression, Expression> consumer) {

        final long offset, rowCount;
        if (offsetValue instanceof Long) {
            offset = (Long) offsetValue;
        } else if (offsetValue instanceof Integer
                || offsetValue instanceof Short
                || offsetValue instanceof Byte) {
            offset = ((Number) offsetValue).longValue();
        } else {
            offset = -1L;
        }

        if (rowCountValue instanceof Long) {
            rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer
                || rowCountValue instanceof Short
                || rowCountValue instanceof Byte) {
            rowCount = ((Number) rowCountValue).longValue();
        } else {
            rowCount = -1L;
        }

        if (offset >= 0 && rowCount >= 0) {
            consumer.accept(operator.apply(LongType.INSTANCE, offset), operator.apply(LongType.INSTANCE, rowCount));
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

    static int standardModifier(final SQLsSyntax.Modifier distinct) {
        return (distinct == SQLs.DISTINCT || distinct == SQLs.ALL) ? 1 : -1;
    }

    static CriteriaException limitParamError(CriteriaContext criteriaContext, @Nullable Object value) {
        String m = String.format("limit clause only support [%s,%s,%s,%s] and non-negative,but input %s"
                , Long.class.getName(), Integer.class.getName(), Short.class.getName(), Byte.class.getName(), value);
        return ContextStack.criteriaError(criteriaContext, m);
    }


    static CriteriaException cteNotEnd(CriteriaContext context, String currentCte, String newCte) {
        String m = String.format("Cte[%s] not end,couldn't start Cte[%s]", currentCte, newCte);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException nonOptionalClause(CriteriaContext context, String clause) {
        String m = String.format("%s clause isn't optional.", clause);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException limitBiConsumerError(CriteriaContext criteriaContext) {
        String m = "You must specified limit clause";
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException ifLimitBiConsumerError(CriteriaContext criteriaContext) {
        String m = "limit clause must specified non-negative parameters";
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException dontSupportMultiParam(CriteriaContext context) {
        return ContextStack.criteriaError(context, "don't support multi-parameter(literal)");
    }


    static List<Object> paramList(final CriteriaContext context, final @Nullable List<?> paramList) {
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
                throw ContextStack.nullPointer(context);
            }
            if (isMap) {
                if (!(param instanceof Map)) {
                    throw ContextStack.criteriaError(context, "Batch parameter must be same java type.");
                }
            } else if (!paramJavaType.isInstance(param)) {
                throw ContextStack.criteriaError(context, "Batch parameter must be same java type.");
            }
            wrapperList.add(param);
        }
        return Collections.unmodifiableList(wrapperList);
    }


    static <T extends Query.SelectModifier> List<T> asModifierList(final CriteriaContext context
            , final @Nullable List<T> list, final Function<T, Integer> function) {
        if (list == null) {
            throw ContextStack.criteriaError(context, "modifier list must non-null");
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
                    throw ContextStack.criteriaError(context, "Modifier list element must non-null");
                }
                if (function.apply(modifier) < 0) {
                    String m = String.format("%s syntax error", modifier);
                    throw ContextStack.criteriaError(context, m);
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
                        throw ContextStack.criteriaError(context, m);
                    }
                    level = function.apply(modifier);
                    if (level <= preLevel) {
                        String m = String.format("%s syntax error", modifier);
                        throw ContextStack.criteriaError(context, m);
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

    static <H extends Hint> List<H> asHintList(final CriteriaContext context, final @Nullable List<Hint> hintList
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
                    throw ContextStack.criteriaError(context, CriteriaUtils::illegalHint, hint);
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
                        throw ContextStack.criteriaError(context, CriteriaUtils::illegalHint, hint);
                    }
                    tempList.add(dialectHint);
                }
                mySqlHintList = Collections.unmodifiableList(tempList);
            }

        }
        return mySqlHintList;
    }


    static CriteriaException illegalHint(@Nullable Hint hint) {
        String m = String.format("Hint %s is illegal."
                , _ClassUtils.safeClassName(hint));
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

    static String sqlWordsToString(Enum<?> type) {
        return _StringUtils.builder()
                .append(type.getClass().getSimpleName())
                .append(_Constant.POINT)
                .append(type.name())
                .toString();
    }


    static CriteriaException unknownSelection(CriteriaContext context, String selectionAlias) {
        String m = String.format("unknown %s[%s]", Selection.class.getName(), selectionAlias);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException criteriaNotMatch(CriteriaContext criteriaContext) {
        String m = "criteria not match.";
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException criteriaContextNotMatch(CriteriaContext criteriaContext) {
        String m = "criteria context not match.";
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException cteListIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "WITH clause couldn't be empty.");
    }

    static CriteriaException childParentDomainListNotMatch(CriteriaContext context, ChildTableMeta<?> child) {
        String m = String.format("%s insert domain list and parent insert statement domain list not match"
                , child);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException childParentRowNotMatch(_Insert._ValuesInsert child, _Insert._ValuesInsert parent) {
        String m = String.format("%s row number[%s] and parent row number[%s] not match."
                , child.table(), child.rowPairList().size(), parent.rowPairList().size());

        return ContextStack.criteriaError(((CriteriaContextSpec) child).getContext(), m);
    }

    static CriteriaException duplicateTabularMethod(CriteriaContext context) {
        return ContextStack.criteriaError(context, "duplicate tabular method");
    }


    private static CriteriaException noDefaultMappingType(CriteriaContext criteriaContext, final Object value) {
        String m = String.format("Not found default %s for %s."
                , MappingType.class.getName(), value.getClass().getName());
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException orderByIsEmpty(CriteriaContext criteriaContext) {
        return ContextStack.criteriaError(criteriaContext, "you don't add any item");
    }

    static CriteriaException funcArgError(String funcName, @Nullable Object errorArg) {
        String m = String.format("%s don't support %s", funcName, errorArg);
        throw ContextStack.criteriaError(ContextStack.peek(), m);
    }

    static CriteriaException funcArgListIsEmpty(String name) {
        String m = String.format("function %s argument list must non-empty.", name);
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }

    static CriteriaException nonCollectionValue(String keyName) {
        String m = String.format("value of %s isn't %s type.", keyName, Collection.class.getName());
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }


    static CriteriaException dontSupportTabularModifier(CriteriaContext context, Object modifier) {
        String m = String.format("Don't support modifier[%s]", modifier);
        return ContextStack.criteriaError(context, m);
    }


    static CriteriaException conflictClauseIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "You don't add conflict pair.");
    }

    static CriteriaException unknownWords(CriteriaContext context, @Nullable Object word) {
        return ContextStack.criteriaError(context, String.format("unknown word[%s]", word));
    }

    static CriteriaException unknownWords(@Nullable Object word) {
        return unknownWords(ContextStack.peek(), word);
    }


    static CriteriaException returningListIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "RETURNING list is empty");
    }

    static CriteriaException funDontSupportMultiValue(String name) {
        String m = String.format("function[%s] don't support multi-value", name);
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }

    private static CriteriaException unknownSelectItem(final RowSet left, final SelectItem item) {
        return ContextStack.criteriaError(((CriteriaContextSpec) left).getContext()
                , _Exceptions::unknownSelectItem, item);
    }


}
