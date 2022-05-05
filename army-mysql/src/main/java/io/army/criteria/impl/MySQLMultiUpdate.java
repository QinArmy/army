package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
abstract class MySQLMultiUpdate<C, WE, UT, US, UP, IR, JT, JS, JP, WR, WA, SR>
        extends WithCteMultiUpdate<C, WE, SR, UT, US, UP, JT, JS, JP, WR, WA>
        implements MySQLUpdate.MultiUpdateClause<C, UT, US, UP>, MySQLQuery._IndexHintClause<C, IR, UT>
        , MySQLQuery._IndexJoinClause<C, UT>, MySQLQuery._MySQLJoinClause<C, JT, JS>
        , Statement._CrossJoinClause<C, UT, US>, MySQLQuery._MySQLDialectJoinClause<C, JP>
        , DialectStatement._DialectCrossJoinClause<C, UP>, _MySQLMultiUpdate {


    static <C> _MultiUpdate57Clause<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> BatchMultiUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    static <C> _WithAndMultiUpdateSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndUpdate<>(criteria);
    }

    static <C> BatchWithAndMultiUpdateSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndUpdate<>(criteria);
    }

    private List<_MySQLHint> hintList;

    private List<MySQLWords> modifierList;

    private JP noActionPartitionBlock;

    private MySQLIndexHint.Command command;

    private boolean updateCrossValid = true;

    private MySQLMultiUpdate(@Nullable C criteria) {
        super(CriteriaContexts.multiDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , TableMeta<?> table) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        return (UP) this.createClause(_JoinType.NONE, table);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , TableMeta<?> table, String tableAlias) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.createAndAddBlock(_JoinType.NONE, table, tableAlias);
        return (UT) this;
    }

    @Override
    public final UP update(TableMeta<?> table) {
        return (UP) this.createClause(_JoinType.NONE, table);
    }

    @Override
    public final UT update(TableMeta<?> table, String tableAlias) {
        this.createAndAddBlock(_JoinType.NONE, table, tableAlias);
        return (UT) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<T> supplier, String alias) {
        this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Function<C, T> function, String alias) {
        this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
        return (US) this;
    }

    /*################################## blow IndexHintClause method ##################################*/

    @Override
    public final IR useIndex() {
        if (this.updateCrossValid) {
            this.command = MySQLIndexHint.Command.USER_INDEX;
        }
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        if (this.updateCrossValid) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        }
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        if (this.updateCrossValid) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
        }
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (this.updateCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.USER_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (this.updateCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (this.updateCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final UT useIndex(List<String> indexList) {
        if (this.updateCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT ignoreIndex(List<String> indexList) {
        if (this.updateCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT forceIndex(List<String> indexList) {
        if (this.updateCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT ifUseIndex(Function<C, List<String>> function) {
        if (this.updateCrossValid) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (indexList != null && indexList.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
            }
        }
        return (UT) this;
    }

    @Override
    public final UT ifIgnoreIndex(Function<C, List<String>> function) {
        if (this.updateCrossValid) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (indexList != null && indexList.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
            }
        }
        return (UT) this;
    }

    @Override
    public final UT ifForceIndex(Function<C, List<String>> function) {
        if (this.updateCrossValid) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (indexList != null && indexList.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
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
    public final List<MySQLWords> modifierList() {
        return this.modifierList;
    }


    /*################################## blow package template method ##################################*/


    @Override
    final void doOnAsUpdate() {
        this.noActionPartitionBlock = null;
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this instanceof BatchUpdate) {
            if (((BatchUpdate<C, WE>) this).paramList == null) {
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
        this.modifierList = null;
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C, WE>) this).paramList = null;
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

    @Override
    final void crossJoinEvent(boolean success) {
        this.updateCrossValid = success;
    }


    /*################################## blow private method ##################################*/

    private UT addIndexHint(final MySQLIndexHint.Command command, final boolean forJoin
            , final @Nullable List<String> indexNames) {

        if (indexNames == null || indexNames.size() == 0) {
            throw MySQLUtils.indexListIsEmpty();
        }
        final _TableBlock block;
        block = this.criteriaContext.lastTableBlockWithoutOnClause();
        if (!(block instanceof MySQLNoOnBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        final MySQLNoOnBlock noOnBlock = (MySQLNoOnBlock) block;
        List<MySQLIndexHint> indexHintList = noOnBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            noOnBlock.indexHintList = indexHintList;
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


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static abstract class SimpleUpdate<C, WE> extends MySQLMultiUpdate<
            C,
            WE,
            _IndexHintJoinSpec<C>,
            _MultiJoinSpec<C>,
            _MultiPartitionJoinClause<C>,
            MySQLUpdate.IndexJoinOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiOnSpec<C>,
            MySQLUpdate.MultiPartitionOnSpec<C>,
            _UpdateSpec,
            MySQLUpdate.MultiWhereAndSpec<C>,
            MySQLUpdate.MultiWhereSpec<C>>
            implements MySQLUpdate.MultiWhereAndSpec<C>, MySQLUpdate.MultiWhereSpec<C>
            , _IndexHintJoinSpec<C>, _MultiUpdate57Clause<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
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

        private List<?> paramList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final _UpdateSpec paramList(List<?> beanList) {
            this.paramList = CriteriaUtils.paramList(beanList);
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
        public final List<?> paramList() {
            return this.paramList;
        }


    }// BatchUpdate

    private static final class SimpleWithAndUpdate<C> extends SimpleUpdate<C, _MultiUpdate57Clause<C>>
            implements _WithAndMultiUpdateSpec<C>, _MySQLWithClause {

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

        private MultiIndexHintOnSpec<C> noActionOnClause;

        private MultiPartitionOnSpec<C> noActionPartitionOnClause;

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }


        @Override
        public _TableBlock createAndAddBlock(final _JoinType joinType, final Object item, final String alias) {
            Objects.requireNonNull(item);
            final _TableBlock tableBlock;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN: {
                    if (item instanceof TableMeta) {
                        tableBlock = new MySQLNoOnBlock(joinType, item, alias);
                    } else {
                        tableBlock = new TableBlock.NoOnTableBlock(joinType, item, alias);
                    }
                }
                break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    if (item instanceof TableMeta) {
                        tableBlock = new SimpleIndexHintOnBlock<>(joinType, (TableMeta<?>) item, alias, this);
                    } else {
                        tableBlock = new OnClauseTableBlock<>(joinType, item, alias, this);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return tableBlock;
        }

        @Override
        public Object createClause(final _JoinType joinType, final TableMeta<?> table) {
            final Object clause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    clause = new SimplePartitionJoinClause<>(joinType, table, this);
                    break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN:
                    clause = new SimplePartitionOnClause<>(joinType, table, this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return clause;
        }

        @Override
        public Object getNoActionClause(final _JoinType joinType) {
            final Object noActionClause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    noActionClause = this;
                    break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN:
                    noActionClause = this.getNoActionOnClause();
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return noActionClause;
        }

        @Override
        public Object getNoActionClauseBeforeAs(_JoinType joinType) {
            final Object noActionClause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    noActionClause = this;
                    break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    MultiPartitionOnSpec<C> clause = this.noActionPartitionOnClause;
                    if (clause == null) {
                        clause = new SimpleNoActionPartitionOnClause<>(this::getNoActionOnClause);
                        this.noActionPartitionOnClause = clause;
                    }
                    noActionClause = clause;
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return noActionClause;
        }

        private MultiIndexHintOnSpec<C> getNoActionOnClause() {
            MultiIndexHintOnSpec<C> clause = this.noActionOnClause;
            if (clause == null) {
                clause = new SimpleNoActionIndexHintOnClause<>(this);
                this.noActionOnClause = clause;
            }
            return clause;
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


        @Override
        public _TableBlock createAndAddBlock(final _JoinType joinType, final Object item, final String alias) {
            final _TableBlock tableBlock;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return tableBlock;
        }

        @Override
        public Object createClause(_JoinType joinType, TableMeta<?> table) {
            return null;
        }

        @Override
        public Object getNoActionClause(_JoinType joinType) {
            return null;
        }

        @Override
        public Object getNoActionClauseBeforeAs(_JoinType joinType) {
            return null;
        }

    }//BatchWithAndUpdate


    private static final class SimplePartitionJoinClause<C, WE>
            extends MySQLPartitionClause<C, _AsClause<_IndexHintJoinSpec<C>>>
            implements _MultiPartitionJoinClause<C>, _AsClause<_IndexHintJoinSpec<C>> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleUpdate<C, WE> update;

        private SimplePartitionJoinClause(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C, WE> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public _IndexHintJoinSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final _TableBlock block;
            if (partitionList == null) {
                block = new MySQLNoOnBlock(this.joinType, this.table, tableAlias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, tableAlias, partitionList);
            }
            this.update.criteriaContext.onAddBlock(block);
            if (this.joinType == _JoinType.CROSS_JOIN) {
                this.update.crossJoinEvent(true);
            }
            return this.update;
        }

    }// SimplePartitionJoinSpec

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


    private static class SimpleIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate.IndexJoinOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            _MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.IndexJoinOnSpec<C> {

        public SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, _MultiJoinSpec<C> stmt) {
            super(joinType, table, alias, stmt);
        }

        public SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias
                , List<String> partitionList, _MultiJoinSpec<C> stmt) {
            super(joinType, table, alias, partitionList, stmt);
        }

    }// SimpleIndexHintBlock


    private static final class SimplePartitionOnClause<C, WE>
            extends MySQLPartitionClause<C, _AsClause<MultiIndexHintOnSpec<C>>>
            implements _AsClause<MultiIndexHintOnSpec<C>>, MySQLUpdate.MultiPartitionOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleUpdate<C, WE> update;

        private SimplePartitionOnClause(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C, WE> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public MultiIndexHintOnSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final SimpleIndexHintOnBlock<C> block;
            if (partitionList == null) {
                block = new SimpleIndexHintOnBlock<>(this.joinType, this.table, alias, this.update);
            } else {
                block = new SimpleIndexHintOnBlock<>(this.joinType, this.table, alias, partitionList, this.update);
            }
            this.update.criteriaContext.onAddBlock(block);
            return block;
        }

    }// SimplePartitionOnClause


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


    private static final class SimpleNoActionIndexHintOnClause<C> extends MySQLNoActionIndexHintOnClause<
            C,
            IndexJoinOnSpec<C>,
            MultiIndexHintOnSpec<C>,
            _MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.IndexJoinOnSpec<C> {

        private SimpleNoActionIndexHintOnClause(_MultiJoinSpec<C> update) {
            super(update);
        }

    }// SimpleNoActionIndexHintOnBlock


    private static final class SimpleNoActionPartitionOnClause<C> extends MySQLNoActionPartitionClause<
            C,
            _AsClause<MultiIndexHintOnSpec<C>>>
            implements MySQLUpdate.MultiPartitionOnSpec<C>, _AsClause<MultiIndexHintOnSpec<C>> {

        private final Supplier<MultiIndexHintOnSpec<C>> supplier;


        private SimpleNoActionPartitionOnClause(Supplier<MultiIndexHintOnSpec<C>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public MultiIndexHintOnSpec<C> as(String alias) {
            return this.supplier.get();
        }

    }// SimpleNoActionPartitionOnClause


    private static final class SimpleNoActionPartitionJoinClause<C> extends MySQLNoActionPartitionClause<
            C,
            _AsClause<MultiIndexhin<C>>>
            implements MySQLUpdate.MultiPartitionOnSpec<C>, _AsClause<MultiIndexHintOnSpec<C>> {

        private final Supplier<MultiIndexHintOnSpec<C>> supplier;


        private SimpleNoActionPartitionJoinClause(Supplier<MultiIndexHintOnSpec<C>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public MultiIndexHintOnSpec<C> as(String alias) {
            return this.supplier.get();
        }

    }// SimpleNoActionPartitionOnClause


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
    private static final class BatchNoActionIndexHintOnBlock<C> extends MySQLNoActionIndexHintOnClause<
            C,
            BatchIndexJoinOnSpec<C>,
            BatchMultiIndexHintOnSpec<C>,
            BatchMultiJoinSpec<C>>
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


}
