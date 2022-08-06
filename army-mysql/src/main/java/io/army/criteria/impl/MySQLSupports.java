package io.army.criteria.impl;


import io.army.criteria.Statement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLQuery;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

abstract class MySQLSupports extends CriteriaSupports {


    private MySQLSupports() {

    }


    interface MySQLBlockParams extends TableBlock.BlockParams {

        List<String> partitionList();

    }


    static final class MySQLNoOnBlock<C, RR> extends TableBlock.NoOnTableBlock
            implements _MySQLTableBlock {


        private final List<String> partitionList;

        private final RR stmt;

        private MySQLQuery._QueryUseIndexClause<C, RR> indexClause;

        private List<MySQLIndexHint> indexHintList;

        MySQLNoOnBlock(_JoinType joinType, TableItem tableItem, String alias, RR stmt) {
            super(joinType, tableItem, alias);
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

        MySQLQuery._QueryUseIndexClause<C, RR> getUseIndexClause() {
            MySQLQuery._QueryUseIndexClause<C, RR> indexClause = this.indexClause;
            if (indexClause == null) {
                final CriteriaContext context;
                context = ((CriteriaContextSpec) this.stmt).getCriteriaContext();
                indexClause = new IndexHintClause<>(context, this::addIndexHint);
                this.indexClause = indexClause;
            }
            return indexClause;
        }

        private RR addIndexHint(final MySQLIndexHint indexHint) {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
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
    static abstract class MySQLOnBlock<C, RR, OR> extends OnClauseTableBlock<C, OR>
            implements _MySQLTableBlock, MySQLQuery._QueryUseIndexClause<C, RR> {

        private final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryUseIndexClause<C, RR> useIndexClause;


        MySQLOnBlock(_JoinType joinType, TableItem tableItem, String alias, OR stmt) {
            super(joinType, tableItem, alias, stmt);
            this.partitionList = Collections.emptyList();
        }

        MySQLOnBlock(MySQLBlockParams params, OR stmt) {
            super(params, stmt);
            this.partitionList = params.partitionList();
        }


        @Override
        public final MySQLQuery._IndexPurposeBySpec<C, RR> useIndex() {
            return this.getUseIndexClause().useIndex();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<C, RR> ignoreIndex() {
            return this.getUseIndexClause().ignoreIndex();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<C, RR> forceIndex() {
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


        private MySQLQuery._QueryUseIndexClause<C, RR> getUseIndexClause() {
            MySQLQuery._QueryUseIndexClause<C, RR> useIndexClause = this.useIndexClause;
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
                throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
            }
            indexHintList.add(indexHint);
            return (RR) this;
        }


    }//MySQLOnBlock


    static abstract class PartitionAsClause<C, AR> implements Statement._AsClause<AR>, MySQLBlockParams
            , MySQLQuery._PartitionClause<C, Statement._AsClause<AR>> {

        private final CriteriaContext criteriaContext;

        private final _JoinType joinType;

        private final TableMeta<?> table;


        private List<String> partitionList;

        private String tableAlias;

        PartitionAsClause(CriteriaContext criteriaContext, _JoinType joinType, TableMeta<?> table) {
            this.criteriaContext = criteriaContext;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public final Statement._LeftParenStringQuadraOptionalSpec<C, Statement._AsClause<AR>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public final AR as(final String alias) {
            if (!_StringUtils.hasText(alias)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "table alias must be non-empty.");
            }
            if (this.tableAlias != null || this.partitionList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.tableAlias = alias;
            return this.asEnd(this);
        }


        @Override
        public final _JoinType joinType() {
            return this.joinType;
        }

        @Override
        public final TableItem tableItem() {
            return this.table;
        }

        @Override
        public final String alias() {
            final String tableAlias = this.tableAlias;
            if (tableAlias == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return tableAlias;
        }

        @Override
        public final List<String> partitionList() {
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return partitionList;
        }

        abstract AR asEnd(MySQLBlockParams params);


        private Statement._AsClause<AR> partitionEnd(List<String> partitionList) {
            if (this.partitionList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.partitionList = partitionList;
            return this;
        }

    }//PartitionAsClause


    private static final class IndexHintClause<C, RR> extends CriteriaSupports.ParenStringConsumerClause<C, RR>
            implements MySQLQuery._IndexPurposeBySpec<C, RR>, MySQLQuery._QueryUseIndexClause<C, RR> {

        private final Function<MySQLIndexHint, RR> function;

        private MySQLIndexHint.Command command;

        private MySQLIndexHint.Purpose purpose;

        private IndexHintClause(CriteriaContext criteriaContext, Function<MySQLIndexHint, RR> function) {
            super(criteriaContext);
            this.function = function;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, RR> useIndex() {
            this.command = MySQLIndexHint.Command.USE_INDEX;
            return this;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, RR> ignoreIndex() {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
            return this;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, RR> forceIndex() {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
            return this;
        }

        @Override
        public Statement._LeftParenStringDualOptionalSpec<C, RR> forJoin() {
            this.purpose = MySQLIndexHint.Purpose.FOR_JOIN;
            return this;
        }

        @Override
        public Statement._LeftParenStringDualOptionalSpec<C, RR> forOrderBy() {
            this.purpose = MySQLIndexHint.Purpose.FOR_ORDER_BY;
            return this;
        }

        @Override
        public Statement._LeftParenStringDualOptionalSpec<C, RR> forGroupBy() {
            this.purpose = MySQLIndexHint.Purpose.FOR_GROUP_BY;
            return this;
        }

        @Override
        RR stringConsumerEnd(final List<String> stringList) {
            final MySQLIndexHint.Command command = this.command;
            if (command == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final MySQLIndexHint.Purpose purpose = this.purpose;
            //clear below for reuse this instance
            this.command = null;
            this.purpose = null;
            return this.function.apply(new MySQLIndexHint(command, purpose, stringList));
        }


    }//IndexHintClause


}
