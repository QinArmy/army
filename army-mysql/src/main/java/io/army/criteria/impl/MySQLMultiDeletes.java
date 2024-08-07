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
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.mysql.*;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This class is the implementation of MySQL 8.0 multi-table delete syntax.
 *
 * @since 0.6.0
 */
abstract class MySQLMultiDeletes<I extends Item> extends JoinableDelete<
        I,
        MySQLCtes,
        MySQLDelete._MySQLMultiDeleteClause<I>,
        MySQLDelete._MultiIndexHintJoinSpec<I>,
        Statement._AsClause<MySQLDelete._ParensJoinSpec<I>>,
        MySQLDelete._MultiJoinSpec<I>,
        Void,
        MySQLDelete._MultiIndexHintOnSpec<I>,
        Statement._AsParensOnClause<MySQLDelete._MultiJoinSpec<I>>,
        Statement._OnClause<MySQLDelete._MultiJoinSpec<I>>,
        Void,
        Statement._DmlDeleteSpec<I>,
        MySQLDelete._MultiWhereAndSpec<I>>
        implements MySQLDelete,
        _MySQLMultiDelete,
        MySQLDelete._MultiWithSpec<I>,
        MySQLDelete._MultiDeleteFromTableClause<I>,
        MySQLDelete._SimpleMultiDeleteUsingClause<I>,
        MySQLDelete._MultiIndexHintJoinSpec<I>,
        MySQLDelete._ParensJoinSpec<I>,
        MySQLDelete._MultiWhereAndSpec<I>,
        JoinableClause.UsingClauseListener {


    static _MultiWithSpec<Delete> simple() {
        return new MySQLSimpleDelete();
    }

    static _MultiWithSpec<_BatchDeleteParamSpec> batch() {
        return new MySQLBatchDelete();
    }


    private List<Hint> hintList;

    private List<MySQLs.Modifier> modifierList;

    boolean usingSyntax;

    private List<String> tableAliasList;

    private List<_Pair<String, TableMeta<?>>> deleteTablePairList;


    _TabularBlock fromCrossBlock;

    private MySQLMultiDeletes(@Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primaryMultiDmlContext(MySQLUtils.DIALECT, spec));
    }


    @Override
    public final MySQLQuery._StaticCteParensSpec<_MySQLMultiDeleteClause<I>> with(String name) {
        return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_MySQLMultiDeleteClause<I>> withRecursive(String name) {
        return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _MultiDeleteFromAliasClause<I> delete(Supplier<List<Hint>> hints,
                                                       List<MySQLs.Modifier> modifiers) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return new MySQLFromAliasClause<>(this);
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias) {
        this.tableAliasList = _Collections.singletonList(alias);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias1, String alias2) {
        this.tableAliasList = ArrayUtils.of(alias1, alias2);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3) {
        this.tableAliasList = ArrayUtils.of(alias1, alias2, alias3);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3, String alias4) {
        this.tableAliasList = ArrayUtils.of(alias1, alias2, alias3, alias4);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(List<String> aliasList) {
        this.tableAliasList = _Collections.asUnmodifiableList(aliasList);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(Consumer<Consumer<String>> consumer) {
        CriteriaUtils.stringList(this.context, true, consumer);
        return this;
    }

    @Override
    public final _MultiPartitionJoinClause<I> from(TableMeta<?> table) {
        return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _MultiPartitionJoinClause<I> using(TableMeta<?> table) {
        this.usingSyntax = true;
        return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
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
    public final MySQLDelete._MultiJoinSpec<I> from(Function<_NestedLeftParenSpec<MySQLDelete._MultiJoinSpec<I>>, MySQLDelete._MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> using(Function<_NestedLeftParenSpec<MySQLDelete._MultiJoinSpec<I>>, MySQLDelete._MultiJoinSpec<I>> function) {
        this.usingSyntax = true;
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> crossJoin(Function<_NestedLeftParenSpec<MySQLDelete._MultiJoinSpec<I>>, MySQLDelete._MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> join(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> straightJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> parens(String first, String... rest) {
        this.getFromClauseDerived().parens(first, rest);
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().parens(this.context, consumer);
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
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

    @Override
    public final List<_Pair<String, TableMeta<?>>> deleteTableList() {
        final List<_Pair<String, TableMeta<?>>> list = this.deleteTablePairList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    public final void onUsing() {
        this.usingSyntax = true;
    }

    @Override
    public final boolean isUsingSyntax() {
        return this.usingSyntax;
    }


    @Override
    final I onAsDelete() {
        if (this.deleteTablePairList == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return this.onAsMySQLDelete();
    }


    abstract I onAsMySQLDelete();

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.deleteTablePairList = null;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final void onEndStatement() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        final List<String> tableAliasList = this.tableAliasList;
        final int tableAliasSize;
        if (tableAliasList == null || (tableAliasSize = tableAliasList.size()) == 0) {
            throw ContextStack.criteriaError(this.context, "table alias list must non-empty.");
        }
        List<_Pair<String, TableMeta<?>>> pairList;
        TableMeta<?> table;
        if (tableAliasSize == 1) {
            final String tableAlias = tableAliasList.get(0);
            table = this.context.getTable(tableAlias);
            if (table == null) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownTableAlias, tableAlias);
            }
            pairList = Collections.singletonList(_Pair.create(tableAlias, table));
        } else {
            pairList = _Collections.arrayList(tableAliasSize);
            for (String tableAlias : tableAliasList) {
                table = this.context.getTable(tableAlias);
                if (table == null) {
                    throw ContextStack.criteriaError(this.context, _Exceptions::unknownTableAlias, tableAlias);
                }
                pairList.add(_Pair.create(tableAlias, table));
            }
            pairList = _Collections.unmodifiableList(pairList);
        }
        this.tableAliasList = null;
        this.deleteTablePairList = pairList;

    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final MySQLDelete._MultiIndexHintJoinSpec<I> onFromTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String alias) {
        final MySQLSupports.MySQLFromClauseTableBlock block;
        block = new MySQLSupports.MySQLFromClauseTableBlock(joinType, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final MySQLDelete._MultiJoinSpec<I> onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
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
    final _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType,
                                                             @Nullable SQLs.DerivedModifier modifier,
                                                             DerivedTable table) {
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


    private TabularBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }

    private MySQLDelete._MultiJoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final _TabularBlock block;
        block = TabularBlocks.fromNestedBlock(joinType, nestedItems);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    private _OnClause<MySQLDelete._MultiJoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final TabularBlocks.JoinClauseNestedBlock<MySQLDelete._MultiJoinSpec<I>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }


    private _MultiIndexHintJoinSpec<I> addIndexHint(final _IndexHint indexHint) {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.MySQLFromClauseTableBlock)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        ((MySQLSupports.MySQLFromClauseTableBlock) block).addIndexHint(indexHint);
        return this;
    }



    private static final class MySQLSimpleDelete extends MySQLMultiDeletes<Delete>
            implements Delete {

        private MySQLSimpleDelete() {
            super(null);
        }

        @Override
        Delete onAsMySQLDelete() {
            return this;
        }


    }//MySQLSimpleDelete

    private static final class MySQLBatchDelete extends MySQLMultiDeletes<_BatchDeleteParamSpec>
            implements BatchDelete, _BatchStatement, _BatchDeleteParamSpec {

        private List<?> paramList;

        private MySQLBatchDelete() {
            super(null);
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
        _BatchDeleteParamSpec onAsMySQLDelete() {
            return this;
        }


    }//MySQLBatchDelete


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintJoinSpec<I>>
            implements MySQLDelete._MultiPartitionJoinClause<I> {

        private final MySQLMultiDeletes<I> stmt;

        private SimplePartitionJoinClause(MySQLMultiDeletes<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLMultiDeletes<I> stmt = this.stmt;

            final MySQLSupports.MySQLFromClauseTableBlock block;
            block = new MySQLSupports.MySQLFromClauseTableBlock(params);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleJoinClauseTableBlock<I extends Item>
            extends MySQLSupports.MySQLJoinClauseBlock<
            _MultiIndexHintOnSpec<I>,
            _MultiJoinSpec<I>>
            implements MySQLDelete._MultiIndexHintOnSpec<I> {


        private SimpleJoinClauseTableBlock(_JoinType joinType, TableMeta<?> tableItem, String alias,
                                           MySQLDelete._MultiJoinSpec<I> stmt) {
            super(joinType, tableItem, alias, stmt);
        }

        private SimpleJoinClauseTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    } // SimpleJoinClauseTableBlock

    private static final class SimplePartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintOnSpec<I>>
            implements MySQLDelete._MultiPartitionOnClause<I> {

        private final MySQLMultiDeletes<I> stmt;

        private SimplePartitionOnClause(MySQLMultiDeletes<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLMultiDeletes<I> stmt = this.stmt;

            final SimpleJoinClauseTableBlock<I> block;
            block = new SimpleJoinClauseTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class MySQLFromAliasClause<I extends Item>
            implements MySQLDelete._MultiDeleteFromAliasClause<I> {

        private final MySQLMultiDeletes<I> stmt;

        private MySQLFromAliasClause(MySQLMultiDeletes<I> stmt) {
            this.stmt = stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias) {
            this.stmt.delete(alias);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2) {
            this.stmt.delete(alias1, alias2);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2, String alias3) {
            this.stmt.delete(alias1, alias2, alias3);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2, String alias3, String alias4) {
            this.stmt.delete(alias1, alias2, alias3, alias4);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(List<String> aliasList) {
            this.stmt.delete(aliasList);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(Consumer<Consumer<String>> consumer) {
            this.stmt.delete(consumer);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias) {
            this.stmt.delete(alias);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias1, String alias2) {
            this.stmt.delete(alias1, alias2);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias1, String alias2, String alias3) {
            this.stmt.delete(alias1, alias2, alias3);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias1, String alias2, String alias3, String alias4) {
            this.stmt.delete(alias1, alias2, alias3, alias4);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(List<String> aliasList) {
            this.stmt.delete(aliasList);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(Consumer<Consumer<String>> consumer) {
            this.stmt.delete(consumer);
            return this.stmt;
        }


    } // MySQLFromAliasClause




}
