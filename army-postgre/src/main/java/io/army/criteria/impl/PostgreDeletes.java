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
import io.army.criteria.dialect.BatchReturningDelete;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreDelete;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.mapping.MappingType;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is the implementation of Postgre DELETE syntax.
 *
 * @see PostgreDelete
 * @since 0.6.0
 */
abstract class PostgreDeletes<I extends Item, Q extends Item> extends JoinableDelete<
        I,
        PostgreCtes,
        PostgreDelete._PostgreDeleteClause<I, Q>,
        PostgreDelete._TableSampleJoinSpec<I, Q>,
        Statement._AsClause<PostgreDelete._ParensJoinSpec<I, Q>>,
        PostgreDelete._SingleJoinSpec<I, Q>,
        PostgreStatement._FuncColumnDefinitionAsClause<PostgreDelete._SingleJoinSpec<I, Q>>,
        PostgreDelete._TableSampleOnSpec<I, Q>,
        PostgreDelete._AsParensOnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
        Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
        PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>>,
        PostgreDelete._ReturningSpec<I, Q>,
        PostgreDelete._SingleWhereAndSpec<I, Q>>
        implements PostgreDelete,
        _PostgreDelete,
        PostgreDelete._RepeatableJoinClause<I, Q>,
        PostgreDelete._SingleWithSpec<I, Q>,
        PostgreDelete._SingleUsingSpec<I, Q>,
        PostgreDelete._TableSampleJoinSpec<I, Q>,
        PostgreDelete._ParensJoinSpec<I, Q>,
        PostgreDelete._SingleWhereAndSpec<I, Q>,
        PostgreDelete._StaticReturningCommaSpec<Q> {


    static _SingleWithSpec<Delete, ReturningDelete> simpleDelete() {
        return new PrimarySimpleDelete();
    }


    static _SingleWithSpec<_BatchDeleteParamSpec, _BatchReturningDeleteParamSpec> batchDelete() {
        return new BatchPrimarySimpleDelete();
    }


    static <I extends Item> _SingleWithSpec<I, I> subSimpleDelete(CriteriaContext outerContext,
                                                                  Function<SubStatement, I> function) {
        return new SubSimpleDelete<>(outerContext, function);
    }


    private SQLs.WordOnly onlyModifier;

    private TableMeta<?> targetTable;

    private SQLs.SymbolAsterisk starModifier;

    private String targetTableAlias;

    private _TabularBlock fromCrossBlock;

    private List<_SelectItem> returningList;

    private PostgreDeletes(@Nullable _Statement._WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }

    @Override
    public final PostgreQuery._StaticCteParensSpec<_PostgreDeleteClause<I, Q>> with(String name) {
        return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final PostgreQuery._StaticCteParensSpec<_PostgreDeleteClause<I, Q>> withRecursive(String name) {
        return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final PostgreDelete._SingleUsingSpec<I, Q> deleteFrom(TableMeta<?> table, SQLs.WordAs as,
                                                                 String tableAlias) {
        return this.doDeleteFrom(null, table, null, as, tableAlias);
    }

    @Override
    public final PostgreDelete._SingleUsingSpec<I, Q> deleteFrom(@Nullable SQLs.WordOnly only, TableMeta<?> table,
                                                                 SQLs.WordAs as, String tableAlias) {
        return this.doDeleteFrom(only, table, null, as, tableAlias);
    }

    @Override
    public final PostgreDelete._SingleUsingSpec<I, Q> deleteFrom(TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star,
                                                                 SQLs.WordAs as, String tableAlias) {
        return this.doDeleteFrom(null, table, star, as, tableAlias);
    }


    @Override
    public final _RepeatableJoinClause<I, Q> tableSample(Expression method) {
        this.getFromCrossBlock().onSampleMethod((ArmyExpression) method);
        return this;
    }

    @Override
    public final <E> _RepeatableJoinClause<I, Q> tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                                                             BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.tableSample(method.apply(valueOperator, supplier.get()));
    }

    @Override
    public final _RepeatableJoinClause<I, Q> tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                                         BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                                         String keyName) {
        return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
    }

    @Override
    public final _RepeatableJoinClause<I, Q> tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                                                         BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final _RepeatableJoinClause<I, Q> ifTableSample(Supplier<Expression> supplier) {
        final Expression value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(value);
        }
        return this;
    }

    @Override
    public final <E> _RepeatableJoinClause<I, Q> ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                                                               BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I, Q> ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                                           BiFunction<MappingType, Object, Expression> valueOperator,
                                                           Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Expression seed) {
        this.getFromCrossBlock().onSeed((ArmyExpression) seed);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Supplier<Expression> supplier) {
        return this.repeatable(supplier.get());
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
        return this.repeatable(valueOperator.apply(seedValue));
    }

    @Override
    public final <E extends Number> _SingleJoinSpec<I, Q> repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.repeatable(valueOperator.apply(supplier.get()));
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                                  String keyName) {
        return this.repeatable(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifRepeatable(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.repeatable(expression);
        }
        return this;
    }

    @Override
    public final <E extends Number> _SingleJoinSpec<I, Q> ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                                    String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> parens(String first, String... rest) {
        this.getFromDerived().parens(first, rest);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> parens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().parens(this.context, consumer);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().ifParens(this.context, consumer);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> using(Function<_NestedLeftParenSpec<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedUsingEnd));
    }

    @Override
    public final _SingleJoinSpec<I, Q> crossJoin(Function<_NestedLeftParenSpec<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedUsingEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> join(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }


    @Override
    public final _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
        this.where(new PostgreCursorPredicate(cursorName));
        return this;
    }

    @Override
    public final _DqlDeleteSpec<Q> returningAll() {
        this.returningList = PostgreSupports.EMPTY_SELECT_ITEM_LIST;
        return this;
    }

    @Override
    public final _DqlDeleteSpec<Q> returning(Consumer<Returnings> consumer) {
        this.returningList = CriteriaUtils.selectionList(this.context, consumer);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Selection selection) {
        this.onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Selection selection1, Selection selection2) {
        this.onAddSelection(selection1)
                .onAddSelection(selection2);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias) {
        this.onAddSelection(function.apply(alias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function1, String alias1,
                                                        Function<String, Selection> function2, String alias2) {
        this.onAddSelection(function1.apply(alias1))
                .onAddSelection(function2.apply(alias2));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                        Selection selection) {
        this.onAddSelection(function.apply(alias))
                .onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Selection selection, Function<String, Selection> function,
                                                        String alias) {
        this.onAddSelection(selection)
                .onAddSelection(function.apply(alias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(String derivedAlias, SQLs.SymbolPeriod period,
                                                        SQLs.SymbolAsterisk star) {
        this.onAddSelection(SelectionGroups.derivedGroup(this.context.getNonNullDerived(derivedAlias), derivedAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(
            String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.onAddSelection(SelectionGroups.singleGroup(table, tableAlias));
        return this;
    }

    @Override
    public final <P> _StaticReturningCommaSpec<Q> returning(
            String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
            String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
        }
        this.onAddSelection(SelectionGroups.singleGroup(parent, parenAlias))
                .onAddSelection(SelectionGroups.groupWithoutId(child, childAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3,
                                                        TableField field4) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3)
                .onAddSelection(field4);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Selection selection) {
        this.onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Selection selection1, Selection selection2) {
        this.onAddSelection(selection1)
                .onAddSelection(selection2);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias) {
        this.onAddSelection(function.apply(alias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function1, String alias1,
                                                    Function<String, Selection> function2, String alias2) {
        this.onAddSelection(function1.apply(alias1))
                .onAddSelection(function2.apply(alias2));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias,
                                                    Selection selection) {
        this.onAddSelection(function.apply(alias))
                .onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Selection selection, Function<String, Selection> function,
                                                    String alias) {
        this.onAddSelection(selection)
                .onAddSelection(function.apply(alias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(String derivedAlias, SQLs.SymbolPeriod period,
                                                    SQLs.SymbolAsterisk star) {
        this.onAddSelection(SelectionGroups.derivedGroup(this.context.getNonNullDerived(derivedAlias), derivedAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(
            String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.onAddSelection(SelectionGroups.singleGroup(table, tableAlias));
        return this;
    }

    @Override
    public final <P> _StaticReturningCommaSpec<Q> comma(
            String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
            String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
        }
        this.onAddSelection(SelectionGroups.singleGroup(parent, parenAlias))
                .onAddSelection(SelectionGroups.groupWithoutId(child, childAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3,
                                                    TableField field4) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3)
                .onAddSelection(field4);
        return this;
    }

    @Override
    public final List<? extends _SelectItem> returningList() {
        //no bug,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public final Q asReturningDelete() {
        final List<_SelectItem> returningList = this.returningList;
        if (!(returningList instanceof ArrayList || returningList == PostgreSupports.EMPTY_SELECT_ITEM_LIST)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.endDeleteStatement();
        if (returningList instanceof ArrayList) {
            this.returningList = _Collections.unmodifiableList(returningList);
        } else {
            this.returningList = CriteriaUtils.returningAll(this.table(), this.tableAlias(), this.tableBlockList());
        }
        return this.onAsReturningDelete();
    }

    @Override
    public final SQLs.WordOnly modifier() {
        return this.onlyModifier;
    }

    @Override
    public final TableMeta<?> table() {
        final TableMeta<?> targetTable = this.targetTable;
        if (targetTable == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return targetTable;
    }

    @Override
    public final SQLs.SymbolAsterisk symbolAsterisk() {
        return this.starModifier;
    }

    @Override
    public final String tableAlias() {
        final String targetTableAlias = this.targetTableAlias;
        if (targetTableAlias == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return targetTableAlias;
    }


    @Override
    final PostgreCtes createCteBuilder(boolean recursive) {
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
    }


    @Override
    final Dialect statementDialect() {
        return PostgreDialect.POSTGRE15;
    }


    @Override
    final _TableSampleJoinSpec<I, Q> onFromTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String alias) {
        final PostgreSupports.FromClauseTableBlock block;
        block = new PostgreSupports.FromClauseTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _AsClause<_ParensJoinSpec<I, Q>> onFromDerived(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final TabularBlocks.FromClauseAliasDerivedBlock block;
            block = TabularBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        };
    }

    @Override
    final _SingleJoinSpec<I, Q> onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _FuncColumnDefinitionAsClause<_SingleJoinSpec<I, Q>> onFromUndoneFunc(
            final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
        return alias -> PostgreBlocks.fromUndoneFunc(joinType, modifier, func, alias, this, this.blockConsumer);
    }


    @Override
    final _TableSampleOnSpec<I, Q> onJoinTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier,
                                               TableMeta<?> table, String alias) {
        final SimpleJoinClauseTableBlock<I, Q> block;
        block = new SimpleJoinClauseTableBlock<>(joinType, modifier, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsParensOnClause<_SingleJoinSpec<I, Q>> onJoinDerived(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final TabularBlocks.JoinClauseAliasDerivedBlock<_SingleJoinSpec<I, Q>> block;
            block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _FuncColumnDefinitionAsClause<_OnClause<_SingleJoinSpec<I, Q>>> onJoinUndoneFunc(
            final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
        return alias -> PostgreBlocks.joinUndoneFunc(joinType, modifier, func, alias, this, this.blockConsumer);
    }

    @Override
    final _OnClause<_SingleJoinSpec<I, Q>> onJoinCte(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        final TabularBlocks.JoinClauseCteBlock<_SingleJoinSpec<I, Q>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    final _SingleJoinSpec<I, Q> nestedUsingEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        this.blockConsumer.accept(TabularBlocks.fromNestedBlock(joinType, nestedItems));
        return this;
    }

    final _OnClause<_SingleJoinSpec<I, Q>> nestedJoinEnd(final _JoinType joinType, final _NestedItems nestedItems) {

        final TabularBlocks.JoinClauseNestedBlock<_SingleJoinSpec<I, Q>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
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
    final void onEndStatement() {
        this.fromCrossBlock = null;

    }

    @Override
    final void onClear() {
        this.returningList = null;
        if (this instanceof BatchPrimarySimpleDelete) {
            ((BatchPrimarySimpleDelete) this).paramList = null;
        }
    }


    private PostgreSupports.FromClauseTableBlock getFromCrossBlock() {
        final _TabularBlock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof PostgreSupports.FromClauseTableBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return (PostgreSupports.FromClauseTableBlock) block;
    }


    final TabularBlocks.FromClauseAliasDerivedBlock getFromDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }


    abstract Q onAsReturningDelete();

    abstract I asPostgreDelete();


    @Override
    final I onAsDelete() {
        if (this.returningList != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.returningList = Collections.emptyList();
        return this.asPostgreDelete();
    }

    private PostgreDeletes<I, Q> onAddSelection(final @Nullable SelectItem selectItem) {
        if (selectItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_SelectItem> list = this.returningList;
        if (list == null) {
            this.returningList = list = _Collections.arrayList();
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        } else if (selectItem instanceof _SelectionGroup._TableFieldGroup) {
            final String tableAlias;
            tableAlias = ((_SelectionGroup._TableFieldGroup) selectItem).tableAlias();
            final TableMeta<?> groupTable;
            if (this.tableAlias().equals(tableAlias)) {
                groupTable = this.table();
            } else {
                groupTable = this.context.getTable(tableAlias);
            }
            if (!((_SelectionGroup._TableFieldGroup) selectItem).isLegalGroup(groupTable)) {
                throw CriteriaUtils.unknownTableFieldGroup(this.context, (_SelectionGroup._TableFieldGroup) selectItem);
            }

        }
        list.add((_SelectItem) selectItem);
        return this;
    }

    private PostgreDelete._SingleUsingSpec<I, Q> doDeleteFrom(final @Nullable SQLs.WordOnly only, final @Nullable TableMeta<?> table,
                                                              final @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as,
                                                              final @Nullable String tableAlias) {
        if (this.targetTable != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        } else if (only != null && only != SQLs.ONLY) {
            throw CriteriaUtils.errorModifier(this.context, only);
        } else if (star != null && star != SQLs.ASTERISK) {
            throw CriteriaUtils.errorModifier(this.context, star);
        } else if (as != SQLs.AS) {
            throw CriteriaUtils.errorModifier(this.context, as);
        } else if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (tableAlias == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.onlyModifier = only;
        this.targetTable = table;
        this.starModifier = star;
        this.targetTableAlias = tableAlias;
        this.context.singleDmlTable(table, tableAlias);
        return this;
    }


    private static final class SimpleJoinClauseTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            PostgreDelete._RepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            PostgreDelete._SingleJoinSpec<I, Q>>
            implements PostgreDelete._TableSampleOnSpec<I, Q>,
            PostgreDelete._RepeatableOnClause<I, Q> {

        private SimpleJoinClauseTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> tableItem,
                                           String alias, PostgreDelete._SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//SimpleJoinClauseTableBlock

    private static final class PrimarySimpleDelete extends PostgreDeletes<Delete, ReturningDelete>
            implements Delete {

        private PrimarySimpleDelete() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(PostgreUtils.DIALECT, null));
        }

        @Override
        Delete asPostgreDelete() {
            PostgreUtils.validateDmlInWithClause(cteList(), this);
            return this;
        }

        @Override
        ReturningDelete onAsReturningDelete() {
            PostgreUtils.validateDmlInWithClause(cteList(), this);
            //ReturningDelete must be wrapped
            return new PrimaryReturningDeleteWrapper(this);
        }

    }//PrimarySimpleDelete

    private static final class BatchPrimarySimpleDelete extends PostgreDeletes<
            _BatchDeleteParamSpec,
            _BatchReturningDeleteParamSpec>
            implements BatchDelete,
            _BatchStatement,
            _BatchDeleteParamSpec {

        private List<?> paramList;

        private BatchPrimarySimpleDelete() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(PostgreUtils.DIALECT, null));
        }

        @Override
        public BatchDelete namedParamList(final List<?> paramList) {
            if (this.paramList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return list;
        }

        @Override
        _BatchDeleteParamSpec asPostgreDelete() {
            PostgreUtils.validateDmlInWithClause(cteList(), this);
            return this;
        }

        @Override
        _BatchReturningDeleteParamSpec onAsReturningDelete() {
            PostgreUtils.validateDmlInWithClause(cteList(), this);
            return this::createBatchReturningDelete;
        }

        private <P> BatchReturningDelete createBatchReturningDelete(List<P> paramList) {
            return new BatchReturningDeleteWrapper(this, paramList);
        }


    }//BatchPrimarySimpleDelete


    static final class SubSimpleDelete<I extends Item> extends PostgreDeletes<I, I>
            implements SubStatement {

        private final Function<SubStatement, I> function;

        /**
         * @see #subSimpleDelete(CriteriaContext, Function)
         */
        private SubSimpleDelete(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, CriteriaContexts.subJoinableSingleDmlContext(PostgreUtils.DIALECT, outerContext));
            this.function = function;
        }

        @Override
        I asPostgreDelete() {
            return this.function.apply(this);
        }

        @Override
        I onAsReturningDelete() {
            return this.function.apply(new PostgreSubReturningDelete(this));
        }



    }//SubSimpleDelete


    static abstract class PostgreReturningDeleteWrapper extends CriteriaSupports.StatementMockSupport
            implements PostgreDelete, _PostgreDelete, _ReturningDml {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final SQLs.WordOnly only;

        private final TableMeta<?> targetTable;

        private final SQLs.SymbolAsterisk starModifier;

        private final String tableAlias;

        private final List<_TabularBlock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<? extends _SelectItem> returningList;

        private Boolean prepared = Boolean.TRUE;

        private PostgreReturningDeleteWrapper(PostgreDeletes<?, ?> stmt) {
            super(stmt.context);
            this.recursive = stmt.isRecursive();
            this.cteList = stmt.cteList();
            this.only = stmt.onlyModifier;
            this.targetTable = stmt.targetTable;

            this.starModifier = stmt.starModifier;
            this.tableAlias = stmt.targetTableAlias;
            this.tableBlockList = stmt.tableBlockList();
            this.wherePredicateList = stmt.wherePredicateList();

            this.returningList = _Collections.safeUnmodifiableList(stmt.returningList);
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public final void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = null;
        }

        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }

        @Override
        public final SQLs.WordOnly modifier() {
            return this.only;
        }

        @Override
        public final TableMeta<?> table() {
            return this.targetTable;
        }


        @Override
        public final SQLs.SymbolAsterisk symbolAsterisk() {
            return this.starModifier;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }


        @Override
        public final List<_Predicate> wherePredicateList() {
            return this.wherePredicateList;
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final List<_TabularBlock> tableBlockList() {
            return this.tableBlockList;
        }

        @Override
        public final List<? extends _SelectItem> returningList() {
            return this.returningList;
        }


    }//PostgreDeleteWrapper


    private static final class PrimaryReturningDeleteWrapper extends PostgreReturningDeleteWrapper
            implements ReturningDelete {

        private PrimaryReturningDeleteWrapper(PrimarySimpleDelete stmt) {
            super(stmt);
        }


    } // PrimaryReturningDeleteWrapper


    private static final class BatchReturningDeleteWrapper extends PostgreReturningDeleteWrapper
            implements BatchReturningDelete, _BatchStatement {

        private final List<?> paramList;

        private BatchReturningDeleteWrapper(BatchPrimarySimpleDelete stmt, List<?> paramList) {
            super(stmt);
            this.paramList = paramList;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    } // BatchReturningDeleteWrapper

    static final class PostgreSubReturningDelete extends PostgreReturningDeleteWrapper
            implements SubStatement {

        private PostgreSubReturningDelete(SubSimpleDelete<?> stmt) {
            super(stmt);
        }

    } // PostgreSubReturningDelete


}
