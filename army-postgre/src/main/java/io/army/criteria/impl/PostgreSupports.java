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
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreCte;
import io.army.criteria.impl.inner.postgre._PostgreTableBlock;
import io.army.criteria.postgre.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.BooleanType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.*;


abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }


    static final List<_SelectItem> EMPTY_SELECT_ITEM_LIST = Collections.emptyList();

    static PostgreCtes postgreCteBuilder(final boolean recursive, final CriteriaContext context) {
        return new PostgreCteBuilder(recursive, context);
    }

    static PostgreWindow._PartitionBySpec namedWindow(String name, CriteriaContext context,
                                                      @Nullable String existingWindowName) {
        return new PostgreWindowImpl(name, context, existingWindowName);
    }

    static PostgreWindow._PartitionBySpec anonymousWindow(CriteriaContext context, @Nullable String existingWindowName) {
        return new PostgreWindowImpl(context, existingWindowName);
    }


    static SimpleDmlStatement closeCursor(String name) {
        if (!_StringUtils.hasText(name)) {
            throw _Exceptions.cursorNameNoText();
        }
        return new CloseCursorStatement(name);
    }

    static SimpleDmlStatement closeAllCursor() {
        return new CloseCursorStatement(SQLs.ALL);
    }


    @SuppressWarnings("unchecked")
    static abstract class PostgreTableOnBlock<TR, RR, OR extends Item>
            extends TabularBlocks.JoinClauseModifierTableBlock<OR>
            implements _PostgreTableBlock,
            PostgreStatement._StaticTableSampleClause<TR>,
            PostgreStatement._RepeatableClause<RR> {
        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        PostgreTableOnBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias, OR stmt) {
            super(joinType, modifier, table, alias, stmt);
        }


        @Override
        public final TR tableSample(final @Nullable Expression method) {
            if (method == null) {
                throw ContextStack.nullPointer(this.getContext());
            }
            this.sampleMethod = (ArmyExpression) method;
            return (TR) this;
        }

        @Override
        public final TR tableSample(
                BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
            return this.tableSample(method.apply(valueOperator, argument));
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method,
                                        BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            return this.tableSample(method.apply(valueOperator, supplier.get()));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                    BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                    String keyName) {
            return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public final TR ifTableSample(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.tableSample(expression);
            }
            return (TR) this;
        }

        @Override
        public final <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            final T argument;
            argument = supplier.get();
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object argument;
            argument = function.apply(keyName);
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final RR repeatable(final @Nullable Expression seed) {
            if (seed == null) {
                throw ContextStack.nullPointer(this.getContext());
            }
            this.seed = (ArmyExpression) seed;
            return (RR) this;
        }

        @Override
        public final RR repeatable(Supplier<Expression> supplier) {
            return this.repeatable(supplier.get());
        }

        @Override
        public final RR repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
            return this.repeatable(valueOperator.apply(seedValue));
        }

        @Override
        public final <E extends Number> RR repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
            return this.repeatable(valueOperator.apply(supplier.get()));
        }

        @Override
        public final RR repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                   String keyName) {
            return this.repeatable(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public final RR ifRepeatable(Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.repeatable(expression);
            }
            return (RR) this;
        }

        @Override
        public final <E extends Number> RR ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.repeatable(valueOperator.apply(value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                     String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.repeatable(valueOperator.apply(value));
            }
            return (RR) this;
        }

        @Override
        public final _Expression sampleMethod() {
            return this.sampleMethod;
        }

        @Override
        public final _Expression seed() {
            return this.seed;
        }


    }//PostgreTableOnBlock


    static final class FromClauseTableBlock extends TabularBlocks.FromClauseModifierTableBlock
            implements _PostgreTableBlock {

        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        FromClauseTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias) {
            super(joinType, modifier, table, alias);
        }

        void onSampleMethod(ArmyExpression sampleMethod) {
            this.sampleMethod = sampleMethod;
        }

        void onSeed(ArmyExpression seed) {
            this.seed = seed;
        }

        @Override
        public _Expression sampleMethod() {
            return this.sampleMethod;
        }

        @Override
        public _Expression seed() {
            return this.seed;
        }

    } // PostgreNoOnTableBlock


    static final class CteSearchClause implements PostgreQuery._SearchBreadthDepthClause,
            PostgreQuery._SetSearchSeqColumnClause,
            _PostgreCte._SearchClause {

        private final boolean required;

        private Boolean breadth;

        private List<String> columnList;

        private String searchSeqColumnName;

        private Selection searchSeqSelction;

        CteSearchClause(boolean required) {
            this.required = required;
        }

        @Override
        public PostgreQuery._SetSearchSeqColumnClause breadthFirstBy(String firstColumnName, String... rest) {
            this.columnList = ArrayUtils.unmodifiableListOf(firstColumnName, rest);
            this.breadth = Boolean.TRUE;
            return this;
        }

        @Override
        public PostgreQuery._SetSearchSeqColumnClause breadthFirstBy(Consumer<Consumer<String>> consumer) {
            this.columnList = ClauseUtils.invokingDynamicConsumer(true, true, consumer);
            this.breadth = Boolean.TRUE;
            return this;
        }

        @Override
        public PostgreQuery._SetSearchSeqColumnClause depthFirstBy(String firstColumnName, String... rest) {
            this.columnList = ArrayUtils.unmodifiableListOf(firstColumnName, rest);
            this.breadth = Boolean.FALSE;
            return this;
        }

        @Override
        public PostgreQuery._SetSearchSeqColumnClause depthFirstBy(Consumer<Consumer<String>> consumer) {
            this.columnList = ClauseUtils.invokingDynamicConsumer(true, true, consumer);
            this.breadth = Boolean.FALSE;
            return this;
        }

        @Override
        public void set(final @Nullable String searchSeqColumnName) {
            if (searchSeqColumnName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.searchSeqColumnName = searchSeqColumnName;
        }

        @Override
        public void set(Supplier<String> supplier) {
            set(ClauseUtils.invokeSupplier(supplier));
        }


        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final Boolean breadth = this.breadth;
            if (breadth == null) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(" SEARCH ");
            if (breadth) {
                sqlBuilder.append("BREADTH");
            } else {
                sqlBuilder.append("DEPTH");
            }
            sqlBuilder.append(" FIRST BY ");

            final List<String> columnList = this.columnList;
            final int columnSize = columnList.size();

            final DialectParser parser = context.parser();
            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                }
                parser.identifier(columnList.get(i), sqlBuilder);
            }

            sqlBuilder.append(_Constant.SPACE_SET_SPACE);
            parser.identifier(this.searchSeqColumnName, sqlBuilder);
        }

        @Override
        public Selection searchSeqSelection() {
            final Selection s = this.searchSeqSelction;
            if (s == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return s;
        }


        /**
         * @return true exists clause
         */
        boolean endClause() {
            final boolean optionIsNull = this.breadth == null;

            final String m = "You don't finish SEARCH clause";
            if (this.required) {
                if (optionIsNull || this.columnList == null || this.searchSeqColumnName == null) {
                    throw ContextStack.clearStackAndCriteriaError(m);
                }
            } else if (optionIsNull ^ (this.columnList == null) || optionIsNull ^ (this.searchSeqColumnName == null)) {
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            if (!optionIsNull) {
                this.searchSeqSelction = ArmySelections.forName(this.searchSeqColumnName, LongType.INSTANCE); // TODO right ?
            }
            return !optionIsNull;

        }


    } // CteSearchClause


    static final class CteCycleClause implements PostgreQuery._CteCycleColumnNameSpace,
            PostgreQuery._SetCycleMarkColumnClause,
            PostgreQuery._CycleToMarkValueSpec,
            PostgreQuery._CyclePathColumnClause,
            _PostgreCte._CycleClause {

        private final boolean required;

        private List<String> columnList;

        private String cycleMarkColumnName;

        private ArmyExpression cycleMarkValue;

        private ArmyExpression cycleMarkDefault;

        private String cyclePathColumnName;


        private Selection cycleMarkSelecion;

        private Selection cyclePathSelection;

        CteCycleClause(boolean required) {
            this.required = required;
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause space(String firstColumnName, String... rest) {
            this.columnList = ArrayUtils.unmodifiableListOf(firstColumnName, rest);
            return this;
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause space(Consumer<Consumer<String>> consumer) {
            this.columnList = ClauseUtils.invokingDynamicConsumer(true, true, consumer);
            return this;
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause ifSpace(Consumer<Consumer<String>> consumer) {
            this.columnList = ClauseUtils.invokingDynamicConsumer(false, true, consumer);
            return this;
        }

        @Override
        public PostgreQuery._CycleToMarkValueSpec set(final @Nullable String cycleMarkColumnName) {
            if (cycleMarkColumnName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.cycleMarkColumnName = cycleMarkColumnName;
            return this;
        }

        @Override
        public PostgreQuery._CycleToMarkValueSpec set(Supplier<String> supplier) {
            return set(ClauseUtils.invokeSupplier(supplier));
        }

        @Override
        public PostgreQuery._CyclePathColumnClause to(Expression cycleMarkValue, SQLs.WordDefault wordDefault, Expression cycleMarkDefault) {

            acceptMarkValueAndDefault(cycleMarkValue, cycleMarkDefault);
            return this;
        }

        @Override
        public PostgreQuery._CyclePathColumnClause to(Consumer<BiConsumer<Expression, Expression>> consumer) {
            final BiConsumer<Expression, Expression> func = this::acceptMarkValueAndDefault;
            CriteriaUtils.invokeConsumer(func, consumer);
            if (this.cycleMarkValue == null) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            return this;
        }

        @Override
        public PostgreQuery._CyclePathColumnClause ifTo(Consumer<BiConsumer<Expression, Expression>> consumer) {
            final BiConsumer<Expression, Expression> func = this::acceptMarkValueAndDefault;
            CriteriaUtils.invokeConsumer(func, consumer);
            return this;
        }

        @Override
        public void using(final @Nullable String cyclePathColumnName) {
            if (cyclePathColumnName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.cyclePathColumnName = cyclePathColumnName;
        }

        @Override
        public void using(Supplier<String> supplier) {
            using(ClauseUtils.invokeSupplier(supplier));
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<String> columnList = this.columnList;
            final int columnSize;
            if (columnList == null || (columnSize = columnList.size()) == 0) {
                throw _Exceptions.castCriteriaApi();
            }

            final DialectParser parser = context.parser();

            sqlBuilder.append(" CYCLE ");
            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                }
                parser.identifier(columnList.get(i), sqlBuilder);
            }

            sqlBuilder.append(_Constant.SPACE_SET_SPACE);
            parser.identifier(this.cycleMarkColumnName, sqlBuilder);

            final ArmyExpression cycleMarkValue = this.cycleMarkValue, cycleMarkDefault = this.cycleMarkDefault;
            if (cycleMarkValue != null) {
                sqlBuilder.append(" TO");
                cycleMarkValue.appendSql(sqlBuilder, context);
                sqlBuilder.append(_Constant.SPACE_DEFAULT);
                assert cycleMarkDefault != null;
                cycleMarkDefault.appendSql(sqlBuilder, context);
            }

            sqlBuilder.append(_Constant.SPACE_USING)
                    .append(_Constant.SPACE);
            parser.identifier(this.cyclePathColumnName, sqlBuilder);

        }

        @Override
        public Selection cycleMarkSelection() {
            final Selection s = this.cycleMarkSelecion;
            if (s == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return s;
        }

        @Override
        public Selection cyclePathSelection() {
            final Selection s = this.cyclePathSelection;
            if (s == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return s;
        }

        private void acceptMarkValueAndDefault(@Nullable Expression cycleMarkValue, @Nullable Expression cycleMarkDefault) {
            if (cycleMarkValue == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (cycleMarkDefault == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.cycleMarkValue = (ArmyExpression) cycleMarkValue;
            this.cycleMarkDefault = (ArmyExpression) cycleMarkDefault;
        }

        /**
         * @return true exists clause
         */
        boolean endClause() {
            final boolean columnListIsNull = this.columnList == null;
            final String m = "You don't finish CYCLE clause";
            if (this.required) {
                if (columnListIsNull || this.cycleMarkColumnName == null || this.cyclePathColumnName == null) {
                    throw ContextStack.clearStackAndNullPointer(m);
                }
            } else if (columnListIsNull ^ (this.cycleMarkColumnName == null)
                    || columnListIsNull ^ (this.cyclePathColumnName == null)) {
                throw ContextStack.clearStackAndNullPointer(m);
            }

            if (!columnListIsNull) {
                final Expression cycleMarkValue = this.cycleMarkValue;
                if (cycleMarkValue == null) {
                    this.cycleMarkSelecion = ArmySelections.forName(this.cycleMarkColumnName, BooleanType.INSTANCE);
                } else {
                    this.cycleMarkSelecion = ArmySelections.forName(this.cycleMarkColumnName, cycleMarkValue.typeMeta().mappingType()); //TODO optimizing me
                }
                this.cyclePathSelection = ArmySelections.forName(this.cyclePathColumnName, StringType.INSTANCE); // TODO right ?

            }
            return !columnListIsNull;
        }


    } // CteCycleClause


    @SuppressWarnings("unchecked")
    static abstract class PostgreCteSearchCycleSpec<T extends Item, U extends Item>
            implements PostgreQuery._CteSearchClause<T>, PostgreQuery._CteCycleClause<U> {

        final SubQuery subQuery;

        private PostgreSupports.CteSearchClause searchClause;

        private PostgreSupports.CteCycleClause cycleClause;

        PostgreCteSearchCycleSpec(SubQuery subQuery) {
            this.subQuery = subQuery;
        }

        @Override
        public final T search(Consumer<PostgreQuery._SearchBreadthDepthClause> consumer) {
            if (this.searchClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final PostgreSupports.CteSearchClause searchClause = new PostgreSupports.CteSearchClause(true);
            CriteriaUtils.invokeConsumer(searchClause, consumer);
            searchClause.endClause();
            this.searchClause = searchClause;
            return (T) this;
        }

        @Override
        public final T ifSearch(Consumer<PostgreQuery._SearchBreadthDepthClause> consumer) {
            if (this.searchClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final PostgreSupports.CteSearchClause searchClause = new PostgreSupports.CteSearchClause(false);
            CriteriaUtils.invokeConsumer(searchClause, consumer);
            if (searchClause.endClause()) {
                this.searchClause = searchClause;
            }
            return (T) this;
        }

        @Override
        public final U cycle(Consumer<PostgreQuery._CteCycleColumnNameSpace> consumer) {
            if (this.cycleClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final PostgreSupports.CteCycleClause searchClause = new PostgreSupports.CteCycleClause(true);
            CriteriaUtils.invokeConsumer(searchClause, consumer);
            searchClause.endClause();
            this.cycleClause = searchClause;
            return (U) this;
        }

        @Override
        public final U ifCycle(Consumer<PostgreQuery._CteCycleColumnNameSpace> consumer) {
            if (this.cycleClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final PostgreSupports.CteCycleClause searchClause = new PostgreSupports.CteCycleClause(false);
            CriteriaUtils.invokeConsumer(searchClause, consumer);
            if (searchClause.endClause()) {
                this.cycleClause = searchClause;
            }
            return (U) this;
        }


        @Nullable
        final _PostgreCte._SearchClause getSearchClause() {
            return this.searchClause;
        }

        @Nullable
        final _PostgreCte._CycleClause getCycleClause() {
            return this.cycleClause;
        }


    } // PostgreCteSearchCycleSpec


    private static final class DynamicCteSearchSpec extends PostgreCteSearchCycleSpec<
            PostgreQuery._DynamicCteCycleSpec,
            Statement._CommaClause<PostgreCtes>>
            implements PostgreQuery._DynamicCteSearchSpec {

        private final DynamicQueryParensClause asClause;

        private DynamicCteSearchSpec(DynamicQueryParensClause asClause, SubQuery subQuery) {
            super(subQuery);
            this.asClause = asClause;
        }

        @Override
        public PostgreCtes comma() {
            return asClause.searchClauseEnd(this);
        }


    } // DynamicCteSearchSpec


    private static final class NonRecursiveDynamicCteSearchSpec implements PostgreQuery._DynamicCteSearchSpec {

        private final PostgreCtes cteBuilder;

        private NonRecursiveDynamicCteSearchSpec(PostgreCtes cteBuilder) {
            this.cteBuilder = cteBuilder;
        }

        @Override
        public PostgreQuery._DynamicCteCycleSpec search(Consumer<PostgreQuery._SearchBreadthDepthClause> consumer) {
            throw PostgreUtils.nonRecursiveWithClause();
        }

        @Override
        public PostgreQuery._DynamicCteCycleSpec ifSearch(Consumer<PostgreQuery._SearchBreadthDepthClause> consumer) {
            throw PostgreUtils.nonRecursiveWithClause();
        }

        @Override
        public Statement._CommaClause<PostgreCtes> cycle(Consumer<PostgreQuery._CteCycleColumnNameSpace> consumer) {
            throw PostgreUtils.nonRecursiveWithClause();
        }

        @Override
        public Statement._CommaClause<PostgreCtes> ifCycle(Consumer<PostgreQuery._CteCycleColumnNameSpace> consumer) {
            throw PostgreUtils.nonRecursiveWithClause();
        }


        @Override
        public PostgreCtes comma() {
            return this.cteBuilder;
        }


    } // NonRecursiveDynamicCteSearchSpec


    static final class PostgreCte implements _PostgreCte {

        private final String name;

        private final List<String> columnAliasList;

        private final Postgres.WordMaterialized modifier;

        final SubStatement subStatement;

        private final _SelectionMap selectionMap;
        private final _SearchClause searchClause;

        private final _CycleClause cycleClause;


        PostgreCte(String name, @Nullable List<String> columnAliasList,
                   @Nullable Postgres.WordMaterialized modifier, SubStatement subStatement) {
            this(name, columnAliasList, modifier, subStatement, null, null);
        }

        PostgreCte(String name, final @Nullable List<String> columnAliasList,
                   @Nullable Postgres.WordMaterialized modifier, SubStatement subStatement,
                   final @Nullable _SearchClause searchClause, final @Nullable _CycleClause cycleClause) {
            this.name = name;
            this.columnAliasList = _Collections.safeUnmodifiableList(columnAliasList);
            this.modifier = modifier;
            this.subStatement = subStatement;

            this.searchClause = searchClause;
            this.cycleClause = cycleClause;

            if (subStatement instanceof DerivedTable) {
                if (this.columnAliasList.size() == 0) {
                    this.selectionMap = createDerivedSelectionMap(((_DerivedTable) subStatement).refAllSelection());
                } else {
                    this.selectionMap = createAliasSelectionMap(((_DerivedTable) subStatement).refAllSelection());
                }
            } else if (!(subStatement instanceof _ReturningDml)) {
                assert searchClause == null && cycleClause == null;
                this.selectionMap = null;
            } else if (this.columnAliasList.size() == 0) {
                this.selectionMap = CriteriaUtils.createDerivedSelectionMap(((_ReturningDml) subStatement).flatSelectItem());
            } else {
                this.selectionMap = CriteriaUtils.createAliasSelectionMap(this.columnAliasList, ((_ReturningDml) subStatement).flatSelectItem(), this.name);
            }

        }


        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Selection refSelection(String derivedAlias) {
            final _SelectionMap selectionMap = this.selectionMap;
            if (selectionMap == null) {
                throw CriteriaUtils.cteHaveNoReturningClause(this.name);
            }
            return selectionMap.refSelection(derivedAlias);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            final _SelectionMap selectionMap = this.selectionMap;
            if (selectionMap == null) {
                throw CriteriaUtils.cteHaveNoReturningClause(this.name);
            }
            return selectionMap.refAllSelection();
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<String> columnAliasList() {
            return this.columnAliasList;
        }

        @Override
        public Postgres.WordMaterialized modifier() {
            return this.modifier;
        }

        @Override
        public _SearchClause searchClause() {
            return this.searchClause;
        }

        @Override
        public _CycleClause cycleClause() {
            return this.cycleClause;
        }


        private _SelectionMap createAliasSelectionMap(final List<? extends Selection> refSelectionList) {

            final List<String> columnAliasList = this.columnAliasList;

            final List<Selection> addSelectionList;
            addSelectionList = createSercherCycleSelectionList();

            final int selectionSize, addSelectionSize, totalSelectionSize;
            selectionSize = refSelectionList.size();

            addSelectionSize = addSelectionList.size();

            totalSelectionSize = selectionSize + addSelectionSize;

            if (columnAliasList.size() != selectionSize) {
                throw CriteriaUtils.derivedColumnAliasSizeNotMatch(this.name, selectionSize, columnAliasList.size());
            }
            if (totalSelectionSize == 1) {
                final Selection selection;
                selection = ArmySelections.renameSelection(refSelectionList.get(0), columnAliasList.get(0));
                return CriteriaUtils.createSingletonSelectionMap(selection);
            }
            final List<Selection> selectionList = _Collections.arrayList(totalSelectionSize);
            final Map<String, Selection> selectionMap = _Collections.hashMapForSize(totalSelectionSize);
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

            for (int i = 0; i < addSelectionSize; i++) {
                selection = addSelectionList.get(i);
                if (selectionMap.putIfAbsent(selection.label(), selection) != null) {
                    throw CriteriaUtils.duplicateColumnAlias(selection.label());
                }
                selectionList.add(selection);
            }

            assert selectionList.size() == selectionMap.size();
            return CriteriaUtils.createSelectionMap(selectionList, selectionMap);


        }


        private _SelectionMap createDerivedSelectionMap(final List<? extends Selection> selectItemList) {

            final List<Selection> addSelectionList;
            addSelectionList = createSercherCycleSelectionList();

            final int selectionSize, addSelectionSize, totalSelectionSize;
            selectionSize = selectItemList.size();
            addSelectionSize = addSelectionList.size();

            totalSelectionSize = selectionSize + addSelectionSize;


            if (totalSelectionSize == 1) {
                return CriteriaUtils.createSingletonSelectionMap(selectItemList.get(0));
            }

            final List<Selection> selectionList = _Collections.arrayList(totalSelectionSize);
            final Map<String, Selection> selectionMap = _Collections.hashMapForSize(totalSelectionSize);

            Selection selection;

            for (int i = 0; i < selectionSize; i++) {
                selection = selectItemList.get(i);
                selectionMap.put(selection.label(), selection); // override, if duplication
            }

            for (int i = 0; i < addSelectionSize; i++) {
                selection = addSelectionList.get(i);
                if (selectionMap.putIfAbsent(selection.label(), selection) != null) {
                    throw CriteriaUtils.duplicateColumnAlias(selection.label());
                }
                selectionList.add(selection);
            }

            return CriteriaUtils.createSelectionMap(selectionList, selectionMap);
        }


        private List<Selection> createSercherCycleSelectionList() {
            final _SearchClause searchClause = this.searchClause;
            final _CycleClause cycleClause = this.cycleClause;

            final List<Selection> list, temp;
            if (searchClause == null && cycleClause == null) {
                list = Collections.emptyList();
            } else if (searchClause != null && cycleClause != null) {
                temp = _Collections.arrayList(3);
                temp.add(searchClause.searchSeqSelection());
                temp.add(cycleClause.cycleMarkSelection());
                temp.add(cycleClause.cyclePathSelection());

                list = _Collections.unmodifiableList(temp);
            } else if (searchClause != null) {
                list = Collections.singletonList(searchClause.searchSeqSelection());
            } else {
                temp = _Collections.arrayList(2);
                temp.add(cycleClause.cycleMarkSelection());
                temp.add(cycleClause.cyclePathSelection());
                list = _Collections.unmodifiableList(temp);
            }
            return list;
        }


    } // PostgreCte


    private static final class DynamicCteInsertParensSpec extends CteParensClause<PostgreInsert._InsertDynamicCteAsClause>
            implements PostgreInsert._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subSingleInsert(String)
         */
        private DynamicCteInsertParensSpec(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreInsert._DynamicSubOptionSpec<DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreInsert._DynamicSubOptionSpec<DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreInserts.dynamicSubInsert(this.context, this::subInsertEnd));
        }

        private DialectStatement._CommaClause<PostgreCtes> subInsertEnd(final SubStatement statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicCteInsertParensSpec


    private static final class DynamicUpdateParensClause extends CteParensClause<PostgreUpdate._UpdateDynamicCteAsClause>
            implements PostgreUpdate._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subSingleUpdate(String)
         */
        private DynamicUpdateParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreUpdate._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreUpdate._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreUpdates.subSimpleUpdate(this.context, this::subUpdateEnd));
        }


        private DialectStatement._CommaClause<PostgreCtes> subUpdateEnd(final SubStatement statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicUpdateParensClause

    private static final class DynamicDeleteParensClause extends CteParensClause<PostgreDelete._DeleteDynamicCteAsClause>
            implements PostgreDelete._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subSingleDelete(String)
         */
        private DynamicDeleteParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreDelete._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreDelete._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreDeletes.subSimpleDelete(this.context, this::subDeleteEnd));
        }

        private DialectStatement._CommaClause<PostgreCtes> subDeleteEnd(final SubStatement statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicDeleteParensClause


    private static final class DynamicQueryParensClause extends CteParensClause<PostgreQuery._QueryDynamicCteAsClause>
            implements PostgreQuery._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subQuery(String)
         */
        private DynamicQueryParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }


        @Override
        public PostgreQuery._DynamicCteSearchSpec as(Function<PostgreQuery.WithSpec<PostgreQuery._DynamicCteSearchSpec>, PostgreQuery._DynamicCteSearchSpec> function) {
            return as(null, function);
        }

        @Override
        public PostgreQuery._DynamicCteSearchSpec as(@Nullable Postgres.WordMaterialized modifier, Function<PostgreQuery.WithSpec<PostgreQuery._DynamicCteSearchSpec>, PostgreQuery._DynamicCteSearchSpec> function) {
            this.modifier = modifier;
            return ClauseUtils.invokeFunction(function, PostgreQueries.subQuery(this.builder.context, this::subQueryEnd));
        }


        private PostgreQuery._DynamicCteSearchSpec subQueryEnd(final SubQuery query) {
            final PostgreQuery._DynamicCteSearchSpec spec;
            if (this.builder.recursive && PostgreUtils.isUnionQuery(query)) {
                this.builder.endLastCte(); // clear old
                this.builder.lastQueryCteEnder = spec = new DynamicCteSearchSpec(this, query);
            } else {
                final PostgreCte cte;
                cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, query);
                this.context.onAddCte(cte);
                spec = new NonRecursiveDynamicCteSearchSpec(this.builder);
            }
            return spec;
        }


        /**
         * @see DynamicCteSearchSpec#comma()
         */
        PostgreCtes searchClauseEnd(final DynamicCteSearchSpec spec) {
            final Statement._CommaClause<?> commaClause = this.builder.lastQueryCteEnder;
            if (commaClause != spec) {
                return this.builder;
            }

            this.builder.lastQueryCteEnder = null; // must clear
            final PostgreCteSearchCycleSpec<?, ?> clause = spec;
            final _Cte cte;
            cte = new PostgreSupports.PostgreCte(this.name, this.columnAliasList, this.modifier, clause.subQuery,
                    clause.searchClause, clause.cycleClause);
            this.builder.context.onAddCte(cte);
            return this.builder;
        }


    } // DynamicQueryParensClause


    private static final class PostgreCteBuilder implements PostgreCtes, CteBuilder,
            DialectStatement._CommaClause<PostgreCtes> {

        private final boolean recursive;

        private final CriteriaContext context;

        /**
         * @see DynamicCteSearchSpec#comma()
         * @see DynamicCteSearchSpec#DynamicCteSearchSpec(DynamicQueryParensClause, SubQuery)
         * @see DynamicQueryParensClause#subQueryEnd(SubQuery)
         */
        private DialectStatement._CommaClause<?> lastQueryCteEnder;

        private PostgreCteBuilder(final boolean recursive, CriteriaContext context) {
            context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.context = context;

        }

        @Override
        public PostgreInsert._DynamicCteParensSpec subSingleInsert(String name) {
            this.endLastCte();
            return new DynamicCteInsertParensSpec(name, this);
        }

        @Override
        public PostgreUpdate._DynamicCteParensSpec subSingleUpdate(String name) {
            this.endLastCte();
            return new DynamicUpdateParensClause(name, this);
        }


        @Override
        public PostgreDelete._DynamicCteParensSpec subSingleDelete(String name) {
            this.endLastCte();
            return new DynamicDeleteParensClause(name, this);
        }

        @Override
        public PostgreQuery._DynamicCteParensSpec subQuery(String name) {
            this.endLastCte();
            return new DynamicQueryParensClause(name, this);
        }


        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public void endLastCte() {
            final DialectStatement._CommaClause<?> ender = this.lastQueryCteEnder;
            this.lastQueryCteEnder = null;
            if (ender != null) {
                ender.comma();
            }
        }

        @Override
        public PostgreCtes comma() {
            return this;
        }


    }//PostgreCteBuilderImpl


    static final class PostgreWindowImpl extends SQLWindow.SQLExcludeWindow<
            PostgreWindow._PartitionByCommaSpec,
            PostgreWindow._OrderByCommaSpec,
            PostgreWindow._FrameExtentSpec,
            PostgreWindow._PostgreFrameExclusionClause,
            PostgreWindow._PostgreFrameBetweenClause,
            PostgreWindow._PostgreFrameExclusionClause,
            PostgreWindow._FrameUnitSpaceSpec,
            Item>
            implements PostgreWindow._PartitionBySpec,
            PostgreWindow._PartitionByCommaSpec,
            PostgreWindow._OrderByCommaSpec,
            PostgreWindow._PostgreFrameBetweenClause,
            PostgreWindow._PostgreFrameExclusionClause,
            PostgreWindow._FrameUnitSpaceSpec {

        /**
         * @see #namedWindow(String, CriteriaContext, String)
         */
        private PostgreWindowImpl(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        /**
         * @see #anonymousWindow(CriteriaContext, String)
         */
        private PostgreWindowImpl(CriteriaContext context, @Nullable String existingWindowName) {
            super(context, existingWindowName);
        }


    } // PostgreWindow


    /**
     * @see #closeCursor(String)
     * @see #closeAllCursor()
     */
    static final class CloseCursorStatement extends StatementMockSupport implements SimpleDmlStatement, _CloseCursor {

        private final Object targetCursor;

        private CloseCursorStatement(Object targetCursor) {
            super(CriteriaContexts.otherPrimaryContext(PostgreUtils.DIALECT));
            // here don't need to push context
            assert targetCursor instanceof String || targetCursor == SQLs.ALL;
            this.targetCursor = targetCursor;
        }


        @Override
        public Object targetCursor() {
            return this.targetCursor;
        }

        @Override
        public void prepared() {
            // no-op
        }

        @Override
        public boolean isPrepared() {
            return true;
        }


        @Override
        public void clear() {
            // no-op,  here don't need to push context
        }


    } // CloseCursorStatement


}
