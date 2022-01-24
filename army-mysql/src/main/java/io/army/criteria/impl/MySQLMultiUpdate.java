package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
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
abstract class MySQLMultiUpdate<C, UP, UT, US, JT, JS, JP, WR, WA, SR, IR> extends MultiUpdate<C, JT, JS, WR, WA, SR>
        implements MySQLUpdate.MultiUpdateClause<C, UP, UT, US>, MySQLQuery.IndexHintClause<C, IR, UT>
        , MySQLQuery.IndexJoinClause<C, UT>, MySQLQuery.MySQLJoinClause<C, JT, JS, JP>, MySQLUpdate
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

    private List<Hint> hintList;

    private List<SQLModifier> modifierList;

    private JP noActionPartitionBlock;

    private MySQLIndexHint.Command command;

    private MySQLMultiUpdate(@Nullable C criteria) {
        super(CriteriaContexts.primaryContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , TableMeta<? extends IDomain> table) {
        this.hintList = CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        return this.createPartitionJoinBlock(table);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , TableMeta<? extends IDomain> table, String tableAlias) {
        this.hintList = CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        this.criteriaContext.onFirstBlock(new FirstBlock(table, tableAlias));
        return (UT) this;
    }

    @Override
    public final UP update(TableMeta<? extends IDomain> table) {
        return this.createPartitionJoinBlock(table);
    }

    @Override
    public final UT update(TableMeta<? extends IDomain> table, String tableAlias) {
        this.criteriaContext.onFirstBlock(new FirstBlock(table, tableAlias));
        return (UT) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , Supplier<T> supplier, String alias) {
        this.hintList = CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<T> supplier, String alias) {
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , Function<C, T> function, String alias) {
        this.hintList = CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Function<C, T> function, String alias) {
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(function.apply(this.criteria), alias));
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
            if (!CollectionUtils.isEmpty(indexList)) {
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
            if (!CollectionUtils.isEmpty(indexList)) {
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
            if (!CollectionUtils.isEmpty(indexList)) {
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
    public final JP straightJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JP ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.ifAddTableBlock(predicate, _JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JP ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.JOIN, table);
    }

    @Override
    public final JP ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.JOIN, table);
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JP ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.RIGHT_JOIN, table);
    }


    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final JP ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.FULL_JOIN, table);
    }


    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }


    /*################################## blow package template method ##################################*/


    abstract JP createNoActionPartitionBlock();

    abstract JP createPartitionOnBlock(_JoinType joinType, TableMeta<?> table);

    abstract UP createPartitionJoinBlock(TableMeta<?> table);

    @Override
    final void doOnAsUpdate() {
        this.noActionPartitionBlock = null;
        if (CollectionUtils.isEmpty(this.hintList)) {
            this.hintList = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(this.modifierList)) {
            this.modifierList = Collections.emptyList();
        }

        if (this instanceof BatchUpdate) {
            if (CollectionUtils.isEmpty(((BatchUpdate<C>) this).wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
        }

        if (this instanceof _MySQLWithClause) {
            if (this instanceof SimpleWithAndUpdate
                    && CollectionUtils.isEmpty(((SimpleWithAndUpdate<C>) this).cteList)) {
                ((SimpleWithAndUpdate<C>) this).cteList = Collections.emptyList();
            }
            if (this instanceof BatchWithAndUpdate
                    && CollectionUtils.isEmpty(((BatchWithAndUpdate<C>) this).cteList)) {
                ((BatchWithAndUpdate<C>) this).cteList = Collections.emptyList();
            }

        }


    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C>) this).wrapperList = null;
        }
        if (this instanceof _MySQLWithClause) {
            if (this instanceof SimpleWithAndUpdate) {
                ((SimpleWithAndUpdate<C>) this).cteList = null;
            }
            if (this instanceof BatchWithAndUpdate) {
                ((BatchWithAndUpdate<C>) this).cteList = null;
            }

        }

    }


    /*################################## blow private method ##################################*/

    private JP ifCreatePartitionOnBlock(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final JP block;
        if (predicate.test(this.criteria)) {
            block = createPartitionOnBlock(joinType, table);
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

        if (CollectionUtils.isEmpty(indexNames)) {
            throw new CriteriaException("index hint clause index name list must not empty.");
        }
        final _TableBlock block;
        block = this.criteriaContext.firstBlock();
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
                    this.partitionList = CollectionUtils.unmodifiableList(partitionList);
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
    private static class SimpleUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate.MultiPartitionJoinSpec<C>,
            MySQLUpdate.IndexHintJoinSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiOnSpec<C>,
            MySQLUpdate.MultiPartitionOnSpec<C>,
            Update.UpdateSpec,
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
        final MultiOnSpec<C> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return new SimpleOnBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        final MultiPartitionOnSpec<C> createPartitionOnBlock(_JoinType joinType, TableMeta<?> table) {
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
    private static class BatchUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate.BatchMultiPartitionJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintJoinSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiOnSpec<C>,
            MySQLUpdate.BatchMultiPartitionOnSpec<C>,
            Statement.BatchParamClause<C, Update.UpdateSpec>,
            MySQLUpdate.BatchMultiWhereAndSpec<C>,
            MySQLUpdate.BatchMultiWhereSpec<C>,
            MySQLUpdate.BatchIndexJoinJoinSpec<C>>
            implements MySQLUpdate.BatchMultiWhereAndSpec<C>, MySQLUpdate.BatchMultiWhereSpec<C>
            , MySQLUpdate.BatchMultiIndexHintJoinSpec<C>, MySQLUpdate.BatchIndexJoinJoinSpec<C>
            , MySQLUpdate.BatchMultiUpdateSpec<C>, _BatchDml {

        private List<ReadWrapper> wrapperList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public final UpdateSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public final UpdateSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public final UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public final UpdateSpec paramBeans(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public final UpdateSpec paramBeans(Supplier<List<?>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public final UpdateSpec paramBeans(Function<C, List<?>> function) {
            return this.paramBeans(function.apply(this.criteria));
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
        final BatchMultiOnSpec<C> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return new BatchOnBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        final BatchMultiPartitionOnSpec<C> createPartitionOnBlock(_JoinType joinType, TableMeta<?> table) {
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
        public final List<ReadWrapper> wrapperList() {
            return this.wrapperList;
        }


    }// BatchUpdate

    private static final class SimpleWithAndUpdate<C> extends SimpleUpdate<C> implements WithAndMultiUpdateSpec<C>
            , _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleWithAndUpdate(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public MultiUpdateSpec<C> with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return this;
        }

        @Override
        public MultiUpdateSpec<C> with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return this;
        }

        @Override
        public MultiUpdateSpec<C> with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return this;
        }

        @Override
        public MultiUpdateSpec<C> with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return this;
        }

        @Override
        public MultiUpdateSpec<C> withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public MultiUpdateSpec<C> withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public MultiUpdateSpec<C> withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public MultiUpdateSpec<C> withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
        }


        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

    }//SimpleWithAndUpdate


    private static final class BatchWithAndUpdate<C> extends BatchUpdate<C> implements BatchWithAndMultiUpdateSpec<C>
            , _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private BatchWithAndUpdate(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public BatchMultiUpdateSpec<C> with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return this;
        }

        @Override
        public BatchMultiUpdateSpec<C> with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return this;
        }

        @Override
        public BatchMultiUpdateSpec<C> with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return this;
        }

        @Override
        public BatchMultiUpdateSpec<C> with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return this;
        }

        @Override
        public BatchMultiUpdateSpec<C> withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public BatchMultiUpdateSpec<C> withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public BatchMultiUpdateSpec<C> withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public BatchMultiUpdateSpec<C> withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
        }


        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

    }//BatchWithAndUpdate


    /**
     * @see SimpleUpdate#createPartitionJoinBlock(TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C>
            extends MySQLPartitionClause<C, Statement.AsClause<IndexHintJoinSpec<C>>>
            implements MultiPartitionJoinSpec<C>, Statement.AsClause<IndexHintJoinSpec<C>> {

        private final TableMeta<?> table;

        private final SimpleUpdate<C> update;

        private SimplePartitionJoinSpec(TableMeta<?> table, SimpleUpdate<C> update) {
            super(update.criteria);
            this.table = table;
            this.update = update;
        }

        @Override
        public IndexHintJoinSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            this.update.criteriaContext.onFirstBlock(new FirstBlock(table, tableAlias, partitionList));
            return this.update;
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchUpdate#createPartitionJoinBlock(TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C>
            extends MySQLPartitionClause<C, Statement.AsClause<BatchMultiIndexHintJoinSpec<C>>>
            implements BatchMultiPartitionJoinSpec<C>, Statement.AsClause<BatchMultiIndexHintJoinSpec<C>> {

        private final TableMeta<?> table;

        private final BatchUpdate<C> update;

        private BatchPartitionJoinSpec(TableMeta<?> table, BatchUpdate<C> update) {
            super(update.criteria);
            this.table = table;
            this.update = update;
        }

        @Override
        public BatchMultiIndexHintJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final FirstBlock block;
            if (partitionList == null) {
                block = new FirstBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            this.update.criteriaContext.onFirstBlock(block);
            return this.update;
        }
    }// BatchPartitionJoinSpec


    /**
     * @see SimpleUpdate#createOnBlock(_JoinType, TableItem, String)
     */
    private static final class SimpleOnBlock<C> extends OnClauseTableBlock<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        private final SimpleUpdate<C> update;

        private SimpleOnBlock(_JoinType joinType, TableItem tableItem, String alias, SimpleUpdate<C> update) {
            super(joinType, tableItem, alias);
            this.update = update;
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.update.criteriaContext;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            return this.update;
        }

    } // SimpleOnBlock


    /**
     * @see SimpleUpdate#createTableBlock(_JoinType, TableMeta, String)
     */
    private static class SimpleIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate.IndexJoinOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.IndexJoinOnSpec<C> {

        private final SimpleUpdate<C> update;

        private final List<String> partitionList;

        private SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, SimpleUpdate<C> update) {
            super(joinType, table, tableAlias);
            this.update = update;
            this.partitionList = Collections.emptyList();
        }

        private SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, SimpleUpdate<C> update, List<String> partitionList) {
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
        CriteriaContext getCriteriaContext() {
            return this.update.criteriaContext;
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
     * @see SimpleUpdate#createPartitionOnBlock(_JoinType, TableMeta)
     */
    private static final class SimplePartitionOnBlock<C>
            extends MySQLPartitionClause<C, Statement.AsClause<MultiIndexHintOnSpec<C>>>
            implements Statement.AsClause<MultiIndexHintOnSpec<C>>, MySQLUpdate.MultiPartitionOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleUpdate<C> update;

        private SimplePartitionOnBlock(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public MultiIndexHintOnSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final SimpleIndexHintOnBlock<C> indexHintOnSpec;
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
     * @see BatchUpdate#createOnBlock(_JoinType, TableItem, String)
     */
    private static final class BatchOnBlock<C> extends OnClauseTableBlock<C, MySQLUpdate.BatchMultiJoinSpec<C>>
            implements BatchMultiOnSpec<C> {

        private final BatchUpdate<C> update;

        private BatchOnBlock(_JoinType joinType, TableItem tableItem, String alias, BatchUpdate<C> update) {
            super(joinType, tableItem, alias);
            this.update = update;
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.update.criteriaContext;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            return this.update;
        }


    }// BatchOnBlock


    /**
     * @see BatchUpdate#createTableBlock(_JoinType, TableMeta, String)
     */
    private static final class BatchIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate.BatchIndexJoinOnSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C>, MySQLUpdate.BatchIndexJoinOnSpec<C> {

        private final BatchUpdate<C> update;

        private final List<String> partitionList;

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, BatchUpdate<C> update) {
            super(joinType, table, tableAlias);
            this.update = update;
            this.partitionList = Collections.emptyList();
        }

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String tableAlias, BatchUpdate<C> update, List<String> partitionList) {
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
        CriteriaContext getCriteriaContext() {
            return this.update.criteriaContext;
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
     * @see BatchUpdate#createPartitionOnBlock(_JoinType, TableMeta)
     */
    private static final class BatchPartitionOnBlock<C>
            extends MySQLPartitionClause<C, Statement.AsClause<MySQLUpdate.BatchMultiIndexHintOnSpec<C>>>
            implements Statement.AsClause<MySQLUpdate.BatchMultiIndexHintOnSpec<C>>
            , MySQLUpdate.BatchMultiPartitionOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchUpdate<C> update;

        private BatchPartitionOnBlock(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> update) {
            super(update.criteria);
            this.update = update;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public BatchMultiIndexHintOnSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final BatchIndexHintOnBlock<C> indexHintBlock;
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
    private static class SimpleNoActionOnBlock<C> extends NoActionOnClause<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        SimpleNoActionOnBlock(SimpleUpdate<C> update) {
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
            C, Statement.AsClause<MultiIndexHintOnSpec<C>>>
            implements MySQLUpdate.MultiPartitionOnSpec<C>, Statement.AsClause<MultiIndexHintOnSpec<C>> {


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
    private static final class BatchNoActionPartitionOnBlock<C> extends MySQLNoActionPartitionClause<
            C, Statement.AsClause<MySQLUpdate.BatchMultiIndexHintOnSpec<C>>>
            implements MySQLUpdate.BatchMultiPartitionOnSpec<C>
            , Statement.AsClause<MySQLUpdate.BatchMultiIndexHintOnSpec<C>> {

        private final BatchMultiIndexHintOnSpec<C> indexHintOnSpec;

        private BatchNoActionPartitionOnBlock(BatchUpdate<C> update) {
            this.indexHintOnSpec = new BatchNoActionIndexHintOnBlock<>(update);
        }

        @Override
        public BatchMultiIndexHintOnSpec<C> as(String tableAlias) {
            return this.indexHintOnSpec;
        }
    }//BatchNoActionPartitionOnBlock


}
