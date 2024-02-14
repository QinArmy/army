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
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreCte;
import io.army.criteria.impl.inner.postgre._PostgreQuery;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.postgre.PostgreDialect;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class PostgreQueries<I extends Item> extends SimpleQueries.WithCteDistinctOnSimpleQueries<
        I,
        PostgreCtes,
        PostgreQuery._SelectSpec<I>,
        Postgres.Modifier,
        PostgreQuery._PostgreSelectCommaSpec<I>,
        PostgreQuery._FromSpec<I>,
        PostgreQuery._TableSampleJoinSpec<I>,
        Statement._AsClause<PostgreQuery._ParensJoinSpec<I>>,
        PostgreQuery._JoinSpec<I>,
        PostgreStatement._FuncColumnDefinitionAsClause<PostgreQuery._JoinSpec<I>>,
        PostgreQuery._TableSampleOnSpec<I>,
        Statement._AsParensOnClause<PostgreQuery._JoinSpec<I>>,
        Statement._OnClause<PostgreQuery._JoinSpec<I>>,
        PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreQuery._JoinSpec<I>>>,
        PostgreQuery._GroupBySpec<I>,
        PostgreQuery._WhereAndSpec<I>,
        PostgreQuery._GroupByCommaSpec<I>,
        PostgreQuery._HavingSpec<I>,
        PostgreQuery._HavingAndSpec<I>,
        PostgreQuery._WindowSpec<I>,
        PostgreQuery._OrderByCommaSpec<I>,
        PostgreQuery._LimitSpec<I>,
        PostgreQuery._OffsetSpec<I>,
        PostgreQuery._FetchSpec<I>,
        PostgreQuery._LockSpec<I>,
        PostgreQuery._QueryWithComplexSpec<I>>
        implements PostgreQuery,
        _PostgreQuery,
        PostgreQuery.WithSpec<I>,
        PostgreQuery._PostgreSelectCommaSpec<I>,
        PostgreQuery._TableSampleJoinSpec<I>,
        PostgreQuery._RepeatableJoinClause<I>,
        PostgreQuery._ParensJoinSpec<I>,
        PostgreQuery._WhereAndSpec<I>,
        PostgreQuery._HavingSpec<I>,
        PostgreQuery._HavingAndSpec<I>,
        PostgreQuery._WindowCommaSpec<I>,
        PostgreQuery._GroupByCommaSpec<I>,
        PostgreQuery._OrderByCommaSpec<I>,
        PostgreQuery._FetchSpec<I> {


    static WithSpec<Select> simpleQuery() {
        return new SimpleSelect<>(null, null, PostgreQueries::postgreIdentitySelect);
    }

    static WithSpec<_BatchSelectParamSpec> batchQuery() {
        return new SimpleSelect<>(null, null, PostgreQueries::mapToBatchSelect);
    }

    static <I extends Item> PostgreQueries<I> fromDispatcher(ArmyStmtSpec spec,
                                                             Function<? super Select, I> function) {
        return new SimpleSelect<>(spec, null, function);
    }

    static <I extends Item> PostgreQueries<I> fromSubDispatcher(ArmyStmtSpec spec,
                                                                Function<? super SubQuery, I> function) {
        return new SimpleSubQuery<>(spec, null, function);
    }


    static <I extends Item> WithSpec<I> subQuery(CriteriaContext outerContext,
                                                 Function<? super SubQuery, I> function) {
        return new SimpleSubQuery<>(null, outerContext, function);
    }


    static <I extends Item> _CteComma<I> complexCte(
            final CriteriaContext context, final boolean recursive, final Function<Boolean, I> function) {
        return new StaticCteComma<>(context, recursive, function);
    }

    private static _BatchSelectParamSpec mapToBatchSelect(final Select select) {
        if (select instanceof _Statement._WithClauseSpec) {
            PostgreUtils.validateDmlInWithClause(((_Statement._WithClauseSpec) select).cteList(), (PostgreQuery) select);
        }
        final _BatchSelectParamSpec spec;
        if (select instanceof _Query) {
            spec = ((SimpleSelect<?>) select)::wrapToBatchSelect;
        } else if (select instanceof UnionSelect) {
            spec = ((UnionSelect) select)::wrapToBatchSelect;
        } else if (select instanceof BracketSelect) {
            spec = ((BracketSelect<?>) select)::wrapToBatchSelect;
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return spec;
    }

    private static Select postgreIdentitySelect(final Select select) {
        if (select instanceof _Statement._WithClauseSpec) {
            PostgreUtils.validateDmlInWithClause(((_Statement._WithClauseSpec) select).cteList(), (PostgreQuery) select);
        }
        return select;
    }

    private SQLs.Modifier groupByModifier;

    private List<_Window> windowList;

    private List<_LockBlock> lockBlockList;

    private _TabularBlock fromCrossBlock;

    private PostgreQueries(@Nullable ArmyStmtSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }


    @Override
    public final _StaticCteParensSpec<_SelectSpec<I>> with(String name) {
        return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _StaticCteParensSpec<_SelectSpec<I>> withRecursive(String name) {
        return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(final @Nullable Expression method) {
        if (method == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.getFromClauseBlock().onSampleMethod((ArmyExpression) method);
        return this;
    }


    @Override
    public final _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
            BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final <E> _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
            BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.tableSample(method.apply(valueOperator, supplier.get()));
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
            BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
    }

    @Override
    public final _RepeatableJoinClause<I> ifTableSample(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.tableSample(expression);
        }
        return this;
    }

    @Override
    public final <E> _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
            BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
            BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(final @Nullable Expression seed) {
        if (seed == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.getFromClauseBlock().onSeed((ArmyExpression) seed);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(Supplier<Expression> supplier) {
        return this.repeatable(supplier.get());
    }

    @Override
    public final _JoinSpec<I> repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
        return this.repeatable(valueOperator.apply(seedValue));
    }

    @Override
    public final <E extends Number> _JoinSpec<I> repeatable(Function<E, Expression> valueOperator,
                                                            Supplier<E> supplier) {
        return this.repeatable(valueOperator.apply(supplier.get()));
    }

    @Override
    public final _JoinSpec<I> repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                         String keyName) {
        return this.repeatable(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.repeatable(expression);
        }
        return this;
    }

    @Override
    public final <E extends Number> _JoinSpec<I> ifRepeatable(Function<E, Expression> valueOperator,
                                                              Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                           String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(String first, String... rest) {
        this.getFromDerivedBlock().parens(first, rest);
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getFromDerivedBlock().parens(this.context, consumer);
        return this;
    }

    @Override
    public final _JoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromDerivedBlock().ifParens(this.context, consumer);
        return this;
    }

    @Override
    public _JoinSpec<I> from(Function<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final _JoinSpec<I> crossJoin(Function<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> join(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _JoinSpec<I> ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item) {
        if (modifier != null && modifier != SQLs.ALL && modifier != SQLs.DISTINCT) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.groupByModifier = modifier;
        return this.groupBy(item);
    }

    @Override
    public final _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item1,
                                              GroupByItem item2) {
        if (modifier != null && modifier != SQLs.ALL && modifier != SQLs.DISTINCT) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.groupByModifier = modifier;
        return this.groupBy(item1, item2);
    }

    @Override
    public final _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item1,
                                              GroupByItem item2, GroupByItem item3) {
        if (modifier != null && modifier != SQLs.ALL && modifier != SQLs.DISTINCT) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.groupByModifier = modifier;
        return this.groupBy(item1, item2, item3);
    }

    @Override
    public final _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item1,
                                              GroupByItem item2, GroupByItem item3, GroupByItem item4) {
        if (modifier != null && modifier != SQLs.ALL && modifier != SQLs.DISTINCT) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.groupByModifier = modifier;
        return this.groupBy(item1, item2, item3, item4);
    }

    @Override
    public final _HavingSpec<I> groupBy(@Nullable SQLs.Modifier modifier,
                                        Consumer<Consumer<GroupByItem>> consumer) {
        if (modifier != null && modifier != SQLs.ALL && modifier != SQLs.DISTINCT) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.groupByModifier = modifier;
        return this.groupBy(consumer);
    }

    @Override
    public final _HavingSpec<I> ifGroupBy(@Nullable SQLs.Modifier modifier,
                                          Consumer<Consumer<GroupByItem>> consumer) {
        if (modifier != null && modifier != SQLs.ALL && modifier != SQLs.DISTINCT) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }

        this.groupBy(consumer);

        if (this.hasGroupByClause()) {
            this.groupByModifier = modifier;
        } else {
            this.groupByModifier = null;
        }
        return this;
    }

    @Override
    public final Window._WindowAsClause<PostgreWindow._PartitionBySpec, _WindowCommaSpec<I>> window(String name) {
        return new NamedWindowAsClause<>(this.context, name, this::onAddWindow, PostgreSupports::namedWindow);
    }

    @Override
    public final Window._WindowAsClause<PostgreWindow._PartitionBySpec, _WindowCommaSpec<I>> comma(String name) {
        return new NamedWindowAsClause<>(this.context, name, this::onAddWindow, PostgreSupports::namedWindow);
    }

    @Override
    public final _OrderBySpec<I> windows(Consumer<Window.Builder<PostgreWindow._PartitionBySpec>> consumer) {
        consumer.accept(this::createDynamicWindow);
        if (this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<I> ifWindows(Consumer<Window.Builder<PostgreWindow._PartitionBySpec>> consumer) {
        consumer.accept(this::createDynamicWindow);
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        return new StaticLockBlock<>(PostgreLockStrength.FOR_UPDATE, this);
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        return new StaticLockBlock<>(PostgreLockStrength.FOR_SHARE, this);
    }


    @Override
    public final _LockOfTableSpec<I> forNoKeyUpdate() {
        return new StaticLockBlock<>(PostgreLockStrength.FOR_NO_KEY_UPDATE, this);
    }

    @Override
    public final _LockOfTableSpec<I> forKeyShare() {
        return new StaticLockBlock<>(PostgreLockStrength.FOR_KEY_SHARE, this);
    }

    @Override
    public final _LockSpec<I> ifFor(Consumer<_PostgreDynamicLockStrengthClause> consumer) {
        final DynamicLockBlock block = new DynamicLockBlock(this);
        consumer.accept(block);
        if (block.lockStrength != null) {
            this.onAddLockBlock(block);
        }
        return this;
    }

    @Override
    public final SQLs.Modifier groupByModifier() {
        return this.groupByModifier;
    }

    @Override
    public final List<_Window> windowList() {
        final List<_Window> list = this.windowList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    public final List<_LockBlock> lockBlockList() {
        final List<_LockBlock> list = this.lockBlockList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    final void onEndQuery() {
        final List<_LockBlock> lockBlockList = this.lockBlockList;
        final List<_Window> windowList = this.windowList;
        if (lockBlockList != null && lockBlockList.size() > 0) {
            if (this.hasGroupByClause()) {
                throw lockWithErrorClause("GROUP BY");
            } else if (windowList != null && windowList.size() > 0) {
                throw lockWithErrorClause("WINDOW");
            }
        }

        this.windowList = _Collections.safeUnmodifiableList(windowList);
        this.lockBlockList = _Collections.safeUnmodifiableList(lockBlockList);
    }


    @Override
    final void onClear() {
        this.windowList = null;
        this.lockBlockList = null;
    }

    @Override
    final Dialect statementDialect() {
        return PostgreUtils.DIALECT;
    }


    @Override
    final List<Postgres.Modifier> asModifierList(@Nullable List<Postgres.Modifier> modifiers) {
        return CriteriaUtils.asModifierList(this.context, modifiers, _PostgreConsultant::queryModifier);
    }

    @Override
    final boolean isErrorModifier(Postgres.Modifier modifier) {
        return _PostgreConsultant.queryModifier(modifier) < 0;
    }

    @Override
    final Postgres.Modifier allModifier() {
        return Postgres.ALL;
    }

    @Override
    final Postgres.Modifier distinctModifier() {
        return Postgres.DISTINCT;
    }


    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //postgre don't support hint
        throw ContextStack.clearStackAndCastCriteriaApi();
    }


    @Override
    final PostgreCtes createCteBuilder(boolean recursive) {
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
    }

    @Override
    final boolean isIllegalTableModifier(@Nullable SQLs.TableModifier modifier) {
        return CriteriaUtils.isIllegalOnly(modifier);
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final _TableSampleJoinSpec<I> onFromTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table,
                                              String alias) {
        final PostgreSupports.FromClauseTableBlock block;
        block = new PostgreSupports.FromClauseTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier,
                                                      DerivedTable table) {
        return alias -> {
            final TabularBlocks.FromClauseAliasDerivedBlock block;
            block = TabularBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        };
    }

    @Override
    final _FuncColumnDefinitionAsClause<_JoinSpec<I>> onFromUndoneFunc(final _JoinType joinType,
                                                                       final @Nullable SQLs.DerivedModifier modifier,
                                                                       final UndoneFunction func) {
        return alias -> PostgreBlocks.fromUndoneFunc(joinType, modifier, func, alias, this, this.blockConsumer);
    }

    @Override
    final _JoinSpec<I> onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem,
                                 String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _TableSampleOnSpec<I> onJoinTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table,
                                            String alias) {
        final JonClauseTableBlock<I> block;
        block = new JonClauseTableBlock<>(joinType, modifier, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsParensOnClause<_JoinSpec<I>> onJoinDerived(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier,
                                                        DerivedTable table) {
        return alias -> {
            final TabularBlocks.JoinClauseAliasDerivedBlock<_JoinSpec<I>> block;
            block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _FuncColumnDefinitionAsClause<_OnClause<_JoinSpec<I>>> onJoinUndoneFunc(
            final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
        return alias -> PostgreBlocks.joinUndoneFunc(joinType, modifier, func, alias, this, this.blockConsumer);
    }

    @Override
    final _OnClause<_JoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem,
                                            String alias) {
        final TabularBlocks.JoinClauseCteBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    /*-------------------below private method -------------------*/

    /**
     * @see #from(Function)
     * @see #crossJoin(Function)
     */
    private _JoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        this.blockConsumer.accept(TabularBlocks.fromNestedBlock(joinType, nestedItems));
        return this;
    }

    /**
     * @see #leftJoin(Function)
     * @see #join(Function)
     * @see #rightJoin(Function)
     * @see #fullJoin(Function)
     */
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {

        final TabularBlocks.JoinClauseNestedBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }

    /**
     * @return get the table block of last FROM or CROSS JOIN clause
     */
    private PostgreSupports.FromClauseTableBlock getFromClauseBlock() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof PostgreSupports.FromClauseTableBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return (PostgreSupports.FromClauseTableBlock) block;
    }

    /**
     * @return get the derived block of last FROM or CROSS JOIN clause
     */
    private TabularBlocks.FromClauseAliasDerivedBlock getFromDerivedBlock() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }


    /**
     * @see #comma(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final ArmyWindow window) {
        window.endWindowClause();
        List<_Window> list = this.windowList;
        if (list == null) {
            this.windowList = list = _Collections.arrayList();
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        list.add(window);
        return this;
    }

    /**
     * @see #windows(Consumer)
     * @see #ifWindows(Consumer)
     */
    private Window._WindowAsClause<PostgreWindow._PartitionBySpec, Item> createDynamicWindow(String name) {
        return new NamedWindowAsClause<>(this.context, name, this::onAddWindow, PostgreSupports::namedWindow);
    }


    private PostgreQueries<I> onAddLockBlock(final _LockBlock block) {
        List<_LockBlock> lockBlockList = this.lockBlockList;
        if (lockBlockList == null) {
            this.lockBlockList = lockBlockList = _Collections.arrayList();
        } else if (!(lockBlockList instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        lockBlockList.add(block);
        return this;
    }


    private static CriteriaException lockWithErrorClause(final String clause) {
        String m = String.format("Currently, FOR NO KEY UPDATE, FOR UPDATE, FOR SHARE and FOR KEY SHARE cannot be specified with %s.", clause);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static final class JonClauseTableBlock<I extends Item> extends PostgreSupports.PostgreTableOnBlock<
            PostgreQuery._RepeatableOnClause<I>,
            Statement._OnClause<PostgreQuery._JoinSpec<I>>,
            PostgreQuery._JoinSpec<I>>
            implements PostgreQuery._TableSampleOnSpec<I>,
            PostgreQuery._RepeatableOnClause<I> {

        /**
         * @see #onJoinTable(_JoinType, SQLs.TableModifier, TableMeta, String)
         */
        private JonClauseTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> tableItem, String alias,
                                    PostgreQuery._JoinSpec<I> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//JonClauseTableBlock


    private enum PostgreLockStrength implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE),
        FOR_SHARE(_Constant.SPACE_FOR_SHARE),
        FOR_NO_KEY_UPDATE(" FOR NO KEY UPDATE"),
        FOR_KEY_SHARE(" FOR KEY SHARE");

        final String spaceWords;

        PostgreLockStrength(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//PostgreLockMode

    private static final class StaticLockBlock<I extends Item> extends LockClauseBlock<
            PostgreQuery._LockWaitOptionSpec<I>,
            PostgreQuery._LockSpec<I>>
            implements PostgreQuery._LockSpec<I>,
            PostgreQuery._LockOfTableSpec<I> {

        private final PostgreLockStrength lockStrength;

        private final PostgreQueries<I> stmt;


        private StaticLockBlock(PostgreLockStrength lockStrength, PostgreQueries<I> stmt) {
            this.lockStrength = lockStrength;
            this.stmt = stmt;
        }

        @Override
        public _LockOfTableSpec<I> forUpdate() {
            return this.stmt.onAddLockBlock(this)
                    .forUpdate();
        }


        @Override
        public _LockOfTableSpec<I> forShare() {
            return this.stmt.onAddLockBlock(this)
                    .forShare();
        }


        @Override
        public _LockOfTableSpec<I> forNoKeyUpdate() {
            return this.stmt.onAddLockBlock(this)
                    .forNoKeyUpdate();
        }

        @Override
        public _LockOfTableSpec<I> forKeyShare() {
            return this.stmt.onAddLockBlock(this)
                    .forKeyShare();
        }

        @Override
        public _LockSpec<I> ifFor(Consumer<_PostgreDynamicLockStrengthClause> consumer) {
            return this.stmt.onAddLockBlock(this)
                    .ifFor(consumer);
        }

        @Override
        public I asQuery() {
            return this.stmt.onAddLockBlock(this)
                    .asQuery();
        }


        @Override
        public SQLWords lockStrength() {
            return this.lockStrength;
        }

        @Override
        public CriteriaContext getContext() {
            return this.stmt.context;
        }


    }//StaticLockBlock

    private static final class DynamicLockBlock extends LockClauseBlock<
            Query._MinLockWaitOptionClause<Item>,
            Item> implements PostgreQuery._PostgreDynamicLockStrengthClause,
            PostgreQuery._DynamicLockOfTableSpec {

        private final PostgreQueries<?> stmt;

        private PostgreLockStrength lockStrength;

        private DynamicLockBlock(PostgreQueries<?> stmt) {
            this.stmt = stmt;
        }


        @Override
        public _DynamicLockOfTableSpec update() {
            if (this.lockStrength != null) {
                throw CriteriaUtils.duplicateDynamicMethod(this.stmt.context);
            }
            this.lockStrength = PostgreLockStrength.FOR_UPDATE;
            return this;
        }

        @Override
        public _DynamicLockOfTableSpec share() {
            if (this.lockStrength != null) {
                throw CriteriaUtils.duplicateDynamicMethod(this.stmt.context);
            }
            this.lockStrength = PostgreLockStrength.FOR_SHARE;
            return this;
        }

        @Override
        public _DynamicLockOfTableSpec noKeyUpdate() {
            if (this.lockStrength != null) {
                throw CriteriaUtils.duplicateDynamicMethod(this.stmt.context);
            }
            this.lockStrength = PostgreLockStrength.FOR_NO_KEY_UPDATE;
            return this;
        }

        @Override
        public _DynamicLockOfTableSpec keyShare() {
            if (this.lockStrength != null) {
                throw CriteriaUtils.duplicateDynamicMethod(this.stmt.context);
            }
            this.lockStrength = PostgreLockStrength.FOR_KEY_SHARE;
            return this;
        }

        @Override
        public CriteriaContext getContext() {
            return this.stmt.context;
        }

        @Override
        public SQLWords lockStrength() {
            final PostgreLockStrength strength = this.lockStrength;
            if (strength == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return strength;
        }


    }//DynamicLockBlock


    private static final class SimpleSelect<I extends Item> extends PostgreQueries<I>
            implements ArmySelect {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerBracketContext,
                             Function<? super Select, I> function) {
            super(spec, CriteriaContexts.primaryQueryContext(PostgreUtils.DIALECT, spec, outerBracketContext, null));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);

            return function.apply(new SimpleSelect<>(null, bracket.context, bracket::parensEnd));
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SelectDispatcher<>(this.context, unionFunc);
        }

        private PostgreBatchSimpleQuery wrapToBatchSelect(List<?> paramList) {
            return new PostgreBatchSimpleQuery(this, CriteriaUtils.paramList(paramList));
        }


    }//SimpleSelect


    private static final class SimpleSubQuery<I extends Item> extends PostgreQueries<I>
            implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;


        private SimpleSubQuery(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                               Function<? super SubQuery, I> function) {
            super(spec, CriteriaContexts.subQueryContext(PostgreUtils.DIALECT, spec, outerContext, null));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);

            return function.apply(PostgreQueries.subQuery(bracket.context, bracket::parensEnd));
        }


        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new SubQueryDispatcher<>(this.context, unionFunc);
        }


    }//SimpleSubQuery


    static abstract class PostgreSelectClauseDispatcher<I extends Item, WE extends Item>
            extends WithDistinctOnSelectClauseDispatcher<
            PostgreCtes,
            WE,
            Postgres.Modifier,
            PostgreQuery._PostgreSelectCommaSpec<I>,
            PostgreQuery._FromSpec<I>> implements PostgreQuery._PostgreSelectClause<I> {

        PostgreSelectClauseDispatcher(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(PostgreUtils.DIALECT, outerContext, leftContext);
        }


    }//PostgreSelectClauseDispatcher


    static abstract class PostgreBracketQuery<I extends Item>
            extends BracketRowSet<
            I,
            PostgreQuery._UnionOrderBySpec<I>,
            PostgreQuery._UnionOrderByCommaSpec<I>,
            PostgreQuery._UnionLimitSpec<I>,
            PostgreQuery._UnionOffsetSpec<I>,
            PostgreQuery._UnionFetchSpec<I>,
            Query._AsQueryClause<I>,
            PostgreQuery._QueryWithComplexSpec<I>>
            implements PostgreQuery._UnionOrderBySpec<I>,
            PostgreQuery._UnionOrderByCommaSpec<I>,
            PostgreQuery,
            PostgreQuery._UnionOffsetSpec<I>,
            PostgreQuery._UnionFetchSpec<I> {

        private PostgreBracketQuery(ArmyStmtSpec spec) {
            super(spec);
        }


        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }

        @Override
        final void onEndQuery() {
            //no-op
        }


    }//PostgreBracketQueries


    private static final class BracketSelect<I extends Item> extends PostgreBracketQuery<I>
            implements ArmySelect {

        private final Function<? super Select, I> function;

        private BracketSelect(ArmyStmtSpec spec, Function<? super Select, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._QueryWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SelectDispatcher<>(this.context, unionFunc);
        }

        BatchBracketSelect wrapToBatchSelect(List<?> paramList) {
            return new BatchBracketSelect(this, CriteriaUtils.paramList(paramList));
        }


    }//BracketSelect


    private static final class BatchBracketSelect extends BracketRowSet.ArmyBatchBracketSelect
            implements PostgreQuery {

        private BatchBracketSelect(BracketSelect<?> select, List<?> paramList) {
            super(select, paramList);
        }

        @Override
        Dialect statementDialect() {
            return PostgreUtils.DIALECT;
        }


    }//BatchBracketSelect

    private static final class BracketSubQuery<I extends Item> extends PostgreBracketQuery<I>
            implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private BracketSubQuery(ArmyStmtSpec spec, Function<? super SubQuery, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._QueryWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new SubQueryDispatcher<>(this.context, unionFunc);
        }


    }//BracketSubQuery


    private static abstract class PostgreQueryDispatcher<I extends Item>
            extends PostgreSelectClauseDispatcher<
            I,
            PostgreQuery._QueryComplexSpec<I>>
            implements PostgreQuery._QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private PostgreQueryDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext.getOuterContext(), leftContext);
            this.function = function;
        }

        private PostgreQueryDispatcher(PostgreBracketQuery<?> bracket, Function<RowSet, I> function) {
            super(bracket.context, null);
            this.function = function;
        }

        @Override
        public final _StaticCteParensSpec<_QueryComplexSpec<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final _StaticCteParensSpec<_QueryComplexSpec<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PostgreQueryDispatcher


    private static final class SelectDispatcher<I extends Item> extends PostgreQueryDispatcher<I> {

        private SelectDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private SelectDispatcher(BracketSelect<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);

            return function.apply(new SelectDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<ValuesParens> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public PostgreValues._StaticValuesRowClause<I> values() {
            this.endDispatcher();

            return PostgreSimpleValues.fromDispatcher(this, this.function)
                    .values();
        }


        @Override
        PostgreQueries<I> createSelectClause() {
            this.endDispatcher();

            return PostgreQueries.fromDispatcher(this, this.function);
        }


    }//SelectDispatcher


    /**
     * @see #createQueryUnion(_UnionType)
     * @see #createQueryUnion(_UnionType)
     */
    private static final class SubQueryDispatcher<I extends Item> extends PostgreQueryDispatcher<I> {

        private SubQueryDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private SubQueryDispatcher(BracketSubQuery<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);

            return function.apply(new SubQueryDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<ValuesParens> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public PostgreValues._StaticValuesRowClause<I> values() {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            return PostgreQueries.fromSubDispatcher(this, this.function);
        }

    }//SubQueryDispatcher


    private static final class StaticCteComma<I extends Item> implements _CteComma<I> {

        private final CriteriaContext context;

        private final boolean recursive;

        private final Function<Boolean, I> function;

        private StaticCteComma(CriteriaContext context, boolean recursive, Function<Boolean, I> function) {
            context.onBeforeWithClause(recursive);
            this.context = context;
            this.recursive = recursive;
            this.function = function;
        }

        @Override
        public _StaticCteParensSpec<I> comma(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.context.onStartCte(name);
            return new StaticCteAsClause<>(name, this);
        }

        @Override
        public I space() {
            return this.function.apply(this.recursive);
        }

    }//SimpleCteComma


    private static final class StaticCteAsClause<I extends Item>
            implements _StaticCteParensSpec<I> {

        private final StaticCteComma<I> comma;

        private final String name;

        private List<String> columnAliasList;

        private Postgres.WordMaterialized modifier;


        private StaticCteAsClause(String name, StaticCteComma<I> comma) {
            this.name = name;
            this.comma = comma;
        }

        @Override
        public _StaticCteAsClause<I> parens(String first, String... rest) {
            return this.onColumnAliasList(ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public _StaticCteAsClause<I> parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAliasList(CriteriaUtils.stringList(this.comma.context, true, consumer));
        }

        @Override
        public _StaticCteAsClause<I> ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAliasList(CriteriaUtils.stringList(this.comma.context, false, consumer));
        }

        @Override
        public <R extends _CteComma<I>> R as(Function<_StaticCteComplexCommandSpec<I>, R> function) {
            return this.as(null, function);
        }

        @Override
        public <R extends _CteComma<I>> R as(@Nullable Postgres.WordMaterialized modifier,
                                             Function<_StaticCteComplexCommandSpec<I>, R> function) {
            if (modifier != null && modifier != Postgres.MATERIALIZED && modifier != Postgres.NOT_MATERIALIZED) {
                throw CriteriaUtils.errorModifier(this.comma.context, modifier);
            }
            this.modifier = modifier;
            return function.apply(new StaticCteComplexCommand<>(this.comma.context, this::subStmtEnd, this::queryEnd));
        }


        private _StaticCteAsClause<I> onColumnAliasList(final List<String> list) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.columnAliasList = list;
            if (list.size() > 0) {
                this.comma.context.onCteColumnAlias(this.name, list);
            }
            return this;
        }


        private _CteComma<I> subStmtEnd(final SubStatement statement) {
            final _Cte cte;
            cte = new PostgreSupports.PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.comma.context.onAddCte(cte);
            return this.comma;
        }


        private _StaticCteSearchSpec<I> queryEnd(final SubQuery query) {
            final _StaticCteSearchSpec<I> spec;
            if (this.comma.recursive && PostgreUtils.isUnionQuery(query)) {
                spec = new StaticCteSearchSpec<>(this, query);
            } else {
                this.subStmtEnd(query);
                spec = PostgreSupports.noOperationStaticCteSearchSpec(this.comma::comma, this.comma::space);
            }
            return spec;
        }

        private _CteComma<I> searchClauseEnd(final StaticCteSearchSpec<I> clause) {
            final _PostgreCte._SearchClause searchClause;
            searchClause = clause.hasSearchClause() ? clause : null;

            final _Cte cte;
            cte = new PostgreSupports.PostgreCte(this.name, this.columnAliasList, this.modifier, clause.query,
                    searchClause, clause.cycleSpec);
            this.comma.context.onAddCte(cte);
            return this.comma;
        }


    }//CteMultiCommandFunction


    private static final class StaticCycleSpec<I extends Item>
            extends PostgreSupports.PostgreCteCycleClause<_CteComma<I>>
            implements PostgreQuery._StaticCteCycleSpec<I> {

        private final StaticCteSearchSpec<I> searchSpec;

        private StaticCycleSpec(StaticCteSearchSpec<I> searchSpec) {
            super(searchSpec.context);
            this.searchSpec = searchSpec;
        }

        @Override
        public _StaticCteParensSpec<I> comma(String name) {
            return this.searchSpec.comma(name);
        }

        @Override
        public I space() {
            return this.searchSpec.space();
        }

    }//StaticCycleSpec


    private static final class StaticCteSearchSpec<I extends Item> extends PostgreSupports.PostgreCteSearchSpec<
            _StaticCteCycleSpec<I>>
            implements PostgreQuery._StaticCteSearchSpec<I> {

        private final StaticCteAsClause<I> clause;

        private final SubQuery query;

        private StaticCycleSpec<I> cycleSpec;

        private StaticCteSearchSpec(StaticCteAsClause<I> clause, SubQuery query) {
            super(clause.comma.context);
            this.clause = clause;
            this.query = query;
        }

        @Override
        public _SetCycleMarkColumnClause<_CteComma<I>> cycle(String firstColumnName, String... rest) {
            if (this.cycleSpec != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final StaticCycleSpec<I> cycleSpec;
            cycleSpec = new StaticCycleSpec<>(this);
            this.cycleSpec = cycleSpec;
            return cycleSpec.cycle(firstColumnName, rest);
        }

        @Override
        public _SetCycleMarkColumnClause<_CteComma<I>> cycle(Consumer<Consumer<String>> consumer) {
            if (this.cycleSpec != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final StaticCycleSpec<I> cycleSpec;
            cycleSpec = new StaticCycleSpec<>(this);
            this.cycleSpec = cycleSpec;
            return cycleSpec.cycle(consumer);
        }

        @Override
        public _SetCycleMarkColumnClause<_CteComma<I>> ifCycle(Consumer<Consumer<String>> consumer) {
            if (this.cycleSpec != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final StaticCycleSpec<I> cycleSpec;
            cycleSpec = new StaticCycleSpec<>(this);
            cycleSpec.ifCycle(consumer);

            this.cycleSpec = cycleSpec.hasCycleClause() ? cycleSpec : null;
            return cycleSpec;
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<I> comma(String name) {
            return this.clause.searchClauseEnd(this).comma(name);
        }

        @Override
        public I space() {
            return this.clause.searchClauseEnd(this).space();
        }


    }//StaticCteSearchSpec


    private static class StaticCteSubQuery<I extends Item>
            extends PostgreSelectClauseDispatcher<I, Item>
            implements PostgreQuery._StaticCteSelectSpec<I>,
            ArmyStmtSpec {

        private final Function<SubQuery, I> queryFunction;

        private StaticCteSubQuery(CriteriaContext outerContext, Function<SubQuery, I> queryFunction) {
            super(outerContext, null);
            this.queryFunction = queryFunction;
        }


        @Override
        public final _UnionOrderBySpec<I> parens(Function<_StaticCteSelectSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.queryFunction);
            return function.apply(new StaticCteSubQuery<>(bracket.context, bracket::parensEnd));
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive) {
            // static WITH clause don't support this
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        @Override
        final PostgreQueries<I> createSelectClause() {
            this.endDispatcher();

            return PostgreQueries.fromSubDispatcher(this, this.queryFunction);
        }


    }//StaticCteSubQuery


    private static final class StaticCteComplexCommand<I extends Item>
            extends StaticCteSubQuery<_StaticCteSearchSpec<I>>
            implements PostgreQuery._StaticCteComplexCommandSpec<I> {


        private final Function<SubStatement, _CteComma<I>> function;


        private StaticCteComplexCommand(CriteriaContext outerContext, Function<SubStatement, _CteComma<I>> function,
                                        Function<SubQuery, _StaticCteSearchSpec<I>> queryFunction) {
            super(outerContext, queryFunction);
            this.function = function;
        }


        @Override
        public PostgreInsert._CteInsertIntoClause<_CteComma<I>> literalMode(LiteralMode mode) {
            this.endDispatcher();

            return PostgreInserts.staticSubInsert(this, this.function)
                    .literalMode(mode);
        }

        @Override
        public PostgreInsert._StaticSubNullOptionSpec<_CteComma<I>> migration() {
            this.endDispatcher();

            return PostgreInserts.staticSubInsert(this, this.function)
                    .migration();
        }

        @Override
        public PostgreInsert._StaticSubPreferLiteralSpec<_CteComma<I>> nullMode(NullMode mode) {
            this.endDispatcher();

            return PostgreInserts.staticSubInsert(this, this.function)
                    .nullMode(mode);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, _CteComma<I>, _CteComma<I>> insertInto(TableMeta<T> table) {
            this.endDispatcher();

            return PostgreInserts.staticSubInsert(this, this.function)
                    .insertInto(table);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_CteComma<I>, _CteComma<I>, T> update(
                TableMeta<T> table, SQLs.WordAs as, String tableAlias) {
            this.endDispatcher();

            return PostgreUpdates.subSimpleUpdate(this.context.getNonNullOuterContext(), this.function)
                    .update(table, as, tableAlias);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_CteComma<I>, _CteComma<I>, T> update(
                @Nullable SQLs.WordOnly only, TableMeta<T> table, SQLs.WordAs as, String tableAlias) {
            this.endDispatcher();

            return PostgreUpdates.subSimpleUpdate(this.context.getNonNullOuterContext(), this.function)
                    .update(only, table, as, tableAlias);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_CteComma<I>, _CteComma<I>, T> update(
                TableMeta<T> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias) {
            return PostgreUpdates.subSimpleUpdate(this.context.getNonNullOuterContext(), this.function)
                    .update(table, star, as, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_CteComma<I>, _CteComma<I>> deleteFrom(
                TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            this.endDispatcher();

            return PostgreDeletes.subSimpleDelete(this.context.getNonNullOuterContext(), this.function)
                    .deleteFrom(table, as, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_CteComma<I>, _CteComma<I>> deleteFrom(
                @Nullable SQLs.WordOnly only, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            this.endDispatcher();

            return PostgreDeletes.subSimpleDelete(this.context.getNonNullOuterContext(), this.function)
                    .deleteFrom(only, table, as, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_CteComma<I>, _CteComma<I>> deleteFrom(
                TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias) {
            this.endDispatcher();

            return PostgreDeletes.subSimpleDelete(this.context.getNonNullOuterContext(), this.function)
                    .deleteFrom(table, star, as, tableAlias);
        }


        @Override
        public PostgreValues._OrderBySpec<_CteComma<I>> values(Consumer<ValuesParens> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public PostgreValues._StaticValuesRowClause<_CteComma<I>> values() {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

    }//StaticCteComplexCommand


    private static final class PostgreBatchSimpleQuery extends ArmyBatchSimpleSelect
            implements PostgreQuery, _PostgreQuery {

        private final List<_Expression> distinctOnExpList;
        private final SQLs.Modifier groupByModifier;
        private final List<_Window> windowList;

        private final SQLWords offsetRow;

        private final SQLWords fetchFirstNext;


        private final SQLWords fetchRowPercent;
        private final SQLWords fetchRow;

        private final SQLWords fetchOnlyWithTies;

        private final List<_LockBlock> lockBlockList;


        private PostgreBatchSimpleQuery(SimpleSelect<?> select, List<?> paramList) {
            super(select, paramList);

            this.distinctOnExpList = select.distinctOnExpressions();
            this.groupByModifier = select.groupByModifier();
            this.windowList = select.windowList();
            this.offsetRow = select.offsetRowModifier();

            this.fetchFirstNext = select.fetchFirstOrNext();
            this.fetchRowPercent = select.fetchPercentModifier();
            this.fetchRow = select.fetchRowModifier();
            this.fetchOnlyWithTies = select.fetchOnlyOrWithTies();

            this.lockBlockList = select.lockBlockList();
        }

        @Override
        public List<_Expression> distinctOnExpressions() {
            return this.distinctOnExpList;
        }

        @Override
        public List<_Window> windowList() {
            return this.windowList;
        }

        @Override
        public SQLWords offsetRowModifier() {
            return this.offsetRow;
        }

        @Override
        public SQLWords fetchFirstOrNext() {
            return this.fetchFirstNext;
        }

        @Override
        public SQLWords fetchPercentModifier() {
            return this.fetchRowPercent;
        }

        @Override
        public SQLWords fetchRowModifier() {
            return this.fetchRow;
        }

        @Override
        public SQLWords fetchOnlyOrWithTies() {
            return this.fetchOnlyWithTies;
        }

        @Override
        public SQLs.Modifier groupByModifier() {
            return this.groupByModifier;
        }

        @Override
        public List<_LockBlock> lockBlockList() {
            return this.lockBlockList;
        }

        @Override
        Dialect statementDialect() {
            return PostgreUtils.DIALECT;
        }


    }//PostgreBatchSimpleQuery


}
