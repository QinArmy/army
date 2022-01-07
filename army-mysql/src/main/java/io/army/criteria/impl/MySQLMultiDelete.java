package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MySQLMultiDelete<C, JT, IT, WR, WA> extends MultiDelete<C, JT, JT, WR, WA> implements _MySQLMultiDelete
        , MySQLDelete, MySQLQuery.MySQLJoinClause<C, JT, JT, IT> {


    static <C> MySQLDelete.MultiDeleteSpec<C> simple57(@Nullable C criteria) {
        return new MultiDeleteSpecIml<>(criteria);
    }

    static <C> MySQLDelete.BatchMultiDeleteSpec<C> batch57(@Nullable C criteria) {
        return new BatchMultiDeleteSpecImpl<>(criteria);
    }

    private final CommandBlock commandBlock;

    private IT noActionPartitionBlock;

    MySQLMultiDelete(CommandBlock commandBlock, FirstBlock block, @Nullable C criteria) {
        super(block, criteria);
        this.commandBlock = commandBlock;
    }


    @Override
    public final List<Hint> hintList() {
        return this.commandBlock.hintList;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.commandBlock.modifierList;
    }

    @Override
    public final boolean usingSyntax() {
        return this.commandBlock.usingSyntax;
    }

    @Override
    public final List<TableMeta<?>> tableList() {
        return this.commandBlock.tableList;
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.STRAIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.ifAddTableBlock(predicate, JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TablePart> JT straightJoin(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.STRAIGHT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JT ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.STRAIGHT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JT straightJoin(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.STRAIGHT_JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JT ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.STRAIGHT_JOIN, supplier, alias);
    }

    @Override
    public final IT leftJoin(TableMeta<?> table) {
        return this.addTablePartitionBlock(JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddTablePartitionBlock(predicate, JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT join(TableMeta<?> table) {
        return this.addTablePartitionBlock(JoinType.JOIN, table);
    }

    @Override
    public final IT ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddTablePartitionBlock(predicate, JoinType.JOIN, table);
    }

    @Override
    public final IT rightJoin(TableMeta<?> table) {
        return this.addTablePartitionBlock(JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddTablePartitionBlock(predicate, JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT straightJoin(TableMeta<?> table) {
        return this.addTablePartitionBlock(JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddTablePartitionBlock(predicate, JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT fullJoin(TableMeta<?> table) {
        return this.addTablePartitionBlock(JoinType.FULL_JOIN, table);
    }

    @Override
    public final IT ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddTablePartitionBlock(predicate, JoinType.FULL_JOIN, table);
    }

    abstract IT addTablePartitionBlock(JoinType joinType, TableMeta<?> table);

    abstract IT createNoActionPartitionBlock();


    @Override
    final void onAsDelete() {
        this.noActionPartitionBlock = null;
        if (this instanceof BatchDelete) {
            final List<ReadWrapper> wrapperList = ((BatchDelete<C>) this).wrapperList;
            if (CollectionUtils.isEmpty(wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
            ((BatchDelete<C>) this).wrapperList = Collections.unmodifiableList(wrapperList);
        }

    }

    @Override
    final void onClear() {
        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).wrapperList = null;
        }
    }

    private IT ifAddTablePartitionBlock(Predicate<C> predicate, JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = this.addTablePartitionBlock(joinType, table);
        } else {
            IT noActionBlock = this.noActionPartitionBlock;
            if (noActionBlock == null) {
                noActionBlock = this.createNoActionPartitionBlock();
                this.noActionPartitionBlock = noActionBlock;
            }
            block = noActionBlock;
        }
        return block;
    }


    /*################################## blow inner class ##################################*/

    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link MultiDeleteSpecIml}</li>
     *         <li>{@link BatchMultiDeleteSpecImpl}</li>
     *     </ul>
     * </p>
     */
    private static abstract class MultiDeleteClauseImpl<C, DR, DP> implements MySQLDelete.MultiDeleteClause<C, DR, DP> {

        final C criteria;

        private MultiDeleteClauseImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public final MultiDeleteFromClause<C, DR, DP> delete(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , List<TableMeta<?>> tableList) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            if (CollectionUtils.isEmpty(tableList)) {
                throw deleteTableListNotEmpty();
            }
            return createFromClause(new CommandBlock(hintList, modifiers, tableList, false));
        }

        @Override
        public final MultiDeleteFromClause<C, DR, DP> delete(List<TableMeta<?>> tableList) {
            if (CollectionUtils.isEmpty(tableList)) {
                throw deleteTableListNotEmpty();
            }
            return createFromClause(new CommandBlock(tableList, false));
        }

        @Override
        public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , List<TableMeta<?>> tableList) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            if (CollectionUtils.isEmpty(tableList)) {
                throw deleteTableListNotEmpty();
            }
            return createUsingClause(new CommandBlock(hintList, modifiers, tableList, true));
        }

        @Override
        public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(List<TableMeta<?>> tableList) {
            if (CollectionUtils.isEmpty(tableList)) {
                throw deleteTableListNotEmpty();
            }
            return createUsingClause(new CommandBlock(tableList, true));
        }

        abstract MultiDeleteFromClause<C, DR, DP> createFromClause(CommandBlock commandBlock);

        abstract MultiDeleteUsingClause<C, DR, DP> createUsingClause(CommandBlock commandBlock);
    }

    /**
     * @see #simple57(Object)
     */
    private static final class MultiDeleteSpecIml<C>
            extends MultiDeleteClauseImpl<C, MySQLDelete.MultiJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C>>
            implements MySQLDelete.MultiDeleteSpec<C> {


        private MultiDeleteSpecIml(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        MultiDeleteFromClause<C, MultiJoinSpec<C>, MultiPartitionJoinSpec<C>> createFromClause(CommandBlock commandBlock) {
            return new SimpleFromUsingClause<>(commandBlock, this.criteria);
        }

        @Override
        MultiDeleteUsingClause<C, MultiJoinSpec<C>, MultiPartitionJoinSpec<C>> createUsingClause(CommandBlock commandBlock) {
            return new SimpleFromUsingClause<>(commandBlock, this.criteria);
        }

    }// MultiDeleteSpecIml

    /**
     * @see #batch57(Object)
     */
    private static final class BatchMultiDeleteSpecImpl<C>
            extends MultiDeleteClauseImpl<C, MySQLDelete.BatchMultiJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C>>
            implements MySQLDelete.BatchMultiDeleteSpec<C> {


        private BatchMultiDeleteSpecImpl(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        MultiDeleteFromClause<C, BatchMultiJoinSpec<C>, BatchMultiPartitionJoinSpec<C>> createFromClause(CommandBlock commandBlock) {
            return new BatchFromUsingClause<>(commandBlock, this.criteria);
        }

        @Override
        MultiDeleteUsingClause<C, BatchMultiJoinSpec<C>, BatchMultiPartitionJoinSpec<C>> createUsingClause(CommandBlock commandBlock) {
            return new BatchFromUsingClause<>(commandBlock, this.criteria);
        }

    }// BatchMultiDeleteSpecImpl


    private static final class SimpleDelete<C> extends MySQLMultiDelete<
            C,
            MySQLDelete.MultiOnSpec<C>,
            MySQLDelete.MultiPartitionOnSpec<C>,
            Delete.DeleteSpec,
            MySQLDelete.MultiWhereAndSpec<C>>
            implements MySQLDelete.MultiWhereAndSpec<C>, MySQLDelete.MultiJoinSpec<C> {

        private SimpleDelete(CommandBlock commandBlock, FirstBlock block, @Nullable C criteria) {
            super(commandBlock, block, criteria);
        }

        @Override
        MultiOnSpec<C> createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new SimpleMultiOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        MultiOnSpec<C> createTablePartBlock(JoinType joinType, TablePart tablePart, String alias) {
            return new SimpleMultiOnBlock<>(joinType, tablePart, alias, this);
        }


        @Override
        MultiPartitionOnSpec<C> addTablePartitionBlock(JoinType joinType, TableMeta<?> table) {
            SimpleMultiPartitionBlock<C> block = new SimpleMultiPartitionBlock<>(joinType, table, this);
            this.addOtherBlock(block);
            return block;
        }

        @Override
        MultiOnSpec<C> createNoActionTableBlock() {
            return new NoActionOnBlock<>(this);
        }

        @Override
        MultiOnSpec<C> createNoActionTablePartBlock() {
            return new NoActionOnBlock<>(this);
        }

        @Override
        MultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new NoActionPartitionBlock<>(this);
        }


    }//SimpleDelete

    private static final class BatchDelete<C> extends MySQLMultiDelete<
            C,
            MySQLDelete.BatchMultiOnSpec<C>,
            MySQLDelete.BatchMultiPartitionOnSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>,
            MySQLDelete.BatchMultiWhereAndSpec<C>>
            implements MySQLDelete.BatchMultiWhereAndSpec<C>, MySQLDelete.BatchMultiJoinSpec<C>, _BatchDml {


        private List<ReadWrapper> wrapperList;

        private BatchDelete(CommandBlock commandBlock, FirstBlock block, @Nullable C criteria) {
            super(commandBlock, block, criteria);
        }

        @Override
        public DeleteSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public DeleteSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public DeleteSpec paramBeans(List<Object> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public DeleteSpec paramBeans(Supplier<List<Object>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public DeleteSpec paramBeans(Function<C, List<Object>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }

        @Override
        BatchMultiOnSpec<C> createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new BatchMultiOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        BatchMultiOnSpec<C> createTablePartBlock(JoinType joinType, TablePart tablePart, String alias) {
            return new BatchMultiOnBlock<>(joinType, tablePart, alias, this);
        }

        @Override
        BatchMultiPartitionOnSpec<C> addTablePartitionBlock(JoinType joinType, TableMeta<?> table) {
            final BatchPartitionBlock<C> block = new BatchPartitionBlock<>(joinType, table, this);
            this.addOtherBlock(block);
            return block;
        }

        @Override
        BatchMultiOnSpec<C> createNoActionTableBlock() {
            return new BatchNoActionOnBlock<>(this);
        }

        @Override
        BatchMultiOnSpec<C> createNoActionTablePartBlock() {
            return new BatchNoActionOnBlock<>(this);
        }

        @Override
        BatchMultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new BatchNoActionPartitionBlock<>(this);
        }

        @Override
        public List<ReadWrapper> wrapperList() {
            prepared();
            return this.wrapperList;
        }


    }//SimpleDelete


    private static final class CommandBlock {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifierList;

        private final List<TableMeta<?>> tableList;

        private final boolean usingSyntax;

        private CommandBlock(List<Hint> hintList, List<SQLModifier> modifierList, List<TableMeta<?>> tableList, boolean usingSyntax) {
            this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            this.modifierList = CollectionUtils.asUnmodifiableList(modifierList);
            this.tableList = tableList;
            this.usingSyntax = usingSyntax;
        }

        private CommandBlock(List<TableMeta<?>> tableList, boolean usingSyntax) {
            this.tableList = tableList;
            this.usingSyntax = usingSyntax;
            this.hintList = Collections.emptyList();
            this.modifierList = Collections.emptyList();
        }


    }// CommandBlock


    private static final class FirstBlock extends TableBlock implements _MySQLTableBlock {

        private final String alias;

        private final List<String> partitionList;

        private FirstBlock(TableMeta<?> table, String alias, List<String> partitionList) {
            super(JoinType.NONE, table);
            this.alias = alias;
            this.partitionList = partitionList;
        }

        private FirstBlock(TablePart tablePart, String alias) {
            super(JoinType.NONE, tablePart);
            this.alias = alias;
            this.partitionList = Collections.emptyList();
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
            return Collections.emptyList();
        }

    }// FirstBlock


    private static abstract class MultiFromUsingClause<C, DR, DP>
            implements MultiDeleteFromClause<C, DR, DP>, MultiDeleteUsingClause<C, DR, DP> {

        final CommandBlock commandBlock;

        final C criteria;


        private MultiFromUsingClause(CommandBlock commandBlock, @Nullable C criteria) {
            this.commandBlock = commandBlock;
            this.criteria = criteria;
        }

        @Override
        public final DR from(TableMeta<?> table, String alias) {
            return this.createUpdate(table, alias);
        }

        @Override
        public final DP from(TableMeta<?> table) {
            return this.createPartitionJoinSpec(table);
        }

        @Override
        public final <T extends TablePart> DR from(Supplier<T> tablePart, String alias) {
            return this.createUpdate(tablePart.get(), alias);
        }

        @Override
        public final <T extends TablePart> DR from(Function<C, T> tablePart, String alias) {
            return this.createUpdate(tablePart.apply(this.criteria), alias);
        }

        @Override
        public final DR using(TableMeta<?> table, String alias) {
            return this.createUpdate(table, alias);
        }

        @Override
        public final DP using(TableMeta<?> table) {
            return this.createPartitionJoinSpec(table);
        }

        @Override
        public final <T extends TablePart> DR using(Supplier<T> tablePart, String alias) {
            return this.createUpdate(tablePart.get(), alias);
        }

        @Override
        public final <T extends TablePart> DR using(Function<C, T> tablePart, String alias) {
            return this.createUpdate(tablePart.apply(this.criteria), alias);
        }

        abstract DR createUpdate(TablePart tablePart, String alias);

        abstract DP createPartitionJoinSpec(TableMeta<?> table);


    }// MultiFromUsingClause


    /**
     * @see SimpleDelete
     */
    private static final class SimpleFromUsingClause<C>
            extends MultiFromUsingClause<C, MySQLDelete.MultiJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C>> {

        private SimpleFromUsingClause(CommandBlock commandBlock, @Nullable C criteria) {
            super(commandBlock, criteria);
        }

        @Override
        MultiJoinSpec<C> createUpdate(TablePart tablePart, String alias) {
            Objects.requireNonNull(tablePart);
            return new SimpleDelete<>(this.commandBlock, new FirstBlock(tablePart, alias), this.criteria);
        }

        @Override
        MultiPartitionJoinSpec<C> createPartitionJoinSpec(TableMeta<?> table) {
            return new SimplePartitionJoinSpec<>(this.commandBlock, table, this.criteria);
        }
    }// SimpleFromUsingClause

    /**
     * @see BatchDelete
     */
    private static final class BatchFromUsingClause<C>
            extends MultiFromUsingClause<C, MySQLDelete.BatchMultiJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C>> {

        private BatchFromUsingClause(CommandBlock commandBlock, @Nullable C criteria) {
            super(commandBlock, criteria);
        }

        @Override
        BatchMultiJoinSpec<C> createUpdate(TablePart tablePart, String alias) {
            Objects.requireNonNull(tablePart);
            return new BatchDelete<>(this.commandBlock, new FirstBlock(tablePart, alias), this.criteria);
        }

        @Override
        BatchMultiPartitionJoinSpec<C> createPartitionJoinSpec(TableMeta<?> table) {
            return new BatchPartitionJoinSpec<>(this.commandBlock, table, this.criteria);
        }

    }// BatchFromUsingClause


    /**
     * @see SimpleFromUsingClause#createPartitionJoinSpec(TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C> extends MySQLPartitionClause<C, MySQLDelete.MultiAsJoinSpec<C>>
            implements MySQLDelete.MultiAsJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C> {

        private final CommandBlock commandBlock;

        private final TableMeta<?> table;

        private SimplePartitionJoinSpec(CommandBlock commandBlock, TableMeta<?> table, @Nullable C criteria) {
            super(criteria);
            this.commandBlock = commandBlock;
            this.table = table;
        }

        @Override
        public MultiJoinSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final FirstBlock block;
            if (partitionList == null) {
                block = new FirstBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }

            return new SimpleDelete<>(this.commandBlock, block, this.criteria);
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchFromUsingClause#createPartitionJoinSpec(TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C>
            extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsJoinSpec<C>>
            implements MySQLDelete.BatchMultiAsJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C> {

        private final CommandBlock commandBlock;

        private final TableMeta<?> table;

        private BatchPartitionJoinSpec(CommandBlock commandBlock, TableMeta<?> table, @Nullable C criteria) {
            super(criteria);
            this.commandBlock = commandBlock;
            this.table = table;
        }

        @Override
        public BatchMultiJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final FirstBlock block;
            if (partitionList == null) {
                block = new FirstBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            return new BatchDelete<>(this.commandBlock, block, this.criteria);
        }
    }// BatchPartitionJoinSpec


    /**
     * @see SimpleDelete#addTablePartBlock(JoinType, TablePart, String)
     * @see SimpleDelete#addTableBlock(JoinType, TableMeta, String)
     */
    private static final class SimpleMultiOnBlock<C> extends OnClauseTableBlock<C, MySQLDelete.MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        final String alias;

        private final SimpleDelete<C> delete;

        private SimpleMultiOnBlock(JoinType joinType, TablePart tablePart, String alias, SimpleDelete<C> delete) {
            super(joinType, tablePart);
            this.alias = alias;
            this.delete = delete;
        }

        @Override
        C getCriteria() {
            return this.delete.criteria;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            return this.delete;
        }

        @Override
        public String alias() {
            return this.alias;
        }


    }// SimpleMultiOnBlock

    /**
     * @see SimpleDelete#addTablePartitionBlock(JoinType, TableMeta)
     */
    private static final class SimpleMultiPartitionBlock<C>
            extends MySQLPartitionClause<C, MySQLDelete.MultiAsOnSpec<C>>
            implements MySQLDelete.MultiPartitionOnSpec<C>, MySQLDelete.MultiAsOnSpec<C>, _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleDelete<C> delete;

        private SimpleMultiOnBlock<C> onBlock;

        private SimpleMultiPartitionBlock(JoinType joinType, TableMeta<?> tablePart, SimpleDelete<C> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = tablePart;
            this.delete = delete;
        }

        @Override
        public MultiOnSpec<C> as(String alias) {
            if (this.onBlock != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            final SimpleMultiOnBlock<C> onBlock;
            onBlock = new SimpleMultiOnBlock<>(this.joinType, this.table, alias, this.delete);
            this.onBlock = onBlock;
            return onBlock;
        }

        @Override
        public TablePart table() {
            return this.table;
        }

        @Override
        public SQLModifier jointType() {
            return this.joinType;
        }

        @Override
        public String alias() {
            final SimpleMultiOnBlock<C> onBlock;
            onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.alias;
        }

        @Override
        public List<_Predicate> predicates() {
            final SimpleMultiOnBlock<C> onBlock;
            onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.predicates();
        }

        @Override
        public List<String> partitionList() {
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            return partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }// SimpleMultiPartitionBlock


    /**
     * @see SimpleDelete#createNoActionPartitionBlock()
     */
    private static final class NoActionPartitionBlock<C>
            extends MySQLNoActionPartitionClause<C, MultiAsOnSpec<C>>
            implements MySQLDelete.MultiAsOnSpec<C>, MySQLDelete.MultiPartitionOnSpec<C> {

        private final MultiOnSpec<C> onSpec;

        private NoActionPartitionBlock(SimpleDelete<C> delete) {
            this.onSpec = new NoActionOnBlock<>(delete);
        }

        @Override
        public MultiOnSpec<C> as(String alias) {
            return this.onSpec;
        }

    }// NoActionPartitionBlock

    /**
     * @see SimpleDelete#createNoActionTableBlock()
     * @see SimpleDelete#createNoActionTablePartBlock()
     */
    private static final class NoActionOnBlock<C> extends NoActionOnClause<C, MySQLDelete.MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private NoActionOnBlock(MultiJoinSpec<C> stmt) {
            super(stmt);
        }

    }

    /**
     * @see BatchDelete#addTableBlock(JoinType, TableMeta, String)
     * @see BatchDelete#addTablePartBlock(JoinType, TablePart, String)
     */
    private static final class BatchMultiOnBlock<C> extends OnClauseTableBlock<C, MySQLDelete.BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {

        private final String alias;

        private final BatchDelete<C> delete;

        private BatchMultiOnBlock(JoinType joinType, TablePart tablePart, String alias, BatchDelete<C> delete) {
            super(joinType, tablePart);
            this.alias = alias;
            this.delete = delete;
        }

        @Override
        C getCriteria() {
            return this.delete.criteria;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            return this.delete;
        }

        @Override
        public String alias() {
            return this.alias;
        }


    }// BatchMultiOnBlock

    /**
     * @see BatchDelete#addTablePartitionBlock(JoinType, TableMeta)
     */
    private static final class BatchPartitionBlock<C> extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C>, _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final BatchDelete<C> delete;

        private BatchMultiOnBlock<C> onBlock;

        private BatchPartitionBlock(JoinType joinType, TableMeta<?> table, BatchDelete<C> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = table;
            this.delete = delete;
        }

        @Override
        public BatchMultiOnSpec<C> as(final String alias) {
            if (this.onBlock != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            final BatchMultiOnBlock<C> onBlock;
            onBlock = new BatchMultiOnBlock<>(this.joinType, this.table, alias, this.delete);
            this.onBlock = onBlock;
            return onBlock;
        }

        @Override
        public TablePart table() {
            return this.table;
        }

        @Override
        public String alias() {
            final BatchMultiOnBlock<C> onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.alias;
        }

        @Override
        public SQLModifier jointType() {
            return this.joinType;
        }

        @Override
        public List<_Predicate> predicates() {
            final BatchMultiOnBlock<C> onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.predicates();
        }

        @Override
        public List<String> partitionList() {
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            return partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }// BatchPartitionBlock


    /**
     * @see BatchDelete#createNoActionTableBlock()
     * @see BatchDelete#createNoActionTablePartBlock()
     */
    private static final class BatchNoActionOnBlock<C> extends NoActionOnClause<C, MySQLDelete.BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {

        private BatchNoActionOnBlock(BatchMultiJoinSpec<C> stmt) {
            super(stmt);
        }

    }// NoActionBatchOnBlock

    /**
     * @see BatchDelete#createNoActionPartitionBlock()
     */
    private static final class BatchNoActionPartitionBlock<C>
            extends MySQLNoActionPartitionClause<C, BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C> {

        private final BatchMultiOnSpec<C> onSpec;

        private BatchNoActionPartitionBlock(BatchMultiJoinSpec<C> delete) {
            this.onSpec = new BatchNoActionOnBlock<>(delete);
        }

        @Override
        public BatchMultiOnSpec<C> as(String alias) {
            return this.onSpec;
        }

    }// BatchNoActionPartitionBlock


    private static CriteriaException deleteTableListNotEmpty() {
        return new CriteriaException("tableList must not empty in multi-table delete clause.");
    }


}
