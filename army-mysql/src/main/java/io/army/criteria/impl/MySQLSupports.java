package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLSupports extends CriteriaSupports {


    private MySQLSupports() {

    }



    static MySQLCtes mySQLCteBuilder(boolean recursive, CriteriaContext context) {
        return new MySQLCteBuilder(recursive, context);
    }

    static <R> MySQLQuery._IndexPurposeBySpec<R> indexHintClause(CriteriaContext context, IndexHintCommand command,
                                                                 Function<_IndexHint, R> function) {
        return new MySQLIndexHintClause<>(context, command, function);
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
        public DialectStatement._CommaClause<MySQLCtes> as(Function<MySQLQuery._WithSpec<DialectStatement._CommaClause<MySQLCtes>>, DialectStatement._CommaClause<MySQLCtes>> function) {
            return function.apply(MySQLQueries.subQuery(this.context, this::subQueryEnd));
        }

        private DialectStatement._CommaClause<MySQLCtes> subQueryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, query);
            return this.builder;
        }


    }//DynamicCteQueryParensSpec

    private static final class MySQLCteBuilder implements MySQLCtes, CteBuilder,
            DialectStatement._CommaClause<MySQLCtes> {

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


    private static abstract class MySQLFromClauseTableBlock<R extends Item>
            extends TabularBlocks.FromClauseTableBlock
            implements _MySQLTableBlock,
            MySQLStatement._QueryIndexHintSpec<R> {

        private final List<String> partitionList;

        final R clause;

        private List<_IndexHint> indexHintList;


        private MySQLFromClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
            super(joinType, table, alias);
            this.clause = clause;
            this.partitionList = Collections.emptyList();

        }

        private MySQLFromClauseTableBlock(MySQLBlockParams params, R clause) {
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
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.clause));
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


    static final class FromClauseForJoinTableBlock<R extends Item> extends MySQLFromClauseTableBlock<R>
            implements MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexForJoinSpec<Object>, R> {

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

    static final class FromClausePurposeTableBlock<R extends Item> extends MySQLFromClauseTableBlock<R>
            implements MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexPurposeBySpec<Object>, R> {

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
     * <p>
     * sub class must implements RR .
     * </p>
     *
     * @param <RR> sub interface of {@link  io.army.criteria.Statement._OnClause}
     * @param <OR> sub interface of {@link MySQLQuery._MySQLJoinClause}
     */
    static abstract class MySQLJoinClauseBlock<T extends Item, RR extends Item, OR extends Item>
            extends TabularBlocks.JoinClauseTableBlock<OR>
            implements _MySQLTableBlock,
            MySQLStatement._QueryIndexHintSpec<RR>,
            MySQLStatement._DynamicIndexHintClause<T, RR> {

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
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.getContext());
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
            this.partitionList = _ArrayUtils.unmodifiableListOf(first, rest);
            return this;
        }

        @Override
        public final Statement._AsClause<R> partition(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw MySQLUtils.partitionListIsEmpty(this.context);
            }
            this.partitionList = _Collections.unmodifiableList(list);
            return this;
        }

        @Override
        public final Statement._AsClause<R> ifPartition(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
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
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.castCriteriaApi(this.context);
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
            this.purpose = IndexHintPurpose.FOR_JOIN;
            return this;
        }

        @Override
        public Statement._ParensStringClause<R> forOrderBy() {
            this.purpose = IndexHintPurpose.FOR_ORDER_BY;
            return this;
        }

        @Override
        public Statement._ParensStringClause<R> forGroupBy() {
            this.purpose = IndexHintPurpose.FOR_GROUP_BY;
            return this;
        }

        @Override
        public R parens(String first, String... rest) {
            return this.stringConsumerEnd(_ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public R parens(Consumer<Consumer<String>> consumer) {
            return this.stringConsumerEnd(CriteriaUtils.stringList(this.context, true, consumer));
        }


        private R stringConsumerEnd(final List<String> stringList) {
            final IndexHintPurpose hintPurpose = this.purpose;
            this.purpose = null;// clear for next.
            return this.function.apply(new MySQLIndexHint(this.command, hintPurpose, stringList));
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

        private final IndexHintPurpose purpose;

        private final List<String> indexNameList;

        private MySQLIndexHint(IndexHintCommand command, @Nullable IndexHintPurpose purpose, List<String> indexNameList) {
            if (indexNameList.size() == 0) {
                throw new CriteriaException("index hint index name list must not empty.");
            }
            this.command = command;
            this.purpose = purpose;
            this.indexNameList = _Collections.asUnmodifiableList(indexNameList);
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


}
