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

/**
 * @see StandardUnionQuery
 */
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

    static <C, E> ColumnSubQuery.StandardColumnSubQuerySpec<C> columnSubQuery(@Nullable C criteria) {
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

    static <C, E> StandardSimpleQuery<C, ColumnSubQuery> unionAndColumnSubQuery(ColumnSubQuery left
            , UnionType unionType, @Nullable C criteria) {
        return new UnionAndColumnSubQuery<>(left, unionType, criteria);
    }

    static <C, E> StandardSimpleQuery<C, ScalarQueryExpression<E>> unionAndScalarSubQuery(ScalarQueryExpression<E> left
            , UnionType unionType, @Nullable C criteria) {
        return new UnionAndScalarSubQuery<>(left, unionType, criteria);
    }

    @SuppressWarnings("unchecked")
    static <C, Q extends Query> StandardSelectClauseSpec<C, Q> asQueryAndQuery(Q query, UnionType unionType, @Nullable C criteria) {
        final StandardSelectClauseSpec<C, ?> spec;
        if (query instanceof Select) {
            spec = StandardSimpleQuery.unionAndSelect((Select) query, unionType, criteria);
        } else if (query instanceof ScalarSubQuery) {
            spec = StandardSimpleQuery.unionAndScalarSubQuery((ScalarQueryExpression<?>) query, unionType, criteria);
        } else if (query instanceof ColumnSubQuery) {
            spec = StandardSimpleQuery.unionAndColumnSubQuery((ColumnSubQuery) query, unionType, criteria);
        } else if (query instanceof RowSubQuery) {
            spec = StandardSimpleQuery.unionAndRowSubQuery((RowSubQuery) query, unionType, criteria);
        } else if (query instanceof SubQuery) {
            spec = StandardSimpleQuery.unionAndSubQuery((SubQuery) query, unionType, criteria);
        } else {
            throw new IllegalStateException("unknown query type");
        }
        return (StandardSelectClauseSpec<C, Q>) spec;
    }


    private List<TableBlock> tableBlockList;

    private LockMode lockMode;

    StandardSimpleQuery(@Nullable C criteria) {
        super(null, criteria);
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
    public final StandardUnionSpec<C, Q> bracketsQuery() {
        final StandardUnionSpec<C, Q> unionSpec;
        if (this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> andQuery = (AbstractUnionAndQuery<C, Q>) this;
            final Q thisQuery = this.asQueryAndQuery();
            if (this instanceof ScalarSubQuery) {
                if (!(thisQuery instanceof ScalarSubQueryExpression)
                        || ((ScalarSubQueryExpression<?>) thisQuery).subQuery != this) {
                    throw asQueryMethodError();
                }
            } else if (thisQuery != this) {
                throw asQueryMethodError();
            }
            final Q right;
            right = StandardUnionQuery.bracket(thisQuery, this.criteria)
                    .asQuery();
            unionSpec = StandardUnionQuery.union(andQuery.left, andQuery.unionType, right, this.criteria);
        } else {
            unionSpec = StandardUnionQuery.bracket(this.asQuery(), this.criteria);
        }
        return unionSpec;
    }

    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(final Q left, final UnionType unionType, final Q right) {
        return StandardUnionQuery.union(left, unionType, right, criteria);
    }

    @Override
    final StandardSelectClauseSpec<C, Q> asQueryAndQuery(final UnionType unionType) {
        return StandardSimpleQuery.asQueryAndQuery(this.asQuery(), unionType, this.criteria);
    }


    @Override
    final StandardJoinSpec<C, Q> addTableFromBlock(TableMeta<?> table, String tableAlias) {
        return this.addTablePartFromBlock(table, tableAlias);
    }

    @Override
    final StandardJoinSpec<C, Q> addTablePartFromBlock(TablePart tablePart, String alias) {
        final List<TableBlock> tableBlockList = new ArrayList<>();
        tableBlockList.add(TableBlock.simple(tablePart, alias));

        if (this.tableBlockList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableBlockList = tableBlockList;
        return this;
    }

    @Override
    final StandardOnSpec<C, Q> addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
        return this.addOnBlock(joinType, table, tableAlias);
    }

    @Override
    final StandardOnSpec<C, Q> addOnBlock(JoinType joinType, TablePart tablePart, String tableAlias) {
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
        return this.createNoActionOnBlock();
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionOnBlock() {
        return new StandardNoActionOnSpec<>(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    final Q onAsQuery(final boolean justAsQuery) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (CollectionUtils.isEmpty(tableBlockList)) {
            this.tableBlockList = Collections.emptyList();
        } else {
            this.tableBlockList = CollectionUtils.unmodifiableList(tableBlockList);
        }
        final Q thisQuery, resultQuery;
        if (this instanceof ScalarSubQuery) {
            thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
        } else {
            thisQuery = (Q) this;
        }
        if (justAsQuery && this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> unionAndQuery = (AbstractUnionAndQuery<C, Q>) this;
            resultQuery = StandardUnionQuery.union(unionAndQuery.left, unionAndQuery.unionType, thisQuery, this.criteria)
                    .asQuery();
        } else {
            resultQuery = thisQuery;
        }
        return resultQuery;
    }


    @Override
    final void onClear() {
        this.tableBlockList = null;
        this.lockMode = null;
    }

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
            extends OnClauseTableBlock.NoActionOnBlock<C, StandardJoinSpec<C, Q>> implements StandardOnSpec<C, Q> {

        private StandardNoActionOnSpec(StandardSimpleQuery<C, Q> query) {
            super(query);
        }

    }

    private static final class StandardOnBlock<C, Q extends Query> extends OnClauseTableBlock.AliasOnClauses<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        StandardOnBlock(TablePart tablePart, String alias, JoinType joinType, StandardSimpleQuery<C, Q> query) {
            super(tablePart, alias, joinType, query);
        }

        @Override
        C getCriteria() {
            return ((StandardSimpleQuery<C, ?>) this.query).criteria;
        }
    } // StandardOnBlock


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q> {

        private final Q left;

        private final UnionType unionType;

        AbstractUnionAndQuery(Q left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }


    }//AbstractUnionAndQuery


    /**
     * @see #standardSelect(Object)
     */
    private static final class SimpleSelect<C> extends StandardSimpleQuery<C, Select>
            implements Select, StandardQuery.SelectSpec<C> {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleSelect

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
    private static final class SimpleSubQuery<C> extends StandardSimpleQuery<C, SubQuery> implements SubQuery
            , SubQuery.StandardSubQuerySpec<C> {

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }


    } // SimpleSubQuery


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
    private static final class SimpleRowSubQuery<C> extends StandardSimpleQuery<C, RowSubQuery> implements RowSubQuery
            , RowSubQuery.StandardRowSubQuerySpec<C> {

        private SimpleRowSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    } // StandardSimpleQuery

    /**
     * @see #unionAndRowSubQuery(RowSubQuery, UnionType, Object)
     */
    private static final class UnionAndRowSubQuery<C> extends AbstractUnionAndQuery<C, RowSubQuery>
            implements RowSubQuery, RowSubQuery.StandardRowSubQuerySpec<C> {

        private UnionAndRowSubQuery(RowSubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndRowSubQuery


    /**
     * @see #columnSubQuery(Object)
     */
    private static final class SimpleColumnSubQuery<C> extends StandardSimpleQuery<C, ColumnSubQuery>
            implements ColumnSubQuery, ColumnSubQuery.StandardColumnSubQuerySpec<C> {

        private SimpleColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleColumnSubQuery


    /**
     * @see #unionAndColumnSubQuery(ColumnSubQuery, UnionType, Object)
     */
    private static final class UnionAndColumnSubQuery<C> extends AbstractUnionAndQuery<C, ColumnSubQuery>
            implements ColumnSubQuery, ColumnSubQuery.StandardColumnSubQuerySpec<C> {

        private UnionAndColumnSubQuery(ColumnSubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }


    }// UnionAndColumnSubQuery

    /**
     * @see #scalarSubQuery(Object)
     */
    private static final class SimpleScalarSubQuery<C, E> extends StandardSimpleQuery<C, ScalarQueryExpression<E>>
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
