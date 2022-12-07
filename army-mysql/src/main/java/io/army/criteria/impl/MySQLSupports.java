package io.army.criteria.impl;


import io.army.criteria.SQLWords;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLSupports extends CriteriaSupports {


    private MySQLSupports() {

    }

    static <RR> MySQLQuery._QueryIndexHintClause<RR> indexHintClause(CriteriaContext context
            , Function<MySQLIndexHint, RR> function) {
        return new IndexHintClause<>(context, function);
    }


    static MySQLCtes mySQLCteBuilder(boolean recursive, CriteriaContext context) {
        return new MySQLCteBuilderImpl(recursive, context);
    }


    private static final class MySQLCteBuilderImpl
            extends ParenStringConsumerClause<MySQLQuery._DynamicCteAsClause>
            implements MySQLCtes
            , MySQLQuery._DynamicCteLeftParenSpec
            , Statement._AsCteClause<MySQLCtes> {

        private final boolean recursive;

        private final CriteriaContext context;

        private String cteName;

        private List<String> columnAliasList;

        private MySQLCteBuilderImpl(boolean recursive, CriteriaContext context) {
            super(context);
            this.recursive = recursive;
            this.context = context;
            context.onBeforeWithClause(recursive);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public MySQLQuery._DynamicCteLeftParenSpec query(final @Nullable String cteName) {
            if (!_StringUtils.hasText(cteName)) {
                throw ContextStack.criteriaError(this.context, _Exceptions::cteNameNotText);
            } else if (this.cteName != null) {
                throw CriteriaUtils.cteNotEnd(this.context, this.cteName, cteName);
            }
            this.cteName = cteName;
            return this;
        }


        @Override
        public MySQLQuery._MinWithSpec<Statement._AsCteClause<MySQLCtes>> as() {
            final String cteName = this.cteName;
            if (cteName == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<String> aliasList = this.columnAliasList;
            final CriteriaContext context = this.context;
            context.onStartCte(cteName);
            if (aliasList != null) {
                context.onCteColumnAlias(cteName, aliasList);
            }
            return MySQLQueries.subQuery(null, this.context, query -> {
                CriteriaUtils.createAndAddCte(context, cteName, aliasList, query);
                MySQLCteBuilderImpl.this.cteName = null; //clear for next cte
                MySQLCteBuilderImpl.this.columnAliasList = null; //clear for next cte
                return MySQLCteBuilderImpl.this;

            });
        }

        @Override
        public MySQLCtes asCte() {
            return this;
        }

        @Override
        MySQLQuery._DynamicCteAsClause stringConsumerEnd(List<String> stringList) {
            if (this.cteName == null || this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = stringList;
            return this;
        }


    }//MySQLCteBuilderImpl


    interface MySQLBlockParams extends TableBlock.DialectBlockParams {

        List<String> partitionList();

    }


    static final class MySQLNoOnBlock<RR> extends TableBlock.NoOnModifierTableBlock
            implements _MySQLTableBlock {

        private final List<String> partitionList;

        private final RR stmt;

        private MySQLQuery._QueryIndexHintClause<RR> indexClause;

        private List<MySQLIndexHint> indexHintList;


        MySQLNoOnBlock(_JoinType joinType, @Nullable SQLWords itemWord, TabularItem tableItem, String alias, RR stmt) {
            super(joinType, itemWord, tableItem, alias);
            this.partitionList = Collections.emptyList();
            this.stmt = stmt;
        }

        MySQLNoOnBlock(MySQLBlockParams params, RR stmt) {
            super(params);
            this.partitionList = params.partitionList();
            this.stmt = stmt;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            } else if (indexHintList instanceof ArrayList) {
                indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }

        MySQLQuery._QueryIndexHintClause<RR> getUseIndexClause() {
            MySQLQuery._QueryIndexHintClause<RR> indexClause = this.indexClause;
            if (indexClause == null) {
                final CriteriaContext context;
                context = ((CriteriaContextSpec) this.stmt).getContext();
                indexClause = new IndexHintClause<>(context, this::addIndexHint);
                this.indexClause = indexClause;
            }
            return indexClause;
        }

        private RR addIndexHint(final @Nullable MySQLIndexHint indexHint) {
            if (indexHint == null) {
                return this.stmt;
            }
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.stmt));
            }
            indexHintList.add(indexHint);
            return this.stmt;
        }


    }//MySQLNoOnBlock


    /**
     * <p>
     * sub class must implements RR .
     * </p>
     *
     * @param <RR> sub interface of {@link  io.army.criteria.Statement._OnClause}
     * @param <OR> sub interface of {@link MySQLQuery._MySQLJoinClause}
     */
    static abstract class MySQLOnBlock<RR, OR> extends OnClauseTableBlock.OnItemTableBlock<OR>
            implements _MySQLTableBlock, MySQLQuery._QueryIndexHintClause<RR> {

        private final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryIndexHintClause<RR> useIndexClause;


        MySQLOnBlock(_JoinType joinType, @Nullable SQLWords modifier, TabularItem tableItem, String alias, OR stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
            this.partitionList = Collections.emptyList();
        }

        MySQLOnBlock(MySQLBlockParams params, OR stmt) {
            super(params, stmt);
            this.partitionList = params.partitionList();
        }


        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> useIndex() {
            return this.getUseIndexClause().useIndex();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> ignoreIndex() {
            return this.getUseIndexClause().ignoreIndex();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> forceIndex() {
            return this.getUseIndexClause().forceIndex();
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList instanceof ArrayList) {
                indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            } else if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }


        private MySQLQuery._QueryIndexHintClause<RR> getUseIndexClause() {
            MySQLQuery._QueryIndexHintClause<RR> useIndexClause = this.useIndexClause;
            if (useIndexClause == null) {
                useIndexClause = new IndexHintClause<>(this.getContext(), this::addIndexHint);
                this.useIndexClause = useIndexClause;
            }
            return useIndexClause;
        }

        @SuppressWarnings("unchecked")
        private RR addIndexHint(final @Nullable MySQLIndexHint indexHint) {
            if (indexHint == null) {
                return (RR) this;
            }
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            indexHintList.add(indexHint);
            return (RR) this;
        }


    }//MySQLOnBlock

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
            this.partitionList = _CollectionUtils.unmodifiableList(list);
            return this;
        }

        @Override
        public final Statement._AsClause<R> ifPartition(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.partitionList = _CollectionUtils.unmodifiableList(list);
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


    static abstract class PartitionAsClause_0<AR> implements Statement._AsClause<AR>, MySQLBlockParams
            , MySQLStatement._PartitionAndAsClause_0<AR> {

        final CriteriaContext context;

        final _JoinType joinType;

        final TableMeta<?> table;


        private List<String> partitionList;

        String tableAlias;

        PartitionAsClause_0(CriteriaContext context, _JoinType joinType, TableMeta<?> table) {
            this.context = context;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public final Statement._LeftParenStringQuadraOptionalSpec<Statement._AsClause<AR>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public final AR as(final String alias) {
            if (!_StringUtils.hasText(alias)) {
                throw ContextStack.criteriaError(this.context, "table alias must be non-empty.");
            }
            if (this.tableAlias != null || this.partitionList == null) {
                throw ContextStack.castCriteriaApi(this.context);
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
        public final SQLWords modifier() {
            //null,currently,MySQL table don't support LATERAL
            return null;
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
        public final List<String> partitionList() {
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return partitionList;
        }

        abstract AR asEnd(MySQLBlockParams params);


        private Statement._AsClause<AR> partitionEnd(List<String> partitionList) {
            if (this.partitionList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.partitionList = partitionList;
            return this;
        }

    }//PartitionAsClause


    private static final class IndexHintClause<RR> implements MySQLQuery._IndexPurposeBySpec<RR>,
            MySQLQuery._QueryIndexHintClause<RR> {

        private final CriteriaContext context;

        private final Function<MySQLIndexHint, RR> function;

        private MySQLIndexHint.Command command;

        private MySQLIndexHint.Purpose purpose;

        private IndexHintClause(CriteriaContext context, Function<MySQLIndexHint, RR> function) {
            this.context = context;
            this.function = function;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<RR> useIndex() {
            this.command = MySQLIndexHint.Command.USE_INDEX;
            return this;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<RR> ignoreIndex() {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
            return this;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<RR> forceIndex() {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
            return this;
        }

        @Override
        public Statement._ParensStringClause<RR> forJoin() {
            this.purpose = MySQLIndexHint.Purpose.FOR_JOIN;
            return this;
        }

        @Override
        public Statement._ParensStringClause<RR> forOrderBy() {
            this.purpose = MySQLIndexHint.Purpose.FOR_ORDER_BY;
            return this;
        }

        @Override
        public Statement._ParensStringClause<RR> forGroupBy() {
            this.purpose = MySQLIndexHint.Purpose.FOR_GROUP_BY;
            return this;
        }


        @Override
        public RR parens(String first, String... rest) {
            return this.stringConsumerEnd(_ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public RR parens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                throw ContextStack.criteriaError(this.context, "You don't add any index name");
            }
            return this.stringConsumerEnd(_CollectionUtils.unmodifiableList(list));
        }

        @Override
        public RR ifParens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.stringConsumerEnd(_CollectionUtils.unmodifiableList(list));
            } else {
                this.command = null;
                this.purpose = null;
            }
            return this.function.apply(null);
        }


        private RR stringConsumerEnd(final List<String> stringList) {
            final MySQLIndexHint.Command command = this.command;
            if (command == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final MySQLIndexHint.Purpose purpose = this.purpose;
            //clear below for reuse this instance
            this.command = null;
            this.purpose = null;
            return this.function.apply(new MySQLIndexHint(command, purpose, stringList));
        }


    }//IndexHintClause


}
