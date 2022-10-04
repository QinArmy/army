package io.army.criteria.impl;

import io.army.criteria.NestedItems;
import io.army.criteria.SQLWords;
import io.army.criteria.SubQuery;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._DialectTableBlock;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p>
 * This class is the implementation of {@link NestedItems} for MySQL.
 * </p>
 *
 * @see MySQLs#nestedItems()
 * @see MySQLs#nestedItems(Object)
 * @since 1.0
 */
final class MySQLNestedItems<C> extends JoinableClause.LeftParenNestedItem<
        C,
        MySQLQuery._NestedUseIndexJoinSpec<C>,
        MySQLQuery._NestedJoinSpec<C>,
        MySQLQuery._NestedPartitionJoinClause<C>,
        MySQLQuery._NestedJoinSpec<C>,
        MySQLQuery._NestedUseIndexOnSpec<C>,
        MySQLQuery._NestedOnSpec<C>,
        MySQLQuery._NestedPartitionOnClause<C>>
        implements MySQLQuery._MySQLNestedLeftParenClause<C>
        , MySQLQuery._NestedUseIndexJoinSpec<C> {

    static <C> MySQLNestedItems<C> create(final @Nullable C criteria) {
        final CriteriaContext currentContext;
        currentContext = ContextStack.peek();
        if (criteria != null && currentContext.criteria() != criteria) {
            throw CriteriaUtils.criteriaNotMatch(currentContext);
        }
        return new MySQLNestedItems<>(currentContext);
    }


    private MySQLSupports.MySQLNoOnBlock<C, MySQLQuery._NestedUseIndexJoinSpec<C>> noOnBlock;


    private MySQLNestedItems(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._NestedUseIndexJoinSpec<C>> useIndex() {
        return this.getUseIndexClause().useIndex();
    }

    @Override
    public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._NestedUseIndexJoinSpec<C>> ignoreIndex() {
        return this.getUseIndexClause().ignoreIndex();
    }

    @Override
    public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._NestedUseIndexJoinSpec<C>> forceIndex() {
        return this.getUseIndexClause().forceIndex();
    }


    @Override
    public MySQLQuery._NestedPartitionJoinClause<C> createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new PartitionJonClause<>(joinType, table, this);
    }

    @Override
    public _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        final MySQLSupports.MySQLNoOnBlock<C, MySQLQuery._NestedUseIndexJoinSpec<C>> noOnBlock;
        noOnBlock = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, this);
        this.noOnBlock = noOnBlock; //update current no on block
        return noOnBlock;
    }

    @Override
    public _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (!(itemWord == null || (itemWord == ItemWord.LATERAL && tableItem instanceof SubQuery))) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new TableBlock.DialectNoOnTableBlock(joinType, itemWord, tableItem, alias);
    }

    @Override
    public _TableBlock createDynamicBlock(_JoinType joinType, DynamicBlock<?> block) {
        return MySQLSupports.createDynamicBlock(joinType, block);
    }

    @Override
    public MySQLQuery._NestedPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new PartitionOnAsClause<>(joinType, table, this);
    }

    @Override
    public MySQLQuery._NestedUseIndexOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new TableOnClauseBlock<>(this, joinType, null, table, tableAlias);
    }

    @Override
    public MySQLQuery._NestedOnSpec<C> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (!(itemWord == null || (itemWord == ItemWord.LATERAL && tableItem instanceof SubQuery))) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new OnClauseBlock<>(this, joinType, itemWord, tableItem, alias);
    }


    private MySQLQuery._QueryIndexHintClause<C, MySQLQuery._NestedUseIndexJoinSpec<C>> getUseIndexClause() {
        final MySQLSupports.MySQLNoOnBlock<C, MySQLQuery._NestedUseIndexJoinSpec<C>> noOnBlock = this.noOnBlock;
        if (this.getFirstBlock() != noOnBlock) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return noOnBlock.getUseIndexClause();
    }


    private static final class PartitionJonClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLQuery._NestedUseIndexJoinSpec<C>>
            implements MySQLQuery._NestedPartitionJoinClause<C> {

        private final MySQLNestedItems<C> nestedClause;

        private PartitionJonClause(_JoinType joinType, TableMeta<?> table, MySQLNestedItems<C> nestedClause) {
            super(nestedClause.criteriaContext, joinType, table);
            this.nestedClause = nestedClause;
        }

        @Override
        MySQLQuery._NestedUseIndexJoinSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLNestedItems<C> nestedClause = this.nestedClause;

            final MySQLSupports.MySQLNoOnBlock<C, MySQLQuery._NestedUseIndexJoinSpec<C>> noOnBlock;
            noOnBlock = new MySQLSupports.MySQLNoOnBlock<>(params, nestedClause);
            nestedClause.noOnBlock = noOnBlock; //update current no on block

            nestedClause.blockConsumer.accept(noOnBlock);
            return nestedClause;
        }


    }//PartitionJonClause


    private static class OnClauseBlock<C> extends JoinableClause.OnOrJoinBlock<
            C,
            MySQLQuery._NestedUseIndexJoinSpec<C>,
            MySQLQuery._NestedJoinSpec<C>,
            MySQLQuery._NestedPartitionJoinClause<C>,
            MySQLQuery._NestedJoinSpec<C>,
            MySQLQuery._NestedUseIndexOnSpec<C>,
            MySQLQuery._NestedOnSpec<C>,
            MySQLQuery._NestedPartitionOnClause<C>>
            implements MySQLQuery._NestedOnSpec<C>, _DialectTableBlock {

        private final ItemWord itemWord;


        private OnClauseBlock(MySQLNestedItems<C> clause, _JoinType joinType, @Nullable ItemWord itemWord
                , TabularItem tableItem, String alias) {
            super(clause, joinType, tableItem, alias);
            this.itemWord = itemWord;
        }

        private OnClauseBlock(MySQLNestedItems<C> clause, MySQLSupports.MySQLBlockParams params) {
            super(clause, params);
            this.itemWord = params.itemWord();
        }

        @Override
        public final SQLWords itemWord() {
            return this.itemWord;
        }


    }//OnClauseBlock

    private static final class TableOnClauseBlock<C> extends OnClauseBlock<C>
            implements MySQLQuery._NestedUseIndexOnSpec<C>, _MySQLTableBlock {

        private final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryIndexHintClause<C, MySQLQuery._NestedUseIndexOnSpec<C>> useIndexClause;

        private TableOnClauseBlock(MySQLNestedItems<C> clause, _JoinType joinType, @Nullable ItemWord itemWord
                , TabularItem tableItem, String alias) {
            super(clause, joinType, itemWord, tableItem, alias);
            this.partitionList = Collections.emptyList();
        }

        private TableOnClauseBlock(MySQLNestedItems<C> clause, MySQLSupports.MySQLBlockParams params) {
            super(clause, params);
            this.partitionList = params.partitionList();
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._NestedUseIndexOnSpec<C>> useIndex() {
            return this.getUseIndexClause().useIndex();
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._NestedUseIndexOnSpec<C>> ignoreIndex() {
            return this.getUseIndexClause().ignoreIndex();
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<C, MySQLQuery._NestedUseIndexOnSpec<C>> forceIndex() {
            return this.getUseIndexClause().forceIndex();
        }


        private MySQLQuery._QueryIndexHintClause<C, MySQLQuery._NestedUseIndexOnSpec<C>> getUseIndexClause() {
            MySQLQuery._QueryIndexHintClause<C, MySQLQuery._NestedUseIndexOnSpec<C>> useIndexClause = this.useIndexClause;
            if (useIndexClause == null) {
                useIndexClause = MySQLSupports.indexHintClause(this.context, this::addIndexHint);
                this.useIndexClause = useIndexClause;
            }
            return useIndexClause;
        }


        private MySQLQuery._NestedUseIndexOnSpec<C> addIndexHint(final MySQLIndexHint indexHint) {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            } else if (!(indexHintList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            indexHintList.add(indexHint);
            return this;
        }


        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            List<MySQLIndexHint> hintIndexList = this.indexHintList;
            if (hintIndexList == null) {
                hintIndexList = Collections.emptyList();
                this.indexHintList = hintIndexList;
            } else if (hintIndexList instanceof ArrayList) {
                hintIndexList = _CollectionUtils.unmodifiableList(hintIndexList);
                this.indexHintList = hintIndexList;
            }
            return hintIndexList;
        }

    }//TableOnClauseBlock


    private static final class PartitionOnAsClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLQuery._NestedUseIndexOnSpec<C>>
            implements MySQLQuery._NestedPartitionOnClause<C> {

        private final MySQLNestedItems<C> nestedClause;

        private PartitionOnAsClause(_JoinType joinType, TableMeta<?> table, MySQLNestedItems<C> nestedClause) {
            super(nestedClause.criteriaContext, joinType, table);
            this.nestedClause = nestedClause;
        }

        @Override
        MySQLQuery._NestedUseIndexOnSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLNestedItems<C> nestedClause = this.nestedClause;

            final TableOnClauseBlock<C> block;
            block = new TableOnClauseBlock<>(nestedClause, params);
            nestedClause.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnAsClause


}
