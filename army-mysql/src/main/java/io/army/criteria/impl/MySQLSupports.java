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


    static <C, AR> MySQLQuery._PartitionClause<C, Statement._AsClause<AR>> partitionAs(
            CriteriaContext criteriaContext, _JoinType joinType, TableMeta<?> table
            , Function<MySQLBlockParams, AR> function) {
        return new PartitionAsClause<>(criteriaContext, joinType, table, function);
    }


    interface MySQLBlockParams extends TableBlock.BlockParams {

        List<String> partitionList();

    }


    static final class MySQLNoOnBlock extends TableBlock.NoOnTableBlock
            implements _MySQLTableBlock {


        private List<? extends _IndexHint> indexHintList;

        private final List<String> partitionList;

        private MySQLNoOnBlock(MySQLBlockParams params) {
            super(params);
            this.partitionList = params.partitionList();
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            List<? extends _IndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            } else if (indexHintList instanceof ArrayList) {
                indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }

        // static MySQLQuery._QueryUseIndexClause<C, RR>


    }//MySQLNoOnBlock


    private static final class PartitionAsClause<C, AR> implements Statement._AsClause<AR>, MySQLBlockParams
            , MySQLQuery._PartitionClause<C, Statement._AsClause<AR>> {

        private final CriteriaContext criteriaContext;

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final Function<MySQLBlockParams, AR> function;

        private List<String> partitionList;

        private String tableAlias;

        private PartitionAsClause(CriteriaContext criteriaContext, _JoinType joinType, TableMeta<?> table
                , Function<MySQLBlockParams, AR> function) {
            this.criteriaContext = criteriaContext;
            this.joinType = joinType;
            this.table = table;
            this.function = function;
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, Statement._AsClause<AR>> partition() {
            if (this.partitionList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public AR as(final String alias) {
            if (!_StringUtils.hasText(alias)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "table alias must be non-empty.");
            }
            if (this.tableAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.tableAlias = alias;
            return function.apply(this);
        }


        @Override
        public _JoinType joinType() {
            return this.joinType;
        }

        @Override
        public TableItem tableItem() {
            return this.table;
        }

        @Override
        public String alias() {
            return this.tableAlias;
        }

        @Override
        public List<String> partitionList() {
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return partitionList;
        }


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
