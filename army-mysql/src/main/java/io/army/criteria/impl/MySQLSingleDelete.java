package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLSingleDelete.SimpleDelete}</li>
 *         <li>{@link MySQLSingleDelete.BatchDelete}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleDelete<C, PR, WR, WA, OR, LR> extends SingleDelete<C, WR, WA>
        implements Query.OrderByClause<C, OR>, MySQLUpdate.LimitClause<C, LR>, MySQLQuery.PartitionClause<C, PR>
        , _MySQLSingleDelete {

    static <C> MySQLDelete.SingleDeleteSpec<C> simple57(@Nullable C criteria) {
        return new SingleDeleteSpecImpl<>(criteria);
    }

    static <C> MySQLDelete.BatchSingleDeleteSpec<C> batch57(@Nullable C criteria) {
        return new BatchSingleDeleteSpecImpl<>(criteria);
    }

    static <C> MySQLDelete.SingleDelete80Spec<C> simple80(@Nullable C criteria) {
        return new SingleDelete80SpecImpl<>(criteria);
    }

    static <C> MySQLDelete.BatchSingleDelete80Spec<C> batch80(@Nullable C criteria) {
        return new BatchDelete80SpecImpl<>(criteria);
    }

    private final CommandBlock commandBlock;

    private List<String> partitionList;

    private List<SortPart> orderByList;

    private long rowCount = -1L;


    private MySQLSingleDelete(CommandBlock commandBlock, CriteriaContext criteriaContext) {
        super(criteriaContext);
        this.commandBlock = commandBlock;
        if (!(this instanceof _MySQLWithClause)) {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }

    @Override
    public final PR partition(String partitionName) {
        this.partitionList = Collections.singletonList(partitionName);
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2) {
        this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
        return (PR) this;
    }

    @Override
    public final PR partition(List<String> partitionNameList) {
        if (CollectionUtils.isEmpty(partitionNameList)) {
            throw new CriteriaException("partitionNameList must not empty.");
        }
        this.partitionList = CollectionUtils.asUnmodifiableList(partitionNameList);
        return (PR) this;
    }

    @Override
    public final PR partition(Supplier<List<String>> supplier) {
        return this.partition(supplier.get());
    }

    @Override
    public final PR partition(Function<C, List<String>> function) {
        return this.partition(function.apply(this.criteria));
    }

    @Override
    public final PR ifPartition(Supplier<List<String>> supplier) {
        final List<String> list;
        list = supplier.get();
        if (!CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }

    @Override
    public final OR orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(sortPart1, sortPart2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(List<SortPart> sortPartList) {
        this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    @Override
    public final OR orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    @Override
    public final OR ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> sortPartList;
        sortPartList = supplier.get();
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> sortPartList;
        sortPartList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
        }
        return (OR) this;
    }

    @Override
    public final LR limit(long rowCount) {
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<Long> supplier) {
        this.rowCount = supplier.get();
        return (LR) this;
    }

    @Override
    public final LR limit(Function<C, Long> function) {
        this.rowCount = function.apply(this.criteria);
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<Long> supplier) {
        final Long rowCount;
        rowCount = supplier.get();
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<C, Long> function) {
        final Long rowCount;
        rowCount = function.apply(this.criteria);
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return (LR) this;
    }

    /*################################## blow _MySQLSingleDelete method ##################################*/

    @Override
    public final List<Hint> hintList() {
        return this.commandBlock.hintList;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.commandBlock.modifierList;
    }

    @Override
    public final SingleTableMeta<?> table() {
        return this.commandBlock.table;
    }

    @Override
    public final String tableAlias() {
        return "t";
    }


    @Override
    public final List<SortPart> orderByList() {
        prepared();
        return this.orderByList;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    @Override
    final void onAsDelete() {
        final List<String> partitionList = this.partitionList;
        if (CollectionUtils.isEmpty(partitionList)) {
            this.partitionList = Collections.emptyList();
        } else {
            this.partitionList = partitionList;
        }

        final List<SortPart> orderByList = this.orderByList;
        if (CollectionUtils.isEmpty(orderByList)) {
            this.orderByList = Collections.emptyList();
        } else {
            this.orderByList = orderByList;
        }

        if (this instanceof BatchDelete) {
            final List<ReadWrapper> wrapperList = ((BatchDelete<C>) this).wrapperList;
            if (CollectionUtils.isEmpty(wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
        }


    }

    @Override
    final void onClear() {
        this.partitionList = null;
        this.orderByList = null;
        this.rowCount = -1L;

        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).wrapperList = null;
        }
    }


    /*################################## blow inner class ##################################*/

    private static class SimpleDelete<C> extends MySQLSingleDelete<
            C,
            MySQLDelete.SingleWhereSpec<C>,
            MySQLDelete.OrderBySpec<C>,
            MySQLDelete.SingleWhereAndSpec<C>,
            MySQLDelete.LimitSpec<C>,
            Delete.DeleteSpec>
            implements MySQLDelete.SinglePartitionSpec<C>, MySQLDelete.SingleWhereAndSpec<C> {

        private SimpleDelete(CommandBlock commandBlock, CriteriaContext criteriaContext) {
            super(commandBlock, criteriaContext);

        }


    }//SimpleDelete

    private static class BatchDelete<C> extends MySQLSingleDelete<
            C,
            MySQLDelete.BatchSingleWhereSpec<C>,
            MySQLDelete.BatchOrderBySpec<C>,
            MySQLDelete.BatchSingleWhereAndSpec<C>,
            MySQLDelete.BatchLimitSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>>
            implements MySQLDelete.BatchSinglePartitionSpec<C>, MySQLDelete.BatchSingleWhereAndSpec<C> {

        private List<ReadWrapper> wrapperList;

        private BatchDelete(CommandBlock commandBlock, CriteriaContext criteriaContext) {
            super(commandBlock, criteriaContext);

        }

        @Override
        public final DeleteSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public final DeleteSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public final DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public final DeleteSpec paramBeans(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public final DeleteSpec paramBeans(Supplier<List<?>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public final DeleteSpec paramBeans(Function<C, List<?>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }


    }//BatchDelete


    /**
     * @see #simple57(Object)
     */
    private static final class SingleDeleteSpecImpl<C> implements MySQLDelete.SingleDeleteSpec<C> {

        private final C criteria;

        private SingleDeleteSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public MySQLDelete.SingleDeleteFromClause<MySQLDelete.SinglePartitionSpec<C>> delete(Supplier<List<Hint>> hints
                , List<SQLModifier> modifiers) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new SimpleDeleteFromClause<>(hintList, modifiers, this.criteria);
        }

        @Override
        public MySQLDelete.SinglePartitionSpec<C> deleteFrom(SingleTableMeta<? extends IDomain> table) {
            return new SimpleDelete<>(new CommandBlock(table), CriteriaContexts.primaryContext(this.criteria));
        }

    }// SingleDeleteSpecImpl


    /**
     * @see SingleDeleteSpecImpl#delete(Supplier, List)
     */
    private static final class SimpleDeleteFromClause<C>
            implements MySQLDelete.SingleDeleteFromClause<MySQLDelete.SinglePartitionSpec<C>> {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifierList;

        private final C criteria;

        private SimpleDeleteFromClause(List<Hint> hintList, List<SQLModifier> modifierList, @Nullable C criteria) {
            this.hintList = hintList;
            this.modifierList = modifierList;
            this.criteria = criteria;
        }

        @Override
        public MySQLDelete.SinglePartitionSpec<C> from(SingleTableMeta<? extends IDomain> table) {
            final CommandBlock commandBlock = new CommandBlock(hintList, modifierList, table);
            return new SimpleDelete<>(commandBlock, CriteriaContexts.primaryContext(this.criteria));
        }

    } // SimpleDeleteFromClause


    private static final class CommandBlock {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifierList;

        private final SingleTableMeta<?> table;

        private CommandBlock(List<Hint> hintList, List<SQLModifier> modifierList, SingleTableMeta<?> table) {
            if (hintList == Collections.EMPTY_LIST) {
                this.hintList = hintList;
            } else {
                this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            }
            if (modifierList == Collections.EMPTY_LIST) {
                this.modifierList = modifierList;
            } else {
                this.modifierList = CollectionUtils.asUnmodifiableList(modifierList);
            }
            this.table = table;
        }

        private CommandBlock(SingleTableMeta<?> table) {
            this.table = table;
            this.hintList = Collections.emptyList();
            this.modifierList = Collections.emptyList();
        }

    }// CommandBlock


    /**
     * @see #batch57(Object)
     */
    private static final class BatchSingleDeleteSpecImpl<C> implements MySQLDelete.BatchSingleDeleteSpec<C> {

        private final C criteria;

        private BatchSingleDeleteSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }


        @Override
        public MySQLDelete.SingleDeleteFromClause<MySQLDelete.BatchSinglePartitionSpec<C>> delete(Supplier<List<Hint>> hints, List<SQLModifier> modifiers) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new BatchDeleteFromClause<>(hintList, modifiers, this.criteria);
        }

        @Override
        public MySQLDelete.BatchSinglePartitionSpec<C> deleteFrom(SingleTableMeta<? extends IDomain> table) {
            return new BatchDelete<>(new CommandBlock(table), CriteriaContexts.primaryContext(this.criteria));
        }

    }// BatchSinglePartitionSpecImpl

    /**
     * @see BatchSingleDeleteSpecImpl#delete(Supplier, List)
     */
    private static final class BatchDeleteFromClause<C>
            implements MySQLDelete.SingleDeleteFromClause<MySQLDelete.BatchSinglePartitionSpec<C>> {

        private final C criteria;

        private final List<Hint> hintList;

        private final List<SQLModifier> modifierList;

        private BatchDeleteFromClause(List<Hint> hintList, List<SQLModifier> modifierList, @Nullable C criteria) {
            this.hintList = hintList;
            this.modifierList = modifierList;
            this.criteria = criteria;
        }

        @Override
        public MySQLDelete.BatchSinglePartitionSpec<C> from(SingleTableMeta<? extends IDomain> table) {
            final CommandBlock commandBlock = new CommandBlock(this.hintList, this.modifierList, table);
            return new BatchDelete<>(commandBlock, CriteriaContexts.primaryContext(this.criteria));
        }

    } // BatchDeleteFromClause


    private static abstract class SingleDelete80Clause<C, WE, DR> implements MySQLQuery.WithClause<C, WE>
            , MySQLDelete.SingleDeleteClause<DR>, MySQLDelete.SingleDeleteFromClause<DR> {

        private final C criteria;

        final CriteriaContext criteriaContext;

        boolean recursive;

        List<Cte> cteList;

        List<Hint> hintList;

        List<SQLModifier> modifierList;

        private SingleDelete80Clause(@Nullable C criteria) {
            this.criteria = criteria;
            this.criteriaContext = CriteriaContexts.primaryContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);

        }

        @Override
        public final WE with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return (WE) this;
        }

        @Override
        public final WE with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return (WE) this;
        }

        @Override
        public final WE with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return (WE) this;
        }

        @Override
        public final WE with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return (WE) this;
        }

        @Override
        public final WE withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public final WE withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public final WE withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public final WE withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
        }

        @Override
        public final MySQLDelete.SingleDeleteFromClause<DR> delete(Supplier<List<Hint>> hints, List<SQLModifier> modifiers) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
            return this;
        }

        @Override
        public final DR deleteFrom(SingleTableMeta<? extends IDomain> table) {
            return createDelete(new CommandBlock(table));
        }

        @Override
        public final DR from(SingleTableMeta<? extends IDomain> table) {
            final List<Hint> hintList = this.hintList;
            final List<SQLModifier> modifierList = this.modifierList;
            if (hintList == null || modifierList == null) {
                throw _Exceptions.castCriteriaApi();
            }
            return createDelete(new CommandBlock(hintList, modifierList, table));
        }

        abstract DR createDelete(CommandBlock block);

    }//MySQLSingleDelete80Clause

    private static final class SingleDelete80SpecImpl<C> extends SingleDelete80Clause<
            C,
            MySQLDelete.SingleDeleteSpec<C>,
            MySQLDelete.SinglePartitionSpec<C>>
            implements MySQLDelete.SingleDelete80Spec<C> {


        private SingleDelete80SpecImpl(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        MySQLDelete.SinglePartitionSpec<C> createDelete(final CommandBlock block) {
            List<Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
            }
            return new SimpleDelete80<>(block, this.recursive, cteList, this.criteriaContext);
        }

    }//SingleDelete80SpecImpl

    private static final class BatchDelete80SpecImpl<C> extends SingleDelete80Clause<
            C,
            MySQLDelete.BatchSingleDeleteSpec<C>,
            MySQLDelete.BatchSinglePartitionSpec<C>>
            implements MySQLDelete.BatchSingleDelete80Spec<C> {

        private BatchDelete80SpecImpl(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        MySQLDelete.BatchSinglePartitionSpec<C> createDelete(CommandBlock block) {
            List<Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
            }
            return new BatchDelete80<>(block, this.recursive, cteList, this.criteriaContext);
        }

    }//BatchDelete80SpecImpl


    private static final class SimpleDelete80<C> extends SimpleDelete<C> implements _MySQLWithClause {

        private final boolean recursive;

        private final List<Cte> cteList;

        private SimpleDelete80(CommandBlock commandBlock, boolean recursive, List<Cte> cteList
                , CriteriaContext criteriaContext) {
            super(commandBlock, criteriaContext);
            this.recursive = recursive;
            this.cteList = cteList;
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

    }//SimpleDelete80


    private static final class BatchDelete80<C> extends BatchDelete<C> implements _MySQLWithClause {

        private final boolean recursive;

        private final List<Cte> cteList;

        private BatchDelete80(CommandBlock commandBlock, boolean recursive, List<Cte> cteList
                , CriteriaContext criteriaContext) {
            super(commandBlock, criteriaContext);
            this.recursive = recursive;
            this.cteList = cteList;
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

    }//BatchDelete80


}
