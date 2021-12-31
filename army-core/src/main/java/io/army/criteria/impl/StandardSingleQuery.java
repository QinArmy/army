package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class StandardSingleQuery<C, Q extends Query> extends SingleQuery<
        C,
        Q,
        StandardQuery.StandardFromSpec<C, Q>, // SR
        StandardQuery.StandardJoinSpec<C, Q>,// FT
        StandardQuery.StandardJoinSpec<C, Q>,// FS
        StandardQuery.StandardOnSpec<C, Q>, // JT
        StandardQuery.StandardOnSpec<C, Q>, // JR
        StandardQuery.StandardGroupBySpec<C, Q>, // WR
        StandardQuery.StandardWhereAndSpec<C, Q>, // AR
        StandardQuery.StandardHavingSpec<C, Q>, // GR
        StandardQuery.StandardOrderBySpec<C, Q>, // HR
        StandardQuery.StandardLimitSpec<C, Q>, // OR
        StandardQuery.StandardLockSpec<C, Q>, // LR
        StandardQuery.StandardUnionSpec<C, Q>, // UR
        StandardQuery.StandardSelectClauseSpec<C, Q>> // SP

        implements StandardQuery.StandardFromSpec<C, Q>, StandardQuery.StandardJoinSpec<C, Q>
        , StandardQuery.StandardGroupBySpec<C, Q>, StandardQuery.StandardWhereAndSpec<C, Q>
        , StandardQuery.StandardHavingSpec<C, Q>, StandardQuery.StandardOrderBySpec<C, Q>
        , StandardQuery.StandardSelectClauseSpec<C, Q>, StandardQuery.StandardLimitSpec<C, Q>
        , StandardQuery.StandardLockSpec<C, Q>, StandardQuery, _StandardQuery {


    static <C> StandardSingleQuery<C, Select> unionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
        return new UnionAndSelect<>(left, unionType, criteria);
    }


    private List<TableBlock> tableBlockList;

    private LockMode lockMode;

    StandardSingleQuery(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final StandardUnionClause<C, Q> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> lock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        Objects.requireNonNull(lockMode);
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode) {
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier) {
        final LockMode lockMode;
        lockMode = supplier.get();
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    final StandardJoinSpec<C, Q> addTableFromBlock(TableMeta<?> table, String tableAlias) {
        return this.addTablePartFromBlock(table, tableAlias);
    }

    @Override
    final StandardJoinSpec<C, Q> addTablePartFromBlock(TablePart tablePart, String alias) {
        final List<TableBlock> tableBlockList = new ArrayList<>();
        tableBlockList.add(new SimpleFormBlock(tablePart, alias));

        if (this.tableBlockList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableBlockList = tableBlockList;
        return this;
    }

    @Override
    final StandardOnSpec<C, Q> addTableBlock(TableMeta<?> table, String tableAlias, JoinType joinType) {
        return this.addTablePartBlock(table, tableAlias, joinType);
    }

    @Override
    final StandardOnSpec<C, Q> addTablePartBlock(TablePart tablePart, String tableAlias, JoinType joinType) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList == null) {
            throw _Exceptions.castCriteriaApi();
        }
        final StandardOnBlock<C, Q> block;
        block = new StandardOnBlock<>(tablePart, tableAlias, joinType, this);
        tableBlockList.add(block);
        return block;
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionTableBlock() {
        return this.createNoActionTablePartBlock();
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionTablePartBlock() {
        return new StandardNoActionOnSpec<>(this);
    }


    @Override
    final Q onAsQuery(final boolean outer) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (CollectionUtils.isEmpty(tableBlockList)) {
            this.tableBlockList = Collections.emptyList();
        } else {
            this.tableBlockList = CollectionUtils.unmodifiableList(tableBlockList);
        }
        return this.doOnAsQuery(outer);
    }


    @Override
    final void onClear() {
        this.tableBlockList = null;
        this.lockMode = null;
    }

    abstract Q doOnAsQuery(boolean outer);

    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }

    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }


    /*################################## blow private inter class method ##################################*/


    private static final class StandardNoActionOnSpec<C, Q extends Query>
            extends NoActionOnBlock<C, StandardJoinSpec<C, Q>> implements StandardOnSpec<C, Q> {

        private StandardNoActionOnSpec(StandardSingleQuery<C, Q> query) {
            super(query);
        }

    }

    private static final class StandardOnBlock<C, Q extends Query> extends OnBlock<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        StandardOnBlock(TablePart tablePart, String alias, JoinType joinType, StandardSingleQuery<C, Q> query) {
            super(tablePart, alias, joinType, query);
        }

        @Override
        C getCriteria() {
            return ((StandardSingleQuery<C, Q>) this.query).criteria;
        }

        @Override
        TableBlock getPreviousBock() {
            final List<TableBlock> tableBlockList = ((StandardSingleQuery<C, Q>) this.query).tableBlockList;
            final int size = tableBlockList.size();
            if (tableBlockList.get(size - 1) != this) {
                throw _Exceptions.castCriteriaApi();
            }
            return tableBlockList.get(size - 2);
        }


    }


    private static final class SingleSelect<C> extends StandardSingleQuery<C, Select>
            implements Select {

        private SingleSelect(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public StandardUnionSpec<C, Select> bracketsQuery() {
            return StandardUnionQuery.bracketSelect(this.asQuery(), this.criteria);
        }

        @Override
        StandardUnionSpec<C, Select> createUnionQuery(Select left, UnionType unionType, Select right) {
            return StandardUnionQuery.unionSelect(left, unionType, right, this.criteria);
        }

        @Override
        StandardSelectClauseSpec<C, Select> asQueryAndSelect(UnionType unionType) {
            return StandardSingleQuery.unionAndSelect(this.asQuery(), unionType, this.criteria);
        }

        @Override
        Select doOnAsQuery(boolean outer) {
            return this;
        }

    }

    private static final class UnionAndSelect<C> extends StandardSingleQuery<C, Select> implements Select {

        private final Select left;

        private final UnionType unionType;

        private UnionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        public StandardUnionSpec<C, Select> bracketsQuery() {
            final Select bracketSelect;
            if (this.asQueryForBracket() != this) {
                throw new IllegalStateException("doOnAsQuery(boolean) error");
            }
            bracketSelect = StandardUnionQuery.bracketSelect(this, this.criteria)
                    .asQuery();
            return StandardUnionQuery.unionSelect(this.left, this.unionType, bracketSelect, this.criteria);
        }

        @Override
        StandardUnionSpec<C, Select> createUnionQuery(Select left, UnionType unionType, Select right) {
            return StandardUnionQuery.unionSelect(left, unionType, right, this.criteria);
        }

        @Override
        StandardSelectClauseSpec<C, Select> asQueryAndSelect(UnionType unionType) {
            return StandardSingleQuery.unionAndSelect(this.asQuery(), unionType, this.criteria);
        }

        @Override
        Select doOnAsQuery(final boolean outer) {
            final Select select;
            if (outer) {
                select = StandardUnionQuery.unionSelect(this.left, this.unionType, this, this.criteria)
                        .asQuery();
            } else {
                select = this;
            }
            return select;
        }

    } // UnionAndSelect


    private static final class SingleSubQuery<C> extends StandardSingleQuery<C, SubQuery> implements SubQuery {

        private SingleSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public Selection selection(String derivedFieldName) {

            return null;
        }

        @Override
        public StandardUnionSpec<C, SubQuery> bracketsQuery() {
            return StandardUnionQuery.bracketSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        StandardUnionSpec<C, SubQuery> createUnionQuery(SubQuery left, UnionType unionType, SubQuery right) {
            return StandardUnionQuery.unionSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        StandardSelectClauseSpec<C, SubQuery> asQueryAndSelect(UnionType unionType) {
            return null;
        }

        @Override
        SubQuery doOnAsQuery(boolean outer) {
            return this;
        }

    } // SingleSubQuery


}
