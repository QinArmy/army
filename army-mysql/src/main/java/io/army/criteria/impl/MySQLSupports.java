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
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.criteria.mysql.MySQLWindow;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLSupports extends CriteriaSupports {


    private MySQLSupports() {

    }


    static MySQLCtes mysqlLCteBuilder(boolean recursive, CriteriaContext context) {
        return new MySQLCteBuilder(recursive, context);
    }

    static <R> MySQLQuery._IndexPurposeBySpec<R> indexHintClause(CriteriaContext context, IndexHintCommand command,
                                                                 Function<_IndexHint, R> function) {
        return new MySQLIndexHintClause<>(context, command, function);
    }

    static MySQLWindow._PartitionBySpec namedWindow(String windowName, CriteriaContext context,
                                                    @Nullable String existingWindowName) {
        return new MySQLWindowImpl(windowName, context, existingWindowName);
    }

    static MySQLWindow._PartitionBySpec anonymousWindow(CriteriaContext context, @Nullable String existingWindowName) {
        return new MySQLWindowImpl(context, existingWindowName);
    }


    static _IndexHint indexHint(IndexHintCommand command, @Nullable String indexName) {
        if (indexName == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new MySQLIndexHint(command, null, Collections.singletonList(indexName));
    }

    static _IndexHint indexHint(IndexHintCommand command, @Nullable String indexName1, @Nullable String indexName2) {
        if (indexName1 == null || indexName2 == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new MySQLIndexHint(command, null, ArrayUtils.of(indexName1, indexName2));
    }

    static _IndexHint indexHint(IndexHintCommand command, @Nullable String indexName1, @Nullable String indexName2,
                                @Nullable String indexName3) {
        if (indexName1 == null || indexName2 == null || indexName3 == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new MySQLIndexHint(command, null, ArrayUtils.of(indexName1, indexName2, indexName3));
    }

    static _IndexHint indexHint(IndexHintCommand command, Consumer<Clause._StaticStringSpaceClause> consumer) {
        return new MySQLIndexHint(command, null, ClauseUtils.staticStringClause(true, consumer));
    }

    static _IndexHint indexHint(IndexHintCommand command, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        if (space != SQLs.SPACE) {
            throw CriteriaUtils.unknownWords(space);
        }
        return new MySQLIndexHint(command, null, ClauseUtils.invokingDynamicConsumer(true, true, consumer));
    }

    @Nullable
    static _IndexHint ifIndexHint(IndexHintCommand command, Consumer<Consumer<String>> consumer) {
        final List<String> list;
        list = ClauseUtils.invokingDynamicConsumer(false, true, consumer);
        if (list.size() == 0) {
            return null;
        }
        return new MySQLIndexHint(command, null, list);
    }


    static _IndexHint indexHint(IndexHintCommand command, SQLs.WordFor wordFor, SQLs.IndexHintPurpose target,
                                @Nullable String indexName) {
        if (indexName == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (wordFor != SQLs.FOR) {
            throw CriteriaUtils.unknownWords(wordFor);
        }
        return new MySQLIndexHint(command, target, Collections.singletonList(indexName));
    }

    static _IndexHint indexHint(IndexHintCommand command, SQLs.WordFor wordFor, SQLs.IndexHintPurpose target,
                                @Nullable String indexName1, @Nullable String indexName2) {
        if (indexName1 == null || indexName2 == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (wordFor != SQLs.FOR) {
            throw CriteriaUtils.unknownWords(wordFor);
        }
        return new MySQLIndexHint(command, target, ArrayUtils.of(indexName1, indexName2));
    }

    static _IndexHint indexHint(IndexHintCommand command, SQLs.WordFor wordFor, SQLs.IndexHintPurpose target,
                                @Nullable String indexName1, @Nullable String indexName2, @Nullable String indexName3) {
        if (indexName1 == null || indexName2 == null || indexName3 == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (wordFor != SQLs.FOR) {
            throw CriteriaUtils.unknownWords(wordFor);
        }
        return new MySQLIndexHint(command, target, ArrayUtils.of(indexName1, indexName2, indexName3));
    }

    static _IndexHint indexHint(IndexHintCommand command, SQLs.WordFor wordFor, SQLs.IndexHintPurpose target,
                                Consumer<Clause._StaticStringSpaceClause> consumer) {
        if (wordFor != SQLs.FOR) {
            throw CriteriaUtils.unknownWords(wordFor);
        }
        return new MySQLIndexHint(command, target, ClauseUtils.staticStringClause(true, consumer));
    }

    static _IndexHint indexHint(IndexHintCommand command, SQLs.WordFor wordFor, SQLs.IndexHintPurpose target,
                                SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
        if (wordFor != SQLs.FOR) {
            throw CriteriaUtils.unknownWords(wordFor);
        } else if (space != SQLs.SPACE) {
            throw CriteriaUtils.unknownWords(space);
        }
        return new MySQLIndexHint(command, target, ClauseUtils.invokingDynamicConsumer(true, true, consumer));
    }

    @Nullable
    static _IndexHint ifIndexHint(IndexHintCommand command, SQLs.WordFor wordFor, SQLs.IndexHintPurpose target,
                                  Consumer<Consumer<String>> consumer) {
        if (wordFor != SQLs.FOR) {
            throw CriteriaUtils.unknownWords(wordFor);
        }
        final List<String> list;
        list = ClauseUtils.invokingDynamicConsumer(false, true, consumer);
        if (list.size() == 0) {
            return null;
        }
        return new MySQLIndexHint(command, target, list);
    }


    private static final class DynamicCteQueryParensSpec
            extends CteParensClause<MySQLQuery._QueryDynamicCteAsClause>
            implements MySQLQuery._DynamicCteParensSpec {

        private final MySQLCteBuilder builder;

        private DynamicCteQueryParensSpec(MySQLCteBuilder builder, String name) {
            super(name, builder.context);
            this.builder = builder;
        }


        @Override
        public DialectStatement._CommaClause<MySQLCtes> as(Function<MySQLQuery.WithSpec<DialectStatement._CommaClause<MySQLCtes>>, DialectStatement._CommaClause<MySQLCtes>> function) {
            return function.apply(MySQLQueries.subQuery(this.context, this::subQueryEnd));
        }

        private DialectStatement._CommaClause<MySQLCtes> subQueryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, query);
            return this.builder;
        }


    }//DynamicCteQueryParensSpec

    private static final class MySQLCteBuilder implements MySQLCtes, CteBuilder,
            Statement._CommaClause<MySQLCtes> {

        private final boolean recursive;

        private final CriteriaContext context;


        private MySQLCteBuilder(boolean recursive, CriteriaContext context) {
            this.recursive = recursive;
            this.context = context;
            context.onBeforeWithClause(recursive);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public MySQLQuery._DynamicCteParensSpec subQuery(String name) {
            return new DynamicCteQueryParensSpec(this, name);
        }

        @Override
        public void endLastCte() {
            //no-op
        }

        @Override
        public MySQLCtes comma() {
            return this;
        }


    }//MySQLCteBuilder


    interface MySQLBlockParams extends TabularBlocks.DialectBlockParams {

        List<String> partitionList();

    }


    private static abstract class MySQLFromClauseTableBlock0<R extends Item>
            extends TabularBlocks.FromClauseTableBlock
            implements _MySQLTableBlock,
            MySQLStatement._QueryIndexHintSpec<R> {

        private final List<String> partitionList;

        final R clause;

        private List<_IndexHint> indexHintList;


        private MySQLFromClauseTableBlock0(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
            super(joinType, table, alias);
            this.clause = clause;
            this.partitionList = Collections.emptyList();

        }

        private MySQLFromClauseTableBlock0(MySQLBlockParams params, R clause) {
            super(params.joinType(), (TableMeta<?>) params.tableItem(), params.alias());
            this.clause = clause;
            this.partitionList = params.partitionList();

        }

        @Override
        public MySQLStatement._IndexPurposeBySpec<R> useIndex() {
            return this.createStaticHintClause(IndexHintCommand.USE_INDEX);
        }

        @Override
        public MySQLStatement._IndexPurposeBySpec<R> ignoreIndex() {
            return this.createStaticHintClause(IndexHintCommand.IGNORE_INDEX);
        }

        @Override
        public MySQLStatement._IndexPurposeBySpec<R> forceIndex() {
            return this.createStaticHintClause(IndexHintCommand.FORCE_INDEX);
        }

        @Override
        public SQLWords modifier() {
            //always null
            return null;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            List<_IndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            } else if (indexHintList instanceof ArrayList) {
                indexHintList = _Collections.unmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }

        final MySQLIndexHintClause<Object> createDynamicHintClause(IndexHintCommand command) {
            return new MySQLIndexHintClause<>(((CriteriaContextSpec) this.clause).getContext(),
                    command, this::indexHintEndAndReturnObject);
        }


        private R indexHintEnd(final _IndexHint indexHint) {
            List<_IndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = _Collections.arrayList();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            indexHintList.add(indexHint);
            return this.clause;
        }


        private MySQLIndexHintClause<R> createStaticHintClause(IndexHintCommand command) {
            return new MySQLIndexHintClause<>(((CriteriaContextSpec) this.clause).getContext(),
                    command, this::indexHintEnd);
        }

        private Object indexHintEndAndReturnObject(final _IndexHint indexHint) {
            this.indexHintEnd(indexHint);
            return Collections.EMPTY_LIST;
        }


    }//MySQLFromClauseTableBlock


    static final class MySQLFromClauseTableBlock
            extends TabularBlocks.FromClauseTableBlock
            implements _MySQLTableBlock {


        private final List<String> partitionList;


        private List<_IndexHint> indexHintList;


        MySQLFromClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias) {
            super(joinType, table, alias);
            this.partitionList = Collections.emptyList();

        }

        MySQLFromClauseTableBlock(MySQLBlockParams params) {
            super(params.joinType(), (TableMeta<?>) params.tableItem(), params.alias());
            this.partitionList = params.partitionList();

        }

        @Nullable
        @Override
        public SQLWords modifier() {
            //always null, MySQL table don't support modifier
            return null;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            List<_IndexHint> list = this.indexHintList;
            if (list == null) {
                this.indexHintList = list = Collections.emptyList();
            } else if (list instanceof ArrayList) {
                this.indexHintList = list = _Collections.unmodifiableList(list);
            }
            return list;
        }

        void addIndexHint(final @Nullable _IndexHint hint) {
            if (hint == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            List<_IndexHint> list = this.indexHintList;
            if (list == null) {
                this.indexHintList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            list.add(hint);
        }


    } // MySQLFromClauseTableBlock


    static final class FromClauseForJoinTableBlock<R extends Item> extends MySQLFromClauseTableBlock0<R>
            implements MySQLStatement._DynamicIndexHintClause0<MySQLStatement._IndexForJoinSpec<Object>, R> {

        FromClauseForJoinTableBlock(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
            super(joinType, table, alias, clause);
        }

        FromClauseForJoinTableBlock(MySQLBlockParams params, R clause) {
            super(params, clause);
        }

        @Override
        public R ifUseIndex(Consumer<MySQLStatement._IndexForJoinSpec<Object>> consumer) {
            consumer.accept(this.createDynamicHintClause(IndexHintCommand.USE_INDEX));
            return this.clause;
        }

        @Override
        public R ifIgnoreIndex(Consumer<MySQLStatement._IndexForJoinSpec<Object>> consumer) {
            consumer.accept(this.createDynamicHintClause(IndexHintCommand.IGNORE_INDEX));
            return this.clause;
        }

        @Override
        public R ifForceIndex(Consumer<MySQLStatement._IndexForJoinSpec<Object>> consumer) {
            consumer.accept(this.createDynamicHintClause(IndexHintCommand.FORCE_INDEX));
            return this.clause;
        }


    }//MySQLFromClauseForJoinTableBlock

    static final class FromClausePurposeTableBlock<R extends Item> extends MySQLFromClauseTableBlock0<R>
            implements MySQLStatement._DynamicIndexHintClause0<MySQLStatement._IndexPurposeBySpec<Object>, R> {

        FromClausePurposeTableBlock(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
            super(joinType, table, alias, clause);
        }

        FromClausePurposeTableBlock(MySQLBlockParams params, R clause) {
            super(params, clause);
        }

        @Override
        public R ifUseIndex(Consumer<MySQLStatement._IndexPurposeBySpec<Object>> consumer) {
            consumer.accept(this.createDynamicHintClause(IndexHintCommand.USE_INDEX));
            return this.clause;
        }

        @Override
        public R ifIgnoreIndex(Consumer<MySQLStatement._IndexPurposeBySpec<Object>> consumer) {
            consumer.accept(this.createDynamicHintClause(IndexHintCommand.IGNORE_INDEX));
            return this.clause;
        }

        @Override
        public R ifForceIndex(Consumer<MySQLStatement._IndexPurposeBySpec<Object>> consumer) {
            consumer.accept(this.createDynamicHintClause(IndexHintCommand.FORCE_INDEX));
            return this.clause;
        }


    }//MySQLFromClausePurposeTableBlock


    /**
     * <p>sub class must implements RR .
     *
     * @param <RR> sub interface of {@link  io.army.criteria.Statement._OnClause}
     * @param <OR> sub interface of {@link MySQLQuery._MySQLJoinClause}
     */
    static abstract class MySQLJoinClauseBlock0<T extends Item, RR extends Item, OR extends Item>
            extends TabularBlocks.JoinClauseTableBlock<OR>
            implements _MySQLTableBlock,
            MySQLStatement._QueryIndexHintSpec<RR>,
            MySQLStatement._DynamicIndexHintClause0<T, RR> {

        private final List<String> partitionList;

        private List<_IndexHint> indexHintList;


        MySQLJoinClauseBlock0(_JoinType joinType, TableMeta<?> table, String alias, OR stmt) {
            super(joinType, table, alias, stmt);
            this.partitionList = Collections.emptyList();
        }

        MySQLJoinClauseBlock0(MySQLBlockParams params, OR stmt) {
            super(params.joinType(), (TableMeta<?>) params.tableItem(), params.alias(), stmt);
            this.partitionList = params.partitionList();
        }


        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> useIndex() {
            return this.createStaticHintClause(IndexHintCommand.USE_INDEX);
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> ignoreIndex() {
            return this.createStaticHintClause(IndexHintCommand.IGNORE_INDEX);
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> forceIndex() {
            return this.createStaticHintClause(IndexHintCommand.FORCE_INDEX);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifUseIndex(Consumer<T> consumer) {
            consumer.accept((T) this.createDynamicHintClause(IndexHintCommand.USE_INDEX));
            return (RR) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifIgnoreIndex(Consumer<T> consumer) {
            consumer.accept((T) this.createDynamicHintClause(IndexHintCommand.IGNORE_INDEX));
            return (RR) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifForceIndex(Consumer<T> consumer) {
            consumer.accept((T) this.createDynamicHintClause(IndexHintCommand.FORCE_INDEX));
            return (RR) this;
        }

        @Override
        public final SQLWords modifier() {
            //always null,MySQL currently don't support modifier for table.
            return null;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            List<_IndexHint> indexHintList = this.indexHintList;
            if (indexHintList instanceof ArrayList) {
                indexHintList = _Collections.unmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            } else if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }


        final MySQLIndexHintClause<Object> createDynamicHintClause(IndexHintCommand command) {
            return new MySQLIndexHintClause<>(this.getContext(),
                    command, this::indexHintEndAndReturnObject);
        }


        @SuppressWarnings("unchecked")
        private RR indexHintEnd(final _IndexHint indexHint) {
            List<_IndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = _Collections.arrayList();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            indexHintList.add(indexHint);
            return (RR) this;
        }


        private MySQLIndexHintClause<RR> createStaticHintClause(IndexHintCommand command) {
            return new MySQLIndexHintClause<>(this.getContext(), command, this::indexHintEnd);
        }

        private Object indexHintEndAndReturnObject(final _IndexHint indexHint) {
            this.indexHintEnd(indexHint);
            return Collections.EMPTY_LIST;
        }


    }//MySQLJoinClauseBlock


    /**
     * <p>sub class must implements RR .
     *
     * @param <RR> sub interface of {@link  io.army.criteria.Statement._OnClause}
     * @param <OR> sub interface of {@link MySQLQuery._MySQLJoinClause}
     */
    static abstract class MySQLJoinClauseBlock<RR extends Item, OR extends Item>
            extends TabularBlocks.JoinClauseTableBlock<OR>
            implements _MySQLTableBlock, MySQLStatement._IndexHintFoPurposeClause<RR> {

        private final List<String> partitionList;

        private List<_IndexHint> indexHintList;


        MySQLJoinClauseBlock(_JoinType joinType, TableMeta<?> table, String alias, OR stmt) {
            super(joinType, table, alias, stmt);
            this.partitionList = Collections.emptyList();
        }

        MySQLJoinClauseBlock(MySQLBlockParams params, OR stmt) {
            super(params.joinType(), (TableMeta<?>) params.tableItem(), params.alias(), stmt);
            this.partitionList = params.partitionList();
        }

        @Override
        public final RR useIndex(String indexName) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName));
        }

        @Override
        public final RR useIndex(String indexName1, String indexName2) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName1, indexName2));
        }

        @Override
        public final RR useIndex(String indexName1, String indexName2, String indexName3) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, indexName1, indexName2, indexName3));
        }

        @Override
        public final RR useIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, consumer));
        }

        @Override
        public final RR useIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, space, consumer));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifUseIndex(Consumer<Consumer<String>> consumer) {
            final _IndexHint indexHint;
            indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.USE_INDEX, consumer);
            if (indexHint != null) {
                addIndexHint(indexHint);
            }
            return (RR) this;
        }

        @Override
        public final RR ignoreIndex(String indexName) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName));
        }

        @Override
        public final RR ignoreIndex(String indexName1, String indexName2) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName1, indexName2));
        }

        @Override
        public final RR ignoreIndex(String indexName1, String indexName2, String indexName3) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, indexName1, indexName2, indexName3));
        }

        @Override
        public final RR ignoreIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, consumer));
        }

        @Override
        public final RR ignoreIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, space, consumer));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifIgnoreIndex(Consumer<Consumer<String>> consumer) {
            final _IndexHint indexHint;
            indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, consumer);
            if (indexHint != null) {
                addIndexHint(indexHint);
            }
            return (RR) this;
        }

        @Override
        public final RR forceIndex(String indexName) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName));
        }

        @Override
        public final RR forceIndex(String indexName1, String indexName2) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName1, indexName2));
        }

        @Override
        public final RR forceIndex(String indexName1, String indexName2, String indexName3) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, indexName1, indexName2, indexName3));
        }

        @Override
        public final RR forceIndex(Consumer<Clause._StaticStringSpaceClause> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, consumer));
        }

        @Override
        public final RR forceIndex(SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, space, consumer));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifForceIndex(Consumer<Consumer<String>> consumer) {
            final _IndexHint indexHint;
            indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, consumer);
            if (indexHint != null) {
                addIndexHint(indexHint);
            }
            return (RR) this;
        }

        @Override
        public final RR useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName));
        }

        @Override
        public final RR useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName1, indexName2));
        }

        @Override
        public final RR useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
        }

        @Override
        public final RR useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, consumer));
        }

        @Override
        public final RR useIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, space, consumer));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifUseIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
            final _IndexHint indexHint;
            indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.USE_INDEX, wordFor, purpose, consumer);
            if (indexHint != null) {
                addIndexHint(indexHint);
            }
            return (RR) this;
        }

        @Override
        public final RR ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName));
        }

        @Override
        public final RR ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName1, indexName2));
        }

        @Override
        public final RR ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
        }

        @Override
        public final RR ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, consumer));
        }

        @Override
        public final RR ignoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, space, consumer));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifIgnoreIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
            final _IndexHint indexHint;
            indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.IGNORE_INDEX, wordFor, purpose, consumer);
            if (indexHint != null) {
                addIndexHint(indexHint);
            }
            return (RR) this;
        }

        @Override
        public final RR forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName));
        }

        @Override
        public final RR forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName1, indexName2));
        }

        @Override
        public final RR forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, String indexName1, String indexName2, String indexName3) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, indexName1, indexName2, indexName3));
        }

        @Override
        public final RR forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Clause._StaticStringSpaceClause> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, consumer));
        }

        @Override
        public final RR forceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, SQLs.SymbolSpace space, Consumer<Consumer<String>> consumer) {
            return addIndexHint(MySQLSupports.indexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, space, consumer));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR ifForceIndex(SQLs.WordFor wordFor, SQLs.IndexHintPurpose purpose, Consumer<Consumer<String>> consumer) {
            final _IndexHint indexHint;
            indexHint = MySQLSupports.ifIndexHint(MySQLSupports.IndexHintCommand.FORCE_INDEX, wordFor, purpose, consumer);
            if (indexHint != null) {
                addIndexHint(indexHint);
            }
            return (RR) this;
        }

        @Nullable
        @Override
        public final SQLWords modifier() {
            //always null, MySQL table don't support modifier
            return null;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            List<_IndexHint> list = this.indexHintList;
            if (list == null) {
                this.indexHintList = list = Collections.emptyList();
            } else if (list instanceof ArrayList) {
                this.indexHintList = list = _Collections.unmodifiableList(list);
            }
            return list;
        }


        @SuppressWarnings("unchecked")
        private RR addIndexHint(final _IndexHint hint) {
            List<_IndexHint> list = this.indexHintList;
            if (list == null) {
                this.indexHintList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            list.add(hint);
            return (RR) this;
        }


    } // MySQLJoinClauseBlock

    static abstract class PartitionAsClause<R> implements Statement._AsClause<R>,
            MySQLBlockParams, MySQLStatement._PartitionAsClause<R> {

        final CriteriaContext context;

        final _JoinType joinType;

        final TableMeta<?> table;

        private List<String> partitionList;

        private String tableAlias;

        PartitionAsClause(CriteriaContext context, _JoinType joinType, TableMeta<?> table) {
            this.context = context;
            this.joinType = joinType;
            this.table = table;
        }


        @Override
        public final Statement._AsClause<R> partition(String first, String... rest) {
            this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
            return this;
        }

        @Override
        public final Statement._AsClause<R> partition(Consumer<Consumer<String>> consumer) {
            final List<String> list = _Collections.arrayList();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw MySQLUtils.partitionListIsEmpty(this.context);
            }
            this.partitionList = _Collections.unmodifiableList(list);
            return this;
        }

        @Override
        public final Statement._AsClause<R> ifPartition(Consumer<Consumer<String>> consumer) {
            final List<String> list = _Collections.arrayList();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.partitionList = _Collections.unmodifiableList(list);
            } else {
                this.partitionList = Collections.emptyList();
            }
            return this;
        }

        @Override
        public final R as(final @Nullable String alias) {
            if (this.tableAlias != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (alias == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return this.asEnd(this);
        }

        @Override
        public final _JoinType joinType() {
            return this.joinType;
        }

        @Override
        public final TabularItem tableItem() {
            return this.table;
        }

        @Override
        public final String alias() {
            final String tableAlias = this.tableAlias;
            if (tableAlias == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return tableAlias;
        }

        @Override
        public final SQLWords modifier() {
            return null;
        }

        @Override
        public final List<String> partitionList() {
            final List<String> list = this.partitionList;
            if (list == null || list instanceof ArrayList) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return list;
        }

        abstract R asEnd(MySQLBlockParams params);


    }//PartitionAsClause


    private static final class MySQLIndexHintClause<R> implements MySQLQuery._IndexPurposeBySpec<R> {

        private final CriteriaContext context;

        private final Function<_IndexHint, R> function;

        private final IndexHintCommand command;

        private IndexHintPurpose purpose;

        private MySQLIndexHintClause(CriteriaContext context, IndexHintCommand command, Function<_IndexHint, R> function) {
            this.context = context;
            this.command = command;
            this.function = function;
        }


        @Override
        public Statement._ParensStringClause<R> forJoin() {
            this.purpose = MySQLSupports.IndexHintPurpose.FOR_JOIN;
            return this;
        }

        @Override
        public Statement._ParensStringClause<R> forOrderBy() {
            this.purpose = MySQLSupports.IndexHintPurpose.FOR_ORDER_BY;
            return this;
        }

        @Override
        public Statement._ParensStringClause<R> forGroupBy() {
            this.purpose = MySQLSupports.IndexHintPurpose.FOR_GROUP_BY;
            return this;
        }

        @Override
        public R parens(String first, String... rest) {
            return this.stringConsumerEnd(ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public R parens(Consumer<Consumer<String>> consumer) {
            return this.stringConsumerEnd(CriteriaUtils.stringList(this.context, true, consumer));
        }


        private R stringConsumerEnd(final List<String> stringList) {
            final IndexHintPurpose hintPurpose = this.purpose;
            this.purpose = null;// clear for next.
            return this.function.apply(new MySQLIndexHint(this.command, null, stringList));
        }


    }//IndexHintClause


    enum IndexHintCommand implements SQLWords {

        USE_INDEX(" USE INDEX"),
        IGNORE_INDEX(" IGNORE INDEX"),
        FORCE_INDEX(" FORCE INDEX");

        private final String words;

        IndexHintCommand(String words) {
            this.words = words;
        }

        @Override
        public final String spaceRender() {
            return this.words;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }//IndexHintCommand

    @Deprecated
    private enum IndexHintPurpose implements SQLWords {

        FOR_ORDER_BY(" FOR ORDER BY"),
        FOR_GROUP_BY(" FOR GROUP BY"),
        FOR_JOIN(" FOR JOIN");

        private final String words;

        IndexHintPurpose(String words) {
            this.words = words;
        }

        @Override
        public final String spaceRender() {
            return this.words;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }// IndexHintPurpose


    private static final class MySQLIndexHint implements _IndexHint {

        private final IndexHintCommand command;

        private final SQLs.IndexHintPurpose purpose;

        private final List<String> indexNameList;

        /**
         * @param indexNameList unmodified list
         */
        private MySQLIndexHint(IndexHintCommand command, @Nullable SQLs.IndexHintPurpose purpose, List<String> indexNameList) {
            if (indexNameList.size() == 0) {
                throw ContextStack.clearStackAndCriteriaError("index hint index name list must not empty.");
            } else if (purpose != SQLs.JOIN && purpose != SQLs.ORDER_BY && purpose != SQLs.GROUP_BY) {
                throw CriteriaUtils.unknownWords(purpose);
            }
            this.command = command;
            this.purpose = purpose;
            this.indexNameList = indexNameList;
        }


        @Override
        public SQLWords command() {
            return this.command;
        }

        @Override
        public SQLWords purpose() {
            return this.purpose;
        }

        @Override
        public List<String> indexNameList() {
            return this.indexNameList;
        }


    }//MySQLIndexHint


    static final class MySQLWindowImpl extends SQLWindow<
            MySQLWindow._PartitionByCommaSpec,
            MySQLWindow._OrderByCommaSpec,
            MySQLWindow._FrameExtentSpec,
            Item,
            MySQLWindow._MySQLFrameBetweenClause,
            Item,
            MySQLWindow._FrameUnitSpaceSpec,
            Item>
            implements MySQLWindow,
            MySQLWindow._PartitionBySpec,
            MySQLWindow._PartitionByCommaSpec,
            MySQLWindow._OrderByCommaSpec,
            MySQLWindow._FrameUnitSpaceSpec,
            MySQLWindow._MySQLFrameBetweenClause {

        private MySQLWindowImpl(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        private MySQLWindowImpl(CriteriaContext context, @Nullable String existingWindowName) {
            super(context, existingWindowName);
        }


    }//MySQLWindowImpl


}
