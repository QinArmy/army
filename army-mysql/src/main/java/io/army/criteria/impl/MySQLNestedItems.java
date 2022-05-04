package io.army.criteria.impl;

import io.army.criteria.NestedItems;
import io.army.criteria.Statement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQL80Query;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.Database;
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
 * This class is the implementation of {@link NestedItems} for MySQL.
 * </p>
 *
 * @see MySQLs80#nestedItems()
 * @see MySQLs80#nestedItems(Object)
 * @since 1.0
 */
final class MySQLNestedItems<C> implements MySQL80Query._NestedLeftBracketClause<C>, NestedItems
        , JoinableClause.NestedClauseSuppler {

    static <C> MySQLNestedItems<C> create(@Nullable C criteria) {
        return new MySQLNestedItems<>(criteria);
    }

    private final C criteria;

    private final List<_TableBlock> blockList = new ArrayList<>();

    private NoActionIndexHintJoinClause<C> noActionJoinClause;

    private NoActionIndexHintOnClause<C> noActionOnClause;

    private NoActionPartitionJoinClause<C> noActionPartitionJoinClause;

    private NoActionPartitionOnClause<C> noActionPartitionOnClause;


    private MySQLNestedItems(@Nullable C criteria) {
        this.criteria = criteria;
    }

    @Override
    public MySQL80Query._NestedPartitionJoinClause<C> leftBracket(TableMeta<?> table) {
        if (this.blockList.size() != 0) {
            throw _Exceptions.castCriteriaApi();
        }
        return new PartitionJoinClause<>(this.criteria, this, _JoinType.NONE, table);
    }

    @Override
    public MySQL80Query._NestedJoinSpec<C> leftBracket(String cteName) {
        return this.leftBracket(cteName, "");
    }

    @Override
    public MySQL80Query._NestedJoinSpec<C> leftBracket(String cteName, String alias) {
        if (this.blockList.size() != 0) {
            throw _Exceptions.castCriteriaApi();
        }
        final OnBlock<C> block;
        block = new OnBlock<>(this, this.criteria, _JoinType.NONE, cteName, alias);
        this.blockList.add(block);
        return block;
    }

    @Override
    public MySQL80Query._NestedIndexHintJoinSpec<C> leftBracket(TableMeta<?> table, String tableAlias) {
        if (this.blockList.size() != 0) {
            throw _Exceptions.castCriteriaApi();
        }
        final IndexHintJoinBlock<C> block;
        block = new IndexHintJoinBlock<>(this, this.criteria, _JoinType.NONE, table, tableAlias);
        this.blockList.add(block);
        return block;
    }

    @Override
    public <T extends TableItem> MySQL80Query._NestedJoinSpec<C> leftBracket(Supplier<T> supplier, String alias) {
        if (this.blockList.size() != 0) {
            throw _Exceptions.castCriteriaApi();
        }
        final OnBlock<C> block;
        block = new OnBlock<>(this, this.criteria, _JoinType.NONE, supplier.get(), alias);
        this.blockList.add(block);
        return block;
    }

    @Override
    public <T extends TableItem> MySQL80Query._NestedJoinSpec<C> leftBracket(Function<C, T> function, String alias) {
        if (this.blockList.size() != 0) {
            throw _Exceptions.castCriteriaApi();
        }
        final OnBlock<C> block;
        block = new OnBlock<>(this, this.criteria, _JoinType.NONE, function.apply(this.criteria), alias);
        this.blockList.add(block);
        return block;
    }

    @Override
    public _TableBlock createAndAddBlock(final _JoinType joinType, final Object item, final String tableAlias) {
        Objects.requireNonNull(item);
        if (item instanceof NestedItems && !(item instanceof MySQLNestedItems)) {
            throw CriteriaUtils.nestedItemsNotMatch((NestedItems) item, Database.MySQL);
        }
        final _TableBlock block;
        switch (joinType) {
            case CROSS_JOIN:
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN: {
                if (item instanceof TableMeta) {
                    block = new IndexHintOnBlock<>(this, this.criteria, joinType, item, tableAlias);
                } else {
                    block = new OnBlock<>(this, this.criteria, joinType, item, tableAlias);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        this.blockList.add(block);
        return block;
    }

    @Override
    public Object createClause(final _JoinType joinType, TableMeta<?> table) {
        final Object clause;
        switch (joinType) {
            case CROSS_JOIN:
                clause = new PartitionJoinClause<>(this.criteria, this, joinType, table);
                break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN:
                clause = new PartitionOnClause<>(this.criteria, this, joinType, table);
                break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        return clause;
    }

    @Override
    public Object getNoActionClause(final _JoinType joinType) {
        final Object clause;
        switch (joinType) {
            case CROSS_JOIN: {
                NoActionIndexHintJoinClause<C> joinClause = this.noActionJoinClause;
                if (joinClause == null) {
                    joinClause = new NoActionIndexHintJoinClause<>(this, this.criteria);
                    this.noActionJoinClause = joinClause;
                }
                clause = joinClause;
            }
            break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN: {
                NoActionIndexHintOnClause<C> onClause = this.noActionOnClause;
                if (onClause == null) {
                    onClause = new NoActionIndexHintOnClause<>(this, this.criteria);
                    this.noActionOnClause = onClause;
                }
                clause = onClause;
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        return clause;
    }

    @Override
    public Object getNoActionClauseBeforeAs(final _JoinType joinType) {
        final Object partitionClause;
        switch (joinType) {
            case CROSS_JOIN: {
                NoActionPartitionJoinClause<C> clause = this.noActionPartitionJoinClause;
                if (clause == null) {
                    clause = new NoActionPartitionJoinClause<>(joinType, this::getNoActionClause);
                    this.noActionPartitionJoinClause = clause;
                }
                partitionClause = clause;
            }
            break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN: {
                NoActionPartitionOnClause<C> clause = this.noActionPartitionOnClause;
                if (clause == null) {
                    clause = new NoActionPartitionOnClause<>(joinType, this::getNoActionClause);
                    this.noActionPartitionOnClause = clause;
                }
                partitionClause = clause;
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        return partitionClause;
    }

    @Override
    public NestedItems endNested() {
        return null;
    }


    private static class OnBlock<C> extends JoinableClause.OnOrJoinBlock<
            C,
            MySQL80Query._NestedIndexHintJoinSpec<C>,
            MySQL80Query._NestedJoinSpec<C>,
            MySQL80Query._NestedPartitionJoinClause<C>,
            MySQL80Query._NestedIndexHintOnSpec<C>,
            MySQL80Query._NestedOnSpec<C>,
            MySQL80Query._NestedPartitionOnClause<C>>
            implements MySQL80Query._NestedOnSpec<C> {

        private OnBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias) {
            super(suppler, criteria, joinType, tableItem, alias);
        }

        @Override
        void crossJoinEvent(boolean success) {
            //no-op
        }


    }//MySQLOnBlock


    @SuppressWarnings("unchecked")
    private static abstract class IndexHintBlock<C, IR, IC> extends OnBlock<C>
            implements MySQLQuery._IndexHintClause<C, IR, IC>, MySQLQuery._IndexPurposeClause<C, IC>
            , _MySQLTableBlock {

        private final List<String> partitionList;

        private boolean bracketCrossValid = true;

        private MySQLIndexHint.Command command;

        private List<MySQLIndexHint> indexHintList;

        private IndexHintBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias) {
            super(suppler, criteria, joinType, tableItem, alias);
            this.partitionList = Collections.emptyList();
        }

        private IndexHintBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias, List<String> partitionList) {
            super(suppler, criteria, joinType, tableItem, alias);
            this.partitionList = partitionList;
        }

        @Override
        public final IR useIndex() {
            if (this.bracketCrossValid) {
                this.command = MySQLIndexHint.Command.USER_INDEX;
            } else {
                this.command = null;
            }
            return (IR) this;
        }

        @Override
        public final IR ignoreIndex() {
            if (this.bracketCrossValid) {
                this.command = MySQLIndexHint.Command.IGNORE_INDEX;
            } else {
                this.command = null;
            }
            return (IR) this;
        }

        @Override
        public final IR forceIndex() {
            if (this.bracketCrossValid) {
                this.command = MySQLIndexHint.Command.FORCE_INDEX;
            } else {
                this.command = null;
            }
            return (IR) this;
        }

        @Override
        public final IR ifUseIndex(Predicate<C> predicate) {
            if (this.bracketCrossValid && predicate.test(this.criteria)) {
                this.command = MySQLIndexHint.Command.USER_INDEX;
            } else {
                this.command = null;
            }
            return (IR) this;
        }

        @Override
        public final IR ifIgnoreIndex(Predicate<C> predicate) {
            if (this.bracketCrossValid && predicate.test(this.criteria)) {
                this.command = MySQLIndexHint.Command.IGNORE_INDEX;
            } else {
                this.command = null;
            }
            return (IR) this;
        }

        @Override
        public final IR ifForceIndex(Predicate<C> predicate) {
            if (this.bracketCrossValid && predicate.test(this.criteria)) {
                this.command = MySQLIndexHint.Command.FORCE_INDEX;
            } else {
                this.command = null;
            }
            return (IR) this;
        }

        @Override
        public final IC useIndex(List<String> indexList) {
            if (this.bracketCrossValid) {
                this.indexHint(MySQLIndexHint.Command.USER_INDEX, null, indexList);
            }
            return (IC) this;
        }

        @Override
        public final IC ignoreIndex(List<String> indexList) {
            if (this.bracketCrossValid) {
                this.indexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, indexList);
            }
            return (IC) this;
        }

        @Override
        public final IC forceIndex(List<String> indexList) {
            if (this.bracketCrossValid) {
                this.indexHint(MySQLIndexHint.Command.FORCE_INDEX, null, indexList);
            }
            return (IC) this;
        }

        @Override
        public final IC ifUseIndex(Function<C, List<String>> function) {
            if (this.bracketCrossValid) {
                final List<String> indexNameList;
                indexNameList = function.apply(this.criteria);
                if (indexNameList != null && indexNameList.size() > 0) {
                    this.indexHint(MySQLIndexHint.Command.USER_INDEX, null, indexNameList);
                }
            }
            return (IC) this;
        }

        @Override
        public final IC ifIgnoreIndex(Function<C, List<String>> function) {
            if (this.bracketCrossValid) {
                final List<String> indexNameList;
                indexNameList = function.apply(this.criteria);
                if (indexNameList != null && indexNameList.size() > 0) {
                    this.indexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, indexNameList);
                }
            }
            return (IC) this;
        }

        @Override
        public final IC ifForceIndex(Function<C, List<String>> function) {
            if (this.bracketCrossValid) {
                final List<String> indexNameList;
                indexNameList = function.apply(this.criteria);
                if (indexNameList != null && indexNameList.size() > 0) {
                    this.indexHint(MySQLIndexHint.Command.FORCE_INDEX, null, indexNameList);
                }
            }
            return (IC) this;
        }


        @Override
        public final IC forOrderBy(List<String> indexList) {
            final MySQLIndexHint.Command command;
            command = this.command;
            if (command != null) {
                this.command = null;
                this.indexHint(command, MySQLIndexHint.Purpose.FOR_ORDER_BY, indexList);
            }
            return (IC) this;
        }

        @Override
        public final IC forOrderBy(Function<C, List<String>> function) {
            final MySQLIndexHint.Command command;
            command = this.command;
            if (command != null) {
                this.command = null;
                this.indexHint(command, MySQLIndexHint.Purpose.FOR_ORDER_BY, function.apply(this.criteria));
            }
            return (IC) this;
        }

        @Override
        public final IC forJoin(List<String> indexList) {
            final MySQLIndexHint.Command command;
            command = this.command;
            if (command != null) {
                this.command = null;
                this.indexHint(command, MySQLIndexHint.Purpose.FOR_JOIN, indexList);
            }
            return (IC) this;
        }

        @Override
        public final IC forJoin(Function<C, List<String>> function) {
            final MySQLIndexHint.Command command;
            command = this.command;
            if (command != null) {
                this.command = null;
                this.indexHint(command, MySQLIndexHint.Purpose.FOR_JOIN, function.apply(this.criteria));
            }
            return (IC) this;
        }

        @Override
        public final IC forGroupBy(List<String> indexList) {
            final MySQLIndexHint.Command command;
            command = this.command;
            if (command != null) {
                this.command = null;
                this.indexHint(command, MySQLIndexHint.Purpose.FOR_GROUP_BY, indexList);
            }
            return (IC) this;
        }

        @Override
        public final IC forGroupBy(Function<C, List<String>> function) {
            final MySQLIndexHint.Command command;
            command = this.command;
            if (command != null) {
                this.command = null;
                this.indexHint(command, MySQLIndexHint.Purpose.FOR_GROUP_BY, function.apply(this.criteria));
            }
            return (IC) this;
        }


        void indexHint(MySQLIndexHint.Command command, @Nullable MySQLIndexHint.Purpose purpose
                , final @Nullable List<String> indexNameList) {
            if (indexNameList == null || indexNameList.size() == 0) {
                throw MySQLUtils.indexListIsEmpty();
            }
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            }
            indexHintList.add(new MySQLIndexHint(command, purpose, indexNameList));
        }

        @Override
        void crossJoinEvent(final boolean success) {
            if (success) {
                throw new IllegalArgumentException();
            }
            this.bracketCrossValid = false;
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
            }
            return indexHintList;
        }

    }//IndexHintBlock


    private static final class IndexHintJoinBlock<C> extends IndexHintBlock<
            C,
            MySQL80Query._NestedIndexPurposeJoinClause<C>,
            MySQL80Query._NestedIndexHintJoinSpec<C>>
            implements MySQL80Query._NestedIndexPurposeJoinClause<C>, MySQL80Query._NestedIndexHintJoinSpec<C> {

        private IndexHintJoinBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias) {
            super(suppler, criteria, joinType, tableItem, alias);
        }

        private IndexHintJoinBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias, List<String> partitionList) {
            super(suppler, criteria, joinType, tableItem, alias, partitionList);
        }

    }//IndexHintJoinBlock

    private static final class IndexHintOnBlock<C> extends IndexHintBlock<
            C,
            MySQL80Query._NestedIndexPurposeOnClause<C>,
            MySQL80Query._NestedIndexHintOnSpec<C>>
            implements MySQL80Query._NestedIndexPurposeOnClause<C>, MySQL80Query._NestedIndexHintOnSpec<C> {

        private IndexHintOnBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias) {
            super(suppler, criteria, joinType, tableItem, alias);
        }

        private IndexHintOnBlock(NestedClauseSuppler suppler, @Nullable C criteria
                , _JoinType joinType, Object tableItem
                , String alias, List<String> partitionList) {
            super(suppler, criteria, joinType, tableItem, alias, partitionList);
        }

    }//IndexHintOnBlock


    private static final class PartitionJoinClause<C>
            extends MySQLPartitionClause<C, Statement._AsClause<MySQL80Query._NestedIndexHintJoinSpec<C>>>
            implements Statement._AsClause<MySQL80Query._NestedIndexHintJoinSpec<C>>
            , MySQL80Query._NestedPartitionJoinClause<C> {

        private final MySQLNestedItems<C> suppler;

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private String alias;

        private PartitionJoinClause(@Nullable C criteria, MySQLNestedItems<C> suppler
                , _JoinType joinType, TableMeta<?> table) {
            super(criteria);
            this.suppler = suppler;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public MySQL80Query._NestedIndexHintJoinSpec<C> as(final String alias) {
            if (this.alias != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            this.alias = alias;
            final List<String> partitionList = this.partitionList;
            final IndexHintJoinBlock<C> block;
            if (partitionList == null) {
                block = new IndexHintJoinBlock<>(this.suppler, this.criteria, this.joinType, this.table, alias);
            } else {
                block = new IndexHintJoinBlock<>(this.suppler, this.criteria
                        , this.joinType, this.table, alias, partitionList);
            }
            this.suppler.blockList.add(block);
            return block;
        }


    }//PartitionJoinClause


    private static final class PartitionOnClause<C>
            extends MySQLPartitionClause<C, Statement._AsClause<MySQL80Query._NestedIndexHintOnSpec<C>>>
            implements Statement._AsClause<MySQL80Query._NestedIndexHintOnSpec<C>>
            , MySQL80Query._NestedPartitionOnClause<C> {

        private final MySQLNestedItems<C> suppler;

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private String alias;

        private PartitionOnClause(@Nullable C criteria, MySQLNestedItems<C> suppler
                , _JoinType joinType, TableMeta<?> table) {
            super(criteria);
            this.suppler = suppler;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public MySQL80Query._NestedIndexHintOnSpec<C> as(final String alias) {
            if (this.alias != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            this.alias = alias;
            final List<String> partitionList = this.partitionList;
            final IndexHintOnBlock<C> block;
            if (partitionList == null) {
                block = new IndexHintOnBlock<>(this.suppler, this.criteria, this.joinType, this.table, alias);
            } else {
                block = new IndexHintOnBlock<>(this.suppler, this.criteria
                        , this.joinType, this.table, alias, partitionList);
            }
            this.suppler.blockList.add(block);
            return block;
        }


    }//PartitionJoinClause

    private static class NoActionOnBlock<C> extends JoinableClause.NoActionOnOrJoinBlock<
            C,
            MySQL80Query._NestedIndexHintJoinSpec<C>,
            MySQL80Query._NestedJoinSpec<C>,
            MySQL80Query._NestedPartitionJoinClause<C>,
            MySQL80Query._NestedIndexHintOnSpec<C>,
            MySQL80Query._NestedOnSpec<C>,
            MySQL80Query._NestedPartitionOnClause<C>>
            implements MySQL80Query._NestedOnSpec<C> {

        private NoActionOnBlock(NestedClauseSuppler suppler, @Nullable C criteria) {
            super(suppler, criteria);
        }


    }//NoActionOnBlock

    @SuppressWarnings("unchecked")
    private static abstract class NoActionIndexHintClause<C, IR, IC> extends NoActionOnBlock<C>
            implements MySQLQuery._IndexHintClause<C, IR, IC>, MySQLQuery._IndexPurposeClause<C, IC> {

        private NoActionIndexHintClause(NestedClauseSuppler suppler, @Nullable C criteria) {
            super(suppler, criteria);
        }

        @Override
        public final IR useIndex() {
            return (IR) this;
        }

        @Override
        public final IR ignoreIndex() {
            return (IR) this;
        }

        @Override
        public final IR forceIndex() {
            return (IR) this;
        }

        @Override
        public final IR ifUseIndex(Predicate<C> predicate) {
            return (IR) this;
        }

        @Override
        public final IR ifIgnoreIndex(Predicate<C> predicate) {
            return (IR) this;
        }

        @Override
        public final IR ifForceIndex(Predicate<C> predicate) {
            return (IR) this;
        }

        @Override
        public final IC useIndex(List<String> indexList) {
            return (IC) this;
        }

        @Override
        public final IC ignoreIndex(List<String> indexList) {
            return (IC) this;
        }

        @Override
        public final IC forceIndex(List<String> indexList) {
            return (IC) this;
        }

        @Override
        public final IC ifUseIndex(Function<C, List<String>> function) {
            return (IC) this;
        }

        @Override
        public final IC ifIgnoreIndex(Function<C, List<String>> function) {
            return (IC) this;
        }

        @Override
        public final IC ifForceIndex(Function<C, List<String>> function) {
            return (IC) this;
        }

        @Override
        public final IC forOrderBy(List<String> indexList) {
            return (IC) this;
        }

        @Override
        public final IC forOrderBy(Function<C, List<String>> function) {
            return (IC) this;
        }

        @Override
        public final IC forJoin(List<String> indexList) {
            return (IC) this;
        }

        @Override
        public final IC forJoin(Function<C, List<String>> function) {
            return (IC) this;
        }

        @Override
        public final IC forGroupBy(List<String> indexList) {
            return (IC) this;
        }

        @Override
        public final IC forGroupBy(Function<C, List<String>> function) {
            return (IC) this;
        }

    }//NoActionIndexHintClause

    private static final class NoActionIndexHintJoinClause<C> extends NoActionIndexHintClause<
            C,
            MySQL80Query._NestedIndexPurposeJoinClause<C>,
            MySQL80Query._NestedIndexHintJoinSpec<C>>
            implements MySQL80Query._NestedIndexPurposeJoinClause<C>, MySQL80Query._NestedIndexHintJoinSpec<C> {

        private NoActionIndexHintJoinClause(NestedClauseSuppler suppler, @Nullable C criteria) {
            super(suppler, criteria);
        }

    }//NoActionIndexHintJoinClause

    private static final class NoActionIndexHintOnClause<C> extends NoActionIndexHintClause<
            C,
            MySQL80Query._NestedIndexPurposeOnClause<C>,
            MySQL80Query._NestedIndexHintOnSpec<C>>
            implements MySQL80Query._NestedIndexPurposeOnClause<C>, MySQL80Query._NestedIndexHintOnSpec<C> {

        private NoActionIndexHintOnClause(NestedClauseSuppler suppler, @Nullable C criteria) {
            super(suppler, criteria);
        }

    }//NoActionIndexHintJoinClause


    private static final class NoActionPartitionJoinClause<C>
            extends MySQLNoActionPartitionClause<C, Statement._AsClause<MySQL80Query._NestedIndexHintJoinSpec<C>>>
            implements Statement._AsClause<MySQL80Query._NestedIndexHintJoinSpec<C>>
            , MySQL80Query._NestedPartitionJoinClause<C> {

        private final _JoinType joinType;

        private final Function<_JoinType, ?> function;

        private NoActionPartitionJoinClause(_JoinType joinType
                , Function<_JoinType, ?> function) {
            this.joinType = joinType;
            this.function = function;
        }

        @SuppressWarnings("unchecked")
        @Override
        public MySQL80Query._NestedIndexHintJoinSpec<C> as(String alias) {
            return (MySQL80Query._NestedIndexHintJoinSpec<C>) this.function.apply(this.joinType);
        }

    }//NoActionPartitionJoinClause

    private static final class NoActionPartitionOnClause<C>
            extends MySQLNoActionPartitionClause<C, Statement._AsClause<MySQL80Query._NestedIndexHintOnSpec<C>>>
            implements Statement._AsClause<MySQL80Query._NestedIndexHintOnSpec<C>>
            , MySQL80Query._NestedPartitionOnClause<C> {

        private final _JoinType joinType;

        private final Function<_JoinType, ?> function;

        private NoActionPartitionOnClause(_JoinType joinType
                , Function<_JoinType, ?> function) {
            this.joinType = joinType;
            this.function = function;
        }

        @SuppressWarnings("unchecked")
        @Override
        public MySQL80Query._NestedIndexHintOnSpec<C> as(String alias) {
            return (MySQL80Query._NestedIndexHintOnSpec<C>) this.function.apply(this.joinType);
        }

    }//NoActionPartitionJoinClause

}
