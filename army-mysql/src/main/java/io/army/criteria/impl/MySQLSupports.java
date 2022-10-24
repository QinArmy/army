package io.army.criteria.impl;


import io.army.criteria.SQLWords;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCteBuilder;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

abstract class MySQLSupports extends CriteriaSupports {


    private MySQLSupports() {

    }

    static <RR> MySQLQuery._QueryIndexHintClause<RR> indexHintClause(CriteriaContext context
            , Function<MySQLIndexHint, RR> function) {
        return new IndexHintClause<>(context, function);
    }

    static _TableBlock createDynamicBlock(final _JoinType joinType, final DynamicBlock<?> block) {
        final _TableBlock tableBlock;
        if (block instanceof DynamicBlock.StandardDynamicBlock) {
            tableBlock = CriteriaUtils.createStandardDynamicBlock(joinType, block);
        } else if (block instanceof MySQLSupports.MySQLDynamicBlock) {
            tableBlock = new MySQLDynamicTableBlock(joinType, (MySQLSupports.MySQLDynamicBlock<?>) block);
        } else {
            throw ContextStack.castCriteriaApi(block.criteriaContext);
        }
        return tableBlock;
    }


    static MySQLCteBuilder mySQLCteBuilder(boolean recursive, CriteriaContext context) {
        return new MySQLCteBuilderImpl(recursive, context);
    }


    private static final class MySQLCteBuilderImpl
            extends ParenStringConsumerClause<MySQLQuery._DynamicCteAsClause>
            implements MySQLCteBuilder
            , MySQLQuery._DynamicCteLeftParenSpec
            , Statement._AsCteClause<MySQLCteBuilder> {

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
        public MySQLQuery._MinWithCteSpec<Statement._AsCteClause<MySQLCteBuilder>> as() {
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
            return MySQLQueries.subQuery(this.context, query -> {
                CriteriaUtils.createAndAddCte(context, cteName, aliasList, query);
                MySQLCteBuilderImpl.this.cteName = null; //clear for next cte
                MySQLCteBuilderImpl.this.columnAliasList = null; //clear for next cte
                return MySQLCteBuilderImpl.this;

            });
        }

        @Override
        public MySQLCteBuilder asCte() {
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

        private RR addIndexHint(final MySQLIndexHint indexHint) {
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
                useIndexClause = new IndexHintClause<>(this.getCriteriaContext(), this::addIndexHint);
                this.useIndexClause = useIndexClause;
            }
            return useIndexClause;
        }

        @SuppressWarnings("unchecked")
        private RR addIndexHint(final MySQLIndexHint indexHint) {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.getCriteriaContext());
            }
            indexHintList.add(indexHint);
            return (RR) this;
        }


    }//MySQLOnBlock


    static abstract class PartitionAsClause<AR> implements Statement._AsClause<AR>, MySQLBlockParams
            , MySQLQuery._PartitionAndAsClause<AR> {

        final CriteriaContext context;

        final _JoinType joinType;

        final TableMeta<?> table;


        private List<String> partitionList;

        String tableAlias;

        PartitionAsClause(CriteriaContext context, _JoinType joinType, TableMeta<?> table) {
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
        public final ItemWord itemWord() {
            //null,currently,MySQL table don't support LATERAL
            return null;
        }

        @Override
        public final String alias() {
            final String tableAlias = this.tableAlias;
            if (tableAlias == null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return tableAlias;
        }

        @Override
        public final List<String> partitionList() {
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return partitionList;
        }

        abstract AR asEnd(MySQLBlockParams params);


        private Statement._AsClause<AR> partitionEnd(List<String> partitionList) {
            if (this.partitionList != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.partitionList = partitionList;
            return this;
        }

    }//PartitionAsClause

//
//    static final class MySQLDynamicBlock<C> extends DynamicBlock<C> implements MySQLQuery._IfUseIndexOnSpec<C> {
//
//        final ItemWord itemWord;
//
//        final List<String> partitionList;
//
//        private List<MySQLIndexHint> indexHintList;
//
//        private MySQLQuery._QueryIndexHintClause<C, MySQLQuery._IfUseIndexOnSpec<C>> useIndexClause;
//
//        private MySQLDynamicBlock(@Nullable C criteria, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
//            super(criteria, tableItem, alias);
//            this.itemWord = itemWord;
//            this.partitionList = Collections.emptyList();
//        }
//
//        private MySQLDynamicBlock(TableMeta<?> table, String alias, List<String> partitionList
//                , CriteriaContext criteriaContext) {
//            super(table, alias, criteriaContext);
//            this.itemWord = null;
//            this.partitionList = partitionList;
//        }
//
//        @Override
//        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._IfUseIndexOnSpec<C>> useIndex() {
//            return this.getUseIndexClause().useIndex();
//        }
//
//        @Override
//        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._IfUseIndexOnSpec<C>> ignoreIndex() {
//            return this.getUseIndexClause().ignoreIndex();
//        }
//
//        @Override
//        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._IfUseIndexOnSpec<C>> forceIndex() {
//            return this.getUseIndexClause().forceIndex();
//        }
//
//
//        private MySQLQuery._QueryIndexHintClause<C, MySQLQuery._IfUseIndexOnSpec<C>> getUseIndexClause() {
//            MySQLQuery._QueryIndexHintClause<C, MySQLQuery._IfUseIndexOnSpec<C>> useIndexClause = this.useIndexClause;
//            if (useIndexClause == null) {
//                useIndexClause = new IndexHintClause<>(this.criteriaContext, this::addIndexHint);
//                this.useIndexClause = useIndexClause;
//            }
//            return useIndexClause;
//        }
//
//        private MySQLQuery._IfUseIndexOnSpec<C> addIndexHint(final MySQLIndexHint indexHint) {
//            List<MySQLIndexHint> indexHintList = this.indexHintList;
//            if (indexHintList == null) {
//                indexHintList = new ArrayList<>();
//                this.indexHintList = indexHintList;
//            } else if (!(indexHintList instanceof ArrayList)) {
//                throw ContextStack.castCriteriaApi(this.criteriaContext);
//            }
//            indexHintList.add(indexHint);
//            return this;
//        }
//
//
//    }//MySQLDynamicBlock


    private static final class MySQLDynamicTableBlock extends TableBlock.DynamicTableBlock
            implements _MySQLTableBlock {

        private final ItemWord itemWord;

        private final List<String> partitionList;

        private final List<MySQLIndexHint> indexHintList;


        private MySQLDynamicTableBlock(_JoinType joinType, MySQLDynamicBlock<?> block) {
            super(joinType, block);
            this.itemWord = block.itemWord;
            this.partitionList = block.partitionList;
            this.indexHintList = _CollectionUtils.safeList(block.indexHintList);
        }

        @Override
        public SQLWords itemWord() {
            return this.itemWord;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return this.indexHintList;
        }


    }//MySQLDynamicTableBlock


    private static final class IndexHintClause<RR> extends CriteriaSupports.ParenStringConsumerClause<RR>
            implements MySQLQuery._IndexPurposeBySpec<RR>, MySQLQuery._QueryIndexHintClause<RR> {

        private final Function<MySQLIndexHint, RR> function;

        private MySQLIndexHint.Command command;

        private MySQLIndexHint.Purpose purpose;

        private IndexHintClause(CriteriaContext criteriaContext, Function<MySQLIndexHint, RR> function) {
            super(criteriaContext);
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
        public Statement._LeftParenStringDualOptionalSpec<RR> forJoin() {
            this.purpose = MySQLIndexHint.Purpose.FOR_JOIN;
            return this;
        }

        @Override
        public Statement._LeftParenStringDualOptionalSpec<RR> forOrderBy() {
            this.purpose = MySQLIndexHint.Purpose.FOR_ORDER_BY;
            return this;
        }

        @Override
        public Statement._LeftParenStringDualOptionalSpec<RR> forGroupBy() {
            this.purpose = MySQLIndexHint.Purpose.FOR_GROUP_BY;
            return this;
        }

        @Override
        RR stringConsumerEnd(final List<String> stringList) {
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
