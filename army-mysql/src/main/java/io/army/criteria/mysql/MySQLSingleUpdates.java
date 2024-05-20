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
import io.army.criteria.mysql.inner._IndexHint;
import io.army.criteria.mysql.inner._MySQLSingleUpdate;
import io.army.criteria.standard.SQLs;
import io.army.dialect.Dialect;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of single-table {@link MySQLUpdate}
 */

abstract class MySQLSingleUpdates<I extends Item, T>
        extends SingleUpdateStatement<
        I,
        FieldMeta<T>,
        MySQLUpdate._SingleWhereSpec<I, T>,
        MySQLUpdate._OrderBySpec<I>,
        MySQLUpdate._SingleWhereAndSpec<I>,
        MySQLUpdate._OrderByCommaSpec<I>,
        MySQLUpdate._LimitSpec<I>,
        Statement._DmlUpdateSpec<I>,
        Object, Object>
        implements _MySQLSingleUpdate,
        MySQLUpdate,
        MySQLUpdate._SingleIndexHintSpec<I, T>,
        MySQLUpdate._SingleWhereSpec<I, T>,
        MySQLUpdate._SingleWhereAndSpec<I>,
        MySQLUpdate._OrderByCommaSpec<I> {

    /**
     * <p>
     * create simple(non-batch) single-table UPDATE statement that is primary statement.
     */
    static _SingleWithSpec<Update> simple() {
        return new SimpleUpdateClause();
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     */
    static _SingleWithSpec<_BatchUpdateParamSpec> batch() {
        return new BatchUpdateClause();
    }


    private final boolean recursive;

    private final List<_Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLs.Modifier> modifierList;

    private final List<String> partitionList;

    private List<_IndexHint> indexHintList;

    private MySQLSingleUpdates(MySQLUpdateClause<?> clause) {
        super(clause.context, clause.updateTable, clause.tableAlias);

        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();
        this.hintList = _Collections.safeList(clause.hintList);
        this.modifierList = _Collections.safeList(clause.modifierList);

        this.partitionList = _Collections.safeList(clause.partitionList);

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
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        return this.modifierList;
    }


    /*################################## blow IndexHintClause method ##################################*/


    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(String indexName) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(String indexName1, String indexName2) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName1, indexName2));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(String indexName1, String indexName2, String indexName3) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName1, indexName2, indexName3));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, space, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifUseIndex(Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.USE_INDEX, consumer);
        if (indexHint != null) {
            indexHintEnd(indexHint);
        }
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(String indexName) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(String indexName1, String indexName2) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName1, indexName2));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(String indexName1, String indexName2, String indexName3) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName1, indexName2, indexName3));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, space, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifIgnoreIndex(Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, consumer);
        if (indexHint != null) {
            indexHintEnd(indexHint);
        }
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(String indexName) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(String indexName1, String indexName2) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName1, indexName2));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(String indexName1, String indexName2, String indexName3) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName1, indexName2, indexName3));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, space, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifForceIndex(Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, consumer);
        if (indexHint != null) {
            indexHintEnd(indexHint);
        }
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName1, indexName2));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, space, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifUseIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, consumer);
        if (indexHint != null) {
            indexHintEnd(indexHint);
        }
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName1, indexName2));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, space, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifIgnoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, consumer);
        if (indexHint != null) {
            indexHintEnd(indexHint);
        }
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName1, indexName2));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        return indexHintEnd(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, space, consumer));
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifForceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
        final _IndexHint indexHint;
        indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, consumer);
        if (indexHint != null) {
            indexHintEnd(indexHint);
        }
        return this;
    }


    @Override
    public final List<String> partitionList() {
        return this.partitionList;
    }

    @Override
    public final _SingleWhereClause<I> sets(Consumer<UpdateStatement._BatchItemPairs<FieldMeta<T>>> consumer) {
        consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
        return this;
    }

    @Override
    public final List<? extends _IndexHint> indexHintList() {
        final List<_IndexHint> list = this.indexHintList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }


    @Override
    final I onAsUpdate() {
        this.indexHintList = _Collections.safeUnmodifiableList(this.indexHintList);
        return this.onAsMySQLUpdate();
    }


    abstract I onAsMySQLUpdate();

    @Override
    final void onClear() {
        this.indexHintList = null;

    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }


    private MySQLUpdate._SingleIndexHintSpec<I, T> indexHintEnd(final _IndexHint indexHint) {

        List<_IndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            this.indexHintList = indexHintList = _Collections.arrayList();
        } else if (!(indexHintList instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        indexHintList.add(indexHint);
        return this;
    }


    private static final class MySQLSimpleUpdate<T> extends MySQLSingleUpdates<Update, T>
            implements Update {

        private MySQLSimpleUpdate(SimpleUpdateClause clause) {
            super(clause);
        }

        @Override
        Update onAsMySQLUpdate() {
            return this;
        }


    }//MySQLSimpleUpdate

    private static final class MySQLBatchUpdate<T> extends MySQLSingleUpdates<_BatchUpdateParamSpec, T>
            implements BatchUpdate, _BatchUpdateParamSpec, _BatchStatement {

        private List<?> paramList;

        private MySQLBatchUpdate(BatchUpdateClause clause) {
            super(clause);
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


    }//MySQLSimpleUpdate

    private static abstract class MySQLUpdateClause<I extends Item>
            extends CriteriaSupports.WithClause<MySQLCtes, _SingleUpdateClause<I>>
            implements MySQLUpdate._SingleWithSpec<I>,
            _SingleUpdateSpaceClause<I> {

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private TableMeta<?> updateTable;

        private List<String> partitionList;

        private String tableAlias;

        private MySQLUpdateClause(@Nullable ArmyStmtSpec spec) {
            super(spec, CriteriaContexts.primarySingleDmlContext(MySQLUtils.DIALECT, null));
            ContextStack.push(this.context);
        }

        @Override
        public final MySQLQuery._StaticCteParensSpec<_SingleUpdateClause<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final MySQLQuery._StaticCteParensSpec<_SingleUpdateClause<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final _SingleUpdateSpaceClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this;
        }

        @Override
        public final <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public final <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public final <T> _SingleIndexHintSpec<I, T> space(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <P> _SingleIndexHintSpec<I, P> space(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <T> _SinglePartitionClause<I, T> space(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public final <P> _SinglePartitionClause<I, P> space(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        final MySQLCtes createCteBuilder(boolean recursive) {
            return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
        }

        abstract <T> _SingleIndexHintSpec<I, T> createUpdateStmt();


    }//PostgreUpdateClause

    private static final class SimpleUpdateClause extends MySQLUpdateClause<Update> {

        private SimpleUpdateClause() {
            super(null);
        }

        @Override
        <T> _SingleIndexHintSpec<Update, T> createUpdateStmt() {
            return new MySQLSimpleUpdate<>(this);
        }


    }//SimpleUpdateClause

    private static final class BatchUpdateClause extends MySQLUpdateClause<_BatchUpdateParamSpec> {

        private BatchUpdateClause() {
            super(null);
        }

        @Override
        <T> _SingleIndexHintSpec<_BatchUpdateParamSpec, T> createUpdateStmt() {
            return new MySQLBatchUpdate<>(this);
        }


    }//BatchUpdateClause


    private static final class SimplePartitionClause<I extends Item, T>
            extends MySQLSupports.PartitionAsClause<_SingleIndexHintSpec<I, T>>
            implements MySQLUpdate._SinglePartitionClause<I, T> {

        private final MySQLUpdateClause<I> clause;

        private SimplePartitionClause(MySQLUpdateClause<I> clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            if (this.table == null) {
                throw ContextStack.nullPointer(clause.context);
            }
            this.clause = clause;
        }

        @Override
        _SingleIndexHintSpec<I, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final MySQLUpdateClause<I> clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return clause.createUpdateStmt();
        }


    }//SimplePartitionClause


}
