package io.army.criteria.impl;


import io.army.criteria.SQLWords;
import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
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

    static <C, RR> MySQLQuery._QueryUseIndexClause<C, RR> indexHintClause(CriteriaContext context
            , Function<MySQLIndexHint, RR> function) {
        return new IndexHintClause<>(context, function);
    }

    static <C> MySQLQuery._IfPartitionAsClause<C> block(@Nullable C criteria, TableMeta<?> table) {
        return new IfPartitionAsClause<>(CriteriaContextStack.getCurrentContext(criteria), table);
    }

    static <C> MySQLQuery._IfUseIndexOnSpec<C> block(@Nullable C criteria, TableMeta<?> table, String tableAlias) {
        return new MySQLDynamicBlock<>(criteria, null, table, tableAlias);
    }

    static <C> MySQLQuery._IfUseIndexOnSpec<C> block(@Nullable C criteria, @Nullable ItemWord itemWord
            , SubQuery subQuery, String alias) {
        return new MySQLDynamicBlock<>(criteria, itemWord, subQuery, alias);
    }

    static _TableBlock createDynamicBlock(final _JoinType joinType, final DynamicBlock<?> block) {
        final _TableBlock tableBlock;
        if (block instanceof DynamicBlock.StandardDynamicBlock) {
            tableBlock = CriteriaUtils.createStandardDynamicBlock(joinType, block);
        } else if (block instanceof MySQLSupports.MySQLDynamicBlock) {
            tableBlock = new MySQLDynamicTableBlock(joinType, (MySQLSupports.MySQLDynamicBlock<?>) block);
        } else {
            throw CriteriaContextStack.castCriteriaApi(block.criteriaContext);
        }
        return tableBlock;
    }


    interface MySQLBlockParams extends TableBlock.DialectBlockParams {

        List<String> partitionList();

    }


    static final class MySQLNoOnBlock<C, RR> extends TableBlock.DialectNoOnTableBlock
            implements _MySQLTableBlock {

        private final List<String> partitionList;

        private final RR stmt;

        private MySQLQuery._QueryUseIndexClause<C, RR> indexClause;

        private List<MySQLIndexHint> indexHintList;


        MySQLNoOnBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias, RR stmt) {
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
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.stmt));
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
    static abstract class MySQLOnBlock<C, RR, OR> extends OnClauseTableBlock.OnItemTableBlock<C, OR>
            implements _MySQLTableBlock, MySQLQuery._QueryUseIndexClause<C, RR> {

        private final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryUseIndexClause<C, RR> useIndexClause;


        MySQLOnBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias, OR stmt) {
            super(joinType, itemWord, tableItem, alias, stmt);
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

        final _JoinType joinType;

        final TableMeta<?> table;


        private List<String> partitionList;

        String tableAlias;

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
        public final ItemWord itemWord() {
            //null,currently,MySQL table don't support LATERAL
            return null;
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


    static final class MySQLDynamicBlock<C> extends DynamicBlock<C> implements MySQLQuery._IfUseIndexOnSpec<C> {

        final ItemWord itemWord;

        final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryUseIndexClause<C, MySQLQuery._IfUseIndexOnSpec<C>> useIndexClause;

        private MySQLDynamicBlock(@Nullable C criteria, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
            super(criteria, tableItem, alias);
            this.itemWord = itemWord;
            this.partitionList = Collections.emptyList();
        }

        private MySQLDynamicBlock(TableMeta<?> table, String alias, List<String> partitionList
                , CriteriaContext criteriaContext) {
            super(table, alias, criteriaContext);
            this.itemWord = null;
            this.partitionList = partitionList;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._IfUseIndexOnSpec<C>> useIndex() {
            return this.getUseIndexClause().useIndex();
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._IfUseIndexOnSpec<C>> ignoreIndex() {
            return this.getUseIndexClause().ignoreIndex();
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._IfUseIndexOnSpec<C>> forceIndex() {
            return this.getUseIndexClause().forceIndex();
        }


        private MySQLQuery._QueryUseIndexClause<C, MySQLQuery._IfUseIndexOnSpec<C>> getUseIndexClause() {
            MySQLQuery._QueryUseIndexClause<C, MySQLQuery._IfUseIndexOnSpec<C>> useIndexClause = this.useIndexClause;
            if (useIndexClause == null) {
                useIndexClause = new IndexHintClause<>(this.criteriaContext, this::addIndexHint);
                this.useIndexClause = useIndexClause;
            }
            return useIndexClause;
        }

        private MySQLQuery._IfUseIndexOnSpec<C> addIndexHint(final MySQLIndexHint indexHint) {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            indexHintList.add(indexHint);
            return this;
        }


    }//MySQLDynamicBlock


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


    private static final class IfPartitionAsClause<C> implements MySQLQuery._IfPartitionAsClause<C>
            , Statement._AsClause<MySQLQuery._IfUseIndexOnSpec<C>> {

        private final CriteriaContext criteriaContext;

        private final TableMeta<?> table;

        private List<String> partitionList;

        private IfPartitionAsClause(CriteriaContext criteriaContext, TableMeta<?> table) {
            this.criteriaContext = criteriaContext;
            this.table = table;
        }

        @Override
        public MySQLQuery._IfUseIndexOnSpec<C> as(final String alias) {
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (!_StringUtils.hasText(alias)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::tableItemAliasNoText
                        , this.table);
            }
            return new MySQLDynamicBlock<>(this.table, alias, partitionList, this.criteriaContext);
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, Statement._AsClause<MySQLQuery._IfUseIndexOnSpec<C>>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        private Statement._AsClause<MySQLQuery._IfUseIndexOnSpec<C>> partitionEnd(List<String> partitionList) {
            if (this.partitionList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.partitionList = partitionList;
            return this;
        }


    }//IfPartitionAsClause


}
