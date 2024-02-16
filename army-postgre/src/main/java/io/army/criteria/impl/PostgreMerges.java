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
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.impl.inner.postgre._PostgreMerge;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreMerge;
import io.army.criteria.postgre.PostgreQuery;
import io.army.meta.FieldMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
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

    private static final class PrimaryMergeIntoClause
            extends CriteriaSupports.WithClause<PostgreCtes, PostgreMerge._MergeIntoClause> implements
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
            return new MergeUsingClause<>(this, only, targetTable, targetAlias, this::simpleEnd);
        }

        @Override
        public <T> PostgreMerge._MergeUsingClause<T, PostgreMerge> mergeInto(SimpleTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias) {
            return new MergeUsingClause<>(this, null, targetTable, targetAlias, this::simpleEnd);
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }

        private PostgreMerge simpleEnd(MergeUsingClause<?, ?> clause) {
            return null;
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
            if (this.sourceBlock == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return new MatchWhenWhenPair<>(this);
        }

        @Override
        public PostgreMerge._NotMatchedThenClause<T, I> whenNotMatched() {
            if (this.sourceBlock == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return new NotMatchWhenWhenPair<>(this);
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

        private MergeUsingClause<T, I> usingClause;

        private MergeUpdateSetClause<T> updateClause;

        private List<_ItemPair> updateItemPairList;

        private boolean delete;

        private boolean doNothing;

        private MatchWhenWhenPair(MergeUsingClause<T, I> usingClause) {
            super(CriteriaContexts.subJoinableSingleDmlContext(usingClause.context.dialect(), usingClause.context));
            this.usingClause = usingClause;
            ContextStack.push(this.context);
        }

        @Override
        public PostgreMerge._MergeWhenSpec<T, I> then(Function<PostgreMerge._MatchedMergeActionSpec<T>, Statement._EndFlag> function) {
            // firstly, end and condition clause
            this.endWhereClauseIfNeed();

            final MergeUsingClause<T, I> clause = this.usingClause;
            if (clause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            CriteriaUtils.invokeFunction(function, this);

            final MergeUpdateSetClause<T> updateClause = this.updateClause;
            List<_ItemPair> itemPairList = null;
            if (!this.doNothing
                    && !this.delete
                    && (updateClause == null || (itemPairList = updateClause.endUpdateSetClause()).size() == 0)) {
                throw CriteriaUtils.dontAddAnyItem();
            }

            ContextStack.pop(this.context);

            if (itemPairList == null) {
                this.updateItemPairList = Collections.emptyList();
            } else {
                this.updateItemPairList = itemPairList;
            }

            this.usingClause = null;
            this.updateClause = null;
            return clause.onWhenEnd(this);
        }

        @Override
        public PostgreMerge._MergeUpdateSetClause<T> update() {
            final MergeUsingClause<T, I> usingClause = this.usingClause;
            if (usingClause == null || this.updateClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            assert usingClause.targetTable != null;
            this.context.singleDmlTable(usingClause.targetTable, usingClause.targetAlias);
            this.context.onAddBlock(usingClause.sourceBlock);

            final MergeUpdateSetClause<T> updateClause;
            this.updateClause = updateClause = new MergeUpdateSetClause<>(this.context, usingClause.targetTable, usingClause.targetAlias);
            return updateClause;
        }

        @Override
        public Statement._EndFlag delete() {
            if (this.usingClause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.delete = true;
            return this;
        }

        @Override
        public Statement._EndFlag doNothing() {
            if (this.usingClause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.doNothing = true;
            return this;
        }


        @Override
        public List<_ItemPair> updateItemPairList() {
            List<_ItemPair> list = this.updateItemPairList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
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
            InsertSupports.ValueSyntaxOptions,
            Statement._EndFlag {

        private MergeUsingClause<T, I> usingClause;

        private boolean migration;

        private LiteralMode literalMode = LiteralMode.DEFAULT;

        private NullMode nullMode = NullMode.INSERT_DEFAULT;

        private MergeInsertComplexValues<T, I> insertClause;

        private boolean doNothing;

        private _Insert subInsertStmt;

        private NotMatchWhenWhenPair(MergeUsingClause<T, I> usingClause) {
            super(CriteriaContexts.subInsertContext(usingClause.context.dialect(), null, usingClause.context));
            this.usingClause = usingClause;
            ContextStack.push(this.context);
        }


        @Override
        public PostgreMerge._MergeWhenSpec<T, I> then(Function<PostgreMerge._NotMatchedMergeActionClause<T>, Statement._EndFlag> function) {
            // firstly, end and condition clause
            this.endWhereClauseIfNeed();

            final MergeUsingClause<T, I> usingClause = this.usingClause;
            if (usingClause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            CriteriaUtils.invokeFunction(function, this);

            final MergeInsertComplexValues<T, I> insertClause;

            if (this.doNothing) {
                this.insertClause = null;
                ContextStack.pop(this.context);
            } else if ((insertClause = this.insertClause) == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else {
                final MergeInsertValuesParensClause<T, I> staticValuesClause = insertClause.staticValuesClause;
                if (staticValuesClause != null) {
                    insertClause.staticValuesClauseEnd(staticValuesClause.endValuesClause());
                }
                final InsertSupports.InsertMode mode = insertClause.getInsertMode();
                switch (mode) {
                    case DOMAIN:
                        this.subInsertStmt = (_Insert) new MergeSimpleDomainSubInsert(insertClause)
                                .asInsert();
                        break;
                    case VALUES:
                        this.subInsertStmt = (_Insert) new MergeSimpleValuesSubInsert(insertClause)
                                .asInsert();
                        break;
                    default:
                        throw ContextStack.clearStackAnd(_Exceptions::unexpectedEnum, mode);
                }
                insertClause.staticValuesClause = null;
            }

            this.insertClause = null;
            this.usingClause = null;
            return usingClause.onWhenEnd(this);
        }

        @Override
        public PostgreMerge._MergeInsertNullOptionSpec<T> migration() {
            this.migration = true;
            return this;
        }

        @Override
        public PostgreMerge._MergeInsertClause<T> literalMode(LiteralMode mode) {
            this.literalMode = mode;
            return this;
        }

        @Override
        public PostgreMerge._MergeInsertPreferLiteralSpec<T> nullMode(NullMode mode) {
            this.nullMode = mode;
            return this;
        }

        @Override
        public PostgreMerge._MergeInsertOverridingValueSpec<T> insert() {
            return createInsertSubStmt();
        }

        @Override
        public PostgreMerge._MergeInsertOverridingValueSpec<T> insert(Consumer<InsertStatement._StaticColumnSpaceClause<T>> consumer) {
            return createInsertSubStmt()
                    .parens(consumer);
        }


        @Override
        public PostgreMerge._MergeInsertOverridingValueSpec<T> insert(SQLs.SymbolSpace space, Consumer<Consumer<FieldMeta<T>>> consumer) {
            return createInsertSubStmt()
                    .parens(space, consumer);
        }

        @Override
        public Statement._EndFlag doNothing() {
            if (this.usingClause == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.doNothing = true;
            return this;
        }


        @Override
        public boolean isIgnoreReturnIds() {
            // always true
            return true;
        }

        @Override
        public boolean isMigration() {
            return this.migration;
        }

        @Override
        public LiteralMode literalMode() {
            LiteralMode mode = this.literalMode;
            if (mode == null) {
                this.literalMode = mode = LiteralMode.DEFAULT;
            }
            return mode;
        }

        @Override
        public NullMode nullHandle() {
            NullMode mode = this.nullMode;
            if (mode == null) {
                this.nullMode = mode = NullMode.INSERT_DEFAULT;
            }
            return mode;
        }

        @Override
        public boolean isDoNothing() {
            return this.doNothing;
        }

        @Override
        public _Insert insertStmt() {
            return this.subInsertStmt;
        }

        private MergeInsertComplexValues<T, I> createInsertSubStmt() {
            final MergeUsingClause<T, I> usingClause = this.usingClause;
            if (usingClause == null || this.insertClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            assert usingClause.targetTable != null;
            final MergeInsertComplexValues<T, I> insertClause;
            this.insertClause = insertClause = new MergeInsertComplexValues<>(this, usingClause.targetTable, usingClause.targetAlias);
            return insertClause;
        }


    } // MatchWhenWhenPair

    private static final class MergeUpdateSetClause<T> extends SetWhereClause<
            FieldMeta<T>,
            PostgreMerge._MergeUpdateSetSpec<T>,
            Object,
            Object,
            Object,
            Object,
            Object,
            Object,
            Object> implements PostgreMerge._MergeUpdateSetSpec<T> {

        private MergeUpdateSetClause(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
            super(context, updateTable, tableAlias);
        }

        @Override
        public PostgreMerge._MergeUpdateSetSpec<T> sets(Consumer<UpdateStatement._RowPairs<FieldMeta<T>>> consumer) {
            CriteriaUtils.invokeConsumer(CriteriaSupports.rowPairs(this::onAddItemPair), consumer);
            return this;
        }


    } // MergeUpdateSetClause


    private static final class MergeInsertComplexValues<T, I extends Item> extends InsertSupports.ComplexInsertValuesClause<
            T,
            PostgreMerge._MergeInsertOverridingValueSpec<T>,
            PostgreMerge._ValuesDefaultSpec<T>,
            Statement._EndFlag> implements PostgreMerge._MergeInsertOverridingValueSpec<T> {

        private final String targetAlias;

        private PostgreInserts.OverridingMode overridingMode;

        private MergeInsertValuesParensClause<T, I> staticValuesClause;

        private MergeInsertComplexValues(NotMatchWhenWhenPair<T, I> whenClaus, TableMeta<T> targetTable,
                                         String targetAlias) {
            super(whenClaus, targetTable, false); // here non-towStmtMode
            this.targetAlias = targetAlias;
        }

        @Override
        public PostgreMerge._MergeInsertStaticValuesParensClause<T> values() {
            final MergeInsertValuesParensClause<T, I> staticValuesClause;
            this.staticValuesClause = staticValuesClause = new MergeInsertValuesParensClause<>(this);
            return staticValuesClause;
        }

        @Override
        public PostgreMerge._ValuesDefaultSpec<T> overridingSystemValue() {
            this.overridingMode = PostgreInserts.OverridingMode.OVERRIDING_SYSTEM_VALUE;
            return this;
        }

        @Override
        public PostgreMerge._ValuesDefaultSpec<T> overridingUserValue() {
            this.overridingMode = PostgreInserts.OverridingMode.OVERRIDING_USER_VALUE;
            return this;
        }

        @Override
        public PostgreMerge._ValuesDefaultSpec<T> ifOverridingSystemValue(BooleanSupplier predicate) {
            if (CriteriaUtils.invokeBooleanSupplier(predicate)) {
                this.overridingMode = PostgreInserts.OverridingMode.OVERRIDING_SYSTEM_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreMerge._ValuesDefaultSpec<T> ifOverridingUserValue(BooleanSupplier predicate) {
            if (CriteriaUtils.invokeBooleanSupplier(predicate)) {
                this.overridingMode = PostgreInserts.OverridingMode.OVERRIDING_USER_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }


        @Nullable
        @Override
        public String tableAlias() {
            return this.targetAlias;
        }


    } // MergeInsertComplexValues

    private static final class MergeInsertValuesParensClause<T, I extends Item> extends InsertSupports.ValuesParensClauseImpl<
            T,
            PostgreMerge._MergeInsertStaticValuesCommaClause<T>>
            implements PostgreMerge._MergeInsertStaticValuesCommaClause<T>,
            PostgreMerge._MergeInsertStaticValuesParensClause<T> {

        private MergeInsertValuesParensClause(MergeInsertComplexValues<T, I> clause) {
            super(clause.context, clause.migration, clause::validateField);
        }

        @Override
        public PostgreMerge._MergeInsertStaticValuesParensClause<T> comma() {
            return this;
        }


    } // MergeInsertValuesParensClause


    static abstract class MergeValuesSyntaxSubInsert extends InsertSupports.ArmyValueSyntaxStatement<SubStatement, SubStatement>
            implements PostgreInsert, _PostgreInsert, SubStatement {

        private final PostgreInserts.OverridingMode overridingMode;

        private MergeValuesSyntaxSubInsert(MergeInsertComplexValues<?, ?> clause) {
            super(clause);
            this.overridingMode = clause.overridingMode;
        }

        @Nullable
        @Override
        public final String rowAlias() {
            // always null
            return null;
        }

        @Override
        public final boolean hasConflictAction() {
            // true
            return true;
        }

        @Override
        public final boolean isIgnorableConflict() {
            return true;
        }

        @Override
        public final boolean isDoNothing() {
            // false
            return false;
        }

        @Override
        public final List<? extends _SelectItem> returningList() {
            // no RETURNING clause
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean isRecursive() {
            return false;
        }

        @Override
        public final List<_Cte> cteList() {
            return Collections.emptyList();
        }

        @Nullable
        @Override
        public final SQLWords overridingValueWords() {
            return this.overridingMode;
        }

        @Nullable
        @Override
        public final _ConflictActionClauseResult getConflictActionResult() {
            // always null
            return null;
        }


    } // MergeValuesSyntaxSubInsert


    private static final class MergeSimpleDomainSubInsert extends MergeValuesSyntaxSubInsert
            implements _PostgreInsert._PostgreDomainInsert {

        private final List<?> domainList;

        private MergeSimpleDomainSubInsert(MergeInsertComplexValues<?, ?> clause) {
            super(clause);
            assert this.insertTable instanceof SimpleTableMeta;
            this.domainList = clause.domainListForSingle();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    } // MergeSimpleDomainSubInsert

    private static final class MergeSimpleValuesSubInsert extends MergeValuesSyntaxSubInsert
            implements _PostgreInsert._PostgreValueInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private MergeSimpleValuesSubInsert(MergeInsertComplexValues<?, ?> clause) {
            super(clause);
            assert this.insertTable instanceof SimpleTableMeta;
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }

    } // MergeSimpleValuesSubInsert


}
