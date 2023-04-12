package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.SqlType;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
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

    /**
     * EMPTY_STRING_LIST couldn't be equals {@link  Collections#EMPTY_LIST}
     */
    static final List<String> EMPTY_STRING_LIST = Collections.unmodifiableList(new ArrayList<>(0));

    static final Item NONE_ITEM = new Item() {
    };

    @Deprecated
    static List<String> columnAliasList(final boolean required, Consumer<Consumer<String>> consumer) {
        List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        if (list.size() > 0) {
            list = _CollectionUtils.unmodifiableList(list);
        } else if (required) {
            list = Collections.emptyList();
        } else {
            list = EMPTY_STRING_LIST;
        }
        return list;
    }

    static List<String> stringList(CriteriaContext ctx, final boolean required, Consumer<Consumer<String>> consumer) {
        List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        if (list.size() > 0) {
            list = _CollectionUtils.unmodifiableList(list);
        } else if (required) {
            throw ContextStack.criteriaError(ctx, "you don't add any string");
        } else {
            list = Collections.emptyList();
        }
        return list;
    }


    static List<_Expression> expressionList(CriteriaContext ctx, final boolean required, Consumer<Consumer<Expression>> consumer) {
        final List<_Expression> list = new ArrayList<>();
        consumer.accept(e -> list.add((ArmyExpression) e));
        final List<_Expression> expressionList;
        switch (list.size()) {
            case 0: {
                if (required) {
                    throw ContextStack.criteriaError(ctx, "you don't add any expression");
                }
                expressionList = Collections.emptyList();
            }
            break;
            case 1:
                expressionList = Collections.singletonList(list.get(0));
                break;
            default:
                expressionList = Collections.unmodifiableList(list);
        }
        return expressionList;
    }


    static List<_SelectItem> selectionList(CriteriaContext context, Consumer<Returnings> consumer) {
        final List<_SelectItem> list = new ArrayList<>();
        consumer.accept(CriteriaSupports.returningBuilder(list::add));
        if (list.size() == 0) {
            throw CriteriaUtils.returningListIsEmpty(context);
        }
        return list;
    }

    static List<_SelectItem> returningAll(final TableMeta<?> targetTable, final String tableAlias,
                                          final List<_TabularBlock> blockList) {

        List<_SelectItem> groupList;
        final int blockSize;
        blockSize = blockList.size();
        if (blockSize == 0) {
            groupList = Collections.singletonList(SelectionGroups.singleGroup(targetTable, tableAlias));
        } else {
            groupList = new ArrayList<>(1 + blockSize);
            groupList.add(SelectionGroups.singleGroup(targetTable, tableAlias));
            appendSelectionGroup(blockList, groupList);
            groupList = Collections.unmodifiableList(groupList);
        }
        return groupList;
    }


    static void createAndAddCte(final CriteriaContext context, final @Nullable String name
            , final @Nullable List<String> columnAliasList, final SubStatement subStatement) {
        if (name == null) {
            throw ContextStack.castCriteriaApi(context);
        }
        assert ((CriteriaContextSpec) subStatement).getContext().getNonNullOuterContext() == context;
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

    static boolean isIllegalLateral(@Nullable Query.DerivedModifier modifier) {
        return modifier != null && modifier != SQLs.LATERAL;
    }

    static boolean isIllegalOnly(@Nullable Query.TableModifier modifier) {
        return modifier != null && modifier != SQLs.ONLY;
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
            selectionMap = Collections.singletonMap(selection.selectionName(), selection);
        } else {
            final Map<String, Selection> map = new HashMap<>((int) (selectItemSize / 0.75F));
            for (SelectItem item : selectItemList) {

                if (item instanceof Selection) {
                    map.put(((Selection) item).selectionName(), (Selection) item); // if alias duplication then override. Be consistent with  statement executor.
                } else if (item instanceof _SelectionGroup) {
                    for (Selection selection : ((_SelectionGroup) item).selectionList()) {
                        map.put(selection.selectionName(), selection); // if alias duplication then override.Be consistent with  statement executor.
                    }
                }
            }
            selectionMap = Collections.unmodifiableMap(map);
        }
        return selectionMap;
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
            if (rowCount < 0L) {
                throw limitParamError(criteriaContext, value);
            }
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


    static String sqlWordsToString(Enum<?> type) {
        return _StringUtils.builder()
                .append(type.getClass().getSimpleName())
                .append(_Constant.POINT)
                .append(type.name())
                .toString();
    }

    @Deprecated
    static _Pair<List<Selection>, Map<String, Selection>> forColumnAlias(final List<String> columnAliasList,
                                                                         final _DerivedTable table) {
        final List<? extends Selection> refSelectionList;
        refSelectionList = table.refAllSelection();
        final int selectionSize;
        selectionSize = refSelectionList.size();
        if (columnAliasList.size() != selectionSize) {
            throw CriteriaUtils.derivedColumnAliasSizeNotMatch(selectionSize, columnAliasList.size());
        }
        if (selectionSize == 1) {
            final Selection selection;
            selection = ArmySelections.renameSelection(refSelectionList.get(0), columnAliasList.get(0));
            return _Pair.create(
                    Collections.singletonList(selection),
                    Collections.singletonMap(selection.selectionName(), selection)
            );
        }
        final List<Selection> selectionList = new ArrayList<>(selectionSize);
        final Map<String, Selection> selectionMap = new HashMap<>((int) (selectionSize / 0.75f));
        Selection selection;
        String columnAlias;
        for (int i = 0; i < selectionSize; i++) {
            columnAlias = columnAliasList.get(i);
            if (columnAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            selection = ArmySelections.renameSelection(refSelectionList.get(i), columnAlias);
            if (selectionMap.putIfAbsent(columnAlias, selection) != null) {
                throw CriteriaUtils.duplicateColumnAlias(columnAlias);
            }
            selectionList.add(selection);
        }
        assert selectionList.size() == selectionMap.size();
        return _Pair.create(
                Collections.unmodifiableList(selectionList),
                Collections.unmodifiableMap(selectionMap)
        );
    }

    static _SelectionMap createAliasSelectionMap(final List<String> columnAliasList,
                                                 final _DerivedTable table) {
        final List<? extends Selection> refSelectionList;
        refSelectionList = table.refAllSelection();
        final int selectionSize;
        selectionSize = refSelectionList.size();
        if (columnAliasList.size() != selectionSize) {
            throw CriteriaUtils.derivedColumnAliasSizeNotMatch(selectionSize, columnAliasList.size());
        }
        if (selectionSize == 1) {
            final Selection selection;
            selection = ArmySelections.renameSelection(refSelectionList.get(0), columnAliasList.get(0));
            return new SelectionMap(
                    Collections.singletonList(selection),
                    Collections.singletonMap(selection.selectionName(), selection)
            );
        }
        final List<Selection> selectionList = new ArrayList<>(selectionSize);
        final Map<String, Selection> selectionMap = new HashMap<>((int) (selectionSize / 0.75f));
        Selection selection;
        String columnAlias;
        for (int i = 0; i < selectionSize; i++) {
            columnAlias = columnAliasList.get(i);
            if (columnAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            selection = ArmySelections.renameSelection(refSelectionList.get(i), columnAlias);
            if (selectionMap.putIfAbsent(columnAlias, selection) != null) {
                throw CriteriaUtils.duplicateColumnAlias(columnAlias);
            }
            selectionList.add(selection);
        }
        assert selectionList.size() == selectionMap.size();
        return new SelectionMap(
                Collections.unmodifiableList(selectionList),
                Collections.unmodifiableMap(selectionMap)
        );


    }


    static CriteriaException subDmlNoReturningClause(String cteName) {
        String m = String.format("cte[%s] no RETURNING clause,couldn't exists alias clause.", cteName);
        throw ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException unknownSelection(CriteriaContext context, String selectionAlias) {
        String m = String.format("unknown %s[%s]", Selection.class.getName(), selectionAlias);
        return ContextStack.criteriaError(context, m);
    }

    /**
     * @param selectionOrdinal based 1
     */
    static CriteriaException unknownSelection(CriteriaContext context, int selectionOrdinal) {
        String m = String.format("unknown %s[ordinal:%s]", Selection.class.getName(), selectionOrdinal);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException unknownRowSet(CriteriaContext context, RowSet rowSet, @Nullable Database database) {
        String m = String.format("%s isn't the %s that is supported by %s criteria api.",
                _ClassUtils.safeClassName(rowSet), RowSet.class.getName(),
                database == null ? "standard" : database.name());
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException criteriaNotMatch(CriteriaContext criteriaContext) {
        String m = "criteria not match.";
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException noPrecision(CriteriaContext context, SqlType type) {
        String m = String.format("You don't specified precision for %s", type);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException dontSupportPrecision(CriteriaContext context, SqlType type) {
        String m = String.format("%s don't support precision", type);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException dontSupportCharset(CriteriaContext context, SqlType type) {
        String m = String.format("%s don't support charset", type);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException dontSupportPrecisionScale(CriteriaContext context, SqlType type) {
        String m = String.format("%s don't support precision and scale", type);
        return ContextStack.criteriaError(context, m);
    }


    static CriteriaException derivedColumnAliasSizeNotMatch(int selectionSize, int aliasSize) {
        return ContextStack.clearStackAnd(_Exceptions::derivedColumnAliasSizeNotMatch, selectionSize, aliasSize);
    }

    static CriteriaException operandError(final Enum<?> operator, final @Nullable Expression operand) {
        String m = String.format("%s don't support %s .", operator, _ClassUtils.safeClassName(operand));
        throw ContextStack.clearStackAndCriteriaError(m);
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
                , child.insertTable(), child.rowPairList().size(), parent.rowPairList().size());

        return ContextStack.criteriaError(((CriteriaContextSpec) child).getContext(), m);
    }

    static CriteriaException duplicateDynamicMethod(CriteriaContext context) {
        return ContextStack.criteriaError(context, "duplicate invoking dynamic method");
    }


    private static CriteriaException noDefaultMappingType(CriteriaContext criteriaContext, final Object value) {
        String m = String.format("Not found default %s for %s."
                , MappingType.class.getName(), value.getClass().getName());
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException orderByIsEmpty(CriteriaContext criteriaContext) {
        return ContextStack.criteriaError(criteriaContext, "you don't add any item");
    }

    static CriteriaException operandError(String operatorName, @Nullable Object errorOperand) {
        String m = String.format("SQL operator %s don't support %s", operatorName, _ClassUtils.safeClassName(errorOperand));
        throw ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException funcArgError(String funcName, @Nullable Object errorArg) {
        String m = String.format("SQL function %s don't support %s", funcName, _ClassUtils.safeClassName(errorArg));
        throw ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException funcArgListIsEmpty(String name) {
        String m = String.format("function %s argument list must non-empty.", name);
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }

    static CriteriaException nonCollectionValue(String keyName) {
        String m = String.format("value of %s isn't %s type.", keyName, Collection.class.getName());
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }


    static CriteriaException errorModifier(CriteriaContext context, @Nullable Object modifier) {
        String m = String.format("error modifier[%s]", modifier);
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

    static CriteriaException illegalItemPair(CriteriaContext context, @Nullable ItemPair pair) {
        String m = String.format("ItemPair %s is illegal.", _ClassUtils.safeClassName(pair));
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException funDontSupportMultiValue(String name) {
        String m = String.format("function[%s] don't support multi-value", name);
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }

    static CriteriaException illegalAssignmentItem(CriteriaContext context, @Nullable AssignmentItem item) {
        String m = String.format("%s is illegal %s", _ClassUtils.safeClassName(item), AssignmentItem.class.getName());
        return ContextStack.criteriaError(context, m);
    }

    @Deprecated
    static CriteriaException duplicateColumnAlias(CriteriaContext context, String columnAlias) {
        String m = String.format("column alias[%s] duplication.", columnAlias);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException duplicateColumnAlias(String columnAlias) {
        String m = String.format("column alias[%s] duplication.", columnAlias);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException columnAliasIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "You don't add any cte column alias");
    }

    static CriteriaException windowNotEnd(CriteriaContext context, ArmyWindow oldWindow, ArmyWindow window) {
        String m = String.format("last window[%s] not end,couldn't start new window[%s]",
                oldWindow.windowName(), window.windowName());
        throw ContextStack.criteriaError(context, m);
    }


    /**
     * @see #returningAll(TableMeta, String, List)
     */
    private static void appendSelectionGroup(final List<_TabularBlock> blockList, final List<_SelectItem> groupList) {
        TabularItem tabularItem;
        for (_TabularBlock block : blockList) {
            tabularItem = block.tableItem();
            if (tabularItem instanceof TableMeta) {
                groupList.add(SelectionGroups.singleGroup((TableMeta<?>) tabularItem, block.alias()));
            } else if (tabularItem instanceof _SelectionMap) {
                groupList.add(SelectionGroups.derivedGroup((_SelectionMap) tabularItem, block.alias()));
            } else if (tabularItem instanceof _NestedItems) {
                appendSelectionGroup(((_NestedItems) tabularItem).tableBlockList(), groupList);
            } else {
                String m;
                m = String.format("unknown %s[%s]", TabularItem.class.getName(), _ClassUtils.safeClassName(tabularItem));
                throw ContextStack.clearStackAndCriteriaError(m);
            }

        }
    }


    private static CriteriaException unknownSelectItem(final RowSet left, final SelectItem item) {
        return ContextStack.criteriaError(((CriteriaContextSpec) left).getContext()
                , _Exceptions::unknownSelectItem, item);
    }

    static CriteriaException childParentNotMatch(CriteriaContext context, ParentTableMeta<?> parent,
                                                 ChildTableMeta<?> child) {
        String m = String.format("%s isn't child of %s", child, parent);
        return ContextStack.criteriaError(context, m);
    }

    static UnknownFieldGroupException unknownFieldDerivedGroup(final @Nullable CriteriaContext currentContext,
                                                               String groupAlias) {
        final String m = String.format("unknown derived field group[%s].", groupAlias);
        final UnknownFieldGroupException e;
        if (currentContext == null) {
            e = ContextStack.clearStackAnd(UnknownFieldGroupException::new, m);
        } else {
            e = ContextStack.criteriaError(currentContext, UnknownFieldGroupException::new, m);
        }
        return e;
    }

    static UnknownFieldGroupException unknownTableFieldGroup(final @Nullable CriteriaContext currentContext,
                                                             final _SelectionGroup._TableFieldGroup group) {

        final TableMeta<?> table;
        table = ((TableField) group.selectionList().get(0)).tableMeta();
        final String m = String.format("unknown table field group[%s.%s].", group.tableAlias(), table);
        final UnknownFieldGroupException e;
        if (currentContext == null) {
            e = ContextStack.clearStackAnd(UnknownFieldGroupException::new, m);
        } else {
            e = ContextStack.criteriaError(currentContext, UnknownFieldGroupException::new, m);
        }
        return e;
    }

    static UnknownFieldGroupException errorInsertTableGroup(CriteriaContext context, TableMeta<?> insertTable,
                                                            TableMeta<?> groupTable) {
        String m = String.format("%s isn't insert table %s", groupTable, insertTable);
        return ContextStack.criteriaError(context, UnknownFieldGroupException::new, m);
    }


    private static final class SelectionMap implements _SelectionMap {

        private final List<Selection> selectionList;

        private final Map<String, Selection> selectionMap;

        private SelectionMap(List<Selection> selectionList, Map<String, Selection> selectionMap) {
            this.selectionList = selectionList;
            this.selectionMap = selectionMap;
        }

        @Override
        public Selection refSelection(String name) {
            return this.selectionMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.selectionList;
        }

    }//SelectionMap


}
