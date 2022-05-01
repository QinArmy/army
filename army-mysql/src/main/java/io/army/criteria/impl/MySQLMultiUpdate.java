package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql.*;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.criteria.mysql.NestedJoin;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SimpleUpdate} ,MySQL multi update api implementation</li>
 *         <li>{@link BatchUpdate} ,MySQL batch multi update api implementation</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiUpdate<C, WE, UP, UT, US, JT, JS, JP, WR, WA, SR, IR>
        extends WithCteMultiUpdate<C, WE, JT, JS, JP, WR, WA, SR>
        implements MySQLUpdate.MultiUpdateClause<C, UP, UT, US>, MySQLQuery._IndexHintClause<C, IR, UT>
        , MySQLQuery._IndexJoinClause<C, UT>, MySQLQuery._MySQLJoinClause<C, JT, JS, JP>, MySQLUpdate
        , _MySQLMultiUpdate {


    static <C> MultiUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> BatchMultiUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    static <C> WithAndMultiUpdateSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndUpdate<>(criteria);
    }

    static <C> BatchWithAndMultiUpdateSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndUpdate<>(criteria);
    }

    private List<_MySQLHint> hintList;

    private Set<MySQLWords> modifierSet;

    private JP noActionPartitionBlock;

    private MySQLIndexHint.Command command;

    private MySQLMultiUpdate(@Nullable C criteria) {
        super(CriteriaContexts.multiDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
            , TableMeta<? extends IDomain> table) {
        this.hintList = MySQLUtils.asHintList(hints.get());
        this.modifierSet = MySQLUtils.asUpdateModifierList(modifiers);
        return this.createPartitionJoinBlock(table);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
            , TableMeta<? extends IDomain> table, String tableAlias) {
        this.hintList = MySQLUtils.asHintList(hints.get());
        this.modifierSet = MySQLUtils.asUpdateModifierList(modifiers);
        this.criteriaContext.onBlockWithoutOnClause(new FirstBlock(table, tableAlias));
        return (UT) this;
    }

    @Override
    public final UP update(TableMeta<? extends IDomain> table) {
        return this.createPartitionJoinBlock(table);
    }

    @Override
    public final UT update(TableMeta<? extends IDomain> table, String tableAlias) {
        this.criteriaContext.onBlockWithoutOnClause(new FirstBlock(table, tableAlias));
        return (UT) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
            , Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(hints.get());
        this.modifierSet = MySQLUtils.asUpdateModifierList(modifiers);
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<T> supplier, String alias) {
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
            , Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(hints.get());
        this.modifierSet = MySQLUtils.asUpdateModifierList(modifiers);
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Function<C, T> function, String alias) {
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(function.apply(this.criteria), alias));
        return (US) this;
    }

    /*################################## blow IndexHintClause method ##################################*/

    @Override
    public final IR useIndex() {
        this.command = MySQLIndexHint.Command.USER_INDEX;
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        this.command = MySQLIndexHint.Command.FORCE_INDEX;
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.useIndex();
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.ignoreIndex();
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.forceIndex();
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final UT useIndex(List<String> indexList) {
        return this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
    }

    @Override
    public final UT ignoreIndex(List<String> indexList) {
        return this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
    }

    @Override
    public final UT forceIndex(List<String> indexList) {
        return this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
    }

    @Override
    public final UT ifUseIndex(Function<C, List<String>> function) {
        if (this.command != null) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (!_CollectionUtils.isEmpty(indexList)) {
                this.useIndex(indexList);
            }
        }
        return (UT) this;
    }

    @Override
    public final UT ifIgnoreIndex(Function<C, List<String>> function) {
        if (this.command != null) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (!_CollectionUtils.isEmpty(indexList)) {
                this.ignoreIndex(indexList);
            }
        }
        return (UT) this;
    }

    @Override
    public final UT ifForceIndex(Function<C, List<String>> function) {
        if (this.command != null) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (!_CollectionUtils.isEmpty(indexList)) {
                this.forceIndex(indexList);
            }
        }
        return (UT) this;
    }

    /*################################## blow IndexJoinClause method ##################################*/

    @Override
    public final UT forJoin(List<String> indexList) {
        final MySQLIndexHint.Command command = this.command;
        if (command != null) {
            this.command = null;
            this.addIndexHint(command, true, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT forJoin(Function<C, List<String>> function) {
        final MySQLIndexHint.Command command = this.command;
        if (command != null) {
            this.command = null;
            this.addIndexHint(command, true, function.apply(this.criteria));
        }
        return (UT) this;
    }


    @Override
    public final List<_MySQLHint> hintList() {
        return this.hintList;
    }

    @Override
    public final Set<MySQLWords> modifierList() {
        return this.modifierSet;
    }


    /*################################## blow package template method ##################################*/


    abstract JP createNoActionPartitionBlock();

    abstract UP createPartitionJoinBlock(TableMeta<?> table);

    @Override
    final void doOnAsUpdate() {
        this.noActionPartitionBlock = null;
        if (_CollectionUtils.isEmpty(this.hintList)) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierSet == null) {
            this.modifierSet = Collections.emptySet();
        }

        if (this instanceof BatchUpdate) {
            if (_CollectionUtils.isEmpty(((BatchUpdate<C, WE>) this).wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
        }

        if (this instanceof SimpleWithAndUpdate) {
            final SimpleWithAndUpdate<C> update = (SimpleWithAndUpdate<C>) this;
            if (update.cteList == null) {
                update.cteList = Collections.emptyList();
            }

        } else if (this instanceof BatchWithAndUpdate) {
            final BatchWithAndUpdate<C> update = (BatchWithAndUpdate<C>) this;
            if (update.cteList == null) {
                update.cteList = Collections.emptyList();
            }
        }


    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierSet = null;
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C, WE>) this).wrapperList = null;
        }

        if (this instanceof SimpleWithAndUpdate) {
            ((SimpleWithAndUpdate<C>) this).cteList = null;
        } else if (this instanceof BatchWithAndUpdate) {
            ((BatchWithAndUpdate<C>) this).cteList = null;
        }

    }

    @Override
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
    }



    /*################################## blow private method ##################################*/

    @Override
    final JP ifJointTableBeforeAs(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final JP block;
        if (predicate.test(this.criteria)) {
            block = createBlockBeforeAs(joinType, table);
        } else {
            JP noActionPartitionBlock = this.noActionPartitionBlock;
            if (noActionPartitionBlock == null) {
                noActionPartitionBlock = createNoActionPartitionBlock();
                this.noActionPartitionBlock = noActionPartitionBlock;
            }
            block = noActionPartitionBlock;
        }
        return block;
    }


    private UT addIndexHint(final MySQLIndexHint.Command command, final boolean forJoin
            , final List<String> indexNames) {

        if (_CollectionUtils.isEmpty(indexNames)) {
            throw new CriteriaException("index hint clause index name list must not empty.");
        }
        final _TableBlock block;
        block = this.criteriaContext.lastTableBlockWithoutOnClause();
        if (!(block instanceof FirstBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        final FirstBlock firstBlock = (FirstBlock) block;
        List<MySQLIndexHint> indexHintList = firstBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            firstBlock.indexHintList = indexHintList;
        }
        final MySQLIndexHint.Purpose purpose;
        if (forJoin) {
            purpose = MySQLIndexHint.Purpose.FOR_JOIN;
        } else {
            purpose = null;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));
        return (UT) this;
    }


    /*################################## blow inner class  ##################################*/


    private static final class FirstBlock extends TableBlock implements _MySQLTableBlock {


        private final String alias;

        private final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;

        private FirstBlock(TableMeta<?> table, String alias) {
            super(_JoinType.NONE, table);
            this.alias = alias;
            this.partitionList = Collections.emptyList();
        }

        private FirstBlock(TableMeta<?> table, String alias, List<String> partitionList) {
            super(_JoinType.NONE, table);
            this.alias = alias;
            switch (partitionList.size()) {
                case 0:
                case 1:
                    this.partitionList = partitionList;
                    break;
                default:
                    this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
        }


        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public List<_Predicate> predicates() {
            return Collections.emptyList();
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
            } else {
                indexHintList = Collections.unmodifiableList(indexHintList);
            }
            return indexHintList;
        }


    }//FirstBlock


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static class SimpleUpdate<C, WE> extends MySQLMultiUpdate<
            C,
            WE,
            MySQLUpdate.MultiPartitionJoinSpec<C>,
            MySQLUpdate.IndexHintJoinSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiOnSpec<C>,
            MySQLUpdate.MultiPartitionOnSpec<C>,
            _UpdateSpec,
            MySQLUpdate.MultiWhereAndSpec<C>,
            MySQLUpdate.MultiWhereSpec<C>,
            MySQLUpdate.IndexJoinJoinSpec<C>>
            implements MySQLUpdate.MultiWhereAndSpec<C>, MySQLUpdate.MultiWhereSpec<C>
            , MySQLUpdate.IndexHintJoinSpec<C>, MySQLUpdate.MultiUpdateSpec<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        final MultiPartitionJoinSpec<C> createPartitionJoinBlock(TableMeta<?> table) {
            return new SimplePartitionJoinSpec<>(table, this);
        }

        @Override
        final MultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new SimpleIndexHintOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        final MultiOnSpec<C> createItemBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return new SimpleOnBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        final MultiPartitionOnSpec<C> createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
            return new SimplePartitionOnBlock<>(joinType, table, this);
        }


        @Override
        final MultiIndexHintOnSpec<C> createNoActionTableBlock() {
            return new SimpleNoActionIndexHintOnBlock<>(this);
        }

        @Override
        final MultiOnSpec<C> createNoActionOnBlock() {
            return new SimpleNoActionOnBlock<>(this);
        }

        @Override
        final MultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new SimpleNoActionPartitionBlock<>(this);
        }


    }// SimpleMultiUpdate

    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     *
     * @see #batch(Object)
     */
    private static class BatchUpdate<C, WE> extends MySQLMultiUpdate<
            C,
            WE,
            MySQLUpdate.BatchMultiPartitionJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintJoinSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiOnSpec<C>,
            MySQLUpdate.BatchMultiPartitionOnSpec<C>,
            Statement.BatchParamClause<C, _UpdateSpec>,
            MySQLUpdate.BatchMultiWhereAndSpec<C>,
            MySQLUpdate.BatchMultiWhereSpec<C>,
            MySQLUpdate.BatchIndexJoinJoinSpec<C>>
            implements MySQLUpdate.BatchMultiWhereAndSpec<C>, MySQLUpdate.BatchMultiWhereSpec<C>
            , MySQLUpdate.BatchMultiIndexHintJoinSpec<C>, MySQLUpdate.BatchIndexJoinJoinSpec<C>
            , MySQLUpdate.BatchMultiUpdateSpec<C>, _BatchDml {

        private List<?> wrapperList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final _UpdateSpec paramList(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramList(beanList);
            return this;
        }

        @Override
        public final _UpdateSpec paramList(Supplier<List<?>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public final _UpdateSpec paramList(Function<C, List<?>> function) {
            return this.paramList(function.apply(this.criteria));
        }

        @Override
        public final _UpdateSpec paramList(Function<String, Object> function, String keyName) {
            this.wrapperList = CriteriaUtils.paramList(function, keyName);
            return this;
        }

        @Override
        final BatchMultiPartitionJoinSpec<C> createPartitionJoinBlock(TableMeta<?> table) {
            return new BatchPartitionJoinSpec<>(table, this);
        }

        @Override
        final BatchMultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new BatchIndexHintOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        final BatchMultiOnSpec<C> createItemBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return new BatchOnBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        final BatchMultiPartitionOnSpec<C> createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
            return new BatchPartitionOnBlock<>(joinType, table, this);
        }

        @Override
        final BatchMultiIndexHintOnSpec<C> createNoActionTableBlock() {
            return new BatchNoActionIndexHintOnBlock<>(this);
        }

        @Override
        final BatchMultiOnSpec<C> createNoActionOnBlock() {
            return new BatchNoActionOnSpec<>(this);
        }

        @Override
        final BatchMultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new BatchNoActionPartitionOnBlock<>(this);
        }

        @Override
        public final List<?> paramList() {
            return this.wrapperList;
        }


    }// BatchUpdate

    private static final class SimpleWithAndUpdate<C> extends SimpleUpdate<C, MySQLUpdate.MultiUpdateSpec<C>>
            implements WithAndMultiUpdateSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleWithAndUpdate(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

    }//SimpleWithAndUpdate


    private static final class BatchWithAndUpdate<C> extends BatchUpdate<C, MySQLUpdate.BatchMultiUpdateSpec<C>>
            implements BatchWithAndMultiUpdateSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private BatchWithAndUpdate(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

    }//BatchWithAndUpdate


    /**
     * @see SimpleUpdate#createPartitionJoinBlock(TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C, WE>
            extends MySQLPartitionClause<C, _AsClause<IndexHintJoinSpec<C>>>
            implements MultiPartitionJoinSpec<C>, _AsClause<IndexHintJoinSpec<C>> {

        private final TableMeta<?> table;

        private final SimpleUpdate<C, WE> update;

        private SimplePartitionJoinSpec(TableMeta<?> table, SimpleUpdate<C, WE> update) {
            super(update.criteria);
            this.table = table;
            this.update = update;
        }

        @Override
        public IndexHintJoinSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final _TableBlock block;
            if (partitionList == null) {
                block = TableBlock.noneBlock(this.table, tableAlias);
            } else {
                block = new FirstBlock(table, tableAlias, partitionList);
            }
            this.update.criteriaContext.onBlockWithoutOnClause(block);
            return this.update;
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchUpdate#createPartitionJoinBlock(TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C, WE>
            extends MySQLPartitionClause<C, _AsClause<BatchMultiIndexHintJoinSpec<C>>>
            implements BatchMultiPartitionJoinSpec<C>, _AsClause<BatchMultiIndexHintJoinSpec<C>> {

        private final TableMeta<?> table;

        private final BatchUpdate<C, WE> update;

        private BatchPartitionJoinSpec(TableMeta<?> table, BatchUpdate<C, WE> update) {
            super(update.criteria);
            this.table = table;
            this.update = update;
        }

        @Override
        public BatchMultiIndexHintJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final _TableBlock block;
            if (partitionList == null) {
                block = TableBlock.noneBlock(this.table, alias);
            } else {
                block = new FirstBlock(table, alias, partitionList);
            }
            this.update.criteriaContext.onBlockWithoutOnClause(block);
            return this.update;
        }
    }// BatchPartitionJoinSpec


    /**
     * @see SimpleUpdate#createItemBlock(_JoinType, TableItem, String)
     */
    private static final class SimpleOnBlock<C, WE> extends OnClauseTableBlock<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        private final SimpleUpdate<C, WE> update;

        private SimpleOnBlock(_JoinType joinType, TableItem tableItem, String alias, SimpleUpdate<C, WE> update) {
            super(joinType, tableItem, alias);
            this.update = update;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            return this.update;
        }

    } // SimpleOnBlock


    /**
     * @see SimpleUpdate#createTableBlock(_JoinType, TableMeta, String)
     */
    private static class SimpleIndexHintOnBlock<C, WE> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate.IndexJoinOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.IndexJoinOnSpec<C> {

        private final SimpleUpdate<C, WE> update;

        private final List<String> partitionList;

        private SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, SimpleUpdate<C, WE> update) {
            super(joinType, table, tableAlias);
            this.update = update;
            this.partitionList = Collections.emptyList();
        }

        /**
         * @param partitionList a unmodified list
         */
        private SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, SimpleUpdate<C, WE> update, List<String> partitionList) {
            super(joinType, table, tableAlias);
            this.update = update;
            this.partitionList = partitionList;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            return this.update;
        }


        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

    }// SimpleIndexHintBlock

    /**
     * @see SimpleUpdate#createBlockBeforeAs(_JoinType, TableMeta)
     */
    private static final class SimplePartitionOnBlock<C, WE>
            extends MySQLPartitionClause<C, _AsClause<MultiIndexHintOnSpec<C>>>
            implements _AsClause<MultiIndexHintOnSpec<C>>, MySQLUpdate.MultiPartitionOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleUpdate<C, WE> update;

        private SimplePartitionOnBlock(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C, WE> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public MultiIndexHintOnSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final SimpleIndexHintOnBlock<C, WE> indexHintOnSpec;
            if (partitionList == null) {
                indexHintOnSpec = new SimpleIndexHintOnBlock<>(this.joinType, this.table, alias, this.update);
            } else {
                indexHintOnSpec = new SimpleIndexHintOnBlock<>(this.joinType, this.table, alias
                        , this.update, partitionList);
            }
            this.update.criteriaContext.onAddBlock(indexHintOnSpec);
            return indexHintOnSpec;
        }

    }// SimplePartitionBlock


    /**
     * @see BatchUpdate#createItemBlock(_JoinType, TableItem, String)
     */
    private static final class BatchOnBlock<C, WE> extends OnClauseTableBlock<C, MySQLUpdate.BatchMultiJoinSpec<C>>
            implements BatchMultiOnSpec<C> {

        private final BatchUpdate<C, WE> update;

        private BatchOnBlock(_JoinType joinType, TableItem tableItem, String alias, BatchUpdate<C, WE> update) {
            super(joinType, tableItem, alias);
            this.update = update;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            return this.update;
        }


    }// BatchOnBlock


    /**
     * @see BatchUpdate#createTableBlock(_JoinType, TableMeta, String)
     */
    private static final class BatchIndexHintOnBlock<C, WE> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate.BatchIndexJoinOnSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C>, MySQLUpdate.BatchIndexJoinOnSpec<C> {

        private final BatchUpdate<C, WE> update;

        private final List<String> partitionList;

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, BatchUpdate<C, WE> update) {
            super(joinType, table, tableAlias);
            this.update = update;
            this.partitionList = Collections.emptyList();
        }

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, BatchUpdate<C, WE> update, List<String> partitionList) {
            super(joinType, table, tableAlias);
            this.update = update;
            switch (partitionList.size()) {
                case 0:
                case 1:
                    this.partitionList = partitionList;
                    break;
                default:
                    this.partitionList = Collections.unmodifiableList(partitionList);
            }
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            return this.update;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

    }//BatchIndexHintOnBlock


    /**
     * @see BatchUpdate#createBlockBeforeAs(_JoinType, TableMeta)
     */
    private static final class BatchPartitionOnBlock<C, WE>
            extends MySQLPartitionClause<C, _AsClause<BatchMultiIndexHintOnSpec<C>>>
            implements _AsClause<BatchMultiIndexHintOnSpec<C>>
            , MySQLUpdate.BatchMultiPartitionOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchUpdate<C, WE> update;

        private BatchPartitionOnBlock(_JoinType joinType, TableMeta<?> table, BatchUpdate<C, WE> update) {
            super(update.criteria);
            this.update = update;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public BatchMultiIndexHintOnSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final BatchIndexHintOnBlock<C, WE> indexHintBlock;
            if (partitionList == null) {
                indexHintBlock = new BatchIndexHintOnBlock<>(this.joinType, this.table, tableAlias, this.update);
            } else {
                indexHintBlock = new BatchIndexHintOnBlock<>(this.joinType, this.table, tableAlias
                        , this.update, partitionList);
            }
            this.update.criteriaContext.onAddBlock(indexHintBlock);
            return indexHintBlock;
        }


    }// BatchPartitionOnBlock


    /**
     * @see SimpleUpdate#createNoActionOnBlock()
     */
    private static class SimpleNoActionOnBlock<C, WE> extends NoActionOnClause<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        SimpleNoActionOnBlock(SimpleUpdate<C, WE> update) {
            super(update);
        }

    }// SimpleNoActionOnBlock


    /**
     * @see SimpleUpdate#createNoActionTableBlock()
     */
    private static final class SimpleNoActionIndexHintOnBlock<C> extends MySQLNoActionIndexHintOnBlock<
            C,
            MySQLUpdate.IndexJoinOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.IndexJoinOnSpec<C> {

        private SimpleNoActionIndexHintOnBlock(MultiJoinSpec<C> update) {
            super(update);
        }

    }// SimpleNoActionIndexHintOnBlock


    /**
     * @see SimpleUpdate#createNoActionPartitionBlock()
     */
    private static final class SimpleNoActionPartitionBlock<C> extends MySQLNoActionPartitionClause<
            C, _AsClause<MultiIndexHintOnSpec<C>>>
            implements MySQLUpdate.MultiPartitionOnSpec<C>, _AsClause<MultiIndexHintOnSpec<C>> {


        private final MultiIndexHintOnSpec<C> hintOnSpec;

        private SimpleNoActionPartitionBlock(MultiJoinSpec<C> update) {
            this.hintOnSpec = new SimpleNoActionIndexHintOnBlock<>(update);
        }

        @Override
        public MultiIndexHintOnSpec<C> as(String alias) {
            return this.hintOnSpec;
        }

    }// SimpleNoActionPartitionBlock


    /**
     * @see BatchUpdate#createNoActionOnBlock()
     */
    private static final class BatchNoActionOnSpec<C> extends NoActionOnClause<C,
            MySQLUpdate.BatchMultiJoinSpec<C>> implements MySQLUpdate.BatchMultiOnSpec<C> {

        private BatchNoActionOnSpec(BatchMultiJoinSpec<C> stmt) {
            super(stmt);
        }
    }//BatchNoActionOnSpec

    /**
     * @see BatchUpdate#createNoActionTableBlock()
     */
    private static final class BatchNoActionIndexHintOnBlock<C> extends MySQLNoActionIndexHintOnBlock<
            C,
            MySQLUpdate.BatchIndexJoinOnSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C>, MySQLUpdate.BatchIndexJoinOnSpec<C> {

        private BatchNoActionIndexHintOnBlock(BatchMultiJoinSpec<C> update) {
            super(update);
        }

    }// BatchNoActionIndexHintOnBlock


    /**
     * @see BatchUpdate#createNoActionPartitionBlock()
     */
    private static final class BatchNoActionPartitionOnBlock<C, WE> extends MySQLNoActionPartitionClause<
            C, _AsClause<BatchMultiIndexHintOnSpec<C>>>
            implements MySQLUpdate.BatchMultiPartitionOnSpec<C>
            , _AsClause<BatchMultiIndexHintOnSpec<C>> {

        private final BatchMultiIndexHintOnSpec<C> indexHintOnSpec;

        private BatchNoActionPartitionOnBlock(BatchUpdate<C, WE> update) {
            this.indexHintOnSpec = new BatchNoActionIndexHintOnBlock<>(update);
        }

        @Override
        public BatchMultiIndexHintOnSpec<C> as(String tableAlias) {
            return this.indexHintOnSpec;
        }
    }//BatchNoActionPartitionOnBlock


    private static final class SimpleNestedJoin<C> extends AbstractTableItemGroup<
            C,
            NestedJoin.UpdateIndexHintSpec<C>,
            NestedJoin.UpdateOnSpec<C>,
            NestedJoin.UpdatePartitionSpec<C>> implements NestedJoin, NestedJoin.UpdateJoinSpec<C> {

        private NestedJoin.UpdatePartitionSpec<C> noActionPartitionBlock;


        private SimpleNestedJoin(TableItem tableItem, String alias, @Nullable C criteria) {
            super(tableItem, alias, criteria);
        }

        @Override
        UpdateIndexHintSpec<C> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new NestedIndexHintOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        UpdateOnSpec<C> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return null;
        }

        @Override
        UpdateIndexHintSpec<C> createNoActionTableBlock() {
            return null;
        }

        @Override
        UpdateOnSpec<C> createNoActionOnBlock() {
            return null;
        }

        @Override
        UpdatePartitionSpec<C> createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
            return super.createBlockBeforeAs(joinType, table);
        }

        @Override
        UpdatePartitionSpec<C> ifJointTableBeforeAs(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
            return super.ifJointTableBeforeAs(predicate, joinType, table);
        }


    }//MySQLNestedJoin


    private static final class NestedIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            NestedJoin.UpdateIndexSpec<C>,
            NestedJoin.UpdateOnSpec<C>,
            NestedJoin.UpdateJoinSpec<C>> implements NestedJoin.UpdateIndexHintSpec<C> {

        private final SimpleNestedJoin<C> nestedJoin;

        private final List<String> partitionList;

        private NestedIndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias
                , SimpleNestedJoin<C> nestedJoin) {
            super(joinType, table, alias);
            this.nestedJoin = nestedJoin;
            this.partitionList = Collections.emptyList();
        }

        private NestedIndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias
                , SimpleNestedJoin<C> nestedJoin, List<String> partitionList) {
            super(joinType, table, alias);
            this.nestedJoin = nestedJoin;
            this.partitionList = partitionList;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        C getCriteria() {
            return this.nestedJoin.criteria;
        }

        @Override
        NestedJoin.UpdateJoinSpec<C> endOnClause() {
            return this.nestedJoin;
        }

    }//NestedIndexHintOnBlock


}
