package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreMerge;
import io.army.criteria.postgre.PostgreQuery;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.List;
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

        private MergeUsingClause(PrimaryMergeIntoClause clause, @Nullable SQLs.WordOnly targetOnly, TableMeta<T> targetTable,
                                 String targetAlias, Function<MergeUsingClause<?, ?>, I> function) {
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
            if (this.sourceBlock == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return this.function.apply(this);
        }


    } // MergeWhenClause


}
