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

package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.mysql.inner._IndexHint;
import io.army.criteria.mysql.inner._MySQLMultiUpdate;
import io.army.criteria.standard.SQLs;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>This class is base class of below:
 * <ul>
 *     <li>{@link MySQLSimpleUpdate} ,MySQL multi update api implementation</li>
 * </ul>
 */
abstract class MySQLMultiUpdates<I extends Item>
        extends JoinableUpdate<
        I,
        MySQLCtes,
        MySQLUpdate._MultiUpdateClause<I>,
        TableField,
        MySQLUpdate._MultiWhereSpec<I>,
        MySQLUpdate._MultiIndexHintJoinSpec<I>,
        Statement._AsClause<MySQLUpdate._ParensJoinSpec<I>>,
        MySQLUpdate._MultiJoinSpec<I>,
        Void,
        MySQLUpdate._MultiIndexHintOnSpec<I>,
        Statement._AsParensOnClause<MySQLUpdate._MultiJoinSpec<I>>,
        Statement._OnClause<MySQLUpdate._MultiJoinSpec<I>>,
        Void,
        Statement._DmlUpdateSpec<I>,
        MySQLUpdate._MultiWhereAndSpec<I>>
        implements _MySQLMultiUpdate,
        MySQLUpdate,
        MySQLUpdate._MultiWithSpec<I>,
        MySQLUpdate._MultiUpdateSpaceClause<I>,
        MySQLUpdate._MultiIndexHintJoinSpec<I>,
        MySQLUpdate._ParensJoinSpec<I>,
        MySQLUpdate._MultiWhereSpec<I>,
        MySQLUpdate._MultiWhereAndSpec<I> {


    static _MultiWithSpec<Update> simple() {
        return new MySQLSimpleUpdate();
    }

    static _MultiWithSpec<_BatchUpdateParamSpec> batch() {
        return new MySQLBatchUpdate();
    }


    private List<Hint> hintList;

    private List<MySQLs.Modifier> modifierList;

    _TabularBlock fromCrossBlock;


    private MySQLMultiUpdates(@Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primaryMultiDmlContext(MySQLUtils.DIALECT, spec));
        ContextStack.push(this.context);
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_MultiUpdateClause<I>> with(String name) {
        return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_MultiUpdateClause<I>> withRecursive(String name) {
        return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _MultiUpdateSpaceClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this;
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final _AsClause<_ParensJoinSpec<I>> update(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onFromDerived(_JoinType.NONE, null, derivedTable);
    }

    @Override
    public final _AsClause<_ParensJoinSpec<I>> update(@Nullable SQLs.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromDerived(_JoinType.NONE, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(Supplier<T> supplier) {
        return this.update(supplier.get());
    }

    @Override
    public final <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(@Nullable SQLs.DerivedModifier modifier, Supplier<T> supplier) {
        return this.update(modifier, supplier.get());
    }

    @Override
    public final _MultiJoinSpec<I> update(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), cteName);
    }

    @Override
    public final _MultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final _MultiJoinSpec<I> update(Function<_NestedLeftParenSpec<_MultiJoinSpec<I>>, _MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final _MultiPartitionJoinClause<I> update(TableMeta<?> table) {
        return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> space(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final _AsClause<_ParensJoinSpec<I>> space(DerivedTable derivedTable) {
        return this.update(derivedTable);
    }

    @Override
    public final _AsClause<_ParensJoinSpec<I>> space(@Nullable SQLs.DerivedModifier modifier, DerivedTable derivedTable) {
        return this.update(modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> space(Supplier<T> supplier) {
        return this.update(supplier.get());
    }

    @Override
    public final <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> space(@Nullable SQLs.DerivedModifier modifier, Supplier<T> supplier) {
        return this.update(modifier, supplier.get());
    }

    @Override
    public final _MultiJoinSpec<I> space(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), cteName);
    }

    @Override
    public final _MultiJoinSpec<I> space(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final _MultiJoinSpec<I> space(Function<_NestedLeftParenSpec<_MultiJoinSpec<I>>, _MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final _MultiPartitionJoinClause<I> space(TableMeta<?> table) {
        return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _OnClause<_MultiJoinSpec<I>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>, _OnClause<_MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_MultiJoinSpec<I>> join(Function<_NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>, _OnClause<_MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_MultiJoinSpec<I>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>, _OnClause<_MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_MultiJoinSpec<I>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>, _OnClause<_MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_MultiJoinSpec<I>> straightJoin(Function<_NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>, _OnClause<_MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _MultiJoinSpec<I> crossJoin(Function<_NestedLeftParenSpec<_MultiJoinSpec<I>>, _MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _MultiJoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _MultiPartitionOnClause<I> leftJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> join(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> rightJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> fullJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> straightJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final _MultiPartitionJoinClause<I> crossJoin(TableMeta<?> table) {
        return new SimplePartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
    }


    @Override
    public final _MultiJoinSpec<I> parens(String first, String... rest) {
        this.getFromClauseDerived().parens(first, rest);
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().parens(this.context, consumer);
        return this;
    }

    @Override
    public final _MultiJoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().ifParens(this.context, consumer);
        return this;
    }


    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(String indexName) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(String indexName1, String indexName2) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName1, indexName2));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(String indexName1, String indexName2, String indexName3) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName1, indexName2, indexName3));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, space, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ifUseIndex(Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.USE_INDEX, consumer);
        if (indexHint != null) {
            addIndexHint(indexHint);
        }
        return this;
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(String indexName) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(String indexName1, String indexName2) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName1, indexName2));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(String indexName1, String indexName2, String indexName3) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName1, indexName2, indexName3));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, space, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ifIgnoreIndex(Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, consumer);
        if (indexHint != null) {
            addIndexHint(indexHint);
        }
        return this;
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(String indexName) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(String indexName1, String indexName2) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName1, indexName2));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(String indexName1, String indexName2, String indexName3) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName1, indexName2, indexName3));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, space, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ifForceIndex(Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, consumer);
        if (indexHint != null) {
            addIndexHint(indexHint);
        }
        return this;
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName1, indexName2));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, space, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ifUseIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, consumer);
        if (indexHint != null) {
            addIndexHint(indexHint);
        }
        return this;
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName1, indexName2));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, space, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ifIgnoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, consumer);
        if (indexHint != null) {
            addIndexHint(indexHint);
        }
        return this;
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName1, indexName2));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, space, consumer));
    }

    @Override
    public final _MultiIndexHintJoinSpec<I> ifForceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, consumer);
        if (indexHint != null) {
            addIndexHint(indexHint);
        }
        return this;
    }

    @Override
    public final _MultiWhereSpec<I> sets(Consumer<UpdateStatement._BatchItemPairs<TableField>> consumer) {
        consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
        return this;
    }

    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        final List<MySQLs.Modifier> list = this.modifierList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }
    /*################################## blow package template method ##################################*/


    @Override
    final I onAsUpdate() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        return this.onAsMySQLUpdate();
    }


    abstract I onAsMySQLUpdate();

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }


    @Override
    final _MultiIndexHintJoinSpec<I> onFromTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String alias) {
        final MySQLSupports.MySQLFromClauseTableBlock block;
        block = new MySQLSupports.MySQLFromClauseTableBlock(joinType, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final MySQLUpdate._MultiJoinSpec<I> onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
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
    final _MultiIndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier,
                                               TableMeta<?> table, String alias) {
        final SimpleJoinClauseTableBlock<I> block;
        block = new SimpleJoinClauseTableBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }


    @Override
    final _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final TabularBlocks.JoinClauseAliasDerivedBlock<_MultiJoinSpec<I>> block;
            block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _OnClause<_MultiJoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier,
                                                 _Cte cteItem, String alias) {
        final TabularBlocks.JoinClauseCteBlock<_MultiJoinSpec<I>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }


    private _MultiJoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final _TabularBlock block;
        block = TabularBlocks.fromNestedBlock(joinType, nestedItems);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    private _OnClause<_MultiJoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final TabularBlocks.JoinClauseNestedBlock<_MultiJoinSpec<I>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }

    private TabularBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }

    /*################################## blow private method ##################################*/


    private _MultiIndexHintJoinSpec<I> addIndexHint(final _IndexHint indexHint) {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.MySQLFromClauseTableBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        ((MySQLSupports.MySQLFromClauseTableBlock) block).addIndexHint(indexHint);
        return this;
    }


    /*################################## blow inner class  ##################################*/

    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     */
    private static final class MySQLSimpleUpdate extends MySQLMultiUpdates<Update>
            implements Update {


        private MySQLSimpleUpdate() {
            super(null);
        }

        @Override
        Update onAsMySQLUpdate() {
            return this;
        }


    }// MySQLSimpleUpdate

    private static final class MySQLBatchUpdate extends MySQLMultiUpdates<_BatchUpdateParamSpec>
            implements BatchUpdate, _BatchStatement, _BatchUpdateParamSpec {

        private List<?> paramList;

        private MySQLBatchUpdate() {
            super(null);
        }

        @Override
        public BatchUpdate namedParamList(final List<?> paramList) {
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
        _BatchUpdateParamSpec onAsMySQLUpdate() {
            return this;
        }


    }//MySQLBatchUpdate


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintJoinSpec<I>>
            implements MySQLUpdate._MultiPartitionJoinClause<I> {

        private final MySQLMultiUpdates<I> stmt;

        private SimplePartitionJoinClause(MySQLMultiUpdates<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLMultiUpdates<I> stmt = this.stmt;

            final MySQLSupports.MySQLFromClauseTableBlock block;
            block = new MySQLSupports.MySQLFromClauseTableBlock(params);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleJoinClauseTableBlock<I extends Item> extends MySQLSupports.MySQLJoinClauseBlock<
            _MultiIndexHintOnSpec<I>,
            _MultiJoinSpec<I>>
            implements MySQLUpdate._MultiIndexHintOnSpec<I> {

        /**
         * @see MySQLSimpleUpdate#onJoinTable(_JoinType, SQLs.TableModifier, TableMeta, String)
         */
        private SimpleJoinClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias,
                                           MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(joinType, table, alias, stmt);
        }

        private SimpleJoinClauseTableBlock(MySQLSupports.MySQLBlockParams params,
                                           MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    } // SimpleJoinClauseTableBlock

    private static final class SimplePartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintOnSpec<I>>
            implements MySQLUpdate._MultiPartitionOnClause<I> {

        private final MySQLMultiUpdates<I> stmt;

        private SimplePartitionOnClause(MySQLMultiUpdates<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLMultiUpdates<I> stmt = this.stmt;

            final SimpleJoinClauseTableBlock<I> block;
            block = new SimpleJoinClauseTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    } // SimplePartitionOnClause


}
