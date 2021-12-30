package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class StandardQueryImpl<C, Q extends Query> extends SingleQuery<
        C,
        Q,
        StandardQuery.StandardFromSpec<C, Q>, // SR
        StandardQuery.StandardJoinSpec<C, Q>,// FT
        StandardQuery.StandardJoinSpec<C, Q>,// FS
        StandardQueryImpl.StandardOnBlock<C, Q>, // JT
        StandardQueryImpl.StandardOnBlock<C, Q>, // JR
        StandardQuery.StandardGroupBySpec<C, Q>, // WR
        StandardQuery.StandardWhereAndSpec<C, Q>, // WA
        StandardQuery.StandardHavingSpec<C, Q>,  // GR
        StandardQuery.StandardOrderBySpec<C, Q>, // HR
        StandardQuery.StandardOrderBySpec<C, Q>, // UR
        StandardQuery.StandardSelectClauseSpec<C, Q>, // SP
        StandardQuery.StandardLimitSpec<C, Q>, // OR
        StandardQuery.StandardLockSpec<C, Q>>  // LR

        implements StandardQuery.StandardFromSpec<C, Q>, StandardQuery.StandardJoinSpec<C, Q>
        , StandardQuery.StandardGroupBySpec<C, Q>, StandardQuery.StandardWhereAndSpec<C, Q>
        , StandardQuery.StandardHavingSpec<C, Q>, StandardQuery.StandardOrderBySpec<C, Q>
        , StandardQuery.StandardSelectClauseSpec<C, Q>, StandardQuery.StandardLimitSpec<C, Q>
        , StandardQuery.StandardLockSpec<C, Q>, StandardQuery {

    private LockMode lockMode;

    StandardQueryImpl(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final StandardUnionClause<C, Q> lock(LockMode lockMode) {
        return null;
    }

    @Override
    public final StandardUnionClause<C, Q> lock(Function<C, LockMode> function) {
        return null;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode) {
        return null;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier) {
        return null;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function) {
        return null;
    }

    static final class StandardOnBlock<C, Q extends Query> extends TableBlock implements TableOnSpec<Q, C> {


        private final StandardQueryImpl<Q, C> query;


        private StandardOnBlock(TablePart tablePart, String alias, JoinType joinType
                , StandardQueryImpl<Q, C> query) {
            super(tablePart, alias, joinType);
            this.query = query;
        }

        @Override
        public TableJoinSpec<Q, C> on(final List<IPredicate> predicateList) {
            final List<_Predicate> predicates = new ArrayList<>(predicateList.size());
            for (IPredicate predicate : predicateList) {
                predicates.add((_Predicate) predicate);
            }
            switch (predicates.size()) {
                case 0:
                    throw _Exceptions.onClauseIsEmpty();
                case 1:
                    this.predicates = Collections.singletonList(predicates.get(0));
                    break;
                default:
                    this.predicates = Collections.unmodifiableList(predicates);
            }
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate) {
            this.predicates = Collections.singletonList((_Predicate) predicate);
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate1, IPredicate predicate2) {
            this.predicates = ArrayUtils.asUnmodifiableList((_Predicate) predicate1, (_Predicate) predicate2);
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(Function<C, List<IPredicate>> function) {
            return this.on(function.apply(this.query.criteria));
        }

        @Override
        public TableJoinSpec<Q, C> on(Supplier<List<IPredicate>> supplier) {
            return this.on(supplier.get());
        }

        @Override
        public List<_Predicate> predicates() {
            final List<_Predicate> list = this.predicates;
            assert list != null;
            return list;
        }

        @Override
        public TableJoinSpec<Q, C> onId() {
            if (!(this.tablePart instanceof TableMeta)) {
                throw _Exceptions.castCriteriaApi();
            }
            final List<TableBlock> tableBlockList = this.query.tableBlockList;
            final int size = tableBlockList.size();
            final TableBlock thisBlock = tableBlockList.get(size - 1);
            if (this != thisBlock) {
                throw _Exceptions.castCriteriaApi();
            }
            final TableBlock lastBlock;
            lastBlock = tableBlockList.get(size - 2);
            if (!(lastBlock.tablePart instanceof TableMeta)) {
                throw _Exceptions.castCriteriaApi();
            }
            final _Predicate predicate;
            predicate = (_Predicate) (((TableMeta<?>) lastBlock.tablePart).id()
                    .equal(((TableMeta<?>) this.tablePart).id()));

            this.predicates = Collections.singletonList(predicate);
            return this.query;
        }


    }


    private static final class NoActionTableBlock<Q extends Query, C> implements TableOnSpec<Q, C> {

        private final StandardQueryImpl<Q, C> query;

        private NoActionTableBlock(StandardQueryImpl<Q, C> query) {
            this.query = query;
        }

        @Override
        public TableJoinSpec<Q, C> on(List<IPredicate> predicateList) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate1, IPredicate predicate2) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(Function<C, List<IPredicate>> function) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(Supplier<List<IPredicate>> supplier) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> onId() {
            return this.query;
        }

    }


}
