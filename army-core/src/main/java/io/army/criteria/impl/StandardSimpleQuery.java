package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

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
        StandardQuery.StandardSelectSpec<C, Q>> // SP

        implements StandardQuery, StandardQuery.StandardSelectSpec<C, Q>, StandardQuery.StandardFromSpec<C, Q>
        , StandardQuery.StandardJoinSpec<C, Q>, StandardQuery.StandardGroupBySpec<C, Q>
        , StandardQuery.StandardWhereAndSpec<C, Q>, StandardQuery.StandardHavingSpec<C, Q>
        , StandardQuery.StandardOrderBySpec<C, Q>, StandardQuery.StandardLimitSpec<C, Q>
        , StandardQuery.StandardLockSpec<C, Q>, _StandardQuery {


    static <C> StandardSelectSpec<C, Select> query(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> StandardSelectSpec<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C> StandardSelectSpec<C, RowSubQuery> rowSubQuery(@Nullable C criteria) {
        return new SimpleRowSubQuery<>(criteria);
    }

    static <C> StandardSelectSpec<C, ColumnSubQuery> columnSubQuery(@Nullable C criteria) {
        return new SimpleColumnSubQuery<>(criteria);
    }

    static <C, E> StandardSelectSpec<C, ScalarQueryExpression<E>> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }


    static <C, Q extends Query> StandardSelectSpec<C, Q> asQueryAndQuery(Q query, UnionType unionType) {
        final StandardSelectSpec<C, ?> spec;
        if (query instanceof Select) {
            spec = new UnionAndSelect<>((Select) query, unionType);
        } else if (query instanceof ScalarSubQuery) {
            spec = new UnionAndScalarSubQuery<>((ScalarQueryExpression<?>) query, unionType);
        } else if (query instanceof ColumnSubQuery) {
            spec = new UnionAndColumnSubQuery<>((ColumnSubQuery) query, unionType);
        } else if (query instanceof RowSubQuery) {
            spec = new UnionAndRowSubQuery<>((RowSubQuery) query, unionType);
        } else if (query instanceof SubQuery) {
            spec = new UnionAndSubQuery<>((SubQuery) query, unionType);
        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return (StandardSelectSpec<C, Q>) spec;
    }


    private LockMode lockMode;

    StandardSimpleQuery(@Nullable C criteria) {
        super(CriteriaContexts.queryContext(criteria));
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
            right = StandardUnionQuery.bracketQuery(thisQuery)
                    .asQuery();
            unionSpec = StandardUnionQuery.unionQuery(andQuery.left, andQuery.unionType, right);
        } else {
            unionSpec = StandardUnionQuery.bracketQuery(this.asQuery());
        }
        return unionSpec;
    }

    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(final Q left, final UnionType unionType, final Q right) {
        return StandardUnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final StandardSelectSpec<C, Q> asQueryAndQuery(final UnionType unionType) {
        return StandardSimpleQuery.asQueryAndQuery(this.asQuery(), unionType);
    }


    @Override
    final StandardJoinSpec<C, Q> addFirstTableBlock(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(table, tableAlias));
        return this;
    }

    @Override
    final StandardJoinSpec<C, Q> addFirstTablePartBlock(TablePart tablePart, String alias) {
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(tablePart, alias));
        return this;
    }

    @Override
    final StandardOnSpec<C, Q> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new OnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final StandardOnSpec<C, Q> createOnBlock(_JoinType joinType, TablePart tablePart, String alias) {
        return new OnBlock<>(joinType, tablePart, alias, this);
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionTableBlock() {
        return new NoActionOnBlock<>(this);
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionOnBlock() {
        return new NoActionOnBlock<>(this);
    }


    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect mode) {
        // no-op
    }

    @SuppressWarnings("unchecked")
    @Override
    final Q onAsQuery(final boolean justAsQuery) {
        final Q thisQuery, resultQuery;
        if (this instanceof ScalarSubQuery) {
            thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
        } else {
            thisQuery = (Q) this;
        }
        if (justAsQuery && this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> unionAndQuery = (AbstractUnionAndQuery<C, Q>) this;
            resultQuery = StandardUnionQuery.unionQuery(unionAndQuery.left, unionAndQuery.unionType, thisQuery)
                    .asQuery();
        } else {
            resultQuery = thisQuery;
        }
        return resultQuery;
    }


    @Override
    final void onClear() {
        this.lockMode = null;
    }


    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }




    /*################################## blow private inter class method ##################################*/


    private static final class NoActionOnBlock<C, Q extends Query> extends NoActionOnClause<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        private NoActionOnBlock(StandardJoinSpec<C, Q> joinSpec) {
            super(joinSpec);
        }

    }// NoActionOnBlock

    private static final class OnBlock<C, Q extends Query> extends OnClauseTableBlock<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        private final StandardSimpleQuery<C, Q> query;

        OnBlock(_JoinType joinType, TablePart tablePart, String alias, StandardSimpleQuery<C, Q> query) {
            super(joinType, tablePart, alias);
            this.query = query;
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.query.criteriaContext;
        }

        @Override
        StandardJoinSpec<C, Q> endOnClause() {
            return this.query;
        }

    } // OnBlock


    private static final class SimpleSelect<C> extends StandardSimpleQuery<C, Select>
            implements Select {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleSelect


    /**
     * @see #subQuery(Object)
     */
    private static class SimpleSubQuery<C, Q extends SubQuery> extends StandardSimpleQuery<C, Q> implements SubQuery {

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }


    } // SimpleSubQuery


    /**
     * @see #rowSubQuery(Object)
     */
    private static final class SimpleRowSubQuery<C> extends SimpleSubQuery<C, RowSubQuery>
            implements RowSubQuery {

        private SimpleRowSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    } // SimpleRowSubQuery

    /**
     * @see #columnSubQuery(Object)
     */
    private static final class SimpleColumnSubQuery<C> extends SimpleSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private SimpleColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleColumnSubQuery


    /**
     * @see #scalarSubQuery(Object)
     */
    private static final class SimpleScalarSubQuery<C, E> extends SimpleSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private SimpleScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }


    }// SimpleScalarSubQuery


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q> {

        private final Q left;

        private final UnionType unionType;

        AbstractUnionAndQuery(Q left, UnionType unionType) {
            super(CriteriaUtils.getCriteria(left));
            this.left = left;
            this.unionType = unionType;
        }


    }//AbstractUnionAndQuery


    private static final class UnionAndSelect<C> extends AbstractUnionAndQuery<C, Select>
            implements Select {

        private UnionAndSelect(Select left, UnionType unionType) {
            super(left, unionType);
        }

    } // UnionAndSelect


    private static class UnionAndSubQuery<C, Q extends SubQuery> extends AbstractUnionAndQuery<C, Q>
            implements SubQuery {

        private UnionAndSubQuery(Q left, UnionType unionType) {
            super(left, unionType);
        }

    }// UnionAndSubQuery


    private static final class UnionAndRowSubQuery<C> extends UnionAndSubQuery<C, RowSubQuery>
            implements RowSubQuery, RowSubQuery.StandardRowSubQuerySpec<C> {

        private UnionAndRowSubQuery(RowSubQuery left, UnionType unionType) {
            super(left, unionType);
        }

    }// UnionAndRowSubQuery


    private static final class UnionAndColumnSubQuery<C> extends UnionAndSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private UnionAndColumnSubQuery(ColumnSubQuery left, UnionType unionType) {
            super(left, unionType);
        }


    }// UnionAndColumnSubQuery


    private static final class UnionAndScalarSubQuery<C, E> extends UnionAndSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private UnionAndScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType) {
            super(left, unionType);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }


    }// UnionAndScalarSubQuery


}
