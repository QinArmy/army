package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.dialect.Constant;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.List;

/**
 * @see StandardSimpleQuery
 */
abstract class StandardUnionQuery<C, Q extends Query> extends PartQuery<
        C,
        Q,
        StandardQuery.StandardUnionSpec<C, Q>, //UR
        StandardQuery.StandardLimitClause<C, Q>,//OR
        StandardQuery.StandardUnionClause<C, Q>,//LR
        StandardQuery.StandardSelectClauseSpec<C, Q>>// SP
        implements StandardQuery.StandardUnionSpec<C, Q>, _UnionQuery, StandardQuery {

    static <C> StandardUnionSpec<C, Select> bracketSelect(Select select, @Nullable C criteria) {
        select.prepared();
        return new BracketSelect<>(select, criteria);
    }

    static <C> StandardUnionSpec<C, Select> unionSelect(Select left, UnionType unionType, Select right, @Nullable C criteria) {
        left.prepared();
        return new UnionSelect<>(left, unionType, right, criteria);
    }


    static <C> StandardUnionSpec<C, SubQuery> bracketSubQuery(SubQuery subQuery, @Nullable C criteria) {
        subQuery.prepared();
        return new BracketSubQuery<>(subQuery, criteria);
    }

    static <C> StandardUnionSpec<C, SubQuery> unionSubQuery(SubQuery left, UnionType unionType
            , SubQuery right, @Nullable C criteria) {
        left.prepared();
        return new UnionSubQuery<>(left, unionType, right, criteria);
    }


    static <C> StandardUnionSpec<C, RowSubQuery> bracketRowSubQuery(RowSubQuery subQuery, @Nullable C criteria) {
        subQuery.prepared();
        return new BracketRowSubQuery<>(subQuery, criteria);
    }

    static <C> StandardUnionSpec<C, RowSubQuery> unionRowSubQuery(RowSubQuery left, UnionType unionType
            , RowSubQuery right, @Nullable C criteria) {
        left.prepared();
        return new UnionRowSubQuery<>(left, unionType, right, criteria);
    }

    static <C> StandardUnionSpec<C, ColumnSubQuery> bracketColumnSubQuery(ColumnSubQuery subQuery, @Nullable C criteria) {
        subQuery.prepared();
        return new BracketColumnSubQuery<>(subQuery, criteria);
    }

    static <C> StandardUnionSpec<C, ColumnSubQuery> unionColumnSubQuery(ColumnSubQuery left, UnionType unionType
            , ColumnSubQuery right, @Nullable C criteria) {
        left.prepared();
        return new UnionColumnSubQuery<>(left, unionType, right, criteria);
    }


    static <C, E> StandardUnionSpec<C, ScalarQueryExpression<E>> bracketScalarSubQuery(ScalarQueryExpression<E> subQuery
            , @Nullable C criteria) {
        subQuery.prepared();
        return new BracketScalarSubQuery<>(subQuery, criteria);
    }

    static <C, E> StandardUnionSpec<C, ScalarQueryExpression<E>> unionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType
            , ScalarQueryExpression<?> right, @Nullable C criteria) {
        left.prepared();
        right.prepared();
        return new UnionScalarSubQuery<>(left, unionType, right, criteria);
    }

    @SuppressWarnings("unchecked")
    static <C, Q extends Query> StandardUnionSpec<C, Q> bracket(Q query, @Nullable C criteria) {
        final StandardUnionSpec<C, ?> unionSpec;
        if (query instanceof Select) {
            unionSpec = StandardUnionQuery.bracketSelect((Select) query, criteria);
        } else if (query instanceof ScalarSubQuery) {
            unionSpec = StandardUnionQuery.bracketScalarSubQuery((ScalarQueryExpression<?>) query, criteria);
        } else if (query instanceof ColumnSubQuery) {
            unionSpec = StandardUnionQuery.bracketColumnSubQuery((ColumnSubQuery) query, criteria);
        } else if (query instanceof RowSubQuery) {
            unionSpec = StandardUnionQuery.bracketRowSubQuery((RowSubQuery) query, criteria);
        } else if (query instanceof SubQuery) {
            unionSpec = StandardUnionQuery.bracketSubQuery((SubQuery) query, criteria);
        } else {
            throw new IllegalStateException("unknown query type");
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }

    @SuppressWarnings("unchecked")
    static <C, Q extends Query> StandardUnionSpec<C, Q> union(Q left, UnionType unionType, Q right, @Nullable C criteria) {
        final StandardUnionSpec<C, ?> unionSpec;
        if (left instanceof Select) {
            unionSpec = StandardUnionQuery.unionSelect((Select) left, unionType, (Select) right, criteria);
        } else if (left instanceof ScalarSubQuery) {
            unionSpec = StandardUnionQuery.unionScalarSubQuery((ScalarQueryExpression<?>) left, unionType, (ScalarQueryExpression<?>) right, criteria);
        } else if (left instanceof ColumnSubQuery) {
            unionSpec = StandardUnionQuery.unionColumnSubQuery((ColumnSubQuery) left, unionType, (ColumnSubQuery) right, criteria);
        } else if (left instanceof RowSubQuery) {
            unionSpec = StandardUnionQuery.unionRowSubQuery((RowSubQuery) left, unionType, (RowSubQuery) right, criteria);
        } else if (left instanceof SubQuery) {
            unionSpec = StandardUnionQuery.unionSubQuery((SubQuery) left, unionType, (SubQuery) right, criteria);
        } else {
            throw new IllegalStateException("unknown query type");
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }


    private StandardUnionQuery(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final StandardUnionSpec<C, Q> bracketsQuery() {
        return bracket(this.asQuery(), this.criteria);
    }

    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return union(left, unionType, right, this.criteria);
    }

    @Override
    final StandardSelectClauseSpec<C, Q> asQueryAndQuery(UnionType unionType) {
        return StandardSimpleQuery.asQueryAndQuery(this.asQuery(), unionType, this.criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    final Q internalAsQuery(final boolean justAsQuery) {
        final Q query;
        if (this instanceof ScalarSubQuery) {
            query = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
        } else {
            query = (Q) this;
        }
        return query;
    }

    @Override
    final void internalClear() {
        //no-op
    }


    private static abstract class BracketQuery<C, Q extends Query> extends StandardUnionQuery<C, Q> {

        final Q query;

        BracketQuery(Q query, @Nullable C criteria) {
            super(criteria);
            this.query = query;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(" (");

            final Q query = this.query;
            if (query instanceof Select) {
                context.dialect().select((Select) query, context);
            } else if (query instanceof SubQuery) {
                context.dialect().subQuery((SubQuery) query, context);
            } else {
                throw _Exceptions.unknownQueryType(query);
            }
            builder.append(" )");
        }

    }// BracketQuery

    /**
     * @see #bracketSelect(Select, Object)
     */
    private static final class BracketSelect<C> extends BracketQuery<C, Select> implements Select {

        private BracketSelect(Select query, @Nullable C criteria) {
            super(query, criteria);
        }

    }

    private static class BracketSubQuery<C, Q extends SubQuery> extends BracketQuery<C, Q> implements SubQuery {

        private BracketSubQuery(Q query, @Nullable C criteria) {
            super(query, criteria);
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return this.query.selectPartList();
        }

    }

    /**
     * @see #bracketRowSubQuery(RowSubQuery, Object)
     */
    private static final class BracketRowSubQuery<C> extends BracketSubQuery<C, RowSubQuery> implements RowSubQuery {

        private BracketRowSubQuery(RowSubQuery query, @Nullable C criteria) {
            super(query, criteria);
        }

    }

    /**
     * @see #bracketColumnSubQuery(ColumnSubQuery, Object)
     */
    private static final class BracketColumnSubQuery<C> extends BracketSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        public BracketColumnSubQuery(ColumnSubQuery query, @Nullable C criteria) {
            super(query, criteria);
        }

    }

    /**
     * @see #bracketScalarSubQuery(ScalarQueryExpression, Object)
     */
    private static final class BracketScalarSubQuery<C, E> extends BracketSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private BracketScalarSubQuery(ScalarQueryExpression<E> query, @Nullable C criteria) {
            super(query, criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.query.selectPartList().get(0)).paramMeta();
        }

    }

    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link UnionSelect}</li>
     *         <li>{@link UnionSubQuery}</li>
     *         <li>{@link UnionRowSubQuery}</li>
     *         <li>{@link UnionColumnSubQuery}</li>
     *         <li>{@link UnionScalarSubQuery}</li>
     *     </ul>
     * </p>
     */
    private static abstract class UnionQuery<C, Q extends Query> extends StandardUnionQuery<C, Q> {

        final Q left;

        private final UnionType unionType;

        private final Q right;

        UnionQuery(Q left, UnionType unionType, Q right, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final Dialect dialect = context.dialect();
            if (this instanceof Select) {
                dialect.select((Select) this.left, context);
                context.sqlBuilder()
                        .append(Constant.SPACE)
                        .append(this.unionType.keyWords);
                dialect.select((Select) this.right, context);
            } else if (this instanceof SubQuery) {
                dialect.subQuery((SubQuery) this.left, context);
                context.sqlBuilder()
                        .append(Constant.SPACE)
                        .append(this.unionType.keyWords);
                dialect.subQuery((SubQuery) this.right, context);
            } else {
                throw _Exceptions.unknownQueryType(this);
            }

        }


    }// UnionQuery

    /**
     * @see #unionSelect(Select, UnionType, Select, Object)
     */
    private static final class UnionSelect<C> extends UnionQuery<C, Select> implements Select {

        private UnionSelect(Select left, UnionType unionType, Select right, @Nullable C criteria) {
            super(left, unionType, right, criteria);
        }

    }

    /**
     * @see #unionSubQuery(SubQuery, UnionType, SubQuery, Object)
     */
    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q> implements SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, Q right, @Nullable C criteria) {
            super(left, unionType, right, criteria);
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return this.left.selectPartList();
        }


    }// UnionSubQuery

    /**
     * @see #unionRowSubQuery(RowSubQuery, UnionType, RowSubQuery, Object)
     */
    private static final class UnionRowSubQuery<C> extends UnionSubQuery<C, RowSubQuery> implements RowSubQuery {

        private UnionRowSubQuery(RowSubQuery left, UnionType unionType, RowSubQuery right, @Nullable C criteria) {
            super(left, unionType, right, criteria);
        }


    }//UnionRowSubQuery

    /**
     * @see #unionColumnSubQuery(ColumnSubQuery, io.army.criteria.impl.UnionType, io.army.criteria.ColumnSubQuery, java.lang.Object)
     */
    private static final class UnionColumnSubQuery<C> extends UnionSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private UnionColumnSubQuery(ColumnSubQuery left, UnionType unionType, ColumnSubQuery right
                , @Nullable C criteria) {
            super(left, unionType, right, criteria);
        }

    }//UnionColumnSubQuery

    /**
     * @see #unionScalarSubQuery(ScalarQueryExpression, UnionType, ScalarQueryExpression, Object)
     */
    private static final class UnionScalarSubQuery<C, E> extends UnionSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private UnionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType, ScalarQueryExpression<?> right
                , @Nullable C criteria) {
            super(left, unionType, right, criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }


    }//UnionScalarSubQuery


}
