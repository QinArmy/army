package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class StandardSimpleQuery<C, Q extends Query> extends SimpleQuery<
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


    static <C> StandardQuery.SelectSpec<C> standardSelect(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> SubQuery.StandardSubQuerySpec<C> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C> RowSubQuery.StandardRowSubQuerySpec<C> rowSubQuery(@Nullable C criteria) {
        return new SimpleRowSubQuery<>(criteria);
    }

    static <C, E> ColumnSubQuery.StandardColumnSubQuerySpec<C, E> columnSubQuery(@Nullable C criteria) {
        return new SimpleColumnSubQuery<>(criteria);
    }

    static <C, E> ScalarSubQuery.StandardScalarSubQuerySpec<C, E> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }

    static <C> StandardSimpleQuery<C, Select> unionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
        return new UnionAndSelect<>(left, unionType, criteria);
    }

    static <C> StandardSimpleQuery<C, SubQuery> unionAndSubQuery(SubQuery left, UnionType unionType, @Nullable C criteria) {
        return new UnionAndSubQuery<>(left, unionType, criteria);
    }

    static <C> StandardSimpleQuery<C, RowSubQuery> unionAndRowSubQuery(RowSubQuery left, UnionType unionType
            , @Nullable C criteria) {
        return new UnionAndRowSubQuery<>(left, unionType, criteria);
    }

    static <C, E> StandardSimpleQuery<C, ColumnSubQuery<E>> unionAndColumnSubQuery(ColumnSubQuery<E> left
            , UnionType unionType, @Nullable C criteria) {
        return new UnionAndColumnSubQuery<>(left, unionType, criteria);
    }

    static <C, E> StandardSimpleQuery<C, ScalarQueryExpression<E>> unionAndScalarSubQuery(ScalarQueryExpression<E> left
            , UnionType unionType, @Nullable C criteria) {
        return new UnionAndScalarSubQuery<>(left, unionType, criteria);
    }


    private List<TableBlock> tableBlockList;

    private LockMode lockMode;

    StandardSimpleQuery(@Nullable C criteria) {
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


    @SuppressWarnings("unchecked")
    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(final Q left, final UnionType unionType, final Q right) {
        final StandardUnionSpec<C, ?> unionSpec;
        if (left instanceof Select) {
            unionSpec = StandardUnionQuery.unionSelect((Select) left, unionType, (Select) right, this.criteria);
        } else if (left instanceof ScalarSubQuery) {
            unionSpec = StandardUnionQuery.unionScalarSubQuery((ScalarQueryExpression<Object>) left, unionType, (ScalarQueryExpression<Object>) right, this.criteria);
        } else if (left instanceof ColumnSubQuery) {
            unionSpec = StandardUnionQuery.unionColumnSubQuery((ColumnSubQuery<Object>) left, unionType, (ColumnSubQuery<Object>) right, this.criteria);
        } else if (left instanceof RowSubQuery) {
            unionSpec = StandardUnionQuery.unionRowSubQuery((RowSubQuery) left, unionType, (RowSubQuery) right, this.criteria);
        } else if (left instanceof SubQuery) {
            unionSpec = StandardUnionQuery.unionSubQuery((SubQuery) left, unionType, (SubQuery) right, this.criteria);
        } else {
            throw new IllegalStateException("unknown query type");
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }

    @SuppressWarnings("unchecked")
    @Override
    final StandardSelectClauseSpec<C, Q> asQueryAndQuery(final UnionType unionType) {
        final Q query;
        query = this.asQuery();
        final StandardSelectClauseSpec<C, ?> spec;
        if (query instanceof Select) {
            spec = StandardSimpleQuery.unionAndSelect((Select) query, unionType, this.criteria);
        } else if (query instanceof ScalarSubQuery) {
            spec = StandardSimpleQuery.unionAndScalarSubQuery((ScalarQueryExpression<?>) query, unionType, this.criteria);
        } else if (query instanceof ColumnSubQuery) {
            spec = StandardSimpleQuery.unionAndColumnSubQuery((ColumnSubQuery<?>) query, unionType, this.criteria);
        } else if (query instanceof RowSubQuery) {
            spec = StandardSimpleQuery.unionAndRowSubQuery((RowSubQuery) query, unionType, this.criteria);
        } else if (query instanceof SubQuery) {
            spec = StandardSimpleQuery.unionAndSubQuery((SubQuery) query, unionType, this.criteria);
        } else {
            throw new IllegalStateException("unknown query type");
        }
        return (StandardSelectClauseSpec<C, Q>) spec;
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
    final Q onAsQuery(final boolean justAsQuery) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (CollectionUtils.isEmpty(tableBlockList)) {
            this.tableBlockList = Collections.emptyList();
        } else {
            this.tableBlockList = CollectionUtils.unmodifiableList(tableBlockList);
        }
        return this.doOnAsQuery(justAsQuery);
    }


    @Override
    final void onClear() {
        this.tableBlockList = null;
        this.lockMode = null;
    }

    abstract Q doOnAsQuery(boolean justAsQuery);

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

        private StandardNoActionOnSpec(StandardSimpleQuery<C, Q> query) {
            super(query);
        }

    }

    private static final class StandardOnBlock<C, Q extends Query> extends OnBlock<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        StandardOnBlock(TablePart tablePart, String alias, JoinType joinType, StandardSimpleQuery<C, Q> query) {
            super(tablePart, alias, joinType, query);
        }

        @Override
        C getCriteria() {
            return ((StandardSimpleQuery<C, Q>) this.query).criteria;
        }

        @Override
        TableBlock getPreviousBock() {
            final List<TableBlock> tableBlockList = ((StandardSimpleQuery<C, Q>) this.query).tableBlockList;
            final int size = tableBlockList.size();
            if (tableBlockList.get(size - 1) != this) {
                throw _Exceptions.castCriteriaApi();
            }
            return tableBlockList.get(size - 2);
        }


    }

    private static abstract class AbstractSimpleQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q> {

        AbstractSimpleQuery(@Nullable C criteria) {
            super(criteria);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final StandardUnionSpec<C, Q> bracketsQuery() {
            final Q query;
            query = this.asQuery();
            final StandardUnionSpec<C, ?> unionSpec;
            if (query instanceof Select) {
                unionSpec = StandardUnionQuery.bracketSelect((Select) query, this.criteria);
            } else if (query instanceof ScalarSubQuery) {
                unionSpec = StandardUnionQuery.bracketScalarSubQuery((ScalarQueryExpression<?>) query, this.criteria);
            } else if (query instanceof ColumnSubQuery) {
                unionSpec = StandardUnionQuery.bracketColumnSubQuery((ColumnSubQuery<?>) query, this.criteria);
            } else if (query instanceof RowSubQuery) {
                unionSpec = StandardUnionQuery.bracketRowSubQuery((RowSubQuery) query, this.criteria);
            } else if (query instanceof SubQuery) {
                unionSpec = StandardUnionQuery.bracketSubQuery((SubQuery) query, this.criteria);
            } else {
                throw new IllegalStateException("unknown query type");
            }
            return (StandardUnionSpec<C, Q>) unionSpec;
        }

        @SuppressWarnings("unchecked")
        @Override
        final Q doOnAsQuery(boolean justAsQuery) {
            final Q query;
            if (this instanceof ScalarSubQuery) {
                query = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
            } else {
                query = (Q) this;
            }
            return query;
        }

    }// AbstractSingleQuery


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q> {

        private final Q left;

        private final UnionType unionType;

        AbstractUnionAndQuery(Q left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final StandardUnionSpec<C, Q> bracketsQuery() {
            final Q thisQuery;
            thisQuery = this.asQueryAndQuery();
            if (thisQuery instanceof ScalarSubQueryExpression) {
                if (((ScalarSubQueryExpression<?>) thisQuery).subQuery != this) {
                    throw new IllegalStateException("doOnAsQuery(boolean) error.");
                }
            } else if (thisQuery != this) {
                throw new IllegalStateException("doOnAsQuery(boolean) error.");
            }
            final Query right;
            if (thisQuery instanceof Select) {
                right = StandardUnionQuery.bracketSelect((Select) thisQuery, this.criteria)
                        .asQuery();
            } else if (thisQuery instanceof ScalarSubQuery) {
                right = StandardUnionQuery.bracketScalarSubQuery((ScalarQueryExpression<Object>) thisQuery, this.criteria)
                        .asQuery();
            } else if (thisQuery instanceof ColumnSubQuery) {
                right = StandardUnionQuery.bracketColumnSubQuery((ColumnSubQuery<Object>) thisQuery, this.criteria)
                        .asQuery();
            } else if (thisQuery instanceof RowSubQuery) {
                right = StandardUnionQuery.bracketRowSubQuery((RowSubQuery) thisQuery, this.criteria)
                        .asQuery();
            } else if (thisQuery instanceof SubQuery) {
                right = StandardUnionQuery.bracketSubQuery((SubQuery) thisQuery, this.criteria)
                        .asQuery();
            } else {
                throw new IllegalStateException("unknown query type");
            }
            return createUnionQuery(this.left, this.unionType, (Q) right);
        }

        @SuppressWarnings("unchecked")
        @Override
        final Q doOnAsQuery(final boolean justAsQuery) {
            final Q thisQuery;
            if (this instanceof ScalarSubQuery) {
                thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
            } else {
                thisQuery = (Q) this;
            }
            final Q query;
            if (justAsQuery) {
                query = createUnionQuery(this.left, unionType, thisQuery)
                        .asQuery();
            } else {
                query = thisQuery;
            }
            return query;
        }

    }//UnionAndQuery


    /**
     * @see #standardSelect(Object)
     */
    private static final class SimpleSelect<C> extends AbstractSimpleQuery<C, Select>
            implements Select, StandardQuery.SelectSpec<C> {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }

    /**
     * @see #unionAndSelect(Select, UnionType, Object)
     */
    private static final class UnionAndSelect<C> extends AbstractUnionAndQuery<C, Select>
            implements Select, StandardQuery.SelectSpec<C> {

        private UnionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    } // UnionAndSelect


    /**
     * @see #subQuery(Object)
     */
    private static final class SimpleSubQuery<C> extends AbstractSimpleQuery<C, SubQuery> implements SubQuery
            , SubQuery.StandardSubQuerySpec<C> {

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }


    } // SingleSubQuery


    /**
     * @see #unionAndSubQuery(SubQuery, UnionType, Object)
     */
    private static final class UnionAndSubQuery<C> extends AbstractUnionAndQuery<C, SubQuery> implements SubQuery
            , SubQuery.StandardSubQuerySpec<C> {

        private UnionAndSubQuery(SubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndSubQuery


    /**
     * @see #rowSubQuery(Object)
     */
    private static final class SimpleRowSubQuery<C> extends AbstractSimpleQuery<C, RowSubQuery> implements RowSubQuery
            , RowSubQuery.StandardRowSubQuerySpec<C> {

        private SimpleRowSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    } // SingleRowSubQuery

    /**
     * @see #unionAndRowSubQuery(RowSubQuery, UnionType, Object)
     */
    private static final class UnionAndRowSubQuery<C> extends AbstractUnionAndQuery<C, RowSubQuery> implements RowSubQuery
            , RowSubQuery.StandardRowSubQuerySpec<C> {

        private UnionAndRowSubQuery(RowSubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndRowSubQuery


    /**
     * @see #columnSubQuery(Object)
     */
    private static final class SimpleColumnSubQuery<C, E> extends AbstractSimpleQuery<C, ColumnSubQuery<E>>
            implements ColumnSubQuery<E>, ColumnSubQuery.StandardColumnSubQuerySpec<C, E> {

        private SimpleColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleColumnSubQuery


    /**
     * @see #unionAndColumnSubQuery(ColumnSubQuery, UnionType, Object)
     */
    private static final class UnionAndColumnSubQuery<C, E> extends AbstractUnionAndQuery<C, ColumnSubQuery<E>>
            implements ColumnSubQuery<E>, ColumnSubQuery.StandardColumnSubQuerySpec<C, E> {

        private UnionAndColumnSubQuery(ColumnSubQuery<E> left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }


    }// UnionAndColumnSubQuery

    /**
     * @see #scalarSubQuery(Object)
     */
    private static final class SimpleScalarSubQuery<C, E> extends AbstractSimpleQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery.StandardScalarSubQuerySpec<C, E>, ScalarSubQuery<E> {

        private SimpleScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }


    }// SimpleScalarSubQuery

    /**
     * @see #unionAndScalarSubQuery(ScalarQueryExpression, UnionType, Object)
     */
    private static final class UnionAndScalarSubQuery<C, E> extends AbstractUnionAndQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery.StandardScalarSubQuerySpec<C, E>, ScalarSubQuery<E> {

        private UnionAndScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }


    }// UnionAndScalarSubQuery


}
