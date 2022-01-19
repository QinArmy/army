package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.List;

/**
 * @see StandardSimpleQuery
 */
@SuppressWarnings("unchecked")
abstract class StandardUnionQuery<C, Q extends Query> extends PartQuery<
        C,
        Q,
        StandardQuery.StandardUnionSpec<C, Q>, //UR
        StandardQuery.StandardLimitClause<C, Q>,//OR
        StandardQuery.StandardUnionClause<C, Q>,//LR
        StandardQuery.StandardSelectSpec<C, Q>>// SP
        implements StandardQuery.StandardUnionSpec<C, Q>, _UnionQuery, StandardQuery {

    static <C, Q extends Query> StandardUnionSpec<C, Q> bracketQuery(final Q query) {
        final StandardUnionSpec<C, ?> unionSpec;
        if (query instanceof Select) {
            unionSpec = new BracketSelect<>((Select) query);
        } else if (query instanceof ScalarSubQuery) {
            unionSpec = new BracketScalarSubQuery<>((ScalarQueryExpression<?>) query);
        } else if (query instanceof ColumnSubQuery) {
            unionSpec = new BracketColumnSubQuery<>((ColumnSubQuery) query);
        } else if (query instanceof RowSubQuery) {
            unionSpec = new BracketRowSubQuery<>((RowSubQuery) query);
        } else if (query instanceof SubQuery) {
            unionSpec = new BracketSubQuery<>((SubQuery) query);
        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }


    static <C, Q extends Query> StandardUnionSpec<C, Q> unionQuery(Q left, UnionType unionType, Q right) {
        final StandardUnionSpec<C, ?> unionSpec;
        if (left instanceof Select) {
            unionSpec = new UnionSelect<>((Select) left, unionType, (Select) right);
        } else if (left instanceof ScalarSubQuery) {
            unionSpec = new UnionScalarSubQuery<>((ScalarQueryExpression<?>) left, unionType, (ScalarQueryExpression<?>) right);
        } else if (left instanceof ColumnSubQuery) {
            unionSpec = new UnionColumnSubQuery<>((ColumnSubQuery) left, unionType, (ColumnSubQuery) right);
        } else if (left instanceof RowSubQuery) {
            unionSpec = new UnionRowSubQuery<>((RowSubQuery) left, unionType, (RowSubQuery) right);
        } else if (left instanceof SubQuery) {
            unionSpec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }


    final Q left;

    private StandardUnionQuery(Q left) {
        super(CriteriaUtils.getUnionContext(left));
        this.left = left;
    }

    @Override
    public final List<SelectPart> selectPartList() {
        return ((_PartQuery) this.left).selectPartList();
    }

    @Override
    public final StandardUnionSpec<C, Q> bracketsQuery() {
        return bracketQuery(this.asQuery());
    }

    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return unionQuery(left, unionType, right);
    }

    @Override
    final StandardSelectSpec<C, Q> asQueryAndQuery(UnionType unionType) {
        return StandardSimpleQuery.asQueryAndQuery(this.asQuery(), unionType);
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


    private static final class BracketSelect<C> extends StandardUnionQuery<C, Select> implements Select {

        private BracketSelect(Select query) {
            super(query);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE_LEFT_BRACKET);

            context.dialect().select(this.left, context);

            builder.append(Constant.SPACE_RIGHT_BRACKET);

        }
    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends StandardUnionQuery<C, Q> implements SubQuery {

        private BracketSubQuery(Q query) {
            super(query);
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE_LEFT_BRACKET);

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE_RIGHT_BRACKET);
        }


    }


    private static final class BracketRowSubQuery<C> extends BracketSubQuery<C, RowSubQuery> implements RowSubQuery {

        private BracketRowSubQuery(RowSubQuery query) {
            super(query);
        }

    }//BracketRowSubQuery

    private static final class BracketColumnSubQuery<C> extends BracketSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private BracketColumnSubQuery(ColumnSubQuery query) {
            super(query);
        }

    }//BracketColumnSubQuery


    private static final class BracketScalarSubQuery<C, E> extends BracketSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private BracketScalarSubQuery(ScalarQueryExpression<E> query) {
            super(query);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//BracketScalarSubQuery

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

        final UnionType unionType;

        final Q right;

        UnionQuery(Q left, UnionType unionType, Q right) {
            super(left);
            this.unionType = unionType;
            this.right = right;
        }


    }// UnionQuery


    private static final class UnionSelect<C> extends UnionQuery<C, Select> implements Select {

        private UnionSelect(Select left, UnionType unionType, Select right) {
            super(left, unionType, right);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            dialect.select(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.select(this.right, context);
        }

    }//UnionSelect


    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q> implements SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, Q right) {
            super(left, unionType, right);
        }

        @Override
        public final void appendSql(_SqlContext context) {
            final _Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }


    }// UnionSubQuery


    private static final class UnionRowSubQuery<C> extends UnionSubQuery<C, RowSubQuery> implements RowSubQuery {

        private UnionRowSubQuery(RowSubQuery left, UnionType unionType, RowSubQuery right) {
            super(left, unionType, right);
        }


    }//UnionRowSubQuery


    private static final class UnionColumnSubQuery<C> extends UnionSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private UnionColumnSubQuery(ColumnSubQuery left, UnionType unionType, ColumnSubQuery right) {
            super(left, unionType, right);
        }

    }//UnionColumnSubQuery


    private static final class UnionScalarSubQuery<C, E> extends UnionSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private UnionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType
                , ScalarQueryExpression<?> right) {
            super(left, unionType, (ScalarQueryExpression<E>) right);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }


    }//UnionScalarSubQuery


}
