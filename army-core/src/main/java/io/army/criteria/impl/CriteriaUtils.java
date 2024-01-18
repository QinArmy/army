/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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


    static void appendSelfDescribedList(final List<? extends _SelfDescribed> list, final StringBuilder sqlBuilder,
                                        final _SqlContext context) {
        final int fieldSize = list.size();
        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            list.get(i).appendSql(sqlBuilder, context);
        }

    }

    static void selfDescribedListToString(final List<? extends _SelfDescribed> list, final StringBuilder builder) {
        final int fieldSize = list.size();
        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(list.get(i));
        }

    }

    @Deprecated
    static List<String> columnAliasList(final boolean required, Consumer<Consumer<String>> consumer) {
        List<String> list = _Collections.arrayList();
        consumer.accept(list::add);
        if (list.size() > 0) {
            list = _Collections.unmodifiableList(list);
        } else if (required) {
            list = Collections.emptyList();
        } else {
            list = EMPTY_STRING_LIST;
        }
        return list;
    }

    static List<String> stringList(final @Nullable CriteriaContext ctx, final boolean required,
                                   final Consumer<Consumer<String>> consumer) {
        List<String> list = _Collections.arrayList();
        consumer.accept(list::add);
        if (list.size() > 0) {
            list = _Collections.unmodifiableList(list);
        } else if (!required) {
            list = Collections.emptyList();

        } else if (ctx == null) {
            throw ContextStack.clearStackAnd(CriteriaUtils::dontAddAnyItem);
        } else {
            throw ContextStack.criteriaError(ctx, CriteriaUtils::dontAddAnyItem);
        }
        return list;
    }


    static void addAllField(final List<_ItemPair> itemPairList, final Consumer<FieldMeta<?>> consumer) {
        SqlField sqlField;
        for (_ItemPair itemPair : itemPairList) {
            if (itemPair instanceof _ItemPair._FieldItemPair) {
                sqlField = ((_ItemPair._FieldItemPair) itemPair).field();
                if (sqlField instanceof FieldMeta) {
                    consumer.accept((FieldMeta<?>) sqlField);
                } else if (sqlField instanceof QualifiedField) {
                    consumer.accept(((QualifiedField<?>) sqlField).fieldMeta());
                } else {
                    //TODO oracle ?
                    throw new UnsupportedOperationException();
                }
                continue;
            }
            if (!(itemPair instanceof _ItemPair._RowItemPair)) {
                throw new IllegalArgumentException("unknown itemPair");
            }
            for (SqlField field : ((_ItemPair._RowItemPair) itemPair).rowFieldList()) {
                if (field instanceof FieldMeta) {
                    consumer.accept((FieldMeta<?>) field);
                } else if (field instanceof QualifiedField) {
                    consumer.accept(((QualifiedField<?>) field).fieldMeta());
                } else {
                    //TODO oracle ?
                    throw new UnsupportedOperationException();
                }
            }// inner for

        }// outer for

    }


    static <T> T invokeConsumer(final T data, final @Nullable Consumer<? super T> consumer) {
        try {
            if (consumer == null) {
                throw new NullPointerException("java.util.function.Consumer is null,couldn't be invoked");
            }

            consumer.accept(data);

            if (data instanceof ArmyFuncClause) {
                ((ArmyFuncClause) data).endClause();
            }
            return data;
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }

    static <T, R> R invokeFunction(final @Nullable Function<T, R> function, final T data) {
        try {
            if (function == null) {
                throw new NullPointerException("java.util.function.Function is null,couldn't be invoked");
            }
            final R result;
            result = function.apply(data);
            if (result == null) {
                throw new NullPointerException("function must return non-null");
            }
            return result;
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }

    static List<_Expression> expressionList(final @Nullable CriteriaContext ctx, final boolean required,
                                            final Consumer<Consumer<Expression>> consumer) {
        final List<_Expression> list = _Collections.arrayList();
        consumer.accept(e -> list.add((ArmyExpression) e));
        final List<_Expression> expressionList;
        switch (list.size()) {
            case 0: {
                if (required) {
                    if (ctx == null) {
                        throw ContextStack.clearStackAnd(CriteriaUtils::dontAddAnyItem);
                    } else {
                        throw ContextStack.criteriaError(ctx, CriteriaUtils::dontAddAnyItem);
                    }
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
        final List<_SelectItem> list = _Collections.arrayList();
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
            selectionMap = Collections.singletonMap(selection.label(), selection);
        } else {
            final Map<String, Selection> map = _Collections.hashMap((int) (selectItemSize / 0.75F));
            for (SelectItem item : selectItemList) {

                if (item instanceof Selection) {
                    map.put(((Selection) item).label(), (Selection) item); // if alias duplication then override. Be consistent with  statement executor.
                } else if (item instanceof _SelectionGroup) {
                    for (Selection selection : ((_SelectionGroup) item).selectionList()) {
                        map.put(selection.label(), selection); // if alias duplication then override.Be consistent with  statement executor.
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

    static int standardModifier(final SQLs.Modifier distinct) {
        return (distinct == SQLs.DISTINCT || distinct == SQLs.ALL) ? 1 : -1;
    }


    static CriteriaException limitParamError(CriteriaContext criteriaContext, @Nullable Object value) {
        String m = String.format("limit clause only support [%s,%s,%s,%s] and non-negative,but input %s"
                , Long.class.getName(), Integer.class.getName(), Short.class.getName(), Byte.class.getName(), value);
        return ContextStack.criteriaError(criteriaContext, m);
    }


    static CriteriaException dontSupportMultiParam(CriteriaContext context) {
        return ContextStack.criteriaError(context, "don't support multi-parameter(literal)");
    }


    @Deprecated
    static List<Object> paramList(final CriteriaContext context, final @Nullable List<?> paramList) {
        throw new UnsupportedOperationException();
    }


    static List<Object> paramList(final @Nullable List<?> paramList) {
        if (paramList == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final int size;
        if ((size = paramList.size()) == 0) {
            throw ContextStack.clearStackAndCriteriaError("Batch dml parameter list must not empty.");
        }
        final List<Object> wrapperList = _Collections.arrayList(size);
        for (Object param : paramList) {
            if (param == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            wrapperList.add(param);
        }
        return _Collections.unmodifiableList(wrapperList);
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
                , ClassUtils.safeClassName(hint));
        return new CriteriaException(m);
    }


    static String enumToString(Enum<?> type) {
        return _StringUtils.builder()
                .append(type.getClass().getSimpleName())
                .append(_Constant.PERIOD)
                .append(type.name())
                .toString();
    }

    static _SelectionMap createAliasSelectionMap(final List<String> columnAliasList,
                                                 final List<? extends Selection> refSelectionList,
                                                 final String cteOrDerivedAlias) {

        final int selectionSize;
        selectionSize = refSelectionList.size();
        if (columnAliasList.size() != selectionSize) {
            throw CriteriaUtils.derivedColumnAliasSizeNotMatch(cteOrDerivedAlias, selectionSize, columnAliasList.size());
        }
        if (selectionSize == 1) {
            final Selection selection;
            selection = ArmySelections.renameSelection(refSelectionList.get(0), columnAliasList.get(0));
            return new SelectionMap(
                    _Collections.singletonList(selection),
                    _Collections.singletonMap(selection.label(), selection)
            );
        }
        final List<Selection> selectionList = _Collections.arrayList(selectionSize);
        final Map<String, Selection> selectionMap = _Collections.hashMap((int) (selectionSize / 0.75f));
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
                _Collections.unmodifiableList(selectionList),
                _Collections.unmodifiableMap(selectionMap)
        );


    }


    static _SelectionMap createDerivedSelectionMap(final List<? extends _SelectItem> selectItemList) {
        final int itemSize = selectItemList.size();
        assert itemSize > 0;

        _SelectItem selectItem;
        Selection selection;

        if (itemSize == 1 && (selectItem = selectItemList.get(0)) instanceof Selection) {
            selection = (Selection) selectItem;
            return new SelectionMap(
                    _Collections.singletonList(selection),
                    _Collections.singletonMap(selection.label(), selection)
            );
        }


        final List<Selection> selectionList = _Collections.arrayList(itemSize);
        final Map<String, Selection> selectionMap = _Collections.hashMap((int) (itemSize / 0.75f));

        List<? extends Selection> selectionGroup;


        for (int i = 0, groupSize; i < itemSize; i++) {
            selectItem = selectItemList.get(i);

            if (selectItem instanceof Selection) {
                selection = (Selection) selectItem;
                selectionList.add(selection);
                selectionMap.put(selection.label(), selection); // override, if duplication
                continue;
            } else if (!(selectItem instanceof _SelectionGroup)) {
                throw _Exceptions.unknownSelectItem(selectItem);
            }

            selectionGroup = ((_SelectionGroup) selectItem).selectionList();
            groupSize = selectionGroup.size();
            for (int j = 0; j < groupSize; j++) {
                selection = selectionGroup.get(j);
                selectionList.add(selection);
                selectionMap.put(selection.label(), selection); // override, if duplication
            }

        }
        return new SelectionMap(
                _Collections.unmodifiableList(selectionList),
                _Collections.unmodifiableMap(selectionMap)
        );
    }

    static List<ArmySQLExpression> asExpElementList(final String name,
                                                    final List<? extends SQLExpression> columnList) {
        final int columnSize;
        columnSize = columnList.size();
        final List<ArmySQLExpression> list;

        switch (columnSize) {
            case 0:
                list = _Collections.emptyList();
                break;
            case 1: {
                final SQLExpression exp;
                exp = columnList.get(0);
                if (!(exp instanceof ArmySQLExpression)) {
                    throw CriteriaUtils.funcArgError(name, exp);
                }
                list = _Collections.singletonList((ArmySQLExpression) exp);
            }
            break;
            default: {
                list = _Collections.arrayList(columnSize);
                for (SQLExpression exp : columnList) {
                    if (!(exp instanceof ArmySQLExpression)) {
                        throw CriteriaUtils.funcArgError(name, exp);
                    }
                    list.add((ArmySQLExpression) exp);
                }
            }
        }
        return list;
    }


    static MappingType arrayUnderlyingType(final TypeMeta typeMeta) {
        MappingType type;
        if (typeMeta instanceof MappingType) {
            type = (MappingType) typeMeta;
        } else {
            type = typeMeta.mappingType();
        }
        while (type instanceof MappingType.SqlArrayType) {
            type = ((MappingType.SqlArrayType) type).elementType();
        }
        return type;
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


    static CriteriaException derivedColumnAliasSizeNotMatch(String tableAlias, int selectionSize,
                                                            int aliasSize) {

        String m;
        m = String.format("cte/derived[%s] column alias list size[%s] and selection list size[%s] not match.",
                tableAlias, aliasSize, selectionSize);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    static CriteriaException notArmyOperator(final Object operator) {
        String m = String.format("%s is not army operator", operator);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    static CriteriaException cteHaveNoReturningClause(String name) {
        String m = String.format("cte[%s] have no RETURNING clause,couldn't reference.", name);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException dontAddAnyItem() {
        return ContextStack.clearStackAndCriteriaError("You don't add any item.");
    }

    static CriteriaException clearStackAndNonDefaultType(Object value) {
        return ContextStack.clearStackAndCriteriaError(_Exceptions::notFoundMappingType, value);
    }

    static CriteriaException mustExpressionOrType(String exp, Class<?> typeClass) {
        String m = String.format("%s must be %s or %s", exp, Expression.class.getName(), typeClass.getName());
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException rejectMultiDimensionArray() {
        return ContextStack.clearStackAndCriteriaError("reject multi dimension array");
    }

    static CriteriaException errorSymbol(@Nullable Object symbol) {
        return ContextStack.clearStackAndCriteriaError(String.format("error symbol %s", symbol));
    }

    static CriteriaException childParentDomainListNotMatch(CriteriaContext context, ChildTableMeta<?> child) {
        String m = String.format("%s insert domain list and parent insert statement domain list not match"
                , child);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException childParentRowNotMatch(_Insert._ValuesInsert child, _Insert._ValuesInsert parent) {
        String m = String.format("%s row number[%s] and parent row number[%s] not match.",
                child.table(), child.rowPairList().size(), parent.rowPairList().size());

        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException duplicateDynamicMethod(CriteriaContext context) {
        return ContextStack.criteriaError(context, "duplicate invoking dynamic method");
    }


    static CriteriaException funcColumnDuplicate(CriteriaContext context, String funcName, String columnName) {
        String m = String.format("tabular function[%s] column[%s] duplication.", funcName, columnName);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException funcColumnNameIsNotSimpleIdentifier(CriteriaContext context, String funcName,
                                                                 String columnName) {
        String m = String.format("tabular function[%s] column[%s] isn't simple identifier.", funcName, columnName);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException funcArgExpError() {
        return ContextStack.clearStackAndCriteriaError("expr error");
    }

    static RuntimeException funcArgError(String funcName, @Nullable Object errorArg) {
        if (errorArg == null) {
            return ContextStack.clearStackAndNullPointer();
        }
        String m = String.format("SQL function %s don't support %s", funcName, ClassUtils.safeClassName(errorArg));
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException notCompositeType(String funcName, Expression exp) {
        String m = String.format("%s isn't composite type expression,function[%s] don't support"
                , ClassUtils.safeClassName(exp), funcName);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException funcArgListIsEmpty(String name) {
        String m = String.format("function %s argument list must non-empty.", name);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    static CriteriaException errorCustomReturnType(String name, MappingType returnType) {
        String m = String.format("You specify error return type[%s] for function[%s]",
                ClassUtils.safeClassName(returnType), name);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException funcFieldNameNoText() {
        return ContextStack.clearStackAndCriteriaError("function field name must have text.");
    }

    static CriteriaException funcFieldDuplication(String name) {
        String m = String.format("function field name[%s] duplication.", name);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    static CriteriaException errorModifier(CriteriaContext context, @Nullable Object modifier) {
        String m = String.format("error modifier[%s]", modifier);
        return ContextStack.criteriaError(context, m);
    }

    static CriteriaException errorModifier(@Nullable SQLWords modifier) {
        String m = String.format("error modifier[%s]", modifier);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    static CriteriaException conflictClauseIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "You don't add conflict pair.");
    }

    static CriteriaException unknownWords(CriteriaContext context, @Nullable Object word) {
        return ContextStack.criteriaError(context, String.format("unknown word[%s]", word));
    }

    static CriteriaException unknownWords(@Nullable Object word) {
        return ContextStack.clearStackAndCriteriaError(String.format("unknown word[%s]", word));
    }

    static CriteriaException spaceMethodNotFirst() {
        return ContextStack.clearStackAndCriteriaError("space method can invoke ony at first time.");
    }

    static CriteriaException returningListIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "RETURNING list is empty");
    }

    static CriteriaException illegalItemPair(CriteriaContext context, @Nullable ItemPair pair) {
        String m = String.format("ItemPair %s is illegal.", ClassUtils.safeClassName(pair));
        return ContextStack.criteriaError(context, m);
    }


    static CriteriaException illegalAssignmentItem(@Nullable CriteriaContext context, @Nullable AssignmentItem item) {
        String m = String.format("%s is illegal %s", ClassUtils.safeClassName(item), AssignmentItem.class.getName());
        final CriteriaException e;
        if (context == null) {
            e = ContextStack.clearStackAndCriteriaError(m);
        } else {
            e = ContextStack.criteriaError(context, m);
        }
        return e;
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

    @Deprecated
    static CriteriaException standard10DontSupportWithClause(CriteriaContext context) {
        return ContextStack.criteriaError(context, "standard 1.0 api don't support WITH syntax.");
    }

    static CriteriaException standard10DontSupportWithClause() {
        return ContextStack.clearStackAndCriteriaError("standard 1.0 api don't support WITH clause.");
    }

    @Deprecated
    static CriteriaException standard10DontSupportWindow(CriteriaContext context) {
        return ContextStack.criteriaError(context, "standard 1.0 api don't support WINDOW syntax.");
    }

    static CriteriaException standard10DontSupportWindowClause() {
        return ContextStack.clearStackAndCriteriaError("standard 1.0 api don't support WINDOW clause.");
    }

    static CriteriaException standard10DontSupportWindowFunc() {
        return ContextStack.clearStackAndCriteriaError("standard 1.0 api don't support window function.");
    }

    static CriteriaException unknownTypeDef() {
        return ContextStack.clearStackAndCriteriaError("unknown TypeDef");
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
                m = String.format("unknown %s[%s]", TabularItem.class.getName(), ClassUtils.safeClassName(tabularItem));
                throw ContextStack.clearStackAndCriteriaError(m);
            }

        }
    }


    static CriteriaException childParentNotMatch(CriteriaContext context, ParentTableMeta<?> parent,
                                                 ChildTableMeta<?> child) {
        String m = String.format("%s isn't child of %s", child, parent);
        return ContextStack.criteriaError(context, m);
    }


    static UnknownFieldGroupException unknownFieldDerivedGroup(final @Nullable CriteriaContext currentContext,
                                                               String groupAlias) {
        final String m = String.format("unknown derived field group[%s]. please check whether or not use static SELECT clause.", groupAlias);
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
