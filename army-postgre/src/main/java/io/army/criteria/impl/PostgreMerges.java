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

import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.impl.inner.postgre._PostgreMerge;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreMerge;
import io.army.criteria.postgre.PostgreQuery;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class PostgreMerges {

    static PostgreMerge._WithSpec mergeStmt(@Nullable ArmyStmtSpec spec) {
        return new PrimaryMergeIntoClause(spec);
    }


    private PostgreMerges() {
        throw new UnsupportedOperationException();
    }

    private static final class PrimaryMergeIntoClause extends CriteriaSupports.WithClause<PostgreCtes, PostgreMerge._MergeIntoClause> implements
            PostgreMerge._WithSpec {


        private PrimaryMergeIntoClause(@Nullable ArmyStmtSpec spec) {
            super(spec, CriteriaContexts.otherPrimaryContext(PostgreUtils.DIALECT)); //TODO add for multi-stmt

        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreMerge._MergeIntoClause> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreMerge._MergeIntoClause> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreMerge._MergeUsingClause<T, PostgreMerge> mergeInto(@Nullable SQLs.WordOnly only, SimpleTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias) {
            if (only == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return null;
        }

        @Override
        public <T> PostgreMerge._MergeUsingClause<T, PostgreMerge> mergeInto(SimpleTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias) {
            return null;
        }

        @Override
        public <T> PostgreMerge._MergeUsingClause<T, PostgreMerge._MergeChildClause<T>> mergeInto(@Nullable SQLs.WordOnly only, ParentTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias) {
            if (only == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return null;
        }

        @Override
        public <T> PostgreMerge._MergeUsingClause<T, PostgreMerge._MergeChildClause<T>> mergeInto(ParentTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias) {
            return null;
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    } // MergeIntoClause


    private static final class MergeUsingClause<T, I extends Item> implements
            PostgreMerge._MergeUsingClause<T, I>,
            PostgreMerge._MergeWhenSpec<T, I> {

        private final CriteriaContext context;

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final SQLs.WordOnly targetOnly;

        private final TableMeta<T> targetTable;

        private final String targetAlias;

        private final Function<MergeUsingClause<?, ?>, I> function;

        private _TabularBlock sourceBlock;

        private List<_PostgreMerge._WhenPair> pairList;

        private MergeUsingClause(PrimaryMergeIntoClause clause, @Nullable SQLs.WordOnly targetOnly, @Nullable TableMeta<T> targetTable,
                                 String targetAlias, Function<MergeUsingClause<?, ?>, I> function) {
            if (targetTable == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(targetAlias)) {
                throw ContextStack.clearStackAnd(_Exceptions::tableAliasIsEmpty);
            }
            this.context = clause.context;
            this.recursive = clause.isRecursive();
            this.cteList = clause.cteList();
            this.targetOnly = targetOnly;

            this.targetTable = targetTable;
            this.targetAlias = targetAlias;
            this.function = function;
        }


        @Override
        public Statement._OnClause<PostgreMerge._MergeWhenClause<T, I>> using(@Nullable SQLs.WordOnly only, TableMeta<?> sourceTable, SQLs.WordAs as, String sourceAlias) {
            if (only == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.sourceBlock != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final TabularBlocks.JoinClauseBlock<PostgreMerge._MergeWhenClause<T, I>> block;
            block = TabularBlocks.joinTableBlock(_JoinType.JOIN, only, sourceTable, sourceAlias, this);
            this.sourceBlock = block;
            return block;
        }

        @Override
        public Statement._OnClause<PostgreMerge._MergeWhenClause<T, I>> using(TableMeta<?> sourceTable, SQLs.WordAs as, String sourceAlias) {
            if (this.sourceBlock != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final TabularBlocks.JoinClauseBlock<PostgreMerge._MergeWhenClause<T, I>> block;
            block = TabularBlocks.joinTableBlock(_JoinType.JOIN, null, sourceTable, sourceAlias, this);
            this.sourceBlock = block;
            return block;
        }

        @Override
        public Statement._AsClause<Statement._OnClause<PostgreMerge._MergeWhenClause<T, I>>> using(final @Nullable SubQuery sourceQuery) {
            if (sourceQuery == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.sourceBlock != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return alias -> {
                final TabularBlocks.JoinClauseDerivedBlock<PostgreMerge._MergeWhenClause<T, I>> block;
                block = TabularBlocks.joinDerivedBlock(_JoinType.JOIN, null, sourceQuery, alias, this);
                this.sourceBlock = block;
                return block;
            };
        }

        @Override
        public Statement._AsClause<Statement._OnClause<PostgreMerge._MergeWhenClause<T, I>>> using(Supplier<SubQuery> supplier) {
            return this.using(CriteriaUtils.invokeSupplier(supplier));
        }

        @Override
        public PostgreMerge._MatchedThenClause<T, I> whenMatched() {
            return null;
        }

        @Override
        public PostgreMerge._NotMatchedThenClause<T, I> whenNotMatched() {
            return null;
        }

        @Override
        public I asCommand() {
            final List<_PostgreMerge._WhenPair> list = this.pairList;
            if (this.sourceBlock == null || list == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.pairList = _Collections.unmodifiableList(list);
            return this.function.apply(this);
        }


        private PostgreMerge._MergeWhenSpec<T, I> onWhenEnd(final _PostgreMerge._WhenPair pair) {
            List<_PostgreMerge._WhenPair> list = this.pairList;
            if (list == null) {
                this.pairList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            list.add(pair);
            return this;
        }


    } // MergeWhenClause


    private static final class MatchWhenWhenPair<T, I extends Item> extends WhereClause<
            Object,
            PostgreMerge._MatchedThenClause<T, I>,
            Object,
            Object,
            Object,
            Object,
            Object> implements PostgreMerge._MatchedThenClause<T, I>,
            PostgreMerge._MatchedMergeActionSpec<T>,
            _PostgreMerge._WhenMatchedPair,
            Statement._EndFlag {

        private MergeUsingClause<T, I> clause;

        private boolean delete;

        private boolean doNothing;

        private MatchWhenWhenPair(MergeUsingClause<T, I> clause) {
            super(clause.context);
            this.clause = clause;
        }

        @Override
        public PostgreMerge._MergeWhenSpec<T, I> then(Function<PostgreMerge._MatchedMergeActionSpec<T>, Statement._EndFlag> function) {
            final MergeUsingClause<T, I> clause = this.clause;
            if (clause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            CriteriaUtils.invokeFunction(function, this);
            this.clause = null;
            return clause.onWhenEnd(this);
        }

        @Override
        public PostgreMerge._MergeUpdateSetClause<T> update() {
            return null;
        }

        @Override
        public Statement._EndFlag delete() {
            if (this.clause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.delete = true;
            return this;
        }

        @Override
        public Statement._EndFlag doNothing() {
            if (this.clause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.doNothing = true;
            return this;
        }

        @Override
        public boolean isDelete() {
            return this.delete;
        }

        @Override
        public boolean isDoNothing() {
            return this.doNothing;
        }


    } // MatchWhenWhenPair


    private static final class NotMatchWhenWhenPair<T, I extends Item> extends WhereClause<
            Object,
            PostgreMerge._NotMatchedThenClause<T, I>,
            Object,
            Object,
            Object,
            Object,
            Object> implements PostgreMerge._NotMatchedThenClause<T, I>,
            PostgreMerge._NotMatchedMergeActionClause<T>,
            _PostgreMerge._WhenNotMatchedPair,
            Statement._EndFlag {

        private MergeUsingClause<T, I> clause;

        private boolean doNothing;

        private NotMatchWhenWhenPair(MergeUsingClause<T, I> clause) {
            super(clause.context);
            this.clause = clause;
        }


        @Override
        public PostgreMerge._MergeWhenSpec<T, I> then(Function<PostgreMerge._NotMatchedMergeActionClause<T>, Statement._EndFlag> function) {
            final MergeUsingClause<T, I> clause = this.clause;
            if (clause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            CriteriaUtils.invokeFunction(function, this);
            this.clause = null;
            return clause.onWhenEnd(this);
        }


        @Override
        public PostgreMerge._MergeInsertOverridingValueSpec<T> insert() {
            return null;
        }

        @Override
        public PostgreMerge._MergeInsertOverridingValueSpec<T> insert(Consumer<InsertStatement._StaticColumnSpaceClause<T>> consumer) {
            return null;
        }

        @Override
        public PostgreMerge._MergeInsertOverridingValueSpec<T> insert(SQLs.SymbolSpace space, Consumer<Consumer<FieldMeta<T>>> consumer) {
            return null;
        }

        @Override
        public Statement._EndFlag doNothing() {
            if (this.clause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.doNothing = true;
            return this;
        }


        @Override
        public boolean isDoNothing() {
            return this.doNothing;
        }


    } // MatchWhenWhenPair

}
